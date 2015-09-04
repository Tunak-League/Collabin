package tunakleague.com.redemption.messaging;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.PreferencesKeys;
import tunakleague.com.redemption.app_constants.ServerConstants;
import tunakleague.com.redemption.experimental.ExpandableHeightGridView;
import tunakleague.com.redemption.profiles.BaseProfileFragment;

public class ChatProfileFragment extends BaseProfileFragment {
    private View view;
    private int profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_profile, container, false);

        /*Initialize the skillsField*/
        skillsField = (ExpandableHeightGridView) view.findViewById(R.id.skills );
        typesField = (ExpandableHeightGridView) view.findViewById(R.id.types);
        /*Initialize image data, and Add listener to imageview*/
        initializeImageData((ImageView) view.findViewById(R.id.user_image), ServerConstants.USERS.USER_IMAGE.string);

        fieldsToPopulate.put(view.findViewById(R.id.username), ServerConstants.USERS.USERNAME.string);
        fieldsToPopulate.put(view.findViewById(R.id.email), ServerConstants.USERS.EMAIL.string);
        fieldsToPopulate.put(view.findViewById(R.id.user_summary), ServerConstants.USERS.USER_SUMMARY.string );
        fieldsToPopulate.put(view.findViewById(R.id.location), ServerConstants.USERS.LOCATION.string );

        profileId = getActivity().getIntent().getIntExtra("recipient", -1);
        String url = ServerConstants.URLS.USER_GET.string + Integer.toString(profileId) + "/";
        StringRequest jsonRequest = new StringRequest
                (url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            renderUI(data);
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
