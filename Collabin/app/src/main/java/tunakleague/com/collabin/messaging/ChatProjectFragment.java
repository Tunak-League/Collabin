package tunakleague.com.collabin.messaging;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tunakleague.com.collabin.MyApplication;
import tunakleague.com.collabin.R;
import tunakleague.com.collabin.app_constants.PreferencesKeys;
import tunakleague.com.collabin.app_constants.ServerConstants;
import tunakleague.com.collabin.custom_views.ExpandableHeightGridView;
import tunakleague.com.collabin.profiles.ProjectUpdateFragment;

/**
 * Fragment displayed as a tab as a part of the ChatMainActivity.
 * Displays the project the two users are matched in
 */
public class ChatProjectFragment extends ProjectUpdateFragment {
    private View view;
    private int projectId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_project, container, false);

        /*Initialize the skillsField*/
        skillsField = (ExpandableHeightGridView) view.findViewById(R.id.skills);
        typesField = (ExpandableHeightGridView) view.findViewById(R.id.types);
        /*Initialize image data, and Add listener to imageview*/
        initializeImageData((ImageView) view.findViewById(R.id.project_image), ServerConstants.PROJECTS.PROJECT_IMAGE.string);

        fieldsToPopulate.put(view.findViewById(R.id.project_name), ServerConstants.PROJECTS.PROJECT_NAME.string);
        fieldsToPopulate.put(view.findViewById(R.id.project_summary), ServerConstants.PROJECTS.PROJECT_SUMMARY.string);

        projectId = getActivity().getIntent().getIntExtra("project", -1);
        String url = ServerConstants.URLS.PROJECT_DETAIL.string + Integer.toString(projectId) + "/";
        StringRequest jsonRequest = new StringRequest
                (url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Log.d("ChatProfileFragment", data.toString());
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


    /*
        Override to use ArrayAdapter,
    */
    @Override
    protected void configureListData(){
                /*Initialize the skills/types adapters and attach them to their respective GridViews*/
        ArrayAdapter skillsAdapter = null;
        ArrayAdapter typesAdapter = null;
        try {
            List<String> skillsList = fieldToList(profileData.getJSONArray(ServerConstants.USERS.SKILLS.string));
            List<String> typesList = fieldToList(profileData.getJSONArray(ServerConstants.USERS.TYPES.string));
            if (getActivity() != null) {
                skillsAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, skillsList);
                typesAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, typesList);
            }
        }
        catch(JSONException ex ) {
            Log.d("ProfileFrag: ", "Issue with getting JSONArray from skills/types" );
        }

        /*Set the adapters for each view, only if it is not null*/
        if (skillsField != null ) {
            skillsField.setAdapter(skillsAdapter);
            skillsField.setExpanded(true);

        }
        if( typesField != null ) {
            typesField.setAdapter(typesAdapter);
            typesField.setExpanded(true);
        }
    }

}
