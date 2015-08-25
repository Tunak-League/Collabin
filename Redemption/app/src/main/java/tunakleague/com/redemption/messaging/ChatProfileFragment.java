package tunakleague.com.redemption.messaging;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.PreferencesKeys;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.ServerConstants;

public class ChatProfileFragment extends Fragment  {
    private View view;
    private int profileId;
    private TextView userName, email, location, userSummary;
    private TextView preferredTypes, skillsGrid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_profile, container, false);
        userName = (TextView) view.findViewById(R.id.chat_username);
        email = (TextView) view.findViewById(R.id.chat_email);
        location = (TextView) view.findViewById(R.id.chat_location);
        userSummary = (TextView) view.findViewById(R.id.chat_user_summary);

        preferredTypes = (TextView) view.findViewById(R.id.chat_types);
        skillsGrid = (TextView) view.findViewById(R.id.skills);

        profileId = getActivity().getIntent().getIntExtra("recipient", -1);
        String url = ServerConstants.URLS.USER_GET.string + Integer.toString(profileId) + "/";
        StringRequest jsonRequest = new StringRequest
                (url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            userName.setText(data.getString("username"));
                            email.setText(data.getString("email"));
                            location.setText(data.getString("location"));
                            userSummary.setText(data.getString("user_summary"));
                        }

                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " +
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(PreferencesKeys.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };
        MyApplication.requestQueue.add(jsonRequest);
        return view;
    }
}
