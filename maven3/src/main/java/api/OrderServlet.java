package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Dish;
import model.Order;
import model.OrderDao;
import model.User;
import util.OrderUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    static class Response {
        public int ok;
        public String reason;
    }


    //新增订单
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response response = new Response();
        req.setCharacterEncoding("utf-8");

        try {
            //1、检查用户登录状态
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new Exception("未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new Exception("未登录");
            }
            //2、判断用户是否是管理员
            if (user.getIsAdmin() == 1) {
                throw new Exception("管理员不可新增订单");
            }
            //3、读取body中的数据，进行解析
            String body = OrderUtil.readBody(req);
            //4、按照json格式解析body
            Integer[] dishIds = gson.fromJson(body, Integer[].class);
            //5、构造订单对象
            Order order = new Order();
            order.setUserId(user.getUserId());
            List<Dish> dishes = new ArrayList<>();

            for (Integer dishId : dishIds) {
                Dish dish = new Dish();
                dish.setDishId(dishId);
                dishes.add(dish);
            }
            order.setDishes(dishes);
            //6、插入到数据库
            OrderDao orderDao = new OrderDao();
            orderDao.add(order);
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

    //查看所有订单

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response response = new Response();
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        try {
            //1、验证登录状态
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new Exception("未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new Exception("未登录");
            }
            //2、判断用户属性
            //3、读取orderId字段，判断该字段是否存在
            OrderDao orderDao = new OrderDao();
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr == null) {
                //4、查找数据库 查找所有订单
                List<Order> orders = null;
                if (user.getIsAdmin() == 0) {
                    orders = orderDao.selectByUserId(user.getUserId());
                } else {
                    orders = orderDao.seleteAll();
                }
                //5、构造响应结果
                String jsonString = gson.toJson(orders);
                resp.getWriter().write(jsonString);
            } else {
                //4、查找数据库 查找指定订单
                int orderId = Integer.parseInt(orderIdStr);
                Order order = orderDao.selectById(orderId);
                if (user.getIsAdmin() == 0 &&
                            order.getUserId() != user.getUserId()) {
                    throw new Exception("无权查看他人订单");
                }
                //5、构造响应结果
                String jsonString = gson.toJson(order);
                resp.getWriter().write(jsonString);
            }
        } catch (Exception e) {
            //5、处理异常
            response.ok = 0;
            response.reason = e.getMessage();
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }


    //修改订单状态
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        try {
            //1、检查用户的登录状态
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new Exception("未登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new Exception("未登录");
            }
            //2、判断用户是否是管理员
            if (user.getIsAdmin() == 0) {
                    throw new Exception("非管理员用户");
            }
            //3、读取字段中的orderId和done
            String orderStr = req.getParameter("orderId");
            String isDoneStr = req.getParameter("isDone");
            if (orderStr == null || isDoneStr == null) {
                throw new Exception("参数错误");
            }
            //4、修改数据库
            OrderDao orderDao = new OrderDao();
            int orderId = Integer.parseInt(orderStr);
            int isDone = Integer.parseInt(isDoneStr);
            //5、返回响应结果
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
}
