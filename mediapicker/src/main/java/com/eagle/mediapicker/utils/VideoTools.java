package com.eagle.mediapicker.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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

//    public List<EntityVideo> getList(Context context) {
//        List<EntityVideo> sysVideoList = new ArrayList<>();
//        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
//        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
//                MediaStore.Video.Thumbnails.VIDEO_ID};
//        // 视频其他信息的查询条件
//        String[] mediaColumns = {MediaStore.Video.Media._ID,
//                MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION};
//
//        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media
//                        .EXTERNAL_CONTENT_URI,
//                mediaColumns, null, null, null);
//
//        if (cursor == null) {
//            return sysVideoList;
//        }
//        if (cursor.moveToFirst()) {
//            do {
//                EntityVideo info = new EntityVideo();
//                int id = cursor.getInt(cursor
//                        .getColumnIndex(MediaStore.Video.Media._ID));
//                Cursor thumbCursor = context.getContentResolver().query(
//                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
//                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
//                                + "=" + id, null, null);
//                if (thumbCursor.moveToFirst()) {
//                    info.setThumbPath(thumbCursor.getString(thumbCursor
//                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
//                }
//                info.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media
//                        .DATA)));
//                info.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video
//                        .Media.DURATION)));
//                sysVideoList.add(info);
//            } while (cursor.moveToNext());
//        }
//        return sysVideoList;
//    }

    public Bitmap getVideoThumbnail(String videoPath,int width,int height,int kind) {
        Bitmap bitmap =null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


}
