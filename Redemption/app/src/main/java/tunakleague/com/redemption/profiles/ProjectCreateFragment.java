package tunakleague.com.redemption.profiles;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import tunakleague.com.redemption.DetailedErrorListener;
import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.ServerConstants;
import tunakleague.com.redemption.experimental.ExpandableHeightGridView;

/**
 * Displays a UI for users to create a new project and sends the new project data up to the app server to be saved.
 */
public class ProjectCreateFragment extends ProfileUpdateFragment {
    public static final String TAG = "ProjectCreateFragment";

    public static ProjectCreateFragment newInstance() {
        ProjectCreateFragment fragment = new ProjectCreateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectCreateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_project, container, false);

        /*Initialize the skillsField and typesField GridViews and their adapters*/
        skillsField = (ExpandableHeightGridView) view.findViewById(R.id.skills );
        typesField = (ExpandableHeightGridView) view.findViewById(R.id.types);
        skillsField.setExpanded(true);
        typesField.setExpanded(true);
        ProfileArrayAdapter skillsAdapter = new ProfileArrayAdapter(getActivity(), R.layout.profile_item,R.id.item_name, new ArrayList<String>() );
        ProfileArrayAdapter typesAdapter = new ProfileArrayAdapter(getActivity(), R.layout.profile_item,R.id.item_name, new ArrayList<String>() );
        skillsField.setAdapter(skillsAdapter);
        typesField.setAdapter(typesAdapter);


        /*Add listeners to the "Add Skills" and "Add Types" buttons*/
        Button skillsAddButton = (Button) view.findViewById(R.id.skills_add);
        Button typesAddButton = (Button) view.findViewById(R.id.types_add);
        skillsAddButton.setOnClickListener(new CreateSkillListener());
        typesAddButton.setOnClickListener(new CreateTypeListener());

        /*Hide delete button since we don't use it here.*/
        Button deleteButton = (Button) view.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.INVISIBLE);

        /*Add listener to the Update button */
        Button updateButton = (Button) view.findViewById(R.id.update_button);
        updateButton.setOnClickListener(new UpdateListener());
        updateButton.setText("Create"); //We're re-using ProjectUpdateFragment's layout, but need to change the name of this button from "Update"

        /*Initialize image data, and Add listener to imageview*/
        initializeImageData((ImageView) view.findViewById(R.id.project_image), ServerConstants.PROJECTS.PROJECT_IMAGE.string);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
                //image.postInvalidate();
            }
        });

        /*Specify all EditText fields in the UI that need to be EXTRACTED (unconditionallY) upon update and their corresponding server model keys*/
        fieldsToExtract.put(view.findViewById(R.id.project_name), ServerConstants.PROJECTS.PROJECT_NAME.string);
        fieldsToExtract.put(view.findViewById(R.id.project_summary), ServerConstants.PROJECTS.PROJECT_SUMMARY.string);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ( super.mListener ).setTabsVisible(true); //Un-hide the tabs when exiting this fragment. Uses ProfileUpdateFragment's callback interface
    }

    /*
    Creates new project and send the project data to the server to update it. Takes user back to updated projects list screen.
    */
    @Override
    protected void updateProfile() {
        JSONObject newProject = extractFields();
        putImage(newProject);
        Log.d(TAG, "Sending this: " + newProject.toString());

        /*Create request to update the Project*/
        String url = ServerConstants.URLS.PROJECT_LIST.string;
        JsonObjectRequest createProjectRequest = new JsonObjectRequest(Request.Method.POST, url, newProject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        profileData = response;
                        Toast.makeText(getActivity(), "Project created", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Created project: " + profileData.toString());
                        //mListener.onProjectUpdated(profileData, position); //Pass updated project info to activity so it can update it in BaseProjectListFragment
                        reloadProjects(); //Take user back to updated list of projects
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
        MyApplication.requestQueue.add(createProjectRequest);
    }


}
