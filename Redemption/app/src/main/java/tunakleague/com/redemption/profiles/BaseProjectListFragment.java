package tunakleague.com.redemption.profiles;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tunakleague.com.redemption.DetailedErrorListener;
import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.R;

//import tunakleague.com.redemption.profiles.dummy.DummyContent;
import tunakleague.com.redemption.app_constants.ServerConstants.*;


/**
 *Retrieves a user's list of projects from the app server and displays them in a list. Also allows user to create new projects
 * and view/update the information for each individual project.
 */
public class BaseProjectListFragment extends android.support.v4.app.Fragment implements AbsListView.OnItemClickListener {
    public final String TAG = "BaseProjectListFragment";

    private JSONArray projects; //Holds each project owned by the user as a JSONObject
    private OnProjectSelectedListener mListener; //Interface implemented by parent Activity. Used to communicate with it.

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static BaseProjectListFragment newInstance() {
        BaseProjectListFragment fragment = new BaseProjectListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BaseProjectListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d( TAG, "Creating the view" );
        View view = inflater.inflate(R.layout.fragment_projectitem_list, container, false);
        mListView = (AbsListView) view.findViewById(android.R.id.list);


        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnProjectSelectedListener) activity; //Get the activity imnplemented as the callback interface.
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        /*Send request for list of projects owned by this user*/
        String url = URLS.PROJECT_LIST.string;
        JsonArrayRequest projectListRequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        projects = response;
                        renderProjects();
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
        MyApplication.requestQueue.add(projectListRequest);

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            Log.d(TAG, "clicked");
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            try {
                mListener.onProjectSelected(projects.getJSONObject(position));
            }
            catch(Exception ex ) {
                Log.d(TAG, "JSONError - Passing the selected project from projectlist");
            }
        }
    }


    /*
    Extracts the names of all projects retrieved from the server and adds them to the ListView to display to the user.
     */
    public void renderProjects(){
        /*Put the name of each project into projectNames*/
        List<String> projectNames = new ArrayList<String>();
        for( int i = 0; i < projects.length(); i++  ) {
            try {
                projectNames.add(projects.getJSONObject(i).getString(PROJECTS.PROJECT_NAME.string));
            }
            catch(JSONException ex ){
                Log.d(TAG, "JSON error - extracting project_name" );
            }
        }
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, projectNames);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);   // Set the adapter
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public interface OnProjectSelectedListener {
        // TODO: Update argument type and name
        /*
        Starts a ProfileUpdateFragment to show the profile of the given project
        @param project - object containing all the fields of the project to be displayed
         */
        public void onProjectSelected(JSONObject project);
    }

}

