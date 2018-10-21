package com.eagle.mediapicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;

import com.eagle.mediapicker.bean.MediaFolder;
import com.eagle.mediapicker.bean.MediaItem;
import com.eagle.mediapicker.loader.GlideImageLoader;
import com.eagle.mediapicker.loader.ImageLoader;
import com.eagle.mediapicker.view.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MediaPicker {

    public static final String TAG = MediaPicker.class.getSimpleName();
    public static final int REQUEST_CODE_TAKE_PHOTO = 1001;
    public static final int REQUEST_CODE_RECORD_VIDEO = 12001;
    public static final int REQUEST_CODE_CROP = 1002;
    public static final int REQUEST_CODE_PREVIEW = 1003;
    public static final int RESULT_CODE_ITEMS = 1004;
    public static final int RESULT_CODE_BACK = 1005;

    public static final String EXTRA_RESULT_ITEMS = "extra_result_items";
    public static final String EXTRA_SELECTED_MEDIA_POSITION = "selected_media_position";
    public static final String EXTRA_MEDIA_ITEMS = "extra_media_items";

    private boolean multiMode = true;    //媒体文件选择模式
    private int selectLimit = 9;         //最大选择媒体文件数量
    private boolean crop = true;         //裁剪
    private boolean showCamera = true;   //显示相机
    private boolean isSaveRectangle = false;  //裁剪后的图片是否是矩形，否者跟随裁剪框的形状
    private int outPutX = 800;           //裁剪保存宽度
    private int outPutY = 800;           //裁剪保存高度
    private int focusWidth = 280;         //焦点框的宽度
    private int focusHeight = 280;        //焦点框的高度
    private ImageLoader imageLoader;     //图片加载器
    private CropImageView.Style style = CropImageView.Style.RECTANGLE; //裁剪框的形状
    private File cropCacheFolder;
    private File takeMediaFile;
    public Bitmap cropBitmap;

    private ArrayList<MediaItem> mSelectedMedias = new ArrayList<>();   //选中的集合
    private List<MediaFolder> mMediaFolders;      //所有的媒体文件文件夹
    private int mCurrentMediaFolderPosition = 0;  //当前选中的文件夹位置 0表示所有图片
    private List<OnMediaSelectedListener> mMediaSelectedListeners;          // 媒体选中的监听回调

    private static MediaPicker mInstance;

    private MediaPicker() {
    }

    public static MediaPicker getInstance() {
        if (mInstance == null) {
            synchronized (MediaPicker.class) {
                if (mInstance == null) {
                    mInstance = new MediaPicker();
                }
            }
        }
        return mInstance;
    }

    public class Builder {

        /**
         * 返回MediaPicker单例
         * @return
         */
        public MediaPicker build() {
            return mInstance;
        }

        /**
         * 默认为显示拍摄按钮
         * @param 是否显示拍摄按钮
         * @return
         */
        public Builder showCamera(boolean 是否显示拍摄按钮) {
            mInstance.setShowCamera(是否显示拍摄按钮);
            return this;
        }

        /**
         * 必须指定
         * @param 图片加载器
         * @return
         */
        public Builder imageloader(ImageLoader 图片加载器) {
            mInstance.setImageLoader(new GlideImageLoader());
            return this;
        }

        /**
         * 默认为多选
         * @param 多选模式
         * @return
         */
        public Builder multiMode(boolean 多选模式) {
            mInstance.setMultiMode(多选模式);
            return this;
        }

        /**
         * 默认为最多选择9个媒体文件
         * @param 选择数量上限
         * @return
         */
        public Builder selectLimit(int 选择数量上限) {
            mInstance.setSelectLimit(选择数量上限);
            return this;
        }

        /**
         * 默认为true
         * @param 是否剪裁图片
         * @return
         */
        public Builder crop(boolean 是否剪裁图片) {
            mInstance.setCrop(是否剪裁图片);
            return this;
        }

        /**
         * 仅裁剪模式下有效。默认为false--以剪裁框形状保存图片。如传入true，则无论何种情况都以矩形保存图片。裁剪框默认是矩形
         * @param 是否按矩形区域保存剪裁图片
         * @return
         */
        public Builder saveCropAsRectangle(boolean 是否按矩形区域保存剪裁图片) {
            mInstance.setSaveRectangle(是否按矩形区域保存剪裁图片);
            return this;
        }

        /**
         * 仅裁剪模式下有效。以指定的宽高保存剪裁图片
         * @param 图片保存宽度 默认800
         * @param 图片保存高度 默认800
         * @return
         */
        public Builder outPutScale(int 图片保存宽度, int 图片保存高度) {
            mInstance.setOutPutX(图片保存宽度);
            mInstance.setOutPutY(图片保存高度);
            return this;
        }

        /**
         * 仅裁剪模式下有效。以指定宽高的矩形显示裁剪框。裁剪框默认是矩形
         * @param 矩形剪裁宽度 默认280
         * @param 矩形剪裁高度 默认280
         * @return
         */
        public Builder cropRectangle(int 矩形剪裁宽度, int 矩形剪裁高度) {
            mInstance.setStyle(CropImageView.Style.RECTANGLE);
            Activity activity = new Activity();
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 矩形剪裁宽度, activity.getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 矩形剪裁高度, activity.getResources().getDisplayMetrics());
            mInstance.setFocusWidth(width);
            mInstance.setFocusHeight(height);
            return this;
        }

        /**
         * 仅裁剪模式下有效。以指定半径的圆形显示裁剪框.
         * @param 圆形剪裁半径 无默认值
         * @return
         */
        public Builder cropCircle(float 圆形剪裁半径){
            mInstance.setStyle(CropImageView.Style.CIRCLE);
            Activity activity = new Activity();
            int mradius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 圆形剪裁半径, activity.getResources().getDisplayMetrics());
            mInstance.setFocusWidth(mradius * 2);
            mInstance.setFocusHeight(mradius * 2);
            return this;
        }

    }

    public Builder builder() {
        return new Builder();
    }


    @Deprecated
    public MediaPicker config(ImageLoader 图片加载器, boolean 多选模式, int 选择数量上限, boolean 是否剪裁图片,
                              CropImageView.Style 剪裁形状, float 圆形剪裁半径,
                              int 矩形剪裁宽度, int 矩形剪裁高度, boolean 是否按矩形区域保存剪裁图片,
                              int 图片保存宽度, int 图片保存高度,
                              boolean 是否显示拍摄按钮) {
        mInstance.setImageLoader(new GlideImageLoader());
        mInstance.setMultiMode(多选模式);
        mInstance.setSelectLimit(选择数量上限);
        mInstance.setStyle(剪裁形状);

        mInstance.setCrop(是否剪裁图片);
        mInstance.setSaveRectangle(是否按矩形区域保存剪裁图片);
        Activity activity = new Activity();
        if (剪裁形状.equals(CropImageView.Style.RECTANGLE)) {
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 矩形剪裁宽度, activity.getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 矩形剪裁高度, activity.getResources().getDisplayMetrics());
            mInstance.setFocusWidth(width);
            mInstance.setFocusHeight(height);
        } else if (剪裁形状.equals(CropImageView.Style.CIRCLE)) {
            int mradius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 圆形剪裁半径, activity.getResources().getDisplayMetrics());
            mInstance.setFocusWidth(mradius * 2);
            mInstance.setFocusHeight(mradius * 2);
        }

        mInstance.setOutPutX(图片保存宽度);
        mInstance.setOutPutY(图片保存高度);
        mInstance.setShowCamera(是否显示拍摄按钮);
        return mInstance;
    }

    public boolean isMultiMode() {
        return multiMode;
    }

    public void setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
    }

    public int getSelectLimit() {
        return selectLimit;
    }

    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }

    public boolean isCrop() {
        return crop;
    }

    public void setCrop(boolean crop) {
        this.crop = crop;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public boolean isSaveRectangle() {
        return isSaveRectangle;
    }

    public void setSaveRectangle(boolean isSaveRectangle) {
        this.isSaveRectangle = isSaveRectangle;
    }

    public int getOutPutX() {
        return outPutX;
    }

    public void setOutPutX(int outPutX) {
        this.outPutX = outPutX;
    }

    public int getOutPutY() {
        return outPutY;
    }

    public void setOutPutY(int outPutY) {
        this.outPutY = outPutY;
    }

    public int getFocusWidth() {
        return focusWidth;
    }

    public void setFocusWidth(int focusWidth) {
        this.focusWidth = focusWidth;
    }

    public int getFocusHeight() {
        return focusHeight;
    }

    public void setFocusHeight(int focusHeight) {
        this.focusHeight = focusHeight;
    }

    public File getTakeMediaFile() {
        return takeMediaFile;
    }

    public File getCropCacheFolder(Context context) {
        if (cropCacheFolder == null) {
            cropCacheFolder = new File(context.getCacheDir() + "/ImagePicker/cropTemp/");
        }
        return cropCacheFolder;
    }

    public void setCropCacheFolder(File cropCacheFolder) {
        this.cropCacheFolder = cropCacheFolder;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public CropImageView.Style getStyle() {
        return style;
    }

    public void setStyle(CropImageView.Style style) {
        this.style = style;
    }

    public List<MediaFolder> getMediaFolders() {
        return mMediaFolders;
    }

    public void setMediaFolders(List<MediaFolder> mediaFolders) {
        mMediaFolders = mediaFolders;
    }

    public int getCurrentMediaFolderPosition() {
        return mCurrentMediaFolderPosition;
    }

    public void setCurrentMediaFolderPosition(int mCurrentSelectedMediaSetPosition) {
        mCurrentMediaFolderPosition = mCurrentSelectedMediaSetPosition;
    }

    public ArrayList<MediaItem> getCurrentMediaFolderItems() {
        return mMediaFolders.get(mCurrentMediaFolderPosition).medias;
    }

    public boolean isSelect(MediaItem item) {
        return mSelectedMedias.contains(item);
    }

    public int getSelectMediaCount() {
        if (mSelectedMedias == null) {
            return 0;
        }
        return mSelectedMedias.size();
    }

    public ArrayList<MediaItem> getSelectedMedias() {
        return mSelectedMedias;
    }

    public void clearSelectedMedias() {
        if (mSelectedMedias != null) mSelectedMedias.clear();
    }

    public void clear() {
        if (mMediaSelectedListeners != null) {
            mMediaSelectedListeners.clear();
            mMediaSelectedListeners = null;
        }
        if (mMediaFolders != null) {
            mMediaFolders.clear();
            mMediaFolders = null;
        }
        if (mSelectedMedias != null) {
            mSelectedMedias.clear();
        }
        mCurrentMediaFolderPosition = 0;
    }

    /**
     * 拍照的方法
     */
    public void takePicture(Activity activity, int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (Utils.existSDCard())
                takeMediaFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
            else takeMediaFile = Environment.getDataDirectory();
            takeMediaFile = createFile(takeMediaFile, "IMG_", ".jpg");
            if (takeMediaFile != null) {
                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
                // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
                // 如果没有指定uri，则data就返回有数据！
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takeMediaFile));
            }
        }
        activity.startActivityForResult(takePictureIntent, requestCode);
    }

    /**
     * @return
     * @Description 视频拍摄
     * @Author eagle
     * @Date 00:42 2018/10/19
     * @Param
     **/
    public void recordVideo(Activity activity, int requestCode) {
        Intent recordVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        recordVideoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (recordVideoIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (Utils.existSDCard())
                takeMediaFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
            else takeMediaFile = Environment.getDataDirectory();
            takeMediaFile = createFile(takeMediaFile, "VID_", ".mp4");
            if (takeMediaFile != null) {
                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
                // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
                // 如果没有指定uri，则data就返回有数据！
                recordVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takeMediaFile));
            }
        }
        activity.startActivityForResult(recordVideoIntent, requestCode);
    }

    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    /**
     * 扫描媒体文件
     */
    public static void galleryAddMedia(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 图片选中的监听
     */
    public interface OnMediaSelectedListener {
        void onMediaSelected(int position, MediaItem item, boolean isAdd);
    }

    public void addOnMediaSelectedListener(OnMediaSelectedListener l) {
        if (mMediaSelectedListeners == null) mMediaSelectedListeners = new ArrayList<>();
        mMediaSelectedListeners.add(l);
    }

    public void removeOnMediaSelectedListener(OnMediaSelectedListener l) {
        if (mMediaSelectedListeners == null) return;
        mMediaSelectedListeners.remove(l);
    }

    public void addSelectedMediaItem(int position, MediaItem item, boolean isAdd) {
        if (isAdd) mSelectedMedias.add(item);
        else mSelectedMedias.remove(item);
        notifyMediaSelectedChanged(position, item, isAdd);
    }

    private void notifyMediaSelectedChanged(int position, MediaItem item, boolean isAdd) {
        if (mMediaSelectedListeners == null) return;
        for (OnMediaSelectedListener l : mMediaSelectedListeners) {
            l.onMediaSelected(position, item, isAdd);
        }
    }
}