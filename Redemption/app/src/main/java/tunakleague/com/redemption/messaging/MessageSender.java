package tunakleague.com.redemption.messaging;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.app_constants.PreferencesKeys;
import tunakleague.com.redemption.app_constants.ServerConstants;

public class MessageSender {
    private Context ctx;
    private String result;

    public MessageSender(Context ctx) {
        this.ctx = ctx;
    }

    public final String sendMessage(final String toUserName, final String messageToSend) {
        String url = ServerConstants.URLS.CHAT.string;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            result = jsonResponse.getString("recipient") + ": " + jsonResponse.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("SENDMESSAGE", "ERROR");
                        error.printStackTrace();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put(Config.RECIPIENT, toUserName);
                paramsMap.put(Config.MESSAGE_KEY, messageToSend);
                return paramsMap;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " +
                        PreferenceManager.getDefaultSharedPreferences(ctx).getString(PreferencesKeys.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };
        MyApplication.requestQueue.add(postRequest);
        return result;
	}
}