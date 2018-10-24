package com.eagle.mediapicker.utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

/**
 * @Description 获取视频缩略图及其它信息
 * @Author eagle
 * @Date 22:27 2018/10/16
 * @Param 
 * @return 
 **/
public  class VideoTools {
    /**
     * 根据路径得到视频缩略图
     * @param videoPath
     * @param kind
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static Bitmap getVideoPhoto(String videoPath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        try {
            if (videoPath.startsWith("http://")
                    || videoPath.startsWith("https://")
                    || videoPath.startsWith("widevine://")) {
                media.setDataSource(videoPath,new Hashtable<String, String>());
            }else {
                media.setDataSource(videoPath);
            }
            bitmap = media.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
            ex.printStackTrace();
        } finally {
            try {
                media.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
                ex.printStackTrace();
            }
        }

        if (bitmap == null) return null;

        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    96,
                    96,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        media.release();
        return bitmap;
    }

    public static byte[] getVideoPhotoBytes(String videoPath, int kind){
        if (videoPath == null || videoPath.trim().isEmpty()) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (VideoTools.getVideoPhoto(videoPath, MediaStore.Images.Thumbnails.MINI_KIND) == null) return null;
        VideoTools.getVideoPhoto(videoPath, MediaStore.Images.Thumbnails.MINI_KIND).compress(Bitmap.CompressFormat.JPEG, 96, baos);
        return baos.toByteArray();

    }

    //获取视频总时长
    public static int getVideoDuration(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        String duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        media.release();
        return Integer.parseInt(duration);

    }

}
