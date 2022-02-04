//package name of the demo.
package com.apig.sdk.demo;

import com.cloud.apigateway.sdk.utils.Client;
import com.cloud.apigateway.sdk.utils.Request;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketDemo {

    public static void main(String[] args) {
        //Create a new request.
        Request request = new Request();
        try {
            request.setKey("apigateway_sdk_demo_key");
            request.setSecret("apigateway_sdk_demo_secret");
            request.setMethod("GET");
            request.setUrl("https://30030113-3657-4fb6-a7ef-90764239b038.apigw.exampleRegion.com/ws?name=value");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            //Sign the request.
            okhttp3.Request signedRequest = Client.signOkhttp(request);

            //Send the request.
            OkHttpClient client = new OkHttpClient.Builder().build();
            WebSocketListener webSocketListener = new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    System.out.println("receive: " + text);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    System.out.println("fail   : " + t.getMessage());
                }
            };
            WebSocket webSocket = client.newWebSocket(signedRequest, webSocketListener);
            for (int i = 0; i < 10; i++) {
                String msg = "hello," + System.currentTimeMillis();
                System.out.println("send   : " + msg);
                webSocket.send(msg);
                Thread.sleep(50);
            }
            webSocket.close(1000, null);
            client.dispatcher().executorService().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
