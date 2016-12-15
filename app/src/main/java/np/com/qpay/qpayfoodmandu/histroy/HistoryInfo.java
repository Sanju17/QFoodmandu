package np.com.qpay.qpayfoodmandu.histroy;

/**
 * Created by CSharp05-user on 14/12/2016.
 */

public class HistoryInfo {

    private String wallet, date, amount;

    public HistoryInfo() {

    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {this.date = date;}

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
