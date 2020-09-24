package com.example.barf_api_25_java.Activities.DogTab.Settings.AllowFoods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.barf_api_25_java.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TestAdapter extends BaseExpandableListAdapter {

    class ViewHolder {
        public CheckBox checkBox;
        public TextView text;
    }

    private Context context;
    private List<String> listTitles;
    private HashMap<String, List<String>> listDetails;

    private List<Boolean> titlesCheckBoxStates = new ArrayList<>();
    private List<List<Boolean>> detailsCheckBoxStates = new ArrayList<>();

    private void initCheckBoxStates() {
        listTitles.forEach(title -> {
            titlesCheckBoxStates.add(true);
            detailsCheckBoxStates.add(new ArrayList<>());
            listDetails.get(title).forEach(item -> detailsCheckBoxStates.get(detailsCheckBoxStates.size() - 1).add(true));
        });
    }

    public TestAdapter(Context context, HashMap<String, List<String>> listDetails) {
        this.context = context;
        this.listDetails = listDetails;
        this.listTitles = new ArrayList<>(listDetails.keySet());
        Collections.sort(listTitles);

        initCheckBoxStates();
    }

    public TestAdapter(Context context, HashMap<String, List<String>> listDetails, List<Boolean> titlesCheckBoxStates, List<List<Boolean>> detailsCheckBoxStates) {
        this.context = context;
        this.listDetails = listDetails;
        this.listTitles = new ArrayList<>(listDetails.keySet());
        this.titlesCheckBoxStates = titlesCheckBoxStates;
        this.detailsCheckBoxStates = detailsCheckBoxStates;
        Collections.sort(listTitles);
    }

    @Override
    public int getGroupCount() {
        return listTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDetails.get(listTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDetails.get(listTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.allow_food_group, null);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox_allowedGroup);
            holder.text = (TextView) convertView.findViewById(R.id.tv_allowedGroup);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(listTitles.get(groupPosition));

        // TODO: Inspect
        if (titlesCheckBoxStates.size() <= groupPosition) {
            titlesCheckBoxStates.add(groupPosition, false);
        } else {
            holder.checkBox.setChecked(titlesCheckBoxStates.get(groupPosition));
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state = titlesCheckBoxStates.get(groupPosition);
                titlesCheckBoxStates.remove(groupPosition);
                titlesCheckBoxStates.add(groupPosition, state ? false : true);

                // TODO: Change to forEach
                for (int i = 0; i < listDetails.get(listTitles.get(groupPosition)).size(); i++) {
                    detailsCheckBoxStates.get(groupPosition).remove(i);
                    detailsCheckBoxStates.get(groupPosition).add(i, state ? false : true);
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.allow_food_item, null);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox_allowedItem);
            holder.text = (TextView) convertView.findViewById(R.id.tv_allowedItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText((String) getChild(groupPosition, childPosition));
        holder.checkBox.setChecked(detailsCheckBoxStates.get(groupPosition).get(childPosition));

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state = detailsCheckBoxStates.get(groupPosition).get(childPosition);
                detailsCheckBoxStates.get(groupPosition).remove(childPosition);
                detailsCheckBoxStates.get(groupPosition).add(childPosition, state ? false : true);

                if (detailsCheckBoxStates.get(groupPosition).get(childPosition) && !titlesCheckBoxStates.get(groupPosition)) {
                    titlesCheckBoxStates.remove(groupPosition);
                    titlesCheckBoxStates.add(groupPosition, true);
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public List<List<Boolean>> getDetailsCheckBoxStates() {return detailsCheckBoxStates;}
}
