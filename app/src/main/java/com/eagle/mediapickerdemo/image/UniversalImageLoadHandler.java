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

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

/**
 * 图片加载框架 ImageLoad 的实现类
 *
 * author: Soulwolf Created on 2015/9/10 0:09.
 * email : Ching.Soulwolf@gmail.com
 */
public class UniversalImageLoadHandler implements ImageLoadHandler{

    public UniversalImageLoadHandler(Context context){
        UniversalImageLoad.init(context);
    }


    @Override
    public void displayImage(String uri, ImageAware imageAware) {
        UniversalImageLoad.getInstance().displayImage(uri,imageAware);
    }

    @Override
    public void displayImage(String uri, ImageAware imageAware, ImageLoadingListener listener) {
        UniversalImageLoad.getInstance().displayImage(uri,imageAware,listener);
    }

    @Override
    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options) {
        UniversalImageLoad.getInstance().displayImage(uri,imageAware,options);
    }

    @Override
    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options, ImageLoadingListener listener) {
        UniversalImageLoad.getInstance().displayImage(uri,imageAware,options,listener);
    }

    @Override
    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        UniversalImageLoad.getInstance().displayImage(uri,imageAware,options,listener,progressListener);
    }

    @Override
    public void displayImage(String uri, ImageView imageView) {
        UniversalImageLoad.getInstance().displayImage(uri,imageView);
    }

    @Override
    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
        UniversalImageLoad.getInstance().displayImage(uri,imageView,options);
    }

    @Override
    public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
        UniversalImageLoad.getInstance().displayImage(uri,imageView,listener);
    }

    @Override
    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
        UniversalImageLoad.getInstance().displayImage(uri,imageView,options,listener);
    }

    @Override
    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        UniversalImageLoad.getInstance().displayImage(uri,imageView,options,listener,progressListener);
    }

    @Override
    public void loadImage(String uri, ImageLoadingListener listener) {
        UniversalImageLoad.getInstance().loadImage(uri,listener);
    }

    @Override
    public void loadImage(String uri, ImageSize targetImageSize, ImageLoadingListener listener) {
        UniversalImageLoad.getInstance().loadImage(uri,targetImageSize,listener);
    }

    @Override
    public void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener) {
        UniversalImageLoad.getInstance().loadImage(uri,options,listener);
    }

    @Override
    public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options, ImageLoadingListener listener) {
        UniversalImageLoad.getInstance().loadImage(uri,targetImageSize,options,listener);
    }

    @Override
    public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        UniversalImageLoad.getInstance().loadImage(uri,targetImageSize,options,listener,progressListener);
    }

    @Override
    public Bitmap loadImageSync(String uri) {
        return UniversalImageLoad.getInstance().loadImageSync(uri);
    }

    @Override
    public Bitmap loadImageSync(String uri, DisplayImageOptions options) {
        return UniversalImageLoad.getInstance().loadImageSync(uri,options);
    }

    @Override
    public Bitmap loadImageSync(String uri, ImageSize targetImageSize) {
        return UniversalImageLoad.getInstance().loadImageSync(uri,targetImageSize);
    }

    @Override
    public Bitmap loadImageSync(String uri, ImageSize targetImageSize, DisplayImageOptions options) {
        return UniversalImageLoad.getInstance().loadImageSync(uri,targetImageSize,options);
    }
}
