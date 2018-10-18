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

import android.content.Context;

/**
 * 图片加载框架的构造器
 *
 * author : Soulwolf Create by 2015/6/16 10:00
 * email  : ToakerQin@gmail.com.
 */
public class ImageLoadFactory {

    private static ImageLoadFactory mImageLoadFactory;

    private Context mContext;

    private ImageLoadHandler mImageLoadHandler;

    /** 初始化 */
    public static void init(Context context){
        if(mImageLoadFactory == null){
            synchronized (ImageLoadFactory.class){
                if(mImageLoadFactory == null){
                    mImageLoadFactory = new ImageLoadFactory(context);
                }
            }
        }
    }

    /** 获取实例 */
    public static ImageLoadFactory getInstance(){
        if(mImageLoadFactory == null){
            throw new IllegalStateException("ImageLoadFactory not initialized!");
        }
        return mImageLoadFactory;
    }

    /** 获取图片加载管理器  */
    public ImageLoadFactory(Context context) {
        this.mContext = context;
//        mImageLoadHandler = new PicassoImageHandler(mContext);
        mImageLoadHandler = new UniversalImageLoadHandler(mContext);
    }

    public ImageLoadHandler getImageLoadHandler(){
        return mImageLoadHandler;
    }
}
