package com.vitorcampos.socialnetworkconnect.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.vitorcampos.socialnetworkconnect.R;
import com.vitorcampos.socialnetworkconnect.activity.MainActivity;
import com.vitorcampos.socialnetworkconnect.adapter.InfiniteLoadAdapter;
import com.vitorcampos.socialnetworkconnect.util.Constants;
import com.vitorcampos.socialnetworkconnect.view.FriendListItem;
import com.vitorcampos.socialnetworkconnect.view.InfiniteListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsListFragment extends Fragment implements InfiniteListView.InfiniteLoadListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ListView applicationListView;
    private InfiniteListView listView;
    private ArrayList<FriendListItem> listElements;
    private int lastPage = 0;
    private int nextPage = 9;
    private InfiniteLoadAdapter infiniteLoadAdapter;
    static Context context;
    private TextView emptyMessage;
    private ProgressBar progressBar;
    View rootView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public FriendsListFragment(Context context, int sectionNumber) {
        this.context = context;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        this.setArguments(args);
    }

    public FriendsListFragment() {
       Log.d(Constants.LOG_TAG, "Constructor with no arguments");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.friends_list_fragment, container, false);
        emptyMessage = (TextView)rootView.findViewById(R.id.lbl_empty_message);
        listView = (InfiniteListView) rootView.findViewById(R.id.el);

        listElements = new ArrayList<FriendListItem>();

        infiniteLoadAdapter = new InfiniteLoadAdapter(getActivity(), listElements, R.layout.friends_listitem_row);
        listView.setLoadingView(R.layout.loading_layout);
        listView.setAdapter(infiniteLoadAdapter);

        listView.setListener(this);

        getFriendsFQL();
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void getFriendsFQL(){
        Session session = Session.getActiveSession();
        String fqlQuery = "SELECT uid,name,pic_square FROM user WHERE uid IN " +
                "(SELECT uid2 FROM friend WHERE uid1 = me()) order by name limit " + lastPage + "," + nextPage;

        Bundle params = new Bundle();
        params.putString("q", fqlQuery);

        Request request = new Request(session,
                "/fql",
                params,
                HttpMethod.GET,
                new Request.Callback(){
                    public void onCompleted(Response response) {
                        Log.i("INFO", "Result: " + response.toString());

                        try{
                            GraphObject graphObject = response.getGraphObject();
                            JSONObject jsonObject = graphObject.getInnerJSONObject();
                            Log.d("data", jsonObject.toString(0));
                            ArrayList<FriendListItem> newData = new ArrayList<FriendListItem>();
                            JSONArray array = jsonObject.getJSONArray("data");
                            for(int i=0;i<array.length();i++){

                                JSONObject friend = array.getJSONObject(i);
                                String url = friend.getString("pic_square");
                                FriendListItem friendItem = new
                                        FriendListItem(friend.getString("pic_square"), friend.getString("name"), i);
                                newData.add(friendItem);
                            }
                            listView.addNewData(newData);
                            lastPage = nextPage + 1;
                            nextPage = lastPage + 9;
                        }catch(JSONException e){
                            Log.e(Constants.LOG_TAG, "Erro na tentativa de recuperar lista de amigos: ",e);
                        }
                    }
                });
        Request.executeBatchAsync(request);
    }

    @Override
    public void loadData() {
        getFriendsFQL();
    }
}