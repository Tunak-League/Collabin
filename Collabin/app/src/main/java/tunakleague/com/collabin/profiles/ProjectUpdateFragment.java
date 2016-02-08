package tunakleague.com.collabin.profiles;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tunakleague.com.collabin.DetailedErrorListener;
import tunakleague.com.collabin.MyApplication;
import tunakleague.com.collabin.R;
import tunakleague.com.collabin.app_constants.ServerConstants.PROJECTS;
import tunakleague.com.collabin.app_constants.ServerConstants.URLS;
import tunakleague.com.collabin.custom_views.ExpandableHeightGridView;

/**
 Displays a specific project's information and allows users to edit them and save them to the app server.
 */
public class ProjectUpdateFragment extends ProfileUpdateFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "ProjectUpdateFragment";

    /*Bundle keys*/
    private static final String PROJECT = "project";
    private static final String POSITION = "position";

    /*Bundle values*/

    int projectID = 0; //id of the project
    private Button updateButton; //keep reference to this so button can be disabled during updates
    private Button deleteButton; //keep reference to disable during updates.

    /*
    Stores the passed project info and its position in the list of all user's projects into a bundle and returns an instance
    of ProjectUpdateFragment.
    @param project - a JSONObject containing all the fields of information about a specific project owned by the user
     */
    public static ProjectUpdateFragment newInstance(JSONObject project) {
        ProjectUpdateFragment fragment = new ProjectUpdateFragment();
        Bundle args = new Bundle();
        Log.d(TAG, "PUTTING IN BUNDLE: " + project.toString() );
        args.putString(PROJECT, project.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                profileData = new JSONObject(getArguments().getString(PROJECT)); //Extract the passed project information

                /*Get ID of the project and store for requests later*/
                try{
                    projectID = profileData.getInt(PROJECTS.PK.string);
                }
                catch(JSONException ex ){
                    Log.d(TAG, "Error retrieving project ID");
                }

                Log.d(TAG, "Received project: " + profileData.toString());
            }
            catch( Exception ex ) {
                Log.d(TAG, "JSONError - Trying to extract project from Bundle");
            }
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu); //Create the ProjectList menu with the "Create a Project" button
        menu.clear();
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
        updateButton = (Button) view.findViewById(R.id.update_button);
        updateButton.setOnClickListener(new UpdateListener());

        deleteButton = (Button) view.findViewById(R.id.delete_button );
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Create confirm dialog to confimrm that user wishes to delete the project*/
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Project")
                        .setMessage("Delete project permanently?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteProject();
                                Log.d(TAG, "I did a delete");
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        /*Initialize image data, and Add listener to imageview*/
        initializeImageData((ImageView) view.findViewById(R.id.project_image), PROJECTS.PROJECT_IMAGE.string);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
                //image.postInvalidate();
            }
        });


        /*Specify all EditText fields in the UI that need to be POPULATED upon retrieving profile info and  and their corresponding server model keys*/
        fieldsToPopulate.put(view.findViewById(R.id.project_name), PROJECTS.PROJECT_NAME.string);
        fieldsToPopulate.put( view.findViewById(R.id.project_summary), PROJECTS.PROJECT_SUMMARY.string );

        /*Specify all EditText fields in the UI that need to be EXTRACTED (unconditionallY) upon update and their corresponding server model keys*/
        fieldsToExtract.put( view.findViewById(R.id.project_summary), PROJECTS.PROJECT_SUMMARY.string );
        fieldsToExtract.put( view.findViewById(R.id.project_name), PROJECTS.PROJECT_NAME.string );

        //TODO: Put in email ONLY if it has changed (probably in updateProfile)
        populateFields(); //Populate the selected fields in the view with the project's info
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ( super.mListener ).setTabsVisible(true); //Un-hide the tabs when exiting this fragment. Uses ProfileUpdateFragment's callback interface
    }

    @Override
    protected void updateProfile() {
        JSONObject updatedInfo = extractFields();
        putImage(updatedInfo);
        Log.d(TAG, "Sending this: " + updatedInfo.toString() );

        final ProgressBar spinner = (ProgressBar) this.getView().findViewById( R.id.project_spinner);

        /*Create request to update the Project*/
        String url = URLS.PROJECT_DETAIL.string + projectID + "/";
        JsonObjectRequest updateProjectRequest = new JsonObjectRequest(Request.Method.PUT, url, updatedInfo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        spinner.setVisibility(View.GONE);
                        updateButton.setClickable(true);
                        profileData = response;
                        Toast.makeText(getActivity(), "Project updated", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Updated info: " + profileData.toString());
                        //mListener.onProjectUpdated(profileData, position); //Pass updated project info to activity so it can update it in BaseProjectListFragment
                        reloadProjects();
                    }
                }
                ,
                new DetailedErrorListener(getActivity()).withLoadingSpinner(spinner).withUpdateButton(updateButton) //TODO: Need to override and make something to STOP/EXIT the Fragment if request fails (or else you operate on null data)
        )
        {
            @Override
            //Add header of request
            public Map<String, String> getHeaders() {
                return MyApplication.getAuthenticationHeader(getActivity());
            }
        };
        MyApplication.requestQueue.add(updateProjectRequest);
        updateButton.setClickable(false);
        spinner.setVisibility(View.VISIBLE); //show progress spinner while waiting for response
    }


    /*Deletes a project from the app server and reloads the updated list of projects on the screen*/
    public void deleteProject() {
        final ProgressDialog progress = new ProgressDialog(this.getActivity());

        String url = URLS.PROJECT_DETAIL.string + projectID + "/";
        StringRequest deleteProjectRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String >() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Toast.makeText(getActivity(), "Project deleted", Toast.LENGTH_LONG).show();
                        //mListener.onProjectUpdated(profileData, position); //Pass updated project info to activity so it can update it in BaseProjectListFragment
                        reloadProjects();
                    }
                },
                new DetailedErrorListener(getActivity())
        )
        {
            @Override
            //Add header of request
            public Map<String, String> getHeaders() {
                return MyApplication.getAuthenticationHeader(getActivity());
            }
        }   ;
        MyApplication.requestQueue.add(deleteProjectRequest);
        progress.setIndeterminate(true);
        progress.setMessage("Deleting project");
        progress.show();


        deleteButton.setClickable(false);
    }
}
