package tunakleague.com.redemption.profiles;

import android.content.DialogInterface;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    JSONObject profileData; //all the fields in the profile retrieved from the app server

    /*Keys are all EditText fields for the profile that need to be populated upon opening the profile; values are the name of the parameter in the app server's database for HTTP params*/
    Map<View, String> fieldsToPopulate;

    /*These need to be separate from the views in "fields" since they have different behaviour/require extra handling*/
    protected ExpandableHeightGridView skillsField;
    protected ExpandableHeightGridView typesField;


    /*Keys are all EditText fields for the profile whose input values need to be extracted and sent to the server on update; values are the name of the parameter in the app server*/
    Map<View, String> fieldsToExtract;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
/*    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fieldsToPopulate = new HashMap<View, String>();
        fieldsToExtract = new HashMap<View, String>();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
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
            skillsAdapter = new ProfileArrayAdapter(getActivity(), R.layout.profile_item,R.id.item_name, fieldToList(profileData.getJSONArray(USERS.SKILLS.string)));
            typesAdapter = new ProfileArrayAdapter(getActivity(), R.layout.profile_item,R.id.item_name, fieldToList(profileData.getJSONArray(USERS.TYPES.string)));
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
    Remove the item from the GridView
     */
/*    public void removeItem(View view ) {
        int position = skillsField.getPositionForView( (View) view.getParent() );//Get the position of the clicked Item in the adapter's dataset

        *//*Item is from *//*
        if( position != AdapterView.INVALID_POSITION ) {
            skillsList.remove(position);
            ((ArrayAdapter<String>) skillsField.getAdapter()).notifyDataSetChanged();
        }
        else {
            position = typesField.getPositionForView( (View) view.getParent() );
            typesList.remove(position);
            ((ArrayAdapter<String>) typesField.getAdapter()).notifyDataSetChanged();
        }

    }*/

    /*
    Adds a profile_item to the specified GridView
    @required: field must be registered with an adapter
     */
    protected void addItem(GridView field, String item) {
        ProfileArrayAdapter adapter = ((ProfileArrayAdapter) field.getAdapter());
        adapter.addItem(item);
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
