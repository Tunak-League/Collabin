package tunakleague.com.redemption;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import tunakleague.com.redemption.authentication.LoginActivity;


public class DetailedErrorListener implements Response.ErrorListener {
    Context context;

    public DetailedErrorListener(Context context){
        super();
        this.context = context;
    }

    public void onErrorResponse(VolleyError error) {
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
