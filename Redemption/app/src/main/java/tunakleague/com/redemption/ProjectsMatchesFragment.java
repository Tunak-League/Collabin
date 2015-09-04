package tunakleague.com.redemption;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Map;

import tunakleague.com.redemption.app_constants.ServerConstants;
import tunakleague.com.redemption.messaging.ChatMainActivity;


// Returns all of project's user matches
public class ProjectsMatchesFragment extends Fragment {
    private View view;
    private ListView listView;
    public static final String TAG = "ProjectsMatchesFragment";
    ArrayList<ProjectChatRow> chatRow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_projects_matches, container, false);

        final String URL = ServerConstants.URLS.PROJECTS_MATCHES.string;
        listView = (ListView) view.findViewById(R.id.project_chat_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatMainActivity.class);
                intent.putExtra("recipient", chatRow.get(position).getProfileId());
                intent.putExtra("sender", chatRow.get(position).getOwnerId());
                intent.putExtra("project", chatRow.get(position).getProjectId());
                intent.putExtra("match_type", "ProjectMatch");
                startActivity(intent);
            }
        });

        JsonArrayRequest projectMatchRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                chatRow = new ArrayList<>();
                String[] chatRows;
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject data = response.getJSONObject(i);
                        String username = data.getString("username");
                        int profile = data.getInt("user_profile");
                        String projectName = data.getString("project_name");
                        int ownerProfile = data.getInt("ownerId");
                        int project = data.getInt("projectId");
                        chatRow.add(new ProjectChatRow(username, profile, projectName, ownerProfile, project));
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
                        Log.d(TAG, "Failed to display user matches");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return MyApplication.getAuthenticationHeader(getActivity());
            }
        };

        MyApplication.requestQueue.add(projectMatchRequest);

        return view;
    }
}
