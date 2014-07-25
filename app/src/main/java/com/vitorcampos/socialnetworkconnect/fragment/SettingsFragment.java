package com.vitorcampos.socialnetworkconnect.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.vitorcampos.socialnetworkconnect.R;
import com.vitorcampos.socialnetworkconnect.activity.LoginActivity;
import com.vitorcampos.socialnetworkconnect.activity.MainActivity;

import java.util.Arrays;


public class SettingsFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private UiLifecycleHelper uiHelper;
    private TextView userInfoTextView;
    private TextView userNameTextView;
    private ProfilePictureView profilePictureView;
    private ProgressDialog dialog;

    public static SettingsFragment newInstance(int sectionNumber) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.settings_fragment, container, false);
        userInfoTextView = (TextView) rootView.findViewById(R.id.text_user_info);
        userNameTextView = (TextView) rootView.findViewById(R.id.text_user_name);
        profilePictureView = (ProfilePictureView) rootView.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        dialog = ProgressDialog.show(getActivity(), "",
                getActivity().getString(R.string.loading_user_info_label), true);

        LoginButton authButton = (LoginButton) rootView.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_likes", "user_status", "user_friends"));

        return rootView;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            userInfoTextView.setVisibility(View.VISIBLE);
            if (!dialog.isShowing()){
                dialog.show();
            }
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (dialog != null && dialog.isShowing()){
                        dialog.cancel();
                    }
                    if (user != null) {
                        userNameTextView.setText(user.getName());
                        userInfoTextView.setText(buildUserInfoDisplay(user));
                    }
                }
            });
        } else if (state.isClosed()) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this)
                    .setPermissions(Arrays.asList("public_profile", "user_friends"))
                    .setCallback(callback));
        } else {
            Session.openActiveSession(getActivity(), this, true, callback);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        userInfo.append(String.format(getActivity().getString(R.string.birthday_label) + " %s\n\n",
                user.getBirthday() != null ? user.getBirthday() : "-"));

        userInfo.append(String.format(getActivity().getString(R.string.location_label) + " %s\n\n",
                user.getLocation() != null ? user.getLocation().getProperty("name") : "-" ));

        userInfo.append(String.format(getActivity().getString(R.string.locale_label) + "  %s\n\n",
                user.getProperty("locale") != null ? user.getProperty("locale") : "-" ));


        return userInfo.toString();
    }
}