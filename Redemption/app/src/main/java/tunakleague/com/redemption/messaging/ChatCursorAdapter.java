package tunakleague.com.redemption.messaging;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
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
        String body = cursor.getString(cursor.getColumnIndexOrThrow("message"));
        // Populate fields with extracted properties
        msg.setText(body);

    }
}
