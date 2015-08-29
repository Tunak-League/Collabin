package tunakleague.com.redemption.profiles;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tunakleague.com.redemption.R;


public class ProfileArrayAdapter extends ArrayAdapter<String> {
    public static final int VIEW_HOLDER = 4444;

    LayoutInflater inflater;
    List<String> items; //The items you want to display

    public ProfileArrayAdapter(Context context, int container, int textview, List<String> objects) {
        super(context, container, textview, objects);
        items = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return (items != null ) ? items.size() : 0;
    }

    @Override
    public View getView( final int position, View convertView, ViewGroup parent ) {
        ViewHolder holder;

        /*Create new profile_item if it doesn't exist*/
        if( convertView == null ) {
            convertView = inflater.inflate(R.layout.profile_item, parent, false );
            holder = new ViewHolder();
            holder.itemName = (TextView) convertView.findViewById(R.id.item_name); //Get the TextView "itemName" from newly created profile_item
            holder.deleteButton = (Button) convertView.findViewById(R.id.delete_button); //Get the Button "delete_button" from profile_item
            convertView.setTag(holder); //Set tag so the contents of the project_item can be recycled later
        }
        else {
            holder = (ViewHolder) convertView.getTag(); //Retrieve stored ViewHolder with all the contents of previous project_item
        }

        holder.itemName.setText( items.get(position) ); //Set the name of the profile item (ie a skill name or type name)
        /*Set listener to delete the profile_item if deleteButton is clicked*/
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.remove(position); //Remove the element at the corresponding position in "items"
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    /*Returns a copy of "items"*/
    public List<String> getItems() {
        return new ArrayList<String>( items );
    }

    /*Adds an item to the dataset and notifies the view to update itself */
    public void addItem(String item ) {
        items.add(item);
        notifyDataSetChanged();
    }

/*Stores the contents of a profile_item*/
static class ViewHolder {
    TextView itemName;
    Button deleteButton;
}

}
