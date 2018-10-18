package com.eagle.mediapickerdemo;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/3/28
 * 描    述：我的Github地址  https://github.com/jeasonlzy0216
 * 修订历史：
 * ================================================
 */

import android.app.Activity;
import android.widget.ImageView;

import com.eagle.mediapicker.loader.MediaLoader;
import com.eagle.mediapickerdemo.image.ImageLoadFactory;
import com.eagle.mediapickerdemo.image.UniversalDisplayOptions;

public class UILMediaLoader implements MediaLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        ImageLoadFactory.getInstance().getImageLoadHandler().displayImage("file://" + path, imageView, UniversalDisplayOptions.create(R.mipmap.default_image));
    }

    @Override
    public void clearMemoryCache() {
    }
}
