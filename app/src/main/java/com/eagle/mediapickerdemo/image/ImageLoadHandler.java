/**
 * <pre>
 * Copyright (C) 2015  Soulwolf XiaoDaoW3.0
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
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

/**
 * 图片加载框架的 接口 ,如果以后需要更换图片加载框架,
 * 只需要让新的图片加载框架实现该接口,然后 修改
 * {@link com.xiaodao360.xiaodaow.factory.ImageLoadFactory}即可
 *
 * author : Soulwolf Create by 2015/6/15 17:01
 * email  : ToakerQin@gmail.com.
 */
public interface ImageLoadHandler {

    public static final String SCHEMA_HTTP       = "http://";

    public static final String SCHEMA_FILE       = "file://";

    public static final String SCHEMA_CONTENT    = "content://";

    public static final String SCHEMA_ASSET      = "asset://";

    public static final String SCHEMA_RES        = "res://";

    public void displayImage(String uri, ImageAware imageAware);

    public void displayImage(String uri, ImageAware imageAware, ImageLoadingListener listener);

    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options);

    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
                             ImageLoadingListener listener);
    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
                             ImageLoadingListener listener, ImageLoadingProgressListener progressListener);

    public void displayImage(String uri, ImageView imageView);

    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options);

    public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener);

    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
                             ImageLoadingListener listener);

    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
                             ImageLoadingListener listener, ImageLoadingProgressListener progressListener);

    public void loadImage(String uri, ImageLoadingListener listener);

    public void loadImage(String uri, ImageSize targetImageSize, ImageLoadingListener listener);

    public void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener);

    public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options,
                          ImageLoadingListener listener);

    public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options,
                          ImageLoadingListener listener, ImageLoadingProgressListener progressListener);

    public Bitmap loadImageSync(String uri);

    public Bitmap loadImageSync(String uri, DisplayImageOptions options);

    public Bitmap loadImageSync(String uri, ImageSize targetImageSize);

    public Bitmap loadImageSync(String uri, ImageSize targetImageSize, DisplayImageOptions options);
}
