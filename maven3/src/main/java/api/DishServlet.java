package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Dish;
import model.DishDao;
import model.User;
import util.OrderUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@WebServlet("/dish")
public class DishServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    static class Request {
        public String name;
        public int price;
    }

    static class Response {
        public int ok;
        public String reason;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        try {
            //1、检查用户的登录状态
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new Exception("当前未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new Exception("当前未登录");
            }
            //2、检查用户是否是管理员
            if (user.getIsAdmin() == 0) {
                throw new Exception("非管理员用户");
            }
            //3、读取请求body
            String body = OrderUtil.readBody(req);
            //4、把body转成request对象
            Request request = gson.fromJson(body, Request.class);
            //5、构造dish对象插入到数据库中
            Dish dish = new Dish();
            dish.setName(request.name);
            dish.setPrice(request.price);
            DishDao dishDao = new DishDao();
            dishDao.add(dish);
            //6、结果返回给客户端
            response.ok = 1;
            response.reason = "";
        } catch (Exception e) {
            response.ok = 0;
            response.reason = e.getMessage();
        } finally {
            resp.setContentType("application/json; charset=utf-8");
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        try {
            //1、检查用户是否登录
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new Exception("未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new Exception("未登录");
            }
            //2、检查用户是否是管理员
            if (user.getIsAdmin() == 0) {
                throw new Exception("非管理员用户");
            }
            //3、读取到dishId
            String dishIdStr = req.getParameter("dishId");
            if (dishIdStr == null) {
                throw new Exception("dishId参数不正确");
            }
            int dishId = Integer.parseInt(dishIdStr);
            //4、删除数据库中的记录
            DishDao dishDao = new DishDao();
            dishDao.delete(dishId);
            //5、返回响应
            response.ok = 1;
            response.reason = "";
        } catch (Exception e) {
            response.ok = 0;
            response.reason = e.getMessage();
        } finally {
            resp.setContentType("application/json; charset=utf-8");
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }


    //查看所有菜品
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response response = new Response();
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        try {
            //1、检测登录状态
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new Exception("未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new Exception("未登录");
            }
            //2、从数据库中读取数据
            DishDao dishDao = new DishDao();
            List<Dish> dishes = dishDao.selectAll();
            //3、把结果返回到页面
            String jsonString = gson.toJson(dishes);
            resp.getWriter().write(jsonString);
        } catch (Exception e) {
            response.ok  = 0;
            response.reason = e.getMessage();
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }
}
