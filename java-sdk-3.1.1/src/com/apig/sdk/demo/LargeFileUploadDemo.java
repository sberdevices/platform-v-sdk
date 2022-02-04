//package name of the demo.
package com.apig.sdk.demo;

import com.cloud.apigateway.sdk.utils.Request;
import com.cloud.sdk.auth.vo.SignResult;
import com.cloud.sdk.util.BinaryUtils;
import com.cloud.sdk.util.SignUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Set;

public class LargeFileUploadDemo {
    private static final String HTTPS = "https";

    static String calcSha256Hex(String fileName) throws Exception {
        byte[] buffer = new byte[8192];
        int count;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
        while ((count = bis.read(buffer)) > 0) {
            digest.update(buffer, 0, count);
        }
        byte[] hash = digest.digest();
        return BinaryUtils.toHex(hash);
    }

    public static void main(String[] args) {
        String fname = "example.rar";
        // Create a new request.
        Request request = new Request();
        try {
            // Set the request parameters.
            // AppKey, AppSecrect, Method and Url are required parameters.
            request.setKey("apigateway_sdk_demo_key");
            request.setSecret("apigateway_sdk_demo_secret");
            request.setMethod("POST");
            request.setUrl("https://30030113-3657-4fb6-a7ef-90764239b038.apigw.exampleRegion.com/app1?name=value");
            request.addHeader("Content-Type", "plain/text");
            String fileHash = calcSha256Hex(fname);
            request.addHeader("x-sdk-content-sha256", fileHash);
            System.out.println("x-sdk-content-sha256:" + fileHash);
            // if it was published in other envs(except for Release),you need to add the
            // information x-stage and the value is env's name
            // request.addHeader("x-stage", "publish_env_name");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        URL uUrl = null;
        HttpURLConnection conn = null;
        OutputStream out = null;
        BufferedReader in = null;
        try {
            // Sign the request.
            SignResult signRet = SignUtils.sign(request);

            // initial connection
            uUrl = signRet.getUrl();
            if (uUrl.getProtocol().toLowerCase().equals(HTTPS)) {
                HttpsURLConnection httpsconn = (HttpsURLConnection) uUrl.openConnection();
                conn = httpsconn;
            } else {
                conn = (HttpURLConnection) uUrl.openConnection();
            }
            conn.setRequestMethod(request.getMethod().name());
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send the request.
            if (signRet.getHeaders().size() > 0) {
                Set<String> headerSet = signRet.getHeaders().keySet();
                for (String key : headerSet) {
                    String value = signRet.getHeaders().get(key);
                    conn.setRequestProperty(key, value);
                }
            }
            // Send a file.
            setFileConent(fname, conn.getOutputStream());

            // Print the status line of the response.
            InputStream is = null;
            if (conn.getResponseCode() > 400) {
                is = conn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }

            if (null != is) {
                StringBuilder result = new StringBuilder();
                in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                if (in != null) {
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                }
                System.out.println(result.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static void setFileConent(String filePath, OutputStream out) throws IOException, FileNotFoundException {
        File file = new File(filePath);
        int count = 0;
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        while ((count = bis.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        bis.close();
    }
}