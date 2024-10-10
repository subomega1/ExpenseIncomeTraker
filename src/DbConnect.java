import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect {
    private static final String DB_NAME = "expenseIncome";
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/"+DB_NAME;
    private static final String USER = "root";
    private static final String Password = "";


    public static Connection getConnection (){
        Connection connection = null ;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL,USER,Password);
            System.out.println("connected to BD");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Connection failed");
        }
        return connection;
    }
}
