package com.cusl.ull.qdemos.fragments.edicion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.cusl.ull.qdemos.R;
import com.cusl.ull.qdemos.utilities.DatosQdada;
import com.facebook.FacebookException;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

// This class provides an example of an Activity that uses FriendPickerFragment to display a list of
// the user's friends. It takes a programmatic approach to creating the FriendPickerFragment with the
// desired parameters -- see PickPlaceActivity in the PlacePickerSample project for an example of an
// Activity creating a fragment (in this case a PlacePickerFragment) via XML layout rather than
// programmatically.
public class PickFriendsFragmentActivity extends FragmentActivity {
    FriendPickerFragment friendPickerFragment;

    private static final String INSTALLED = "installed";

    private static final Configuration CONFIGURATION_LONG = new Configuration.Builder()
            .setDuration(Configuration.DURATION_LONG)
            .build();

    // A helper to simplify life for callers who want to populate a Bundle with the necessary
    // parameters. A more sophisticated Activity might define its own set of parameters; our needs
    // are simple, so we just populate what we want to pass to the FriendPickerFragment.
    public static void populateParameters(Intent intent, String userId, boolean multiSelect, boolean showTitleBar) {
        intent.putExtra(FriendPickerFragment.USER_ID_BUNDLE_KEY, userId);
        intent.putExtra(FriendPickerFragment.MULTI_SELECT_BUNDLE_KEY, multiSelect);
        intent.putExtra(FriendPickerFragment.SHOW_TITLE_BAR_BUNDLE_KEY, showTitleBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_friends_activity);

        FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            // First time through, we create our fragment programmatically.
            final Bundle args = getIntent().getExtras();
            friendPickerFragment = new FriendPickerFragment(args);
            fm.beginTransaction().add(R.id.friend_picker_fragment, friendPickerFragment).commit();
        } else {
            // Subsequent times, our fragment is recreated by the framework and already has saved and
            // restored its state, so we don't need to specify args again. (In fact, this might be
            // incorrect if the fragment was modified programmatically since it was created.)
            friendPickerFragment = (FriendPickerFragment) fm.findFragmentById(R.id.friend_picker_fragment);
        }

        friendPickerFragment.setExtraFields(Arrays.asList(INSTALLED));

        friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
            @Override
            public void onError(PickerFragment<?> fragment, FacebookException error) {
                PickFriendsFragmentActivity.this.onError(error);
            }
        });

        friendPickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
            @Override
            public void onDoneButtonClicked(PickerFragment<?> fragment) {
                // We just store our selection in the Application for other activities to look at.
                DatosQdada.setSelectedUsers(friendPickerFragment.getSelection());
                Collections.sort(DatosQdada.getSelectedUsers(), new Comparator<GraphUser>() {
                    public int compare(GraphUser o1, GraphUser o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                setResult(RESULT_OK, null);
                finish();
            }
        });

        friendPickerFragment.setFilter(new PickerFragment.GraphObjectFilter<GraphUser>() {
            @Override
            public boolean includeItem(GraphUser graphObject) {
                Boolean installed = graphObject.cast(GraphUserWithInstalled.class).getInstalled();
                return (installed != null) && installed.booleanValue();
            }
        });

        Crouton crouton = Crouton.makeText(this, R.string.friendsInstallApp, Style.INFO);
        crouton.setConfiguration(CONFIGURATION_LONG).show();
    }

    private void onError(Exception error) {
        String text = getString(R.string.exception, error.getMessage());
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            List<GraphUser> selectedUsers = DatosQdada.getSelectedUsers();
            if (selectedUsers != null && !selectedUsers.isEmpty()) {
                friendPickerFragment.setSelection(selectedUsers);
            }
            // Load data, unless a query has already taken place.
            friendPickerFragment.loadData(false);
        } catch (Exception ex) {
            onError(ex);
        }
    }

    private interface GraphUserWithInstalled extends GraphUser {
        Boolean getInstalled();
    }
}