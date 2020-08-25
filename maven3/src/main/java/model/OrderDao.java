package model;


import jdk.management.resource.internal.inst.FileOutputStreamRMHooks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//操作订单
//新增订单 查看所有订单 查看指定用户的订单 查看订单的详细信息 修改订单状态（是否完成）
public class OrderDao {
    //新增订单
    public void add(Order order) throws Exception {
        //和两个表关联 order_user
        // order_dish 一个订单可能涉及点多个菜，所以就要一次性插入多个记录
        //先操作order_user 表
        addOrderUser(order);

        //操作order_dish
        addOrderDish(order);
    }

    private void addOrderUser(Order order) throws Exception {
        Connection connection = DBUtil.getConnection();
        String sql = "insert into order_user values (null,?,now(),0)";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            //插入数据的同时会返回生成的自增主键的值
            statement = connection.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS );
            statement.setInt(1,order.getUserId());
            int ret = statement.executeUpdate();
            if (ret != 1) {
                throw new Exception("插入订单失败");
            }
            //读出自增主键的值
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                order.setOrderId(resultSet.getInt(1));
            }
            System.out.println("插入订单第一步成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("插入订单失败");
        } finally {
            DBUtil.close(connection,statement,null);
        }

    }

    //把菜品信息插入到表中
    private void addOrderDish(Order order) throws Exception {
        Connection connection = DBUtil.getConnection();
        String sql = "inser into order_dish values (?,?)";
        PreparedStatement statement = null;
        try {
            connection.setAutoCommit(false);//关闭自动提交
            statement = connection.prepareStatement(sql);
            List<Dish> dishes = order.getDishes();
            //遍历dishes，给sql添加多个values的值
            for (Dish dish : dishes) {
                //orderId 是在刚才获取到的自增组件
                statement.setInt(1,order.getOrderId());
                statement.setInt(2,dish.getDishId());
                statement.addBatch();//给sql新增一个片段
            }
            //执行sql -- 并不是真的执行
            statement.executeBatch();//把刚才的sql统一执行
            //发送给服务器 -- 真的执行  commit可以执行多个sql，一次调用统一发送
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            deleteOrderUser(order.getOrderId());
        } finally {
            DBUtil.close(connection,statement,null);
        }
    }

    private void deleteOrderUser(int orderId) throws Exception {
        Connection connection = DBUtil.getConnection();
        String sql = "delete from order_user where orderId = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,orderId);
            int ret = statement.executeUpdate();
            if (ret != 1) {
                throw new Exception("回滚失败");
            }
            System.out.println("回滚成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("回滚失败");
        } finally {
            DBUtil.close(connection,statement,null);
        }
    }

    //获取到所有的订单信息
    public List<Order> seleteAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        Connection connection = DBUtil.getConnection();
        String sql = "select * from order_user";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("orderId"));
                order.setUserId(resultSet.getInt("userId"));
                order.setTime(resultSet.getTimestamp("time"));
                order.setIsDone(resultSet.getInt("isDone"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return orders;
    }

    public List<Order> selectByUserId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        Connection connection = DBUtil.getConnection();
        String sql = "select * from order_user where userId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,userId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("orderId"));
                order.setUserId(resultSet.getInt("userId"));
                order.setTime(resultSet.getTimestamp("time"));
                order.setIsDone(resultSet.getInt("isDone"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return orders;
    }


    public Order selectById(int orderId) throws Exception {
        //1、根据orderId得到一个order对象
        Order order = buildOrder(orderId);
        //2、根据orderId得到对应的菜品id列表
        List<Integer> dishIds = selectDishIds(orderId);
        //3、根据菜品id列表，查询dishes表，获取菜品详情
        getDishDetail(order,dishIds);
        return order;
    }

    ///根据orderId查询order对象的基本信息
    private Order buildOrder(int orderId) throws SQLException {
        Connection connection = DBUtil.getConnection();
        String sql = "select * from order_user where oderId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,orderId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("orderId"));
                order.setUserId(resultSet.getInt("userId"));
                order.setTime(resultSet.getTimestamp("time"));
                order.setIsDone(resultSet.getInt("isDone"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

    private List<Integer> selectDishIds(int orderId) throws SQLException {
        List<Integer> dishIds = new ArrayList<>();
        Connection connection = DBUtil.getConnection();
        String sql = "select * from order_dish where orderId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,orderId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dishIds.add(resultSet.getInt("dishId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return dishIds;
    }

    private Order getDishDetail(Order order, List<Integer> dishIds) throws Exception {
        //1、准备好要返回的结果
        List<Dish> dishes = new ArrayList<>();
        DishDao dishDao = new DishDao();
        //2、遍历dishIds在dish 表中查
        for (Integer dishId : dishIds) {
            Dish dish = dishDao.selectById(dishId);
            dishes.add(dish);
        }

        //3、把dishes设到order对象中
        order.setDishes(dishes);
        return order;
    }
}
