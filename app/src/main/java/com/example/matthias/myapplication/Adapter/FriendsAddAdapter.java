package com.example.matthias.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matthias.myapplication.Entities.Person;
import com.example.matthias.myapplication.R;

import java.util.List;

/**
 * Created by FH on 22.02.2018.
 */

public class FriendsAddAdapter extends RecyclerView.Adapter<FriendsAddAdapter.FriendsItemViewHolder>{
    private List<Person> mFriends;
    private  IFriendsAddClickListener mainClickListener;

    public FriendsAddAdapter(List<Person> mFriends, IFriendsAddClickListener mainClickListener) {
        this.mFriends = mFriends;
        this.mainClickListener = mainClickListener;
    }
    public void swapList(List<Person> newList){
        mFriends = newList;
        notifyDataSetChanged();
    }
    public interface IFriendsAddClickListener{
        void onListItemClick(Person clickedPerson);
    }
    @Override
    public FriendsAddAdapter.FriendsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.friends_list_item_add;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new FriendsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendsAddAdapter.FriendsItemViewHolder holder, int position) {
        if (position < mFriends.size())
            holder.bind(mFriends.get(position));
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public class FriendsItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameTextView;
        public FriendsItemViewHolder(View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.tv_friend_add);
            itemView.setOnClickListener(this);
        }

        public void bind(Person person) {
            mNameTextView.setText(person.name);
        }

        @Override
        public void onClick(View view) {
            int clickedItem = getAdapterPosition();
            if(clickedItem < mFriends.size()){
                mainClickListener.onListItemClick(mFriends.get(clickedItem));
            }
        }
    }
}
