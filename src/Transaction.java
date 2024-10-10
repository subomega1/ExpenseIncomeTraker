public class Transaction {
    private int id;
    private String type;
    private String description;
    private double amount;


    public Transaction (){}

    public Transaction (int id , String type ,String description , double amount){
        this.id = id  ;
        this.type = type;
        this.description = description;
        this.amount = amount;

    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }
}
