package io.kevsoft.userrestclient;

import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kevin_000 on 03.04.2018.
 */

public class RESTExecutor {
    public static void ExecuteLoginRequest(String username, String password, final RESTSuccessCallback successCallback, final RESTFailedCallback failedCallback) {
        RequestParams params = new RequestParams();
        // Put Http parameter username with value of username
        params.put("username", username);
        // Put Http parameter password with value of password
        params.put("password", password);

        params.put("responseType", "json");

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(RESTConfig.URL_LOGIN, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    // JSON Object
                    JSONObject obj = new JSONObject(response);

                    int statusResponseCode = obj.getInt("code");
                    // When the JSON response has status boolean value assigned with true
                    if(statusResponseCode == 0) {
                        successCallback.onSuccess();
                    } else if(statusResponseCode == 1) {
                        failedCallback.onFailed("User does not exist!");
                    } else if(statusResponseCode == 2) {
                        failedCallback.onFailed("Invalid password, try again!");
                    } else { // Else display error message
                        failedCallback.onFailed("Error: Unknown error code");
                    }
                } catch (UnsupportedEncodingException e) {
                    failedCallback.onFailed("Error Occured [Server's JSON response is not properly UTF8 encoded]!");
                } catch (JSONException e) {
                    failedCallback.onFailed("Error Occured [Server's JSON response might be invalid]!");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    failedCallback.onFailed("Requested resource not found");
                } else if(statusCode == 500){
                    failedCallback.onFailed("Something went wrong at server end");
                } else{
                    failedCallback.onFailed("Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]");
                }
            }
        });
    }

    public static void ExecuteRegisterRequest(String username, String email, String password, final RESTSuccessCallback successCallback, final RESTFailedCallback failedCallback) {
        RequestParams params = new RequestParams();

        params.put("username", username);
        // Put Http parameter username with value of email
        params.put("email", email);
        // Put Http parameter password with value of password
        params.put("password", password);
        params.put("responseType", "json");

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(RESTConfig.URL_REGISTER, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    // JSON Object
                    JSONObject obj = new JSONObject(response);

                    int statusResponseCode = obj.getInt("code");
                    // When the JSON response has status boolean value assigned with true
                    if(statusResponseCode == 0){
                        successCallback.onSuccess();
                    } else if(statusResponseCode == 1) {
                        failedCallback.onFailed("User already exist!");
                    } else if(statusResponseCode == 2) {
                        failedCallback.onFailed("Failed to register, try again later!");
                    } else { // Else display error message
                        failedCallback.onFailed("Error: Unknown error code");
                    }
                } catch (UnsupportedEncodingException e) {
                    failedCallback.onFailed("Error Occured [Server's JSON response is not properly UTF8 encoded]!");
                    e.printStackTrace();
                } catch (JSONException e) {
                    failedCallback.onFailed("Error Occured [Server's JSON response might be invalid]!");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    failedCallback.onFailed("Requested resource not found");
                } else if(statusCode == 500){ // When Http response code is '500'
                    failedCallback.onFailed("Something went wrong at server end");
                } else { // When Http response code other than 404, 500
                    failedCallback.onFailed("Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]");
                }
            }
        });

    }
}
