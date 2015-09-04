package tunakleague.com.redemption.profiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.Constants;
import tunakleague.com.redemption.app_constants.ServerConstants.*;
import tunakleague.com.redemption.experimental.ExpandableHeightGridView;

/**
 * Base class for displaying profile information in a fragment using
 */
public abstract class BaseProfileFragment extends android.support.v4.app.Fragment {
    public final String TAG = "BaseProfileFragment";
    public static int PICK_IMAGE_REQUEST = 4;

    /*Members for handling images*/
    protected Bitmap imageBitmap; //bitmap of the image user selects from gallery.
    protected ImageView image = null;
    protected String imageFieldName = null; //The name of the image field when retrieving from the server. (Different for users and projects)
    private boolean redownload = false;


    JSONObject profileData; //all the fields in the profile retrieved from the app server

    /*Keys are all EditText fields for the profile that need to be populated upon opening the profile; values are the name of the parameter in the app server's database for HTTP params*/
    protected Map<View, String> fieldsToPopulate;

    /*These need to be separate from the views in "fields" since they have different behaviour/require extra handling*/
    protected ExpandableHeightGridView skillsField;
    protected ExpandableHeightGridView typesField;




    public BaseProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fieldsToPopulate = new HashMap<View, String>();
    }

    @Override
    public void onStop(){
        super.onStop();


        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);

        boolean screenOn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            screenOn = pm.isInteractive();
        } else {
            screenOn = pm.isScreenOn();
        }

        if( screenOn && imageBitmap != null ) {
            Log.d(TAG, "recycling");
            image.setImageBitmap(null);
            imageBitmap.recycle();
            imageBitmap = null;
            System.gc();
            redownload = true;
        }
        else{
            redownload = false;
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "activity started");
        /*Re-download and display the image again, if activity was stopped*/
        if( profileData != null && imageBitmap == null && redownload) {
            Log.d(TAG, "re-downloading image");
            downloadImage();
        }
    }

    /*
        Takes the given profile data and renders it to the UI.
        @param profile - JSONObject containing all the data fields for a profile
     */
    public void renderUI( JSONObject profile ){
        profileData = profile;
        Log.d(TAG, "Rendering: " + profileData.toString());
        populateFields();
    }


    /*
        Uses the fields from profileData to populate each of the views in fieldsToPopulate, skillsField
        and typesField
     */
    protected void populateFields() {
        /*Set text in each view in fieldsToPopulate to the corresponding value of the field in profileData*/
        for (View view : fieldsToPopulate.keySet()) {
            try {
                String fieldValue = profileData.getString(fieldsToPopulate.get(view) );

                /*Only set the EditText field if the value isn't null*/
                if(! fieldValue.equals(Constants.NULL_STRING) )
                    ((EditText) view).setText(profileData.getString(fieldsToPopulate.get(view)));
            } catch (JSONException ex) {

            }
        }

        /*Set up adapters for list data (skills, types) and populate their fields.*/
        configureListData();

        /*Handle image downloading*/
      downloadImage();
    }

/*Gets the profile's image from AWS if the image field url is not null and displays it */
    protected void downloadImage(){
        try {
            String imageURL = profileData.getString( imageFieldName );
            Log.d( TAG, "Image URL: " + imageURL );
            /*If the returned image field isn't null, request the image from AWS and set it to this profile's ImageView*/
            if( ! imageURL.equals(Constants.NULL_STRING)){
                imageURL = imageURL.replace("\\", ""); //Clean the URL by removing any added backslashes from the server

                ImageRequest request = new ImageRequest(imageURL,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                imageBitmap = bitmap;
                                image.setImageBitmap(imageBitmap);
                                Log.d( TAG, "I JUST SET THE IMAGE" );
                            }
                        }, 0, 0, null, null,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                Log.d( TAG, "Profile has no image" );
                            }
                        });
                MyApplication.requestQueue.add( request );
            }
        } catch (JSONException e) {
            Log.d( TAG, "JSON Error during image downloading");
            e.printStackTrace();
        }
    }

    /*
        Configures any data that must be displayed in a list (skills, types) by setting their adapters and attaching them
        to the views containing the list data. To use different adapters from the default (ArrayAdapter), override this function.
     */
    protected void configureListData(){
                /*Initialize the skills/types adapters and attach them to their respective GridViews*/
        ArrayAdapter skillsAdapter = null;
        ArrayAdapter typesAdapter = null;
        try {
            List<String> skillsList = fieldToList(profileData.getJSONArray(USERS.SKILLS.string));
            List<String> typesList = fieldToList(profileData.getJSONArray(USERS.TYPES.string));
            skillsAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, skillsList);
            typesAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, typesList);
        }
        catch(JSONException ex ) {
            Log.d("ProfileFrag: ", "Issue with getting JSONArray from skills/types" );
        }

        /*Set the adapters for each view, only if it is not null*/
        if( skillsField != null ) {
            skillsField.setAdapter(skillsAdapter);
            skillsField.setExpanded(true);

        }
        if( typesField != null ) {
            typesField.setAdapter(typesAdapter);
            typesField.setExpanded(true);
        }
    }


    /*
    Converts a JSONArray to a list of strings.Used for extracting the "skills" and "types" fields from profileData.
     */
    protected List<String> fieldToList(JSONArray array) {
        List<String> list = new ArrayList<String>();

        for( int i = 0; i < array.length(); i++ ) {
            try {
                list.add(array.getString(i));
            }
            catch(JSONException ex ) {
                Log.d("ProfileFrag: ", "Error in fieldToList" );
            }
        }

        return list;
    }

    /*
    Opens the gallery for users to selct a profile picture
     */
    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                /*A precaution?*/
                if( imageBitmap != null ){
                    Log.d( TAG, "recycled a stray one" );
                    imageBitmap.recycle();
                }

                imageBitmap = BitmapFactory.decodeFile(imgDecodableString); //convert image string to image bitmap for use in extending classes


            } else {
                Toast.makeText(getActivity(), "You haven't picked an Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    /*Initialize the image data that is specific to User or Project profiles, namely the ImageView and the imageFieldName*/
    protected void initializeImageData( ImageView image, String imageName ){
        this.image = image;
        this.imageFieldName = imageName;
    }

}
