package model;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    public static final String URL = "jdbc:mysql://127.0.0.1:3306/order_system?characterEncoding=utf8&useSSL=true";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";

    public static volatile DataSource dataSource = null;
    public static DataSource getDataSource() {

        if (dataSource == null) {
            synchronized (DBUtil.class) {
                if (dataSource == null) {
                    dataSource = new MysqlDataSource();
                    ((MysqlDataSource)dataSource).setURL(URL);
                    ((MysqlDataSource)dataSource).setURL(USERNAME);
                    ((MysqlDataSource)dataSource).setURL(PASSWORD);

                }
            }
        }
        return dataSource;
    }
    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("数据库连接失败，请检查数据库是否正确启动");
        return null;
    }
    public static void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
