package com.example.barf_api_25_java.Activities.DogTab.Settings.AllowFoods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.barf_api_25_java.R;
import com.example.barf_api_25_java.Utils.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllowFoodsListAdapter extends BaseExpandableListAdapter {
    class ViewHolder {
        public CheckBox checkBox;
        public TextView text;
    }

    private Context context;
    private List<String> listTitles;
    private HashMap<String, List<Entry<String, Boolean>>> details;
    private List<Boolean> titlesCheckBoxStates = new ArrayList<>();

    public HashMap<String, List<Entry<String, Boolean>>> getData() { return details; }

    private void createTitlesCheckBoxesStates() {
        listTitles.forEach(title -> {
            titlesCheckBoxStates.add(listTitles.indexOf(title), details.get(title).stream().anyMatch(entry -> entry.getValue()));
        });
    }

    public AllowFoodsListAdapter(Context context, HashMap<String, List<Entry<String, Boolean>>> details) {
        this.context = context;
        this.details = details;
        this.listTitles = new ArrayList<>(details.keySet());
//        Collections.sort(listTitles);

        createTitlesCheckBoxesStates();
    }

    @Override
    public int getGroupCount() {
        return listTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return details.get(listTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return details.get(listTitles.get(groupPosition)).get(childPosition).getKey();
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
                titlesCheckBoxStates.add(groupPosition, !state);

                details.get(details.keySet().toArray()[groupPosition]).forEach(entry -> {entry.setValue(!state);});
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

        List<Entry<String, Boolean>> entryList = details.get(details.keySet().toArray()[groupPosition]);
        holder.checkBox.setChecked(entryList.get(childPosition).getValue());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state = entryList.get(childPosition).getValue();
                details.get(details.keySet().toArray()[groupPosition]).get(childPosition).setValue(!state);

                if (!state && !titlesCheckBoxStates.get(groupPosition)) {
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

}
