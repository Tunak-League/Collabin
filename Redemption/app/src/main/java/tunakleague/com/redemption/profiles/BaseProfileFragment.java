package tunakleague.com.redemption.profiles;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;


import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tunakleague.com.redemption.app_constants.ServerConstants.*;
import tunakleague.com.redemption.experimental.ExpandableHeightGridView;

/**
 * Base class for displaying profile information in a fragment using
 */
public abstract class BaseProfileFragment extends android.support.v4.app.Fragment {
    public final String TAG = "BaseProfileFragment";
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
                ((EditText) view).setText(profileData.getString(fieldsToPopulate.get(view)));
            } catch (JSONException ex) {

            }
        }

        /*Set up adapters for list data (skills, types) and populate their fields.*/
        configureListData();
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
        Type listType = new TypeToken<List<String>>() {
        }.getType();
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


}
