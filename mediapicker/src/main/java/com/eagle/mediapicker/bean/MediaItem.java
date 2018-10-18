package com.eagle.mediapicker.bean;

import java.io.Serializable;

/**
 * @Description  媒体信息
 * @Author  eagle
 * @Date  00:07 2018/10/17
 * @Param
 * @return
 **/
public class MediaItem implements Serializable {

    public String name;       //名字
    public String path;       //路径
    public long size;         //大小
    public int width;         //宽度
    public int height;        //高度
    public String mimeType;   //类型
    public long addTime;      //创建时间

    /** 媒体文件的路径和创建时间相同就认为是同一个文件 */
    @Override
    public boolean equals(Object o) {
        try {
            MediaItem other = (MediaItem) o;
            return this.path.equalsIgnoreCase(other.path) && this.addTime == other.addTime;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

    public boolean isPic(){
        return this.mimeType.contains("image");
    }
    public boolean isVideo(){
        return this.mimeType.contains("video");
    }
    public boolean isAudio(){
        return this.mimeType.contains("audio");
    }
}
