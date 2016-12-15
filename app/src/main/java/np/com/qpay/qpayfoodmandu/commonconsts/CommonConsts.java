package np.com.qpay.qpayfoodmandu.commonconsts;

/**
 * Created by qpay on 12/12/16.
 */

public class CommonConsts {

    /**
     * URLS
     * */
    public static final String BASE_URL = "https://testportal.qpaysolutions.net/api/";
//    public static final String REGISTER_DEVICE =  BASE_URL + "Restaurant/Info";

    public static final String BASE_API_MERCHANT = "https://node.qpaysolutions.net/QPay.svc/";
    public static final String CHECK_STATUS_MERCHANT = CommonConsts.BASE_API_MERCHANT + "activatemerchant";
    public static final String PROCESS_LOG_TXN = CommonConsts.BASE_API_MERCHANT + "logtxn";
    public static final String CHECK_TRANSCTION_STATUSM = CommonConsts.BASE_API_MERCHANT + "checktxnStatusm";
    public static final String CANCEL_MERCH_TRXN=BASE_API_MERCHANT+"canceltxnm";
    public static final String MERCHANT_HISTORY_URL = BASE_URL + "transaction/merchanthistory";

    /**
     * PARAMS
     * */
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String IS_MERCHANT_REGISTERED = "IsMerchantRegistered";
    public static final String MERCHANT_NAME = "merchantName";
    public static final String MERCHANT_ADDRESS = "merchantAddress";
    public static final String MERCHANT_ID = "merchantId";
    public static final String MERCHANT_ENCRYPTED_ID = "MERCHANT_ENCRYPTED_ID";
    public static final String TAXI_FLAG = "TaxiFlag";
    public static final String APP_ID = "AppId";
    public static final String MERCHANT_CELL_PHONE = "MERCHANT_CELL_PHONE";
    public static final String MERCHANT_MCC = "MERCHANT_MCC";

    /**
     * Tags
     * **/
    public static final String RT="RT";
    public static final String LO="LO";
    public static final String PR="PR";
}
