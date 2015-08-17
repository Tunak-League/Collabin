package tunakleague.com.redemption.profiles;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;



import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tunakleague.com.redemption.Constants;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.ServerConstants.*;
import tunakleague.com.redemption.experimental.ExpandableHeightGridView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class ProfileFragment extends android.support.v4.app.Fragment {
    HideTabsListener mListener;

    JSONObject profileData; //all the fields in the profile retrieved from the app server

    /*Keys are all EditText fields for the profile that need to be populated upon opening the profile; values are the name of the parameter in the app server's database for HTTP params*/
    Map<View, String> fieldsToPopulate;

    /*These need to be separate from the views in "fields" since they have different behaviour/require extra handling*/
    protected ExpandableHeightGridView skillsField;
    protected ExpandableHeightGridView typesField;


    /*Keys are all EditText fields for the profile whose input values need to be extracted and sent to the server on update; values are the name of the parameter in the app server*/
    Map<View, String> fieldsToExtract;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fieldsToPopulate = new HashMap<View, String>();
        fieldsToExtract = new HashMap<View, String>();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HideTabsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement HideTabListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface HideTabsListener{
        /*Implemented by Activity if it uses a TabLayout with fragments and wants to hide the tabs for fragments implementing this class*/
        public void setTabsVisible(boolean visible);
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

        //TODO: Implement the GRIDVIEW stuff with the Add/Delete buttons for "Skills", and "Types"
        ProfileArrayAdapter skillsAdapter = null;
        ProfileArrayAdapter typesAdapter = null;

        /*Initialize the skills/types adapters and attach them to their respective GridViews*/
        try {
            List<String> skillsList = fieldToList(profileData.getJSONArray(USERS.SKILLS.string));
            List<String> typesList = fieldToList(profileData.getJSONArray(USERS.TYPES.string));
            skillsAdapter = new ProfileArrayAdapter(getActivity(), R.layout.profile_item,R.id.item_name, skillsList);
            typesAdapter = new ProfileArrayAdapter(getActivity(), R.layout.profile_item,R.id.item_name, typesList);
        }
        catch(JSONException ex ) {
            Log.d("ProfileFrag: ", "Issue with getting JSONArray from skills/types" );
        }
        skillsField.setExpanded(true);
        typesField.setExpanded(true);
        skillsField.setAdapter(skillsAdapter);
        typesField.setAdapter(typesAdapter);
    }

    /*
        Extracts the data from each of the views in fieldsToExtract, skillsField, and typesField and returns the data as a JSONObject
        @throws JSONException ex - if an error occurs while attempting to put items into the ProfileFragment
     */
    protected JSONObject extractFields() {
        //TODO: Implement this.
        /*Extract all fields except skills and types*/
        JSONObject data = new JSONObject();
        for (View view : fieldsToExtract.keySet()) {
            try {
                data.put(fieldsToExtract.get(view), ((EditText) view).getText().toString());
            } catch (JSONException ex) {
                Log.d("ProfileFrag:", "JSONError in extractFields() ");
            }
        }

        /*Extract the skills and types field using their adapters*/
        try {
            data.put(USERS.SKILLS.string, new JSONArray( ((ProfileArrayAdapter) skillsField.getAdapter()).getItems()) );
            data.put(USERS.TYPES.string, new JSONArray( ( (ProfileArrayAdapter) typesField.getAdapter()).getItems()) ) ;

        } catch (JSONException ex) {
            Log.d("ProfileFrag:", "JSONError in extractFields() - Skills and Types ");
        }
        return data;
    }

    /*
        Updates the app server with the newly entered profile information.Places a copy of the updated
        information in profileData
     */
    protected abstract void updateProfile();

    /*
    Adds a profile_item to the specified GridView
    @required: field must be registered with an adapter
     */
    protected void addItem(GridView field, String item) {
        ProfileArrayAdapter adapter = ((ProfileArrayAdapter) field.getAdapter());
        adapter.addItem(item);
    }

    /*
    Restarts the ProfileActivity and starts the ProjectList fragment. Used after creating or updating a project.
     */
    protected void reloadProjects() {
        Intent reloadIntent = new Intent(getActivity(), getActivity().getClass());
        reloadIntent.setAction(Constants.ACTION_PROJECT);
        startActivity(reloadIntent);
        getActivity().finish();
    }

    /*
    Converts one of the array fields in profileData from JSONObject to List<String>
    @requires: The name of an array-field must be provided such as "types" or "skills"
     */
    private List<String> fieldToList(JSONArray array) {
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

    class CreateSkillListener implements View.OnClickListener {
        /*
        Attached to an "Add Skills" button to create a dialog for users to enter a new skill to add to their profile.
         */
        @Override
        public void onClick(View v) {
            //TODO: Put in some CREATION dialog (for adding new skill)
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Add a skill");
            builder.setMessage("Enter a skill?");
            final EditText inputField = new EditText(getActivity());
            builder.setView(inputField);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    addItem(skillsField, inputField.getText().toString());
                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
    }

    class CreateTypeListener implements View.OnClickListener {
    /*
    Attached to an "Add Types" button to create a dialog with a selection of types for user to select from to add to their profile.
     */
        @Override
        public void onClick(View v) {
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

            final String[] types = {"Software", "Electrical", "Mechanical", "Business"};
            b.setItems(types, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int typeSelected) {
                            addItem(typesField, types[typeSelected]); //Add the selected type based on its position in the "types" array
                            dialog.dismiss();
                        }

                    }
            );
            b.show();

        }
    }

    class UpdateListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            updateProfile();
        }
        /*Attached to an "Update Profile" button. Calls the updateProfile method to be implemented by subclasses of this class*/

    }
}
