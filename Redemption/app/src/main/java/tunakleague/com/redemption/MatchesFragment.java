package tunakleague.com.redemption;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.app_constants.PreferencesKeys;
import tunakleague.com.redemption.app_constants.ServerConstants;
import tunakleague.com.redemption.messaging.ChatMainActivity;

public class MatchesFragment extends Fragment {
    private View view;
    private ListView listView;
    public static final String TAG = "MatchesFragment";
    ArrayList<ChatRowData> chatRow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_matches, container, false);

        listView = (ListView) view.findViewById(R.id.chat_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatMainActivity.class);
                intent.putExtra("recipient", chatRow.get(position).getOwnerId());
                intent.putExtra("sender", chatRow.get(position).getUserProfileId());
                intent.putExtra("project", chatRow.get(position).getProjectId());
                intent.putExtra("match_type", "UserMatch");
                startActivity(intent);
            }
        });

        final String URL = ServerConstants.URLS.USER_MATCHES.string;
        JsonArrayRequest userMatchRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        chatRow = new ArrayList<>();
                        String[] chatRows;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject data = response.getJSONObject(i);
                                String ownerName = data.getString("owner");
                                int owner = data.getInt("ownerId");
                                String projectName = data.getString("project_name");
                                int profile = data.getInt("userProfileId");
                                int project = data.getInt("projectId");
                                chatRow.add(new ChatRowData(ownerName, owner, projectName, profile, project));
                            }

                            chatRows = new String[chatRow.size()];
                            for (int i = 0; i < chatRow.size(); i++) {
                                chatRows[i] = chatRow.get(i).toString();
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_list_item_1, chatRows);
                            listView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("MatchesFragment", "Failed to display user matches");
                        }
                    }
                )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " +
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(PreferencesKeys.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };

        MyApplication.requestQueue.add(userMatchRequest);

        return view;
    }

}
