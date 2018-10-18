/**
 * <pre>
 * Copyright 2015 Soulwolf Ching
 * Copyright 2015 The Android Open Source Project for XDW-Android-Client
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </pre>
 */
package com.eagle.mediapickerdemo.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * ImageLoad 配置信息
 * <p/>
 * author: Soulwolf Created on 2015/9/10 22:01.
 * email : Ching.Soulwolf@gmail.com
 */
public final class UniversalDisplayOptions extends DisplayImageOptions.Builder {

    public UniversalDisplayOptions() {
        super();
        resetViewBeforeLoading(true);  // default
        cacheOnDisk(true);
        cacheInMemory(true);//内存缓存
        considerExifParams(false); //是否考虑JPEG图像EXIF参数（旋转，翻转）
        //displayer(new FadeInBitmapDisplayer(1000));// 淡入
        //resetViewBeforeLoading(true);// 设置图片在下载前是否重置，复位
        imageScaleType(ImageScaleType.EXACTLY); // default
        imageScaleType(ImageScaleType.IN_SAMPLE_INT); // brucend
        bitmapConfig(Bitmap.Config.RGB_565); // default
        //.handler(new Handler()) // default
    }

    public static DisplayImageOptions create(Drawable shouldDrawable) {
        UniversalDisplayOptions options = new UniversalDisplayOptions();
        options.showImageForEmptyUri(shouldDrawable);
        options.showImageOnLoading(shouldDrawable); // resource or drawable
        options.showImageOnFail(shouldDrawable); // resource or drawable
        return options.build();
    }

    public static DisplayImageOptions create(@DrawableRes int shouldDrawable) {
        UniversalDisplayOptions options = new UniversalDisplayOptions();
        options.showImageForEmptyUri(shouldDrawable);
        options.showImageOnLoading(shouldDrawable); // resource or drawable
        options.showImageOnFail(shouldDrawable); // resource or drawable
        return options.build();
    }

}
