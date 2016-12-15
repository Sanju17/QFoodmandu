package np.com.qpay.qpayfoodmandu.utils;

/**
 * Created by deadlydragger on 11/9/16.
 */
public class Decimalformate {
    public String decimalFormate(String value){
        String decimal="";
        try {
         decimal= String.format("%.2f", Double.parseDouble(value));

        }catch (Exception e){
            e.printStackTrace();
        }
        return decimal;
    }
}
