package com.shawon.yousuf.facebookintegration.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawon.yousuf.facebookintegration.R;
import com.shawon.yousuf.facebookintegration.model.Friend;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by user on 4/19/2016.
 */
public class FriendsAdapter extends ArrayAdapter<Friend> {

    private Context mContext;
    private List<Friend> friendList;
    private int resourceId;

    public FriendsAdapter(Context context, int resource, List<Friend> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.friendList = objects;
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if( convertView == null ){
            convertView = LayoutInflater.from(mContext).inflate(resourceId, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewName.setText(friendList.get(position).getName()  );

        Picasso.with(mContext)
                .load(friendList.get(position).getProfilePicUrl())
                .into(holder.imageViewProfilePic);

        return convertView;

    }





    private static class ViewHolder{
        ImageView imageViewProfilePic;
        TextView textViewName;

        public ViewHolder(View view){
            imageViewProfilePic = (ImageView) view.findViewById(R.id.imageViewProfilePic);
            textViewName = (TextView) view.findViewById(R.id.textViewFriendsName);
        }
    }
}
