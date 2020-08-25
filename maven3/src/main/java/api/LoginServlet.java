package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.User;
import model.UserDao;
import util.OrderUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    static class Request {
        public String name;
        public String password;
    }

    static class Response {
        public String reason;
        public int ok;
        public String name;
        public int isAdmin;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        try {
            //1、读取body数据
            String body = OrderUtil.readBody(req);
            //2、将读取的数据解析成对象
            Request request = gson.fromJson(body,Request.class);
            //3、按用户名查找，校验密码
            UserDao userDao = new UserDao();
            User user = userDao.selectByName(request.name);
            if (user == null || !user.getPassword().equals(request.password)) {
                throw new Exception("用户名或密码错误");
            }
            //4、登录成功，创建session对象
            HttpSession session = req.getSession(true);
            session.setAttribute("user",user);
            response.ok = 1;
            response.reason = "";
            response.name = user.getName();
            response.isAdmin = user.getIsAdmin();
        } catch (Exception e) {
            //5、登录失败，返回错误提示
            response.ok = 0;
            response.reason = e.getMessage();
        } finally {
            //6、结果写回给客户端
            resp.setContentType("application/json; charset=utf-8");
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }


    //检测登录状态
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        try {
            //1、获取用户当前的session，不存在，认为未登录状态
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new Exception("当前未登录");
            }
            //2、从session获取user对象
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new Exception("当前未登录");
            }
            //3、把user中的信息填充进返回值结果中
            response.ok = 0;
            response.reason = "";
            response.name = user.getName();
            response.isAdmin = user.getIsAdmin();
        } catch (Exception e) {
            response.ok = 0;
            response.reason = e.getMessage();
        } finally {
            resp.setContentType("application/json; charset=utf-8");
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }
}
