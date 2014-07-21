package com.meslize.fotomaton;

import java.util.List;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.framework.library.view.ManagedImageView;
import com.meslize.fotomaton.model.Path;

public class PathsAdapter extends BaseAdapter {

	static class ViewHolder {
		ManagedImageView image;
		ImageView imageState;
	}
	
	private LayoutInflater inflater;
	private List<Path> data;
	
	private SparseBooleanArray mSelectedItemsIds;
	
	private int selectedColor;
	
	public PathsAdapter(LayoutInflater inflater, List<Path> data) {		
		this.inflater = inflater;
		this.data = data;
		
		this.selectedColor = inflater.getContext().getResources().getColor(R.color.pink_transparent);
		
		this.mSelectedItemsIds = new SparseBooleanArray();
	}

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_paths_list, null);
			
			viewHolder = new ViewHolder();
			viewHolder.image = (ManagedImageView) convertView.findViewById(R.id.image);
			viewHolder.imageState = (ImageView) convertView.findViewById(R.id.image_state);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Path path = data.get(position);
		viewHolder.image.load(path.getThumb());
		
		viewHolder.imageState.setBackgroundColor(mSelectedItemsIds.get(position) ? selectedColor : Color.TRANSPARENT);
		
		return convertView;
	}
	
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }
 
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
 
    public void selectView(int position, boolean value) {
        if (value){
        	
            mSelectedItemsIds.put(position, value);
        }else{
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }
 
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }
 
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}