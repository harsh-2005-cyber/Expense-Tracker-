import java.util.Date;

public class Income {
    private String source;
    private double amount;
    private Date date;

    public Income(String source, double amount, Date date) {
        this.source = source;
        this.amount = amount;
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Income{" +
                "source='" + source + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
