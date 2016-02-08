package tunakleague.com.collabin;

import android.content.Context;
import android.opengl.Visibility;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class DetailedErrorListener implements Response.ErrorListener {
    Context context;
    ProgressBar spinner = null; //must hide this when error response received as well for update fragments
    Button updateButton = null; //must re-enable the update button for update fragments.

    public DetailedErrorListener(Context context){
        super();
        this.context = context;
    }

    public void onErrorResponse(VolleyError error) {
        /*Turn off any loading spinner if it exists*/
        if( spinner != null ){
            spinner.setVisibility(View.GONE);
        }

        /*Make update button clickable again if it exists*/
        if( updateButton != null ){
            updateButton.setClickable(true);
        }

        String errorMessage = null;

        NetworkResponse response = error.networkResponse;
        if(response != null && response.data != null){
            switch(response.statusCode){
                case 400:
                    errorMessage = new String(response.data);
                    errorMessage = trimMessage(errorMessage, "non_field_errors");
                    break;
                default:
                    errorMessage = "We have no idea what went wrong";
            }
            if(errorMessage != null)
                displayMessage(errorMessage); //Display the message as a Toast notification
            //TODO: Additional cases. Idk maybe  a generic "SERVER ERROR" in case it's not 400
        }
    }

    /*Adds an optional loading spinner, so that it can be turned off by this class on error response*/
    public DetailedErrorListener withLoadingSpinner( ProgressBar spinner ){
        this.spinner = spinner;
        return this;
    }

    /*Adds an optional update button which is made clickable again on error response*/
    public DetailedErrorListener withUpdateButton( Button updateButton ){
        this.updateButton = updateButton;
        return this;
    }

    /* Helper method.
    Extracts the error messages from the JSONObject into one readable String and returns it.
     */
    private String trimMessage(String json, String key){ //TODO: GET RID OF THE SQUARE BRACKETS SOMEHOW
        String errorMessage = "";

        try{
            JSONObject obj = new JSONObject(json);
            Iterator<String> iter = obj.keys();
            errorMessage = "";
                            /*Build an errorMessage using all fields of JSONObject returned*/
            while( iter.hasNext()){
                errorMessage += obj.getString( iter.next() ) + "\n";
            }
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return errorMessage;
    }

    //Somewhere that has access to a context
    public void displayMessage(String toastString){
        Toast.makeText( context, toastString, Toast.LENGTH_LONG).show();
    }
}
