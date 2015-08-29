package tunakleague.com.redemption.search;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import tunakleague.com.redemption.DrawerActivity;
import tunakleague.com.redemption.profiles.BaseProfileFragment;

public abstract class SearchActivity extends DrawerActivity {
    public final String TAG = "SearchActivity";
    private GestureDetector gestureDetector;

    public final String PROFILE_TAG = "Profile Fragment Tag"; //tag used to retrieve the fragment used for displaying profiles
    JSONArray profileList; //List of profiles to be displayed to the user.
    int position; //Keeps track of which profile you are currently displaying to the user
    BaseProfileFragment profileDisplay; //fragment to display a single profile on the screen

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(getApplicationContext(), new SwipeGestureDetector());
        position = 0;
    }

    /*Placeholder for the actual left/right swipe listener. All this does is load the next profile to the screen on every touch*/


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onLeftSwipe() {
        Toast.makeText(getApplicationContext(), "RECRUIT!", Toast.LENGTH_SHORT).show();
    }

    private void onRightSwipe() {
        Toast.makeText(getApplicationContext(), "NEXT ONE!", Toast.LENGTH_SHORT).show();
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

    // Private class for gestures
    public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        // Swipe properties, you can change it to make the swipe
        // longer or shorter and speed
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffAbs = Math.abs(e1.getY() - e2.getY());
                float diff = e1.getX() - e2.getX();

                if (diffAbs > SWIPE_MAX_OFF_PATH)
                    return false;

                // Left swipe
                if (diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    SearchActivity.this.onLeftSwipe();

                    // Right swipe
                }
                //
                else if (-diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    SearchActivity.this.onRightSwipe();
                }
            } catch (Exception e) {
                Log.e("SearchActivity", "Error on gestures");
            }
            return false;
        }
    }

}
