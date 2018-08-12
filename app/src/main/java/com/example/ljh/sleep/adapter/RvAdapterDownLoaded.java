package com.example.ljh.sleep.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ljh.sleep.R;
import com.example.ljh.sleep.bean.DownLoadBean;

import java.util.List;

public class RvAdapterDownLoaded extends RecyclerView.Adapter<RvAdapterDownLoaded.ViewHolder>{
    private List<DownLoadBean> datalist;
    private LayoutInflater layoutInflater;

    public RvAdapterDownLoaded(Context context,List<DownLoadBean> list){
        datalist = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_downloaded,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DownLoadBean bean = datalist.get(position);
        bean.setPosition(position);
        holder.tvName.setText(bean.getName());
        holder.tvAuthor.setText(bean.getAuthor());
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tvAuthor;
        private ImageView ivMore;
        private CheckBox checkBox;
        private LinearLayout parentView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            ivMore = itemView.findViewById(R.id.ivMore);
            checkBox = itemView.findViewById(R.id.cbSelect);
            parentView = itemView.findViewById(R.id.linearLayout_downloaded);
        }
    }
}
