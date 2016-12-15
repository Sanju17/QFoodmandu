package np.com.qpay.qpayfoodmandu.apicallers;

/**
 * Created by Owner on 7/28/2016.
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by dinesh on 7/15/16.
 */
public class HttpRequestHandler {

    static String error = "";

    public static String getRequest(String url, String username, String password) {
        String json = "";
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();

            URI uri = new URI(url);
            httpGet.setURI(uri);
            /// httpGet.addHeader(BasicScheme.authenticate(
            //        new UsernamePasswordCredentials(username, password),
            //        HTTP.UTF_8, false));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    json = stringBuffer.toString();
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }
        return json;
    }

    public static String putRequest(String url, String username, String password,
                                    List<NameValuePair> params) {
        String json = "";
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPut httpGet = new HttpPut();

            URI uri = new URI(url);
            httpGet.setURI(uri);
//            httpGet.addHeader(BasicScheme.authenticate(
//                    new UsernamePasswordCredentials(username, password),
//                    HTTP.UTF_8, false));
            httpGet.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    json = stringBuffer.toString();
                    json = json.replace("\\\"", "\"");
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }
        return json;
    }

    public static String postRequest(Context context, String url, String username, String password,
                                     JSONObject params) {

        String s = postRequest(url, username, password, params);
        if (!error.equals("")) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            error = "";
        }
        return s;
    }

    // function get json from url
    // by making HTTP POST or GET mehtod using json Params
    public static String postRequest(String url, String username, String password,
                                     JSONObject params) {

        String json = "";
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {

            //////TIMEOUT SETTING//////////////////////
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 30000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 40000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            //////////////////////////////////////////

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpGet = new HttpPost();

            URI uri = new URI(url);
            httpGet.setURI(uri);
            StringEntity entity = new StringEntity(params.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            httpGet.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (SocketTimeoutException e) {
            // TODO: handle exception
            e.printStackTrace();
            error = e.getMessage();
            Log.d("sangharsha", "SocketTimeout: " + error);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            error = e.getMessage();
            Log.d("sangharsha", "Exception: " + error);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    json = stringBuffer.toString();
                    json = json.replace("\\\"", "\"");
                } catch (IOException e) {
                    // TODO: handle exception
                    Log.d("sangharsha", error);
                }
            }
        }
        return json;
    }

    // function get json from url
    // by making HTTP POST or GET mehtod using NameValue Pair
    public static String postRequest(String url, String username, String password,
                                     List<NameValuePair> params) {

        String json = "";
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpGet = new HttpPost();

            URI uri = new URI(url);
            httpGet.setURI(uri);
//            httpGet.addHeader(BasicScheme.authenticate(
//                    new UsernamePasswordCredentials(username, password),
//                    HTTP.UTF_8, false));
            httpGet.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    json = stringBuffer.toString();
                    json = json.replace("\\\"", "\"");
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }

        return json;
    }

    public String postThreadRequest(String url, String username, String password,
                                    JSONObject params) {

        String json = "";
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {

            //////TIMEOUT SETTING//////////////////////
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 30000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 40000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            //////////////////////////////////////////

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpGet = new HttpPost();

            URI uri = new URI(url);
            httpGet.setURI(uri);
            StringEntity entity = new StringEntity(params.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            httpGet.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } catch (SocketTimeoutException e) {
            // TODO: handle exception
            e.printStackTrace();
            error = e.getMessage();
            Log.d("sangharsha", error);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            error = e.getMessage();
            Log.d("sangharsha", error);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    json = stringBuffer.toString();
                    json = json.replace("\\\"", "\"");
                } catch (IOException e) {
                    // TODO: handle exception
                    Log.d("sangharsha", error);
                }
            }
        }
        return json;
    }
}

