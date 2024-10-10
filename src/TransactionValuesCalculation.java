import java.util.List;

public class TransactionValuesCalculation {
    public static  Double getTotalIncomes (List<Transaction> transactionsList){
        double totalIncome = 0.0;
        for (Transaction transaction : transactionsList){
            if ("Income".equals(transaction.getType())){
                totalIncome += transaction.getAmount();

            }
        }

        return totalIncome ;
    } public static  Double getTotalExpenses (List<Transaction> transactionsList){
        double totalExpenses = 0.0;
        for (Transaction transaction : transactionsList){
            if ("Expense".equals(transaction.getType())){
                totalExpenses += transaction.getAmount();

            }
        }

        return totalExpenses;
    }

    public  static Double getTotalValue (List<Transaction> transactions){
        Double totalIncome = getTotalIncomes(transactions);
        Double totalExpense = getTotalExpenses(transactions);
        return totalIncome -totalExpense ;
    }
}
