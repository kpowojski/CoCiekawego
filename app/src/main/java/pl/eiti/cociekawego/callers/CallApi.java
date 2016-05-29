package pl.eiti.cociekawego.callers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by krystian on 2016-05-08.
 */
public class CallApi extends AsyncTask<String, Void, JSONObject> {

    //to handle resposne from API
    public AsyncResponse delegate = null;

    private String auth = "ute:ute2016$";
    private String authB64;

    private Context context;
    private ProgressDialog progressDialog;

    public CallApi(Context context){
        this.context = context;
    }


    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };


    @Override
    protected void onPreExecute(){
        progressDialog  = new ProgressDialog(this.context);
        progressDialog.setMessage("Pobieranie danych. Proszę czekać!");
        progressDialog.setCancelable(true);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        authB64 = Base64.encodeToString(auth.getBytes(), Base64.DEFAULT);
        Log.i("CoCiekawego CallApi", "onPreExecution");
    }

    @Override
    protected void onPostExecute(JSONObject result){
        if(progressDialog != null || progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        delegate.processFinish(result);
        Log.i("CoCiekawego CallApi", "onPostExecute");
    }


    @Override
    protected JSONObject doInBackground(String... params) {

        StringBuilder urlString = new StringBuilder();
        urlString.append(params[0]);
        if (params.length > 1){
            urlString.append(params[1]);
            urlString.append(",");
            urlString.append(params[2]);
            urlString.append(",");
            urlString.append(params[3]);
        }


        HttpsURLConnection httpsURLConnection = null;
        InputStream in = null;
        BufferedReader bufferedReader = null;
        JSONObject jsonObject = null;

        try{

            URL url = new URL(urlString.toString());
            Log.d("CoCiekawego CallApi", urlString.toString());
            trustAllHosts();
            httpsURLConnection = (HttpsURLConnection)url.openConnection();
            httpsURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setRequestProperty("Authorization", "Basic " + authB64);
            httpsURLConnection.setDoInput(true);


            httpsURLConnection.connect();



            in = httpsURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String tmp;
            StringBuilder responseBuilder = new StringBuilder();
            while ((tmp = bufferedReader.readLine()) != null){
                responseBuilder.append(tmp);

            }
            //
            //Log.d("CoCiekawego CallApi", "Pobrane dane");

            bufferedReader.close();
            in.close();
            httpsURLConnection.disconnect();
            jsonObject = new JSONObject(responseBuilder.toString());
        }catch (FileNotFoundException e){
            return null;
        }
        catch(Exception e){
            e.printStackTrace();
        }finally {
            httpsURLConnection.disconnect();
        }
        return jsonObject;
    }



    private static void trustAllHosts(){
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        TrustManager[] trustAllCerts = new TrustManager[] {trustManager};

        //install this manager
        try{
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }catch(NoSuchAlgorithmException exp){
            exp.printStackTrace();
        }catch(KeyManagementException exp){
            exp.printStackTrace();
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }
}
