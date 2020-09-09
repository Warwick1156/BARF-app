package com.example.barf_api_25_java;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barf_api_25_java.Data.DogDatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private static final String DOG_ID = "DOG_ID";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<byte[]> Images = new ArrayList<>();
    private ArrayList<Integer> ids = new ArrayList<>();
    private Context mContext;

    private int position;

    public RecyclerViewAdapter(Context context, ArrayList<String> mImageNames, ArrayList<byte[]> Images, ArrayList<Integer> ids) {
        this.mImageNames = mImageNames;
        this.Images = Images;
        this.ids = ids;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        if (Images == null) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(mImages.get(position))
                    .into(holder.image);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Images.get(position))
                    .into(holder.image);
        }

        holder.imageName.setText(mImageNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));
                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();

                setPosition(holder.getAdapterPosition());
                Intent intent = new Intent(mContext, DogMainTabActivity.class);
                intent.putExtra(DOG_ID, getItemDatabaseId());
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

        /*holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    DogDatabaseHelper dogDatabaseHelper = new DogDatabaseHelper(mContext);
                    dogDatabaseHelper.removeDogById(position);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        CircleImageView image;
        TextView imageName;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);

            parentLayout.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 121, 0, "Remove");
        }

    }

    public void removeItem(int position) {
        mImageNames.remove(position);
        Images.remove(position);
        ids.remove(position);

    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return  position;
    }

    public int getItemDatabaseId() {
        return ids.get(position);
    }

    public void updateItemList(ArrayList<String> mImageNames, ArrayList<byte[]> Images, ArrayList<Integer> ids) {
        this.mImageNames = mImageNames;
        this.Images = Images;
        this.ids = ids;
    }
}