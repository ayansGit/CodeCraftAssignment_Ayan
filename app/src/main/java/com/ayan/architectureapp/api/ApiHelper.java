package com.ayan.architectureapp.api;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {

    private static ApiHelper instance;
    public ApiHelper() {
    }

    public static synchronized ApiHelper getInstance(){
        if(instance == null){
            instance = new ApiHelper();
        }
        return instance;
    }

    public void get(String url, ApiListener apiListener){
        new ApiRequest(apiListener).execute(url);
    }

    private static class ApiRequest extends AsyncTask<String, Void, String>{

        ApiListener apiListener;
        private static boolean isError = false;

        public ApiRequest(ApiListener apiListener) {
            this.apiListener = apiListener;
            isError  = false;
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                result = getRequest(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
                isError = true;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if (isError){
                apiListener.onFailure(s);
            }else {
                apiListener.onSuccess(s);
            }

        }
    }


     private static String getRequest(String serverUrl) throws IOException {

        StringBuilder sb = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) (new URL(serverUrl)).openConnection();
        con.connect();
        int resCode = con.getResponseCode();
        InputStream in;
        if (resCode == HttpURLConnection.HTTP_OK) {
            in = con.getInputStream();
        } else {
            in = con.getErrorStream();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        con.disconnect();

        return sb.toString();
    }

}
