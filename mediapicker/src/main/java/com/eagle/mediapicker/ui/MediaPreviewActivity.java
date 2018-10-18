package com.eagle.mediapicker.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eagle.mediapicker.MediaPicker;
import com.eagle.mediapicker.R;
import com.eagle.mediapicker.Utils;
import com.eagle.mediapicker.adapter.MediaPageAdapter;
import com.eagle.mediapicker.bean.MediaItem;
import com.eagle.mediapicker.view.SuperCheckBox;
import com.eagle.mediapicker.view.ViewPagerFixed;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;

public class MediaPreviewActivity extends MediaBaseActivity implements MediaPicker.OnMediaSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String ISORIGIN = "isOrigin"; // "原文件大小"是否被选中

    private boolean isOrigin;    //是否选中原图
    private MediaPicker mediaPicker;
    private ArrayList<MediaItem> mMediaItems;      //跳转进MediaPreviewFragment的图片文件夹
    private int mCurrentPosition = 0;         //跳转进MediaPreviewFragment时的序号，第几个图片
    private TextView mTitleCount;             //显示当前媒体文件的位置  例如  5/31
    private SuperCheckBox mCbCheck;           //是否选中当前媒体文件的CheckBox
    private SuperCheckBox mCbOrigin;          //原图
    private TextView mBtnOk;                    //确认媒体文件的选择
    private ArrayList<MediaItem> selectedMedias;   //所有已经选中的媒体文件
    private View content;
    private View topBar;
    private View bottomBar;

    // 视频相关
    StandardGSYVideoPlayer gsyVideoPlayer = null;
    OrientationUtils orientationUtils = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_preview);

        isOrigin = getIntent().getBooleanExtra(MediaPreviewActivity.ISORIGIN, false);
        mCurrentPosition = getIntent().getIntExtra(MediaPicker.EXTRA_SELECTED_MEDIA_POSITION, 0);
        mMediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra(MediaPicker.EXTRA_MEDIA_ITEMS);
        mediaPicker = MediaPicker.getInstance();
        mediaPicker.addOnMediaSelectedListener(this);
        selectedMedias = mediaPicker.getSelectedMedias();

        //初始化控件
        content = findViewById(R.id.content);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.top_bar);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
        params.topMargin = Utils.getStatusHeight(this);
        topBar.setLayoutParams(params);

        bottomBar = findViewById(R.id.bottom_bar);
        findViewById(R.id.btn_back).setOnClickListener(this);
        mTitleCount = (TextView) findViewById(R.id.tv_pic);
        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mCbCheck = (SuperCheckBox) findViewById(R.id.cb_check);
        mCbOrigin = (SuperCheckBox) findViewById(R.id.cb_origin);
        mCbOrigin.setText(getString(R.string.origin));
        mCbOrigin.setOnCheckedChangeListener(this);
        mCbOrigin.setChecked(isOrigin);

        ViewPagerFixed viewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        MediaPageAdapter adapter = new MediaPageAdapter(this, mMediaItems, gsyVideoPlayer, orientationUtils);
        adapter.setMediaViewClickListener(new MediaPageAdapter.MediaViewClickListener() {
            @Override
            public void OnMediaViewClickListener(View view, float v, float v1) {
                onMediaSingleTap();
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(mCurrentPosition, false);

        //初始化当前页面的状态
        onMediaSelected(0, null, false);
        MediaItem item = mMediaItems.get(mCurrentPosition);
        boolean isSelected = mediaPicker.isSelect(item);
        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mMediaItems.size()));
        mCbCheck.setChecked(isSelected);
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                MediaItem item = mMediaItems.get(mCurrentPosition);
                boolean isSelected = mediaPicker.isSelect(item);
                mCbCheck.setChecked(isSelected);
                mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mMediaItems.size()));
            }
        });
        //当点击当前选中按钮的时候，需要根据当前的选中状态添加和移除图片
        mCbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaItem mediaItem = mMediaItems.get(mCurrentPosition);
                int selectLimit = mediaPicker.getSelectLimit();
                if (mCbCheck.isChecked() && selectedMedias.size() >= selectLimit) {
                    Toast.makeText(MediaPreviewActivity.this, MediaPreviewActivity.this.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                    mCbCheck.setChecked(false);
                } else {
                    mediaPicker.addSelectedMediaItem(mCurrentPosition, mediaItem, mCbCheck.isChecked());
                }
            }
        });
    }

    /**
     * 图片添加成功后，修改当前图片的选中数量
     * 当调用 addSelectedMediaItem 或 removeSelectedMediaItem 都会触发当前回调
     */
    @Override
    public void onMediaSelected(int position, MediaItem item, boolean isAdd) {
        if (mediaPicker.getSelectMediaCount() > 0) {
            mBtnOk.setText(getString(R.string.select_complete, mediaPicker.getSelectMediaCount(), mediaPicker.getSelectLimit()));
            mBtnOk.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.complete));
            mBtnOk.setEnabled(false);
        }

        if (mCbOrigin.isChecked()) {
            long size = 0;
            for (MediaItem mediaItem : selectedMedias)
                size += mediaItem.size;
            String fileSize = Formatter.formatFileSize(this, size);
            mCbOrigin.setText(getString(R.string.origin_size, fileSize));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(MediaPicker.EXTRA_RESULT_ITEMS, mediaPicker.getSelectedMedias());
            setResult(MediaPicker.RESULT_CODE_ITEMS, intent);
            finish();
        } else if (id == R.id.btn_back) {
            Intent intent = new Intent();
            intent.putExtra(MediaPreviewActivity.ISORIGIN, isOrigin);
            setResult(MediaPicker.RESULT_CODE_BACK, intent);
            finish();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_origin) {
            if (isChecked) {
                long size = 0;
                for (MediaItem item : selectedMedias)
                    size += item.size;
                String fileSize = Formatter.formatFileSize(this, size);
                isOrigin = true;
                mCbOrigin.setText(getString(R.string.origin_size, fileSize));
            } else {
                isOrigin = false;
                mCbOrigin.setText(getString(R.string.origin));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gsyVideoPlayer!=null) gsyVideoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gsyVideoPlayer!=null)gsyVideoPlayer.onVideoResume();
    }

    @Override
    public void onBackPressed() {
        if (gsyVideoPlayer!=null && orientationUtils!=null) {
            //先返回正常状态
            if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                gsyVideoPlayer.getFullscreenButton().performClick();
//                return; // mark 不知为何要return
            }
            //释放所有
            gsyVideoPlayer.setVideoAllCallBack(null);
        }

        Intent intent = new Intent();
        intent.putExtra(MediaPreviewActivity.ISORIGIN, isOrigin);
        setResult(MediaPicker.RESULT_CODE_BACK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mediaPicker.removeOnMediaSelectedListener(this);

        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
        super.onDestroy();
    }

    /** 单击时，隐藏头和尾 */
    public void onMediaSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
            tintManager.setStatusBarTintResource(R.color.transparent);//通知栏所需颜色
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
            if (Build.VERSION.SDK_INT >= 16)
                content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
            tintManager.setStatusBarTintResource(R.color.status_bar);//通知栏所需颜色
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
            if (Build.VERSION.SDK_INT >= 16)
                content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
