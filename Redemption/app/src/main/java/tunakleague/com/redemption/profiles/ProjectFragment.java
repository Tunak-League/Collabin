package tunakleague.com.redemption.profiles;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tunakleague.com.redemption.DetailedErrorListener;
import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.ServerConstants;
import tunakleague.com.redemption.ServerConstants.*;
import tunakleague.com.redemption.experimental.ExpandableHeightGridView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectFragment extends ProfileFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "ProjectFragment";

    /*Bundle keys*/
    private static final String PROJECT = "project";
    private static final String POSITION = "position";

    /*Bundle values*/
    private int position;

    private OnProjectUpdatedListener mListener; //Instaance of callback interface implemented by activity

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProjectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProjectFragment newInstance(JSONObject project, int position) {
        ProjectFragment fragment = new ProjectFragment();
        Bundle args = new Bundle();
        Log.d(TAG, "PUTTING IN BUNDLE: " + project.toString() );
        args.putString(PROJECT, project.toString());
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                profileData = new JSONObject(getArguments().getString(PROJECT)); //Extract the passed project information
                position = getArguments().getInt(POSITION);
                Log.d(TAG, "Received project: " + profileData.toString());
            }
            catch( Exception ex ) {
                Log.d(TAG, "JSONError - Trying to extract project from Bundle");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project, container, false);

        /*Initialize the skillsField and typesField GridViews*/
        skillsField = (ExpandableHeightGridView) view.findViewById(R.id.skills );
        typesField = (ExpandableHeightGridView) view.findViewById(R.id.types);

        /*Add listeners to the "Add Skills" and "Add Types" buttons*/
        Button skillsAddButton = (Button) view.findViewById(R.id.skills_add);
        Button typesAddButton = (Button) view.findViewById(R.id.types_add);
        skillsAddButton.setOnClickListener(new CreateSkillListener() );
        typesAddButton.setOnClickListener(new CreateTypeListener());

        /*Add listener to the Update button */
        Button updateButton = (Button) view.findViewById(R.id.update_button);
        updateButton.setOnClickListener(new UpdateListener());


        /*Specify all EditText fields in the UI that need to be POPULATED upon retrieving profile info and  and their corresponding server model keys*/
        fieldsToPopulate.put( view.findViewById(R.id.project_name), PROJECTS.PROJECT_NAME.string );
        fieldsToPopulate.put( view.findViewById(R.id.project_summary), PROJECTS.PROJECT_SUMMARY.string );

        /*Specify all EditText fields in the UI that need to be EXTRACTED (unconditionallY) upon update and their corresponding server model keys*/
        fieldsToExtract.put( view.findViewById(R.id.project_summary), PROJECTS.PROJECT_SUMMARY.string );

        //TODO: Put in email ONLY if it has changed (probably in updateProfile)
        populateFields(); //Populate the selected fields in the view with the project's info
        return view;    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnProjectUpdatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener.setTabVisible(true); //Un-hide the tabs when exiting this fragment
    }

    /**
     * Callback interface to be implemented by ProfileActivity
     */
    public interface OnProjectUpdatedListener {
        // TODO: Update argument type and name
        /*

         */
        public void onProjectUpdated(JSONObject project, int position);
        public void setTabVisible( boolean visible);
    }

    @Override
    protected void updateProfile() {
        JSONObject updatedInfo = extractFields();

        /*Add project_name to request body "updatedInfo" only if its name has changed from the name retrieved from the server*/
        String project_name = ( (EditText) getView().findViewById(R.id.project_name) ).getText().toString();//Extract project_name
        String original_name = "";
        try{
            original_name = profileData.getString( PROJECTS.PROJECT_NAME.string);
            if( ! project_name.equals(original_name) ) {
                updatedInfo.put(PROJECTS.PROJECT_NAME.string, project_name );
            }
        }
        catch(JSONException ex ) {
            Log.d(TAG, "Error checking if project_name has changed" );
        }

        /*Retrieve project ID to use in the update request*/
        int projectID = 0;
        try{
            projectID = profileData.getInt(PROJECTS.PK.string);
        }
        catch(JSONException ex ){
            Log.d(TAG, "Error retrieving project ID");
        }

        Log.d(TAG, "Sending this: " + updatedInfo.toString() );

        /*Create request to update the Project*/
        String url = URLS.PROJECT_DETAIL.string + projectID + "/";
        JsonObjectRequest updateProjectRequest = new JsonObjectRequest(Request.Method.PUT, url, updatedInfo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        profileData = response;
                        Toast.makeText(getActivity(), "Project updated", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Updated info: " + profileData.toString());
                        mListener.onProjectUpdated(profileData, position); //Pass updated project info to activity so it can update it in ProjectListFragment
                    }
                }
                ,
                new DetailedErrorListener(getActivity()) //TODO: Need to override and make something to STOP/EXIT the Fragment if request fails (or else you operate on null data)
        )
        {
            @Override
            //Add header of request
            public Map<String, String> getHeaders() {
                return MyApplication.getAuthenticationHeader(getActivity());
            }
        };
        MyApplication.requestQueue.add(updateProjectRequest);
    }
}
