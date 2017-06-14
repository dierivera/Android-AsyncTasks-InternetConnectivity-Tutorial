package com.dierivera.ubp.unidaduno;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Button btnDownload;
    private TextView tvContentDownloaded;
    private EditText etShowContent;
    private EditText etSiteURL;
    private Context mContext;

    private String TAG = "btn_click";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        etSiteURL = (EditText) findViewById(R.id.etSiteURL);
        etSiteURL.setText("https://www.google.com");
        tvContentDownloaded = (TextView) findViewById(R.id.tvContentDownloaded);
        etShowContent = (EditText) findViewById(R.id.etContentDownloaded);
        btnDownload = (Button) findViewById(R.id.btn_download);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "buttonclicked");
                String url = etSiteURL.getText().toString();
                if (hasInternetAccess()) {
                    downloadContent(url);
                }else{
                    Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean hasInternetAccess() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void downloadContent(String url){
        DownloadTask task = new DownloadTask();
        task.execute(url);
    }

    public String readStream(InputStream stream) {
        //BufferedReader buffer = new BufferedReader(stream);
        Reader reader = new InputStreamReader(stream);
        BufferedReader buffer = new BufferedReader(reader);

        String response = "";
        String s = "";
        try {
            while ((s = buffer.readLine()) != null) {
                response += s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            etShowContent.setText("");
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Descargando");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
            //Se deja cancelable por si se tiene que cancelar para comprobar que la tarea se ejecuta en el background.
        }

        @Override
        protected String doInBackground(String... url) {
            String response = "";
            try {
                URL urlToFetch = new URL(url[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urlToFetch.openConnection();
                //urlConnection.setRequestMethod("GET");
                //urlConnection.setDoOutput(true);
                //urlConnection.connect();
                response = readStream(urlConnection.getInputStream());
                urlConnection.disconnect();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                response = e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = e.toString();
            }
            return response;
        }


        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            tvContentDownloaded.setVisibility(View.VISIBLE);
            etShowContent.setVisibility(View.VISIBLE);
            etShowContent.setText(result);

        }
    }
}