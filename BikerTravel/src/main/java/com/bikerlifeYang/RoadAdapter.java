package com.bikerlifeYang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class RoadAdapter extends RecyclerView.Adapter<RoadAdapter.ViewHolder> implements View.OnClickListener {

    private final Context mContext;
    private final ArrayList<Post20> mData;
    //    -------------------------------------------------------------------
    private OnItemClickListener mOnItemClickListener = null;

    public RoadAdapter(Context context, ArrayList<Post20> data) {
        this.mContext = context;
        this.mData = data;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意這裡使用getTag方法獲取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    @NonNull
    @Override
    public com.bikerlifeYang.RoadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.plan_post, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.Name = (TextView) view.findViewById(R.id.Name);
        holder.Content = (TextView) view.findViewById(R.id.Content);
        holder.Add = (TextView) view.findViewById(R.id.Addr);
        //----------------------------------------------------
        //將創建的View註冊點擊事件
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull com.bikerlifeYang.RoadAdapter.ViewHolder holder, int position) {
        final Post20 post = mData.get(position);
        //-----------------------------------
        holder.Name.setText(post.Name);
        holder.Add.setText(post.Add);
        holder.Content.setText(post.Description);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView Name;
        public TextView Content;
        public TextView Add;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
