package com.eagle.mediapicker.bean;

import java.io.Serializable;
import java.util.ArrayList;

/** 媒体文件夹 */
public class MediaFolder implements Serializable {

    public String name;  //当前文件夹的名字
    public String path;  //当前文件夹的路径
    public MediaItem cover;   //当前文件夹需要要显示的缩略图，默认为最近的一次图片
    public ArrayList<MediaItem> medias;  //当前文件夹下所有媒体文件的集合

    /** 只要文件夹的路径和名字相同，就认为是相同的文件夹 */
    @Override
    public boolean equals(Object o) {
        try {
            MediaFolder other = (MediaFolder) o;
            return this.path.equalsIgnoreCase(other.path) && this.name.equalsIgnoreCase(other.name);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
