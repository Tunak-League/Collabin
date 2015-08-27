package tunakleague.com.redemption.search;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import tunakleague.com.redemption.DrawerActivity;
import tunakleague.com.redemption.profiles.BaseProfileFragment;

public abstract class SearchActivity extends DrawerActivity implements View.OnTouchListener {
    public final String TAG = "SearchActivity";

    public final String PROFILE_TAG = "Profile Fragment Tag"; //tag used to retrieve the fragment used for displaying profiles
    JSONArray profileList; //List of profiles to be displayed to the user.
    int position; //Keeps track of which profile you are currently displaying to the user
    BaseProfileFragment profileDisplay; //fragment to display a single profile on the screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = 0;


    }

    /*Placeholder for the actual left/right swipe listener. All this does is load the next profile to the screen on every touch*/
    @Override
    public boolean onTouch( View v, MotionEvent event ){
        //TODO: Call sendSwipe here after implementing it in the child class.
        Log.d(TAG, "Touched it");
        loadNextProfile();
        return true;
    }


    /*
        Retrieves the list of profiles from the server, creates the ProfileUpdateFragment, and displays the first profile to the user.
        @required: When overriding and instantiating fragments, you MUST use this class' PROFILE_TAG constant as the fragment's tag.
     */
    protected abstract void initializeData();

    /*
        Sends a swipe request to the server to indicate if the swiping user likes the specified profile
        @param profile - A JSONObject representing the profile that the user is swiping on.
        @param answer - boolean, true if the user likes the profile, false if they do not.
     */
    protected abstract void sendSwipe( JsonObject profile, boolean answer );

    /*
        Increments "position" and displays the next profile from "profileList" to the screen
     */
    private void loadNextProfile() {
        position++;
        /*Get the profile fragment  */
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        BaseProfileFragment profileFragment = (BaseProfileFragment) manager.findFragmentByTag(PROFILE_TAG);


        /*call fragment's renderUI() method to update the display with the next profile's data, only if there are profiles left*/
        if( position < profileList.length() ) {
            try {
                profileFragment.renderUI(profileList.getJSONObject(position));
            } catch (JSONException e) {
                Log.d(TAG, "Error passing next profile to renderUI() ");
            }
        }
        else{
            //TODO: Proper screen telling you you're done searching
            Log.d(TAG, "No more profiles left to show" );
        }
    }

}
