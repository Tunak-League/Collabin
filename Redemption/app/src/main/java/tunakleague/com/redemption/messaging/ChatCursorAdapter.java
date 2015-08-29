package tunakleague.com.redemption.messaging;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import tunakleague.com.redemption.R;

public class ChatCursorAdapter extends CursorAdapter {
    public ChatCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }


    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.chat_message, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView msg = (TextView) view.findViewById(R.id.singleMessage);
        // Extract properties from cursor
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (cursor.getString(cursor.getColumnIndexOrThrow("received_or_sent")).equals("received")) {
            params.gravity = Gravity.LEFT;
        }
        else {
            params.gravity = Gravity.RIGHT;
        }
        msg.setLayoutParams(params);

        String body = cursor.getString(cursor.getColumnIndexOrThrow("message"));
        String dateSent = getDate(cursor.getString(cursor.getColumnIndexOrThrow("time_sent")));
        // Populate fields with extracted properties
        msg.setText(body + "\n" + dateSent);
    }

    public String getDate(String rawDate) {
        String[] dateTime = rawDate.split(" ");
        String[] time = dateTime[1].split(":");

        return dateTime[0] + ", " + time[0] + ":" + time[1];
    }
}
