package com.eagle.mediapicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eagle.mediapicker.MediaPicker;
import com.eagle.mediapicker.R;
import com.eagle.mediapicker.Utils;
import com.eagle.mediapicker.bean.MediaFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/4
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MediaFolderAdapter extends BaseAdapter {

    private MediaPicker mediaPicker;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private int mImageSize;
    private List<MediaFolder> mediaFolders;
    private int lastSelected = 0;

    public MediaFolderAdapter(Activity activity, List<MediaFolder> folders) {
        mActivity = activity;
        if (folders != null && folders.size() > 0) mediaFolders = folders;
        else mediaFolders = new ArrayList<>();

        mediaPicker = MediaPicker.getInstance();
        mImageSize = Utils.getImageItemWidth(mActivity);
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshData(List<MediaFolder> folders) {
        if (folders != null && folders.size() > 0) mediaFolders = folders;
        else mediaFolders.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mediaFolders.size();
    }

    @Override
    public MediaFolder getItem(int position) {
        return mediaFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getItemViewHeight() {
        View itemView = mInflater.inflate(R.layout.adapter_folder_list_item, null);
        itemView.measure(0, 0);
        return itemView.getMeasuredHeight();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_folder_list_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MediaFolder folder = getItem(position);
        holder.folderName.setText(folder.name);
        holder.imageCount.setText(mActivity.getString(R.string.folder_image_count, folder.medias.size()));
        // fixme 此处如果是视频或音频，则需要应用另外的图片加载机制
        mediaPicker.getImageLoader().displayImage(mActivity, folder.cover.path != null ? folder.cover.path : "", holder.cover, mImageSize, mImageSize);

        if (lastSelected == position) {
            holder.folderCheck.setVisibility(View.VISIBLE);
        } else {
            holder.folderCheck.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i) {
            return;
        }
        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    private class ViewHolder {
        ImageView cover;
        TextView folderName;
        TextView imageCount;
        ImageView folderCheck;

        public ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.iv_cover);
            folderName = (TextView) view.findViewById(R.id.tv_folder_name);
            imageCount = (TextView) view.findViewById(R.id.tv_image_count);
            folderCheck = (ImageView) view.findViewById(R.id.iv_folder_check);
            view.setTag(this);
        }
    }
}
