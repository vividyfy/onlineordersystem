package model;


import com.sun.xml.internal.fastinfoset.tools.FI_DOM_Or_XML_DOM_SAX_SAXEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//1、插入用户    注册
//2、按名字查找用户   登录
//3、按照用户id查找  展示信息时使用
public class UserDao {
    public void add(User user) throws Exception {
        //1、获取数据库的连接
        Connection connection = DBUtil.getConnection();
        //2、拼装sql语句
        String sql = "insert into user values(null, ?, ?, ?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getPassword());
            statement.setInt(2, user.getIsAdmin());
            statement.setString(3, user.getName());

            //3、执行sql
            int ret = statement.executeUpdate();
            if (ret != 1) {
                throw new Exception("插入用户失败");
            }
            System.out.println("插入用户成功");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("插入用户失败");

        } finally {
            //4、关闭连接
            DBUtil.close(connection,statement,null);
        }
    }

    public User selectByName(String name) throws Exception {
        //1、获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2、拼装sql
        String sql = "select * from user where name = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            //3、执行sql
            resultSet = statement.executeQuery();
            //4、遍历结果集（按照名字查，只能查到一个）
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setIsAdmin(resultSet.getInt("isAdmin"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("按姓名查找失败");
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

    public User selectById(int userId) throws Exception {
        Connection connection = DBUtil.getConnection();
        String sql = "select * from user where id = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,userId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setName((resultSet.getString("name")));
                user.setPassword(resultSet.getString("password"));
                user.setIsAdmin(resultSet.getInt("isAdmina"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("按id查找用户失败");
        } finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }
}
