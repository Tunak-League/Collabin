package tunakleague.com.redemption.messaging;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DataProvider extends ContentProvider {
    public static final Uri CONTENT_URI_MESSAGES = Uri.parse("content://tunakleague.com.redemption.messaging.provider/chat");

    public static final String COL_ID = "_id";
    public static final String TABLE_CHAT = "chat";
    public static final String COL_MESSAGE = "message";
    public static final String COL_RECIPIENT = "recipient";
    public static final String COL_SENDER = "sender";
    public static final String COL_TIME_SENT = "time_sent";
    public static final String RECEIVED_OR_SENT = "received_or_sent";

    private DbHelper dbHelper;

    private static final int MESSAGES = 1;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("tunakleague.com.redemption.messaging.provider", "chat", MESSAGES);
    }
    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch(uriMatcher.match(uri)) {
            case MESSAGES:
                qb.setTables(TABLE_CHAT);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id;
        switch(uriMatcher.match(uri)) {
            case MESSAGES:
                id = db.insertOrThrow(TABLE_CHAT, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Uri insertUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(insertUri, null);
        return insertUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count;
        switch(uriMatcher.match(uri)) {
            case MESSAGES:
                count = db.delete(TABLE_CHAT, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count;
        switch(uriMatcher.match(uri)) {
            case MESSAGES:
                count = db.update(TABLE_CHAT, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
