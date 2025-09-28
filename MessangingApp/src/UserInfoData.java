import java.sql.*;

public class UserInfoData {
    static Connection connection;
    public UserInfoData() throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found. Add the jar to your classpath!", e);
        }
        connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema",
                "root",
                "Nbvjatq@200822"

        );
    }

    public static String find_port(String username) throws SQLException {
        String sql = "SELECT port AS port " +
                "FROM users WHERE username = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            return rs.getString("port");
        }
        return "no port";
    }

    public static boolean check_credentials(String username, String password) throws SQLException {
        String sql = "SELECT password AS pass FROM login_schema.users WHERE username = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            return rs.getString("pass").equals(password);
        }
        return false;
    }

    public static void add_credentials(String username, String password, String port) throws SQLException {
        String sql = "INSERT INTO users (username, password, port) VALUES(?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setString(3, port);
        ps.executeUpdate();
        System.out.println("credentials have been added");
    }

    public static void add_user_port(String port, String username) throws SQLException{
        String sql = "UPDATE users SET port = ? WHERE username = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, port);
        ps.setString(2, username);
        ps.executeUpdate();
        System.out.println("User port added");
    }

}
