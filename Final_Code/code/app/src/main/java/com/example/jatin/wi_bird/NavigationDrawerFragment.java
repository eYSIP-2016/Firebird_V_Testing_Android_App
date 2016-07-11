/*
 *
 * Project Name: Android App development for testing Firebird V Robot
 * Author List: Jatin Mittal
 * Filename: NavigationDrawerFragment.java
 * Functions: onCreateView(LayoutInflater, ViewGroup, Bundle), setUp(int, DrawerLayout, final Toolbar), getData(),
 *            saveToPreferences(Context, String, String), readFromPreferences(Context, String, String),onCreate(Bundle)
 *            NavigationDrawerFragment()
 * Global Variables: PREF_FILE_NAME, KEY_USER_LEARNED_DRAWER, mDrawerToggle, ActionBarDrawerToggle, DrawerLayout,
 *                   mUserLearnedDrawer, adapter, recyclerView
 *
 */
package com.example.jatin.wi_bird;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/*
 *
 * Class Name: NavigationDrawerFragment
 * Logic: this fragment sets up the Drawer in the app
 * Example Call: new NavigationDrawerFragment()
 *
 */
public class NavigationDrawerFragment extends Fragment {
    //constants
    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    //to hold ActionBarDrawerToggle object
    private ActionBarDrawerToggle mDrawerToggle;
    //to hold DrawerLayout object
    private DrawerLayout mDrawerLayout;
    private boolean mUserLearnedDrawer;
    //to store VivzAdapter object
    private VivzAdapter adapter;
    //to hold RecyclerView object
    private RecyclerView recyclerView;
    private boolean mFromSavedInstanceState;
    private View containerView;


    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    /**
     * Function Name: onCreateView
     * Input: inflater --> The LayoutInflater object that can be used to inflate any views in the fragment,
     * container	--> If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * savedInstanceState --> If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * Output: creates and returns the view hierarchy associated with the fragment.
     * Logic: Called to have the fragment instantiate its user interface view. This is optional, and non-graphical fragments can return null (which is the default
     * implementation).
     * Example Call: This will be called between onCreate(Bundle) and onActivityCreated(Bundle).
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        //holding the VivzAdapter object
        adapter = new VivzAdapter(getActivity(), getData());
        //setting adapter to the recyclerView
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }

    /**
     * Function Name: setUp
     * Input: fragmentId --> holds the fragment id of the activityto which drawer is to be set
     * drawerLayout --> holds the drawerlayout of the activity to which drawer is to be set
     * toolbar --> holds the toolbar of the activity to which drawer is to be set
     * Output: builds navigation drawer to the activity with different action specified to different events
     * Logic: creating a ActionBarDrawerToggle object and setting its functions
     * Example Call:  df.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar)
     */

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            //called on drawer opened
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
            }

            //called on drawer closed
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            //called on drawer slide
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    /**
     * Function Name: getData
     * Input: None
     * Output: List for drawer
     * Logic: creating an object of Information class and set the info to each object
     * Example Call:  NavigationDrawerFragment.getData()
     */

    public static List<Information> getData() {
        //load only static data inside a drawer
        List<Information> data = new ArrayList<>();
        //holds the icon for drawer list
        int[] icons = {R.drawable.ic_about_us, R.drawable.ic_wireless, R.drawable.ic_velocity};
        //holds the title for the drawer list
        String[] titles = {"About us", "Sensor values", "Set Velocity"};
        //add the items into list
        for (int i = 0; i < titles.length && i < icons.length; i++) {
            Information current = new Information();
            current.iconId = icons[i];
            current.title = titles[i];
            data.add(current);
        }
        return data;
    }

    /**
     * Function Name: saveToPreferences
     * Input: context --> holds the context, preferenceName --> holds the preference name,
     * preferenceValue --> holds the preference value
     * Output: data saved to preference
     * Logic: using sharedPreferences
     * Example Call:  NavigationDrawerFragment.saveToPreferences()
     */
    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    /**
     * Function Name: readFromPreferences
     * Input: context --> holds the context, preferenceName --> holds the preference name,
     * preferenceValue --> holds the preference value
     * Output: data read from preference
     * Logic: using sharedPreferences
     * Example Call:  NavigationDrawerFragment.readFromPreferences()
     */
    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    /**
     * Function Name: onCreate
     * Input: savedInstanceState --> If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently
     * supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     * Output: sets up the ControlActivity
     * Logic: Called when the activity is starting. This is where most initialization should go: calling setContentView(int) to inflate the activity's UI,
     * using findViewById(int) to programmatically interact with widgets in the UI
     * Example Call: Called automatically when the activity is created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

}


