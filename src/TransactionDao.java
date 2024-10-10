import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {


    public static List<Transaction> getAllTransaction(){

        List <Transaction> transactions = new ArrayList<>();

        Connection connection = DbConnect.getConnection();
        PreparedStatement ps;
        ResultSet rs;


        try {
             ps = connection.prepareStatement("SELECT * FROM `transaction_table`");
             rs = ps.executeQuery();
             while (rs.next()){
                 int id = rs.getInt("id");
                 String type = rs.getString("transaction_type");
                 String description = rs.getString("description");
                 double amount = rs.getDouble("amount");
                 Transaction transaction = new Transaction(id ,type,description,amount);
                 transactions.add(transaction);
             }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transactions;

    }

}
