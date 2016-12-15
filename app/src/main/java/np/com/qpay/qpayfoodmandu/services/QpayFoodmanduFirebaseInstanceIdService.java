package np.com.qpay.qpayfoodmandu.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Owner on 9/22/2016.
 */
public class QpayFoodmanduFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Sangharsha", "Refreshed token: " + refreshedToken);
//        sendRegistrationToServer(refreshedToken);
    }
}
