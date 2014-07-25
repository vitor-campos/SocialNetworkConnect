package com.vitorcampos.socialnetworkconnect.view;

import com.vitorcampos.socialnetworkconnect.adapter.InfiniteLoadAdapter;

/**
 * Created by vitorcampos on 24/07/14.
 */
public class FriendListItem {
    private String friendPicURL;
    private String friendName;
    private int requestCode;
    private InfiniteLoadAdapter adapter;

    public FriendListItem(String friendPicURL, String friendName, int requestCode) {
        this.friendPicURL = friendPicURL;
        this.friendName = friendName;
        this.requestCode = requestCode;
    }

    public String getFriendPicURL() {
        return friendPicURL;
    }

    public void setFriendPicURL(String friendPicURL) {
        this.friendPicURL = friendPicURL;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void setAdapter(InfiniteLoadAdapter adapter) {
        this.adapter = adapter;
    }

}
