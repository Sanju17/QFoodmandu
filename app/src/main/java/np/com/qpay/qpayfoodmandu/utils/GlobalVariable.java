package np.com.qpay.qpayfoodmandu.utils;

import android.support.multidex.MultiDexApplication;

/**
 * Created by qpay on 12/14/16.
 */

public class GlobalVariable extends MultiDexApplication {
    public double lat;
    public double lng;
    public String dev_token;

    public String getDev_token() {
        return dev_token;
    }

    public void setDev_token(String dev_token) {
        this.dev_token = dev_token;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
