package tunakleague.com.redemption.messaging;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.HomeActivity;
import tunakleague.com.redemption.MyApplication;

import tunakleague.com.redemption.app_constants.PreferencesKeys;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.ServerConstants;

public class ChatMainActivity extends AppCompatActivity {
    private int toUser, project;
    private String username, first_name, last_name, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toUser = getIntent().getIntExtra("recipient", -1);
        project = getIntent().getIntExtra("project", -1);
        String url = ServerConstants.URLS.USER_GET.string + Integer.toString(toUser) + "/";
        StringRequest jsonRequest = new StringRequest
                (url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            username = data.getString("username");
                            first_name = data.getString("first_name");
                            last_name = data.getString("last_name");
                            email = data.getString("email");
                            ActionBar actionBar = getSupportActionBar();
                            actionBar.setTitle(username + " (" + first_name + " " + last_name + ")");
                            actionBar.setSubtitle(email);
                        } catch (JSONException e) {
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
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PreferencesKeys.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };
        MyApplication.requestQueue.add(jsonRequest);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.chat_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Chat"));
        tabLayout.addTab(tabLayout.newTab().setText("User Profile"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.chat_pager);
        final ChatPagerAdapter adapter = new ChatPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_unmatch):
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Unmatch Action")
                        .setMessage("Are you sure you want to unmatch?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                unmatch();
                                Toast.makeText(ChatMainActivity.this, "Unmatched.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ChatMainActivity.this, HomeActivity.class));
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void unmatch() {
        if (getIntent().getStringExtra("match_type").equals("UserMatch")) {
            userUnmatch();
        }
        else {
            projectUnmatch();
        }
    }

    public void userUnmatch() {
        String url = ServerConstants.URLS.USER_SWIPE.string + Integer.toString(project) + "/";
        StringRequest jsonRequest = new StringRequest
                (Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                        } catch (JSONException e) {
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
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_likes", "NO");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " +
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PreferencesKeys.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };
        MyApplication.requestQueue.add(jsonRequest);
    }

    public void projectUnmatch() {
        String url = ServerConstants.URLS.PROJECT_SWIPE.string + Integer.toString(project) + "/" + Integer.toString(toUser) + "/";
        StringRequest jsonRequest = new StringRequest
                (Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                        } catch (JSONException e) {
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
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project_likes", "NO");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " +
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PreferencesKeys.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };
        MyApplication.requestQueue.add(jsonRequest);
    }
}
