package com.eagle.mediapicker;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.eagle.mediapicker.bean.MediaFolder;
import com.eagle.mediapicker.bean.MediaItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** 加载手机多媒体数据实现类 */
public class MediaDataSource implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int PIC = 1;
    public static final int VIDEO = 2;
    public static final int AUDIO = 3;

    public int mediaType = 1;

    public static final int LOADER_ALL_PIC = 10;         //加载所有图片
    public static final int LOADER_CATEGORY_PIC = 11;    //分类加载图片

    public static final int LOADER_ALL_VIDEO = 21;         //加载所有视频
    public static final int LOADER_CATEGORY_VIDOE = 22;    //分类加载视频

    public static final int LOADER_ALL_AUDIO = 31;         //加载所有音频
    public static final int LOADER_CATEGORY_AUDIO = 32;    //分类加载音频


    private final String[] MEDIA_PROJECTION = {     //查询媒体文件需要的数据列
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,           // 真实路径
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.MIME_TYPE,      // 媒体类型
            MediaStore.MediaColumns.DATE_ADDED};
    private final String[] AUDIO_PROJECTION = {     //查询音频文件需要的数据列
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,           // 真实路径
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE,      // 媒体类型
            MediaStore.Audio.Media.DATE_ADDED};


    private FragmentActivity activity;
    private OnMediasLoadedListener loadedListener;                     //媒体数据加载完成的回调接口
    private ArrayList<MediaFolder> mediaFolders = new ArrayList<>();   //所有的媒体文件夹

    /**
     * @param activity       用于初始化LoaderManager，需要兼容到2.3
     * @param path           指定扫描的文件夹目录，可以为 null，表示扫描所有对应类型的媒体
     * @param loadedListener 加载完成的监听
     */
    public MediaDataSource(FragmentActivity activity, String path, OnMediasLoadedListener loadedListener, int mediaType) {
        this.activity = activity;
        this.loadedListener = loadedListener;
        this.mediaType = mediaType;

        LoaderManager loaderManager = activity.getSupportLoaderManager();
        if (path == null) {
            switch (mediaType) {
                case PIC:
                    loaderManager.initLoader(LOADER_ALL_PIC, null, this);//加载所有的图片
                    break;
                case VIDEO:
                    loaderManager.initLoader(LOADER_ALL_VIDEO, null, this);//加载所有的视频
                    break;
                case AUDIO:
                    loaderManager.initLoader(LOADER_ALL_AUDIO, null, this);//加载所有的音频
                    break;
                default:
                    break;
            }

        } else {
            //加载指定目录的媒体数据
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            switch (mediaType) {
                case PIC:
                    loaderManager.initLoader(LOADER_CATEGORY_PIC, bundle, this);
                    break;
                case VIDEO:
                    loaderManager.initLoader(LOADER_CATEGORY_VIDOE, bundle, this);
                    break;
                case AUDIO:
                    loaderManager.initLoader(LOADER_CATEGORY_AUDIO, bundle, this);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case LOADER_ALL_PIC:
                cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MEDIA_PROJECTION, null, null, MEDIA_PROJECTION[6] + " DESC");
                break;
            case LOADER_ALL_VIDEO:
                cursorLoader = new CursorLoader(activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MEDIA_PROJECTION, null, null, MEDIA_PROJECTION[6] + " DESC");
                break;
            case LOADER_ALL_AUDIO:
                cursorLoader = new CursorLoader(activity, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, AUDIO_PROJECTION, null, null, AUDIO_PROJECTION[4] + " DESC");
                break;
            case LOADER_CATEGORY_PIC:
                cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MEDIA_PROJECTION, MEDIA_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, MEDIA_PROJECTION[6] + " DESC");
                break;
            case LOADER_CATEGORY_VIDOE:
                cursorLoader = new CursorLoader(activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MEDIA_PROJECTION, MEDIA_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, MEDIA_PROJECTION[6] + " DESC");
                break;
            case LOADER_CATEGORY_AUDIO:
                cursorLoader = new CursorLoader(activity, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, AUDIO_PROJECTION, AUDIO_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, AUDIO_PROJECTION[4] + " DESC");
                break;
            default:
                cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MEDIA_PROJECTION, null, null, MEDIA_PROJECTION[6] + " DESC");
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mediaFolders.clear();
        if (data != null) {
            ArrayList<MediaItem> allMedias = new ArrayList<>();   //所有媒体文件的集合,不分文件夹
            while (data.moveToNext()) {
                //查询数据
                String mediaPath = data.getString(data.getColumnIndexOrThrow(MEDIA_PROJECTION[1]));
                long meidaSize = data.getLong(data.getColumnIndexOrThrow(MEDIA_PROJECTION[2]));

                //封装实体
                MediaItem mediaItem = new MediaItem();

                if (mediaType != AUDIO) {
                    String mediaName = data.getString(data.getColumnIndexOrThrow(MEDIA_PROJECTION[0]));
                    int mediaWidth = data.getInt(data.getColumnIndexOrThrow(MEDIA_PROJECTION[3]));
                    int mediaHeight = data.getInt(data.getColumnIndexOrThrow(MEDIA_PROJECTION[4]));
                    String mediaMimeType = data.getString(data.getColumnIndexOrThrow(MEDIA_PROJECTION[5]));
                    long mediaAddTime = data.getLong(data.getColumnIndexOrThrow(MEDIA_PROJECTION[6]));

                    mediaItem.name = mediaName;
                    mediaItem.width = mediaWidth;
                    mediaItem.height = mediaHeight;
                    mediaItem.mimeType = mediaMimeType;
                    mediaItem.addTime = mediaAddTime;
                }
                else {
                    String mediaName = data.getString(data.getColumnIndexOrThrow(AUDIO_PROJECTION[0]));
                    String mediaMimeType = data.getString(data.getColumnIndexOrThrow(AUDIO_PROJECTION[3]));
                    long mediaAddTime = data.getLong(data.getColumnIndexOrThrow(AUDIO_PROJECTION[4]));

                    mediaItem.name = mediaName;
                    mediaItem.width = 0;
                    mediaItem.height = 0;
                    mediaItem.mimeType = mediaMimeType;
                    mediaItem.addTime = mediaAddTime;
                }

                mediaItem.path = mediaPath;
                mediaItem.size = meidaSize;

                allMedias.add(mediaItem);
                //根据父路径分类存放媒体文件
                File mediaFile = new File(mediaPath);
                File mediaParentFile = mediaFile.getParentFile();
                MediaFolder mediaFolder = new MediaFolder();
                mediaFolder.name = mediaParentFile.getName();
                mediaFolder.path = mediaParentFile.getAbsolutePath();

                if (!mediaFolders.contains(mediaFolder)) {
                    ArrayList<MediaItem> medias = new ArrayList<>();
                    medias.add(mediaItem);
                    mediaFolder.cover = mediaItem;
                    mediaFolder.medias = medias;
                    mediaFolders.add(mediaFolder);
                } else {
                    mediaFolders.get(mediaFolders.indexOf(mediaFolder)).medias.add(mediaItem);
                }
            }
            //防止没有媒体文件报异常
            if (data.getCount() > 0) {
                //构造所有媒体文件的集合
                MediaFolder allMediasFolder = new MediaFolder();
                allMediasFolder.name = activity.getResources().getString(R.string.all_images);
                switch (this.mediaType) {
                    case PIC:
                        allMediasFolder.name = activity.getResources().getString(R.string.all_images);
                        break;
                    case VIDEO:
                        allMediasFolder.name = activity.getResources().getString(R.string.all_videos);
                        break;
                    case AUDIO:
                        allMediasFolder.name = activity.getResources().getString(R.string.all_audios);
                        break;
                }
                allMediasFolder.path = "/";
                allMediasFolder.cover = allMedias.get(0);
                allMediasFolder.medias = allMedias;
                mediaFolders.add(0, allMediasFolder);  //确保第一条是所有媒体文件
            }
        }

        //回调接口，通知媒体数据准备完成
        MediaPicker.getInstance().setMediaFolders(mediaFolders);
        loadedListener.onMediasLoaded(mediaFolders, this.mediaType);
        loader.reset(); // mark 此处释放loader，否则会导致下次加载媒体数据是laoder的cusordata指针仍然在末尾而读不到数据
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        System.out.println("--------");
    }

    /** 所有媒体文件加载完成的回调接口 */
    public interface OnMediasLoadedListener {
        void onMediasLoaded(List<MediaFolder> mediaFolders, int mediaType);
    }
}
