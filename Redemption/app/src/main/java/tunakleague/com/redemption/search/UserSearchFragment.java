package tunakleague.com.redemption.search;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.ServerConstants;
import tunakleague.com.redemption.experimental.ExpandableHeightGridView;
import tunakleague.com.redemption.profiles.BaseProfileFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSearchFragment extends BaseProfileFragment {
    public static final String TAG = "UserSearchFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProjectSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserSearchFragment newInstance() {
        UserSearchFragment fragment = new UserSearchFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public UserSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_search, container, false);

        /*Initialize the skillsField*/
        typesField = (ExpandableHeightGridView) view.findViewById(R.id.types );

                /*Initialize image data, and Add listener to imageview*/
        initializeImageData((ImageView) view.findViewById(R.id.project_image), ServerConstants.PROJECTS.PROJECT_IMAGE.string);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
                //image.postInvalidate();
            }
        });

        /*Specify all EditText fields in the UI that need to be POPULATED upon retrieving profile info and  and their corresponding server model keys*/
        fieldsToPopulate.put( view.findViewById(R.id.project_name), ServerConstants.PROJECTS.PROJECT_NAME.string );
        fieldsToPopulate.put(view.findViewById(R.id.project_summary), ServerConstants.PROJECTS.PROJECT_SUMMARY.string);

        Log.d(TAG, "Set up fieldsToPopulate");

        return view;
    }


}
