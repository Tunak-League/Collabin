package tunakleague.com.redemption.profiles;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tunakleague.com.redemption.app_constants.Constants;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.ServerConstants.*;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileUpdateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class ProfileUpdateFragment extends BaseProfileFragment {
    HideTabsListener mListener;
    protected String base64Image;

    /*Keys are all EditText fields for the profile whose input values need to be extracted and sent to the server on update; values are the name of the parameter in the app server*/
    Map<View, String> fieldsToExtract;

    public ProfileUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Extracts the data from each of the views in fieldsToExtract, skillsField, and typesField and returns the data as a JSONObject
        @throws JSONException ex - if an error occurs while attempting to put items into the ProfileUpdateFragment
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
            if( skillsField != null )
                data.put(USERS.SKILLS.string, new JSONArray( ((ProfileArrayAdapter) skillsField.getAdapter()).getItems()) );
            if( typesField != null )
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
        getActivity().finish();
        startActivity(reloadIntent);
    }

    /*
    Override to use ProfileArrayAdapter, which allows removal of skills/types from each list.
     */
    @Override
    protected void configureListData(){
                /*Initialize the skills/types adapters and attach them to their respective GridViews*/
        ProfileArrayAdapter skillsAdapter = null;
        ProfileArrayAdapter typesAdapter = null;
        try {
            List<String> skillsList = fieldToList(profileData.getJSONArray(USERS.SKILLS.string));
            List<String> typesList = fieldToList(profileData.getJSONArray(USERS.TYPES.string));
            skillsAdapter = new ProfileArrayAdapter(getActivity(), R.layout.profile_item,R.id.item_name, skillsList);
            typesAdapter = new ProfileArrayAdapter(getActivity(), R.layout.profile_item,R.id.item_name, typesList);        }
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

            final AutoCompleteTextView inputField = new AutoCompleteTextView(getActivity());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, ( (ProfileActivity) getActivity() ).getSkillsCollection() );
            inputField.setAdapter(adapter);


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
                            addItem(typesField, types[typeSelected]); //Add the se.lected type based on its position in the "types" array
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

    /*Gets the image selected by user and sets it as the profile picture. Also encodes it to a base64 string to update to the server*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            image.setImageBitmap(imageBitmap);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }

    protected void putImage(JSONObject data){
        try {
            data.put(imageFieldName, base64Image);
            Log.d(TAG, "I put the image in");
        } catch (JSONException e) {
            Log.d(TAG, "failed to put image" );
            e.printStackTrace();
        }
    }


}
