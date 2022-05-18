package de.j3ramy.edom.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpHelper {

    public static final int ERROR_CODE = -1;

    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public static boolean executeQuery(String url, Map<String, String> parameters){
        try{
            URL connectionUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) connectionUrl.openConnection();

            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(HttpHelper.getParamsString(parameters));
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            int resultCode = Integer.parseInt(in.readLine());
            in.close();

            con.disconnect();

            return resultCode != ERROR_CODE;
        }
        catch (Exception e){
            return false;
        }
    }
}
