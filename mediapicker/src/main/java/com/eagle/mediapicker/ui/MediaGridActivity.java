package com.eagle.mediapicker.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.eagle.mediapicker.MediaDataSource;
import com.eagle.mediapicker.MediaPicker;
import com.eagle.mediapicker.R;
import com.eagle.mediapicker.Utils;
import com.eagle.mediapicker.adapter.MediaFolderAdapter;
import com.eagle.mediapicker.adapter.MediaGridAdapter;
import com.eagle.mediapicker.bean.MediaFolder;
import com.eagle.mediapicker.bean.MediaItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaGridActivity extends MediaBaseActivity implements MediaDataSource.OnMediasLoadedListener, MediaGridAdapter.OnMediaItemClickListener, MediaPicker.OnMediaSelectedListener, View.OnClickListener {

    private MediaPicker mediaPicker;

    private int mediaType = MediaDataSource.PIC;

    private boolean isOrigin = false;  //是否选中原图
    private int screenWidth;     //屏幕的宽
    private int screenHeight;    //屏幕的高
    private GridView mGridView;  //媒体文件展示控件
    private View mTopBar;        //顶部栏
    private View mFooterBar;     //底部栏
    private TextView mBtnOk;       //确定按钮
    private Button mBtnDir;      //文件夹切换按钮
    private Button mBtnPre;      //预览按钮
    private ListPopupWindow mFolderPopupWindow;  //ImageSet的PopupWindow

    public LinearLayout mProgressBarLayout;
    private Map<String, Object[]> mMediaDataMap = new HashMap(); //媒体文件九宫格展示的适配器
    private Map<String, List<MediaFolder>> mMediaFoldesMap = new HashMap(); //媒体文件夹的适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_grid);

        mediaPicker = MediaPicker.getInstance();
        mediaPicker.clear();
        mediaPicker.addOnMediaSelectedListener(this);
        DisplayMetrics dm = Utils.getScreenPix(this);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnDir = (Button) findViewById(R.id.btn_dir);
        mBtnDir.setOnClickListener(this);
        mBtnPre = (Button) findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridview);
        mTopBar = findViewById(R.id.top_bar);

        TextView tv_pic = (TextView) findViewById(R.id.tv_pic);
        tv_pic.setOnClickListener(this);
        TextView tv_video = (TextView) findViewById(R.id.tv_video);
        tv_video.setOnClickListener(this);
        TextView tv_audio = (TextView) findViewById(R.id.tv_audio);
        tv_audio.setOnClickListener(this);


        mFooterBar = findViewById(R.id.footer_bar);
        if (mediaPicker.isMultiMode()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else {
            mBtnOk.setVisibility(View.GONE);
            mBtnPre.setVisibility(View.GONE);
        }

        mProgressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);

        int[] mode = getIntent().getExtras().getIntArray("mode");

        if (mode == null || mode.length < 1) {
            switchMediaGrid(mediaType);
            tv_video.setVisibility(View.GONE);
            tv_audio.setVisibility(View.GONE);
        }
        else {
            switchMediaGrid(mode[0]);
            tv_pic.setVisibility(View.GONE);
            tv_video.setVisibility(View.GONE);
            tv_audio.setVisibility(View.GONE);
            for (int i : mode) {
                switch (i){
                    case MediaDataSource.PIC:
                        tv_pic.setVisibility(View.VISIBLE);
                        break;
                    case MediaDataSource.VIDEO:
                        tv_video.setVisibility(View.VISIBLE);
                        break;
                    case MediaDataSource.AUDIO:
                        tv_audio.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }

    }

    private void switchMediaGrid(int mediaType) {// fixme 从预览界面切回时，该方法被多次调用，而且每次传入参数不一样
        mProgressBarLayout.setVisibility(View.VISIBLE);
        highlightMenu(mediaType);
        loadMediaData(mediaType);
    }

    private void highlightMenu(int mediaType) {
        ((TextView) findViewById(R.id.tv_pic)).setTextColor(Color.parseColor("white"));
        ((TextView) findViewById(R.id.tv_video)).setTextColor(Color.parseColor("white"));
        ((TextView) findViewById(R.id.tv_audio)).setTextColor(Color.parseColor("white"));
        switch (mediaType) {
            case MediaDataSource.PIC:
                ((TextView) findViewById(R.id.tv_pic)).setTextColor(Color.parseColor("green"));
                break;
            case MediaDataSource.VIDEO:
                ((TextView) findViewById(R.id.tv_video)).setTextColor(Color.parseColor("green"));
                break;
            case MediaDataSource.AUDIO:
                ((TextView) findViewById(R.id.tv_audio)).setTextColor(Color.parseColor("green"));
                break;
        }
    }

    private void loadMediaData(int mediaType) {
        if (!this.mMediaDataMap.containsKey(mediaType + "")) {
            MediaGridAdapter mediaGridAdapter;
            MediaFolderAdapter mediaFolderAdapter;
            mediaGridAdapter = new MediaGridAdapter(this, null, mediaType);
            mediaGridAdapter.setOnMediaRenderListener(new MediaGridAdapter.OnMediaRenderListener() {
                @Override
                public void OnMediaRenderFinished(int count) {
                    mProgressBarLayout.setVisibility(View.GONE);
                }
            });
            mediaFolderAdapter = new MediaFolderAdapter(this, null);
            new MediaDataSource(this, null, this, mediaType);

            mMediaDataMap.put(mediaType + "", new Object[]{mediaGridAdapter, mediaFolderAdapter});

            onMediaSelected(0, null, false);
        } else {
            mProgressBarLayout.setVisibility(View.VISIBLE);
            mediaPicker.setMediaFolders(getMediaFolders(mediaType));
            mGridView.setAdapter(getMediaGridAdapter(mediaType));
            if (getMediaFolders(mediaType).size() == 0)
                getMediaGridAdapter(mediaType).refreshData(null);
            else
                getMediaGridAdapter(mediaType).refreshData(getMediaFolders(mediaType).get(0).medias);
            getMediaGridAdapter(mediaType).setOnMediaItemClickListener(this);
            getMediaFolderAdapter(mediaType).refreshData(getMediaFolders(mediaType));
        }
    }

    private List<MediaFolder> getMediaFolders(int mediaType) {
        return this.mMediaFoldesMap.get(mediaType + "");
    }

    private MediaGridAdapter getMediaGridAdapter(int mediaType) {
        return (MediaGridAdapter) this.mMediaDataMap.get(mediaType + "")[0];
    }

    private MediaFolderAdapter getMediaFolderAdapter(int mediaType) {
        return (MediaFolderAdapter) this.mMediaDataMap.get(mediaType + "")[1];
    }

    @Override
    protected void onDestroy() {
        mediaPicker.removeOnMediaSelectedListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(MediaPicker.EXTRA_RESULT_ITEMS, mediaPicker.getSelectedMedias());
            setResult(MediaPicker.RESULT_CODE_ITEMS, intent);  //多选不允许裁剪裁剪，返回数据
            finish();
        } else if (id == R.id.btn_dir) {
            if (getMediaFolders(mediaType) == null) {
                Log.i("MediaGridActivity", "您的手机没有图片/视频/音频");
                return;
            }
            //点击文件夹按钮
            if (mFolderPopupWindow == null) createPopupFolderList(screenWidth, screenHeight);
            backgroundAlpha(0.3f);   //改变View的背景透明度
            getMediaFolderAdapter(mediaType).refreshData(getMediaFolders(mediaType));  //刷新数据
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.show();
                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = getMediaFolderAdapter(mediaType).getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.getListView().setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            Intent intent = new Intent(MediaGridActivity.this, MediaPreviewActivity.class);
            intent.putExtra(MediaPicker.EXTRA_SELECTED_MEDIA_POSITION, 0);
            intent.putExtra(MediaPicker.EXTRA_MEDIA_ITEMS, mediaPicker.getSelectedMedias());
            intent.putExtra(MediaPreviewActivity.ISORIGIN, isOrigin);
            startActivityForResult(intent, MediaPicker.REQUEST_CODE_PREVIEW);
        } else if (id == R.id.tv_pic) {
            this.mediaType = MediaDataSource.PIC;
            switchMediaGrid(mediaType);
        } else if (id == R.id.tv_video) {
            this.mediaType = MediaDataSource.VIDEO;
            switchMediaGrid(mediaType);
        } else if (id == R.id.tv_audio) {
            this.mediaType = MediaDataSource.AUDIO;
            switchMediaGrid(mediaType);
        } else if (id == R.id.btn_back) {
            //点击返回按钮
            finish();
        }
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList(int width, int height) {
        mFolderPopupWindow = new ListPopupWindow(this);
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFolderPopupWindow.setAdapter(getMediaFolderAdapter(mediaType));
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);  //如果不设置，就是 AnchorView 的宽度
        int maxHeight = height * 5 / 8;
        int realHeight = getMediaFolderAdapter(mediaType).getItemViewHeight() * getMediaFolderAdapter(mediaType).getCount();
        int popHeight = realHeight > maxHeight ? maxHeight : realHeight;
        mFolderPopupWindow.setHeight(popHeight);
        mFolderPopupWindow.setAnchorView(mFooterBar);  //ListPopupWindow总会相对于这个View
        mFolderPopupWindow.setModal(true);  //是否为模态，影响返回键的处理
        //mFolderPopupWindow.setAnimationStyle(R.style.popupwindow_anim_style);
        mFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                getMediaFolderAdapter(mediaType).setSelectIndex(position);
                mediaPicker.setCurrentMediaFolderPosition(position);
                mFolderPopupWindow.dismiss();
                MediaFolder mediaFolder = (MediaFolder) adapterView.getAdapter().getItem(position);
                if (null != mediaFolder) {
                    getMediaGridAdapter(mediaType).refreshData(mediaFolder.medias);
                    mBtnDir.setText(mediaFolder.name);
                }
                mGridView.smoothScrollToPosition(0);//滑动到顶部
            }
        });
    }


    /**
     * 设置屏幕透明度  0.0透明  1.0不透明
     */
    public void backgroundAlpha(float alpha) {
        mGridView.setAlpha(alpha);
        mTopBar.setAlpha(alpha);
        mFooterBar.setAlpha(1.0f);
    }

    @Override
    public void onMediasLoaded(List<MediaFolder> mediaFolders, int mediaType) {
        this.mediaType = mediaType;
        mMediaFoldesMap.put(mediaType + "", mediaFolders);
        loadMediaData(mediaType);
    }

    @Override
    public void onMediaItemClick(View view, MediaItem mediaItem, int position) {
        //根据是否有相机按钮确定位置
        position = mediaPicker.isShowCamera() ? position - 1 : position;
        if (mediaPicker.isMultiMode()) {
            Intent intent = new Intent(MediaGridActivity.this, MediaPreviewActivity.class);
            intent.putExtra(MediaPicker.EXTRA_SELECTED_MEDIA_POSITION, position);
            intent.putExtra(MediaPicker.EXTRA_MEDIA_ITEMS, mediaPicker.getCurrentMediaFolderItems());
            intent.putExtra(MediaPreviewActivity.ISORIGIN, isOrigin);
            startActivityForResult(intent, MediaPicker.REQUEST_CODE_PREVIEW);  //如果是多选，点击图片进入预览界面
        } else {
            mediaPicker.clearSelectedMedias(); // mark 此处清除了之前的选择
            mediaPicker.addSelectedMediaItem(position, mediaPicker.getCurrentMediaFolderItems().get(position), true);
            if (mediaPicker.isCrop() && this.mediaType == MediaDataSource.PIC) {
                Intent intent = new Intent(MediaGridActivity.this, ImageCropActivity.class);
                startActivityForResult(intent, MediaPicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
            } else {
                Intent intent = new Intent();
                intent.putExtra(MediaPicker.EXTRA_RESULT_ITEMS, mediaPicker.getSelectedMedias());
                setResult(MediaPicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                finish();
            }
        }
    }

    @Override
    public void onMediaSelected(int position, MediaItem item, boolean isAdd) {
        if (mediaPicker.getSelectMediaCount() > 0) {
            mBtnOk.setText(getString(R.string.select_complete, String.valueOf(mediaPicker.getSelectMediaCount()), String.valueOf(mediaPicker.getSelectLimit())));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.complete));
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
        }
        mBtnPre.setText(getResources().getString(R.string.preview_count, String.valueOf(mediaPicker.getSelectMediaCount())));
        getMediaGridAdapter(mediaType).notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == MediaPicker.RESULT_CODE_BACK) {
                isOrigin = data.getBooleanExtra(MediaPreviewActivity.ISORIGIN, false);
                switchMediaGrid(mediaType); // 只有当预览返回时才刷新界面
            }
            // 如果是拍摄视频返回
            else if (resultCode == RESULT_OK && requestCode == MediaPicker.REQUEST_CODE_RECORD_VIDEO) {
                //发送广播通知媒体增加了
                MediaPicker.galleryAddMedia(this, mediaPicker.getTakeMediaFile());
                MediaItem mediaItem = new MediaItem();
                mediaItem.path = mediaPicker.getTakeMediaFile().getAbsolutePath();
                mediaPicker.clearSelectedMedias();
                mediaPicker.addSelectedMediaItem(0, mediaItem, true);

                Intent intent = new Intent();
                intent.putExtra(MediaPicker.EXTRA_RESULT_ITEMS, mediaPicker.getSelectedMedias());
                setResult(MediaPicker.RESULT_CODE_ITEMS, intent);   //返回数据
                finish();
            } else {

                //从拍照界面返回
                //点击 X , 没有选择媒体文件
                if (data.getSerializableExtra(MediaPicker.EXTRA_RESULT_ITEMS) == null) {
                    //什么都不做
                } else {
                    //说明是从裁剪页面过来的数据，直接返回就可以
                    setResult(MediaPicker.RESULT_CODE_ITEMS, data);
                    finish();
                }
            }
        } else {
            //发送广播通知媒体增加了
            MediaPicker.galleryAddMedia(this, mediaPicker.getTakeMediaFile());

            MediaItem mediaItem = new MediaItem();
            mediaItem.path = mediaPicker.getTakeMediaFile().getAbsolutePath();
            mediaPicker.clearSelectedMedias();
            mediaPicker.addSelectedMediaItem(0, mediaItem, true);

            //如果是图片裁剪，因为裁剪指定了存储的Uri，所以返回的data一定为null
            if (resultCode == RESULT_OK && requestCode == MediaPicker.REQUEST_CODE_TAKE_PHOTO) {
                if (mediaPicker.isCrop() && this.mediaType == MediaDataSource.PIC) {
                    Intent intent = new Intent(MediaGridActivity.this, ImageCropActivity.class);
                    startActivityForResult(intent, MediaPicker.REQUEST_CODE_CROP);  //需要裁剪，进入裁剪界面
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(MediaPicker.EXTRA_RESULT_ITEMS, mediaPicker.getSelectedMedias());
                    setResult(MediaPicker.RESULT_CODE_ITEMS, intent);   //不需要裁剪，返回数据
                    finish();
                }
            }
        }

    }

    @Override
    protected void onResume() {
        // fixme 返回界面时数据错乱，问题应该出在这
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}