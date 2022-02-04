//package name of the demo.
package com.apig.sdk.demo;
import com.cloud.apigateway.sdk.utils.Client;
import com.cloud.apigateway.sdk.utils.Request;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
public class OkHttpDemo {

    public static void main(String[] args) {
        //Create a new request.
        Request request = new Request();
        try {
            request.setKey("apigateway_sdk_demo_key");
            request.setSecret("apigateway_sdk_demo_secret");
            request.setMethod("GET");
            request.setUrl("https://30030113-3657-4fb6-a7ef-90764239b038.apigw.exampleRegion.com/app1?name=value");
            request.addHeader("Content-Type", "text/plain");
            request.setBody("demo");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            //Sign the request.
            okhttp3.Request signedRequest = Client.signOkhttp(request);

            //Send the request.
            OkHttpClient client = new OkHttpClient.Builder().build();
            Response response = client.newCall(signedRequest).execute();

            //Print the status line of the response.
            System.out.println("status:" + response.code());

            //Print the header fields of the response.
            Headers resHeaders = response.headers();
            for (String h : resHeaders.names()) {
                System.out.println(h + ":" + resHeaders.get(h));
            }

            //Print the body of the response.
            ResponseBody resEntity = response.body();
            System.out.println("\n" + resEntity.string());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
