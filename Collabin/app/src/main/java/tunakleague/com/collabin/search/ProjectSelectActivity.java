package tunakleague.com.collabin.search;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import tunakleague.com.collabin.app_constants.Constants;
import tunakleague.com.collabin.R;
import tunakleague.com.collabin.app_constants.ServerConstants;
import tunakleague.com.collabin.profiles.BaseProjectListFragment;

/*This Activity presents the list of projects a user currently owns, so they can select one and discover/recruit users for it*/
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
    public void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
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
