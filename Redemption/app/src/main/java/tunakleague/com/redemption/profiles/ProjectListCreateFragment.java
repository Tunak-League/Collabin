package tunakleague.com.redemption.profiles;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import org.json.JSONArray;

import tunakleague.com.redemption.R;

//import tunakleague.com.redemption.profiles.dummy.DummyContent;


/**
 *Retrieves a user's list of projects from the app server and displays them in a list. Also allows user to create new projects
 * and view/update the information for each individual project.
 */
public class ProjectListCreateFragment extends BaseProjectListFragment  {
    public final String TAG = "ProjectListCreateFragment";

    private JSONArray projects; //Holds each project owned by the user as a JSONObject
    private OnProjectCreateListener mListener; //Interface implemented by parent Activity. Used to communicate with it.

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
    public static ProjectListCreateFragment newInstance() {
        ProjectListCreateFragment fragment = new ProjectListCreateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProjectListCreateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //Make menu item show up
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_projectlist, menu); //Create the ProjectList menu with the "Create a Project" button
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.createproject_item:
                Log.d( TAG, "CREATING A NEW PROJECT" );
                mListener.onCreateProject(); //Call activity to load a ProjectCreateFragment
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnProjectCreateListener) activity; //Get the activity imnplemented as the callback interface.
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


    public interface OnProjectCreateListener {
        // TODO: Update argument type and name
        /*
            Opens ProjectCreateFragment for user to create a new project
         */
        public void onCreateProject();
    }

}

