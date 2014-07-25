package com.vitorcampos.socialnetworkconnect.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vitorcampos.socialnetworkconnect.R;
import com.vitorcampos.socialnetworkconnect.task.ImageLoader;
import com.vitorcampos.socialnetworkconnect.view.FriendListItem;

import java.util.List;

public class InfiniteLoadAdapter extends ArrayAdapter<FriendListItem> {
	
	private List<FriendListItem> itemList;
	private Context context;
	private int layoutId;

    private class ViewHolder {
        ImageView iconFriendPic;
        TextView txtFriendName;
    }
	
	public InfiniteLoadAdapter(Context context, List<FriendListItem> itemList, int layoutId) {
		super(context, layoutId, itemList);
		this.itemList = itemList;
		this.context = context;
		this.layoutId = layoutId;
	}

	@Override
	public int getCount() {		
		return itemList.size() ;
	}

	@Override
	public FriendListItem getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {		
		return itemList.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.txtFriendName = (TextView) convertView.findViewById(R.id.text_friend_name);
            holder.iconFriendPic = (ImageView) convertView.findViewById(R.id.icon_friend_pic);
            convertView.setTag(holder);
		} else {
            holder = (ViewHolder) convertView.getTag();
        }
		
        FriendListItem item = itemList.get(position);
        holder.txtFriendName.setText(item.getFriendName());

        if (item.getFriendPicURL() != null && !"".equalsIgnoreCase(item.getFriendPicURL())){
            //Faz o download da imagem do perfil de forma assincrona
            ImageLoader imageLoader = new ImageLoader( holder.iconFriendPic );
            imageLoader.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR, item.getFriendPicURL() );
        }
		return convertView;

	}
	

	

}