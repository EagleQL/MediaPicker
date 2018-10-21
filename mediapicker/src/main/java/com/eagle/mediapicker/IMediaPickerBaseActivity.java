package com.eagle.mediapicker;

import android.content.Intent;

import com.eagle.mediapicker.bean.MediaItem;

import java.util.List;
/**
 * 调用mediapicker以及相册请实现此接口
 * <br>
 * <br>
 * by <font color='blue'><b>eagle</b></font>
 * <br>
 * 03:15 2018/10/22
 * <br>
 */
interface IMediaPickerBaseActivity {
    int OPENGALLERAY = 100;
    int[] fullModes = new int[]{MediaDataSource.PIC, MediaDataSource.VIDEO, MediaDataSource.AUDIO};
    MediaPicker mediaPicker = MediaPicker.getInstance();

    void buildMediaPicker();
    /**
     * 打开相册：MediaGridActivity
     */
    void onOpenGalleray(int[] mode);

    /**
     * 检查已选择的媒体数量是否在允许范围内
     * @return
     */
    boolean checkImg();

    /**
     * 媒体选择超限回调
     * @param selectLimit
     * @param selectMediaCount
     */
    void onInputMediaCountOverstep(int selectLimit, int selectMediaCount);

    /**
     * 媒体数据上传完成回调，参数返回上传结果
     * eagle
     * 19:27 2018/10/19
     * @param success
     **/
    void onMediasUploadedFinish(boolean success);

    /**
     * 该方法接收已选择的媒体数据，方法体内可以做渲染媒体数据至界面等动作
     * @param images
     */
    void onMediasSelectedReturn(List<MediaItem> images);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 请在此事件内清空诸如选择的媒体列表、mediaPicker.clear()之类的缓存
     */
    void onBackPressed();
}
