//package name of the demo.
package com.apig.sdk.demo;

//Import the external dependencies.

import com.cloud.apigateway.sdk.utils.Client;
import com.cloud.apigateway.sdk.utils.Request;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        //Create a new request.
        Request request = new Request();
        try {
            //Set the request parameters.
            //AppKey, AppSecrect, Method and Url are required parameters.
            request.setKey("apigateway_sdk_demo_key");
            request.setSecret("apigateway_sdk_demo_secret");
            request.setMethod("GET");
            request.setUrl("https://30030113-3657-4fb6-a7ef-90764239b038.apigw.exampleRegion.com/app1?a=1");
            request.addHeader("Content-Type", "text/plain");
            //if it was published in other envs(except for Release),you need to add the information x-stage and the value is env's name
            //request.addHeader("x-stage", "publish_env_name");
            request.setBody("demo");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        CloseableHttpClient client = null;
        try {
            //Sign the request.
            HttpRequestBase signedRequest = Client.sign(request);

            //Send the request.
            client = HttpClients.custom().build();
            HttpResponse response = client.execute(signedRequest);

            //Print the status line of the response.
            System.out.println(response.getStatusLine().toString());

            //Print the header fields of the response.
            Header[] resHeaders = response.getAllHeaders();
            for (Header h : resHeaders) {
                System.out.println(h.getName() + ":" + h.getValue());
            }

            //Print the body of the response.
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                System.out.println(System.getProperty("line.separator") + EntityUtils.toString(resEntity, "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}