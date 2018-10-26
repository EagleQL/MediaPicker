package com.eagle.mediapicker.adapter;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.eagle.mediapicker.MediaPicker;
import com.eagle.mediapicker.R;
import com.eagle.mediapicker.bean.MediaItem;
import com.eagle.mediapicker.Utils;
import com.eagle.mediapicker.utils.VideoTools;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
//import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.shuyu.gsyvideoplayer.GSYVideoBaseManager.TAG;

public class MediaPageAdapter extends PagerAdapter {

    private int screenWidth;
    private int screenHeight;
    private MediaPicker mediaPicker;
    private ArrayList<MediaItem> medias = new ArrayList<>();
    private Activity mActivity;
    public MediaViewClickListener listener;

    StandardGSYVideoPlayer gsyVideoPlayer;
    OrientationUtils orientationUtils;

    public MediaPageAdapter(Activity activity, ArrayList<MediaItem> medias, StandardGSYVideoPlayer gsyVideoPlayer, OrientationUtils orientationUtils) {
        this.mActivity = activity;
        this.medias = medias;
        this.gsyVideoPlayer = gsyVideoPlayer;
        this.orientationUtils = orientationUtils;

        DisplayMetrics dm = Utils.getScreenPix(activity);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        mediaPicker = MediaPicker.getInstance();
    }

    public void setMediaViewClickListener(MediaViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (gsyVideoPlayer != null) resetVideoPlayer();
        final MediaItem mediaItem = medias.get(position);
        if (mediaItem.isPic()) {
            PhotoView photoView = new PhotoView(mActivity);
            mediaPicker.getImageLoader().displayImage(mActivity, mediaItem.path, photoView, screenWidth, screenHeight);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (listener != null) listener.OnMediaViewClickListener(view, x, y);
                }
            });
            container.addView(photoView);
            return photoView;
        } else if (mediaItem.isVideo()) {
            /*JCVideoPlayerStandard jcVideoPlayerStandard = new JCVideoPlayerStandard(mActivity);
            jcVideoPlayerStandard.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, screenHeight));
            jcVideoPlayerStandard.setUp("http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4", JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "TOOOT");
            jcVideoPlayerStandard.thumbImageView.setImageBitmap(VideoTools.getVideoPhoto(mediaItem.path, MediaStore.Images.Thumbnails.MINI_KIND));
            container.addView(jcVideoPlayerStandard);
            return jcVideoPlayerStandard;*/

            gsyVideoPlayer = new StandardGSYVideoPlayer(mActivity);
            gsyVideoPlayer.setUpLazy(mediaItem.path, true, null, null, "这是title");
            //增加封面
            ImageView imageView = new ImageView(mActivity);
            imageView.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(mActivity)
                    .load(VideoTools.getVideoPhotoBytes(mediaItem.path, MediaStore.Images.Thumbnails.MINI_KIND))
                    .into(imageView);
            gsyVideoPlayer.setThumbImageView(imageView);
            gsyVideoPlayer.getThumbImageViewLayout().setVisibility(View.VISIBLE);
            //设置title
            gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);
            //设置返回键
            gsyVideoPlayer.getBackButton().setVisibility(View.GONE);
            //设置全屏按键功能
            gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gsyVideoPlayer.startWindowFullscreen(mActivity, false, true);
                }
            });
            //设置返回按键功能
            gsyVideoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.onBackPressed();
                }
            });
            //防止错位设置
            gsyVideoPlayer.setPlayTag(TAG);
            gsyVideoPlayer.setPlayPosition(position);
            //是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏
            gsyVideoPlayer.setAutoFullWithSize(true);
            //音频焦点冲突时是否释放
            gsyVideoPlayer.setReleaseWhenLossAudio(false);
            //全屏动画
            gsyVideoPlayer.setShowFullAnimation(true);
            //小屏时不触摸滑动
            gsyVideoPlayer.setIsTouchWiget(false);
            //设置旋转
            orientationUtils = new OrientationUtils(mActivity, gsyVideoPlayer);

            container.addView(gsyVideoPlayer);
            return gsyVideoPlayer;

        } else if (mediaItem.isAudio()) {
            MediaPlayer mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(mActivity, Uri.fromFile(new File(mediaItem.path)));
            } catch (IOException e) {
                Toast.makeText(mActivity, "播放出错", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            mMediaPlayer.prepareAsync();
            mMediaPlayer.start();
            TextView textView = new TextView(mActivity);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setText(mediaItem.name);
            return textView;
        } else {
            ImageView imageView2 = new ImageView(mActivity);
            Glide.with(mActivity)
                    .load(mediaItem.path)
                    .into(imageView2);
            return imageView2;
        }
    }

    private void resetVideoPlayer() {
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();

    }

    @Override
    public int getCount() {
        return medias.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();

        container.removeView((View) object);
        Log.d("MediaPageAdapter", "destroyItem: ");
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public interface MediaViewClickListener {
        /**
         * @Description  for photoview组件
         * @Author  eagle
         * @Date  21:49 2018/10/17
         * @Param
         * @return
         **/
        void OnMediaViewClickListener(View view, float v, float v1);
    }


}
