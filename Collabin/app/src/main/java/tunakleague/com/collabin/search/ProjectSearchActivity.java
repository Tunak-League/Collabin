package tunakleague.com.collabin.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.collabin.app_constants.Constants;
import tunakleague.com.collabin.DetailedErrorListener;
import tunakleague.com.collabin.MyApplication;
import tunakleague.com.collabin.R;
import tunakleague.com.collabin.app_constants.ServerConstants.*;
import tunakleague.com.collabin.profiles.BaseProfileFragment;

public class ProjectSearchActivity extends SearchActivity {
    View contentView;
    int projectID; //id of the project you're recruiting for

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Collabin");
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.activity_project_search, null, false);
        //contentView.setId(R.id.user_search);
        mDrawerLayout.addView(contentView, 0);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //Stop opening of drawer by swiping to prevent conflict with Swipes in this activity
//        setContentView(R.layout.activity_user_search);
        projectID = getIntent().getIntExtra(Constants.PROJECT_ID, 0);
        initializeData();
//        findViewById(R.id.user_search).setOnTouchListener(this); //Set the UI to respond to touch events using SearchActivity's touch listener
        contentView.setOnTouchListener(this);


    }

    @Override
    protected void initializeData() {
        final ProgressDialog progress = new ProgressDialog(this);

        /*Send request to get list of users that this project might be interested in*/
        String url = URLS.PROJECT_SEARCH.string + projectID + "/";
        JsonArrayRequest projectSearchRequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        profileList = response;

                        /*Create the profile fragment and display the first profile on it*/
                        if (profileList.length() > 0) {
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            BaseProfileFragment profileFragment = ProjectSearchFragment.newInstance();
                            transaction.add( R.id.project_search, profileFragment , PROFILE_TAG );
                            transaction.commit();
                            manager.executePendingTransactions();

                            /*Render the UI with the first profile from profileList*/
                            try {
                                profileFragment.renderUI( profileList.getJSONObject(position) );
                            } catch (JSONException e) {
                                Log.d(TAG, "Error rendering FIRST profile" );
                            }
                        }
                        else {
                            showNoProfilesStart();
                        }
                        progress.dismiss();
                    }
                }
                ,
                new DetailedErrorListener(this) //TODO: Need to override and make something to STOP/EXIT the Fragment if request fails (or else you operate on null data)
        )
        {
            @Override
            //Add header of request
            public Map<String, String> getHeaders() {
                return MyApplication.getAuthenticationHeader(ProjectSearchActivity.this);
            }
        };
        MyApplication.requestQueue.add(projectSearchRequest);

        /*Show loading indicator*/
        progress.setMessage("Finding collaborators");
        progress.setIndeterminate(true);
        progress.show();
    }

    @Override
    protected void sendSwipe(JSONObject profile,final String answer) {
        //TODO: Implement the actual sending of the swipe request

        /*Get the ID of the user being swiped on*/
        int id = 0;
        try{
            id = profile.getInt(PROJECTS.PK.string);
        }
        catch( JSONException ex ){
            Log.d(TAG, "Error extracting profile ID in sendSwipe" );
        }
        final int userID = id;

        /*Make the swipe request*/
        String url = URLS.PROJECT_SWIPE.string + projectID + "/" + userID + "/";
        StringRequest projectSwipeRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d( TAG, "Swipe sent: " + response );
                    }
                },
                new DetailedErrorListener(this)
        )
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(SWIPES.USER_PROFILE.string, String.valueOf(userID));
                params.put( SWIPES.PROJECT_LIKES.string, answer );
                return params;
            }

            @Override
            //Add header of request
            public Map<String, String> getHeaders() {
                return MyApplication.getAuthenticationHeader(ProjectSearchActivity.this);
            }
        };
        MyApplication.requestQueue.add(projectSwipeRequest);

    }


}
