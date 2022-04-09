package com.jonathan.pam_tugas3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;
import com.jonathan.pam_tugas3.R;
import com.jonathan.pam_tugas3.model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder>{

        private Context context;
        private List<Order> list;
        private Dialog dialog;

        public interface Dialog{
            void onClick(int pos);
        }

        public void setDialog(Dialog dialog) {
            this.dialog = dialog;
        }

        public OrderAdapter(Context context, List<Order> list){
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_order, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.name.setText(list.get(position).getName());
            holder.date.setText(list.get(position).getDate());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView name, date;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                date = itemView.findViewById(R.id.date);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialog!=null){
                            dialog.onClick(getLayoutPosition());
                        }
                    }
                });
            }
        }
}
