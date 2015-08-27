package tunakleague.com.redemption.search;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import tunakleague.com.redemption.app_constants.Constants;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.ServerConstants;
import tunakleague.com.redemption.profiles.BaseProjectListFragment;

public class ProjectSelectActivity extends AppCompatActivity implements BaseProjectListFragment.OnProjectSelectedListener {
    public final String TAG = "ProjectSelectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_select);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.project_select, BaseProjectListFragment.newInstance());
        transaction.commit();
    }


    @Override
    public void onProjectSelected(JSONObject project) {
          /*Get ID of the project and store for requests later*/
        try{
            int projectID = project.getInt(ServerConstants.PROJECTS.PK.string);
            Intent projectSearchIntent = new Intent( this, ProjectSearchActivity.class);
            projectSearchIntent.putExtra(Constants.PROJECT_ID, projectID);
            startActivity(projectSearchIntent);

        }
        catch(JSONException ex ){
            Log.d(TAG, "Error passing project to ProjectSearchActivity");
        }
    }
}
