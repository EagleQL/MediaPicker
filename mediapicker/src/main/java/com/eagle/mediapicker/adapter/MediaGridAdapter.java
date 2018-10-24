package com.eagle.mediapicker.adapter;

import android.app.Activity;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.eagle.mediapicker.MediaDataSource;
import com.eagle.mediapicker.MediaPicker;
import com.eagle.mediapicker.R;
import com.eagle.mediapicker.Utils;
import com.eagle.mediapicker.bean.MediaItem;
import com.eagle.mediapicker.utils.VideoTools;
import com.eagle.mediapicker.view.SuperCheckBox;

import java.util.ArrayList;

/**
 * @Description  媒体文件网格列表界面适配器
 * @Author  eagle
 * @Date  21:13 2018/10/18
 * @Param
 * @return
 **/
public class MediaGridAdapter extends BaseAdapter {

    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机

    private int mediaType = MediaDataSource.PIC;
    private MediaPicker mediaPicker;
    private Activity mActivity;
    private ArrayList<MediaItem> medias;       //当前需要显示的所有的媒体数据
    private ArrayList<MediaItem> mSelectedImages; //全局保存的已经选中的媒体数据
    private boolean isShowCamera;         //是否显示拍照按钮
    private int mMediaSize;               //每个条目的大小
    private OnMediaItemClickListener listener;   //媒体文件被点击的监听
    private OnMediaRenderListener onMediaRenderListener;

    public MediaGridAdapter(Activity activity, ArrayList<MediaItem> medias, int mediaType) {
        this.mActivity = activity;
        this.mediaType = mediaType;
        if (medias == null || medias.size() == 0) this.medias = new ArrayList<>();
        else this.medias = medias;


        mMediaSize = Utils.getImageItemWidth(mActivity);
        mediaPicker = MediaPicker.getInstance();
        isShowCamera = mediaPicker.isShowCamera();
        mSelectedImages = mediaPicker.getSelectedMedias();
    }

    public void refreshData(ArrayList<MediaItem> medias) {
        if (medias == null || medias.size() == 0) this.medias = new ArrayList<>();
        else this.medias = medias;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return isShowCamera ? medias.size() + 1 : medias.size();
    }

    @Override
    public MediaItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) return null;
            return medias.get(position - 1);
        } else {
            return medias.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        final MediaItem mediaItem = getItem(position);
        if (itemViewType == ITEM_TYPE_CAMERA) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.adapter_camera_item, parent, false);
            TextView cameraText = (TextView) convertView.findViewById(R.id.camera_text);
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mMediaSize)); //让图片是个正方形
            convertView.setTag(null);
            if (mediaType == MediaDataSource.PIC) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPicker.takePicture(mActivity, MediaPicker.REQUEST_CODE_TAKE_PHOTO); // mark 拍摄照片
                    }
                });
            }
            else if (mediaType == MediaDataSource.VIDEO) {
                cameraText.setText("拍摄视频");
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPicker.recordVideo(mActivity, MediaPicker.REQUEST_CODE_RECORD_VIDEO); // mark 拍摄视频
                    }
                });
            }
            else if (mediaType == MediaDataSource.AUDIO) {
                cameraText.setText("录制音频");
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // todo
                    }
                });
            }

        } else {
            final ViewHolder holder;
            int itemListLayout = R.layout.adapter_image_list_item;
            if (convertView == null) {
                switch (this.mediaType) {
                    case MediaDataSource.PIC:
                        itemListLayout = R.layout.adapter_image_list_item;
                        break;
                    case MediaDataSource.VIDEO:
                        itemListLayout = R.layout.adapter_video_list_item;
                        break;
                    case MediaDataSource.AUDIO:
                        itemListLayout = R.layout.adapter_audio_list_item;
                        break;
                }
                convertView = LayoutInflater.from(mActivity).inflate(itemListLayout, parent, false);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mMediaSize)); //让图片是个正方形
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onMediaItemClick(holder.rootView, mediaItem, position);
                }
            });
            holder.cbCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectLimit = mediaPicker.getSelectLimit();
                    if (holder.cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                        Toast.makeText(mActivity, mActivity.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                        holder.cbCheck.setChecked(false);
                        holder.mask.setVisibility(View.GONE);
                    } else {
                        mediaPicker.addSelectedMediaItem(position, mediaItem, holder.cbCheck.isChecked());
                        holder.mask.setVisibility(View.VISIBLE);
                    }
                }
            });
            //根据是否多选，显示或隐藏checkbox
            if (mediaPicker.isMultiMode()) {
                holder.cbCheck.setVisibility(View.VISIBLE);
                boolean checked = mSelectedImages.contains(mediaItem);
                if (checked) {
                    holder.mask.setVisibility(View.VISIBLE);
                    holder.cbCheck.setChecked(true);
                } else {
                    holder.mask.setVisibility(View.GONE);
                    holder.cbCheck.setChecked(false);
                }
            } else {
                holder.cbCheck.setVisibility(View.GONE);
            }
            if (this.mediaType == MediaDataSource.VIDEO) {
                byte[] videoPhotoBytes = VideoTools.getVideoPhotoBytes(mediaItem.path, MediaStore.Images.Thumbnails.MINI_KIND);
                Glide.with(mActivity)
                        .load(videoPhotoBytes != null ? videoPhotoBytes : "")
                        .error(R.mipmap.default_image)           //设置错误图片
                        .placeholder(R.mipmap.default_image)     //设置占位图片
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                        .into(holder.ivThumb);
            }
            else {
                mediaPicker.getImageLoader().displayImage(mActivity, mediaItem.path, holder.ivThumb, mMediaSize, mMediaSize); //显示图片
            }
        }
        if (medias.size() < 10 && position + 1 == medias.size()) onMediaRenderListener.OnMediaRenderFinished(position + 1);
        if (medias.size() > 10 && position + 1 == 9) onMediaRenderListener.OnMediaRenderFinished(position + 1);
        return convertView;
    }

    private class ViewHolder {
        public View rootView;
        public ImageView ivThumb;
        public View mask;
        public SuperCheckBox cbCheck;

        public ViewHolder(View view) {
            rootView = view;
            ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
            mask = view.findViewById(R.id.mask);
            cbCheck = (SuperCheckBox) view.findViewById(R.id.cb_check);
        }
    }

    public void setOnMediaItemClickListener(OnMediaItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnMediaItemClickListener {
        void onMediaItemClick(View view, MediaItem mediaItem, int position);
    }

    public interface OnMediaRenderListener {
        void OnMediaRenderFinished(int count);
    }

    public void setOnMediaRenderListener(OnMediaRenderListener onMediaRenderListener){
        this.onMediaRenderListener = onMediaRenderListener;
    }
}