package com.bikerlifeYang;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.bikerlifeYang.Biker_home.phone_height_img;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Biker_home_RecyclerAdapter extends RecyclerView.Adapter<Biker_home_RecyclerAdapter.ViewHolder> implements View.OnClickListener {
    private final Context mContext;
    private final ArrayList<Post> mData;
    private final int TABLE_ID;
    //    -------------------------------------------------------------------
    private OnItemClickListener mOnItemClickListener = null;

    //--------------------------------------------
    public Biker_home_RecyclerAdapter(int TABLE_ID, Context context, ArrayList<Post> data) {
        this.TABLE_ID=TABLE_ID;
        this.mContext = context;
        this.mData = data;
    }

    //    -------------------------------------------------------------------
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    //-------------------------------------------------------------------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(TABLE_ID==1)
        {
            //----------------------------------------------------
            //將創建的View註冊點擊事件

        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.home_attractions, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.img = (ImageView) view.findViewById(R.id.img);
        holder.Name = (TextView) view.findViewById(R.id.Name);
        holder.Content = (TextView) view.findViewById(R.id.Content);
        holder.Add = (TextView) view.findViewById(R.id.Addr);
        holder.Zipcode = (TextView) view.findViewById(R.id.Zipcode);

        holder.Picdescribe1 = (TextView) view.findViewById(R.id.Picdescribe1);
        holder.tel = (TextView) view.findViewById(R.id.tel);
        holder.Ticketinfo = (TextView) view.findViewById(R.id.Ticketinfo);
        holder.Opentime = (TextView) view.findViewById(R.id.Opentime);



        //----------------------------------------------------
        //將創建的View註冊點擊事件
        view.setOnClickListener(this);

        view.setOnClickListener(this);
        return holder;
        }
        else if(TABLE_ID==2)
        {
            View view = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.home_routelist, parent, false);
            ViewHolder holder = new ViewHolder(view);
            holder.route_Name = (TextView) view.findViewById(R.id.route_Name);
            holder.route_S_PlaceDes = (TextView) view.findViewById(R.id.route_S_PlaceDes);
            holder.route_Bike_length = (TextView) view.findViewById(R.id.route_Bike_length);
            holder.route_Toldescribe = (TextView) view.findViewById(R.id.route_Toldescribe);
            view.setOnClickListener(this);
            return holder;
        }



        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = mData.get(position);
        String a="";

        //-----------------------------------
        //=================車道路線
        if(TABLE_ID==1)
        {
            //====光觀旅遊
            holder.Name.setText(post.Name);
            holder.Add.setText(post.Add);
            holder.Content.setText(post.Content);
            if (post.Zipcode.length() > 0) {
                holder.Zipcode.setText("[" + post.Zipcode + "]");
            } else {
                holder.Zipcode.setText("[000]");
            }
            holder.img.getLayoutParams().height=phone_height_img;
            holder.Picdescribe1.setText(post.Picdescribe1);
            holder.tel.setText(post.Tel);
            holder.Ticketinfo.setText(post.Ticketinfo);
            holder.Opentime.setText(post.Opentime);


            //====光觀旅遊
            ////---------------------------------------
//        若圖片檔名是中文無法下載,可用此段檢查圖片網址且將中文解碼
        String ans_Url = post.posterThumbnailUrl;
        if (post.posterThumbnailUrl.getBytes().length == post.posterThumbnailUrl.length() ||
                post.posterThumbnailUrl.getBytes().length > 100) {
            ans_Url = post.posterThumbnailUrl;//不包含中文，不做處理
//            ans_Url=post.posterThumbnailUrl.replace("http://", "https://");
        } else {
//    ans_Url = utf8Togb2312(post.posterThumbnailUrl);
           ans_Url = utf8Togb2312(post.posterThumbnailUrl).replace("http://", "https://");
        }
///*        Glide.with(mContext)
//                .load(ans_Url)
//                .into(holder.img);*/
//
////----------------------------------------
        Glide.with(mContext)
                .load(ans_Url)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(300, 300)//大小
//                .thumbnail(0.5f )//縮圖比例
                .transition(withCrossFade())

//                . centerCrop()//均勻地縮放圖像（保持圖像的寬高比），
                // 以使圖片填滿給定區域，盡可能多地顯示圖片。如果需要，圖片將被水準或垂直裁剪。
                .fitCenter()//均勻地縮放圖片（保持圖像的縱橫比）以適應給
                // 定的區域。整個圖片是可見的，但可能有垂直或水準的填充。
                .error(
                        Glide.with(mContext)
                                .load("https://bklifetw.com/img/nopic1.jpg"))
                .into(holder.img);


        }
        else if(TABLE_ID==2)
        {
            ///=================車道路線
            //         Name;  車道名稱
            holder.route_Name.setText(post.Name);
//         S_PlaceDes; 鄉鎮
            holder.route_S_PlaceDes.setText(post.S_PlaceDes);
//         Bike_length; 距離
            holder.route_Bike_length.setText(post.Bike_length);
//        Toldescribe; 描述
            holder.route_Toldescribe.setText(post.Toldescribe);


        }









        holder.itemView.setTag(position);
    }

//    //    -----------把中文字符轉換為帶百分號的瀏覽器編碼-----------
    public static String utf8Togb2312(String inputstr) {
        String r_data = "";
        try {
            for (int i = 0; i < inputstr.length(); i++) {
                char ch_word = inputstr.charAt(i);
//            下面這段代碼的意義是:只對中文進行轉碼
                if (ch_word + "".getBytes().length > 1 && ch_word != ':' && ch_word != '/') {
                    r_data = r_data + java.net.URLEncoder.encode(ch_word + "", "utf-8");
                } else {
                    r_data = r_data + ch_word;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
//            System.out.println(r_data);
        }
        return r_data;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意這裡使用getTag方法獲取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    //======= sub class   ==================
    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView Name;
        public TextView Add;
        public TextView Content;
        public TextView Zipcode;
        public TextView Picdescribe1;
        public TextView tel;
        public TextView Ticketinfo;
        public TextView Opentime;




        public TextView route_Name;
        public TextView route_S_PlaceDes;
        public TextView route_Bike_length;
        public TextView route_Toldescribe;


        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
//-----------------------------------------------
}