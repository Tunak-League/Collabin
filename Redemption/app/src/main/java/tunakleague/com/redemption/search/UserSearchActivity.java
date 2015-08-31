package tunakleague.com.redemption.search;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
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

import tunakleague.com.redemption.DetailedErrorListener;
import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.ServerConstants.*;
import tunakleague.com.redemption.profiles.BaseProfileFragment;

public class UserSearchActivity extends SearchActivity {
    View contentView;
    LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.activity_user_search, null, false);
        //contentView.setId(R.id.user_search);
        mDrawerLayout.addView(contentView, 0);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //Stop opening of drawer by swiping to prevent conflict with Swipes in this activity
//        setContentView(R.layout.activity_user_search);

        initializeData();
//        findViewById(R.id.user_search).setOnTouchListener(this); //Set the UI to respond to touch events using SearchActivity's touch listener
        contentView.setOnTouchListener(this);


    }

    @Override
    protected void initializeData() {
        /*Send request to get list of projects that this user might be interested in*/
        String url = URLS.USER_SEARCH.string;
        JsonArrayRequest userSearchRequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        profileList = response;

                        /*Create the profile fragment and display the first profile on it*/
                        if( profileList.length() > 0) {
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            BaseProfileFragment profileFragment = UserSearchFragment.newInstance();
                            transaction.add( R.id.user_search, profileFragment , PROFILE_TAG );
                            transaction.commit();
                            manager.executePendingTransactions();

                            /*Render the UI with the first profile from profileList*/
                            try {
                                profileFragment.renderUI( profileList.getJSONObject(position) );
                            } catch (JSONException e) {
                                Log.d(TAG, "Error rendering FIRST profile" );
                            }
                        }
                        else{ //TODO: Properly handle no matches with some screen that says "no matches"
                            Log.d( TAG, "No profiles found" );
                            showNoProfilesStart();
                        }
                    }
                }
                ,
                new DetailedErrorListener(this) //TODO: Need to override and make something to STOP/EXIT the Fragment if request fails (or else you operate on null data)
        )
        {
            @Override
            //Add header of request
            public Map<String, String> getHeaders() {
                return MyApplication.getAuthenticationHeader(UserSearchActivity.this);
            }
        };
        MyApplication.requestQueue.add(userSearchRequest);
    }

    @Override
    protected void sendSwipe(JSONObject profile,final String answer) {
        //TODO: Implement the actual sending of the swipe request

        /*Get the ID of the project being swiped on*/
        int id = 0;
        try{
            id = profile.getInt(PROJECTS.PK.string);
        }
        catch( JSONException ex ){
            Log.d(TAG, "Error extracting profile ID in sendSwipe" );
        }
        final int projectID = id;

        /*Make the swipe request*/
        String url = URLS.USER_SWIPE.string + projectID + "/";
        StringRequest userSwipeRequest = new StringRequest(Request.Method.PUT, url,
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
                params.put(SWIPES.PROJECT.string, String.valueOf(projectID));
                params.put( SWIPES.USER_LIKES.string, answer );
                return params;
            }

            @Override
            //Add header of request
            public Map<String, String> getHeaders() {
                return MyApplication.getAuthenticationHeader(UserSearchActivity.this);
            }
        };
        MyApplication.requestQueue.add(userSwipeRequest);

    }


}
