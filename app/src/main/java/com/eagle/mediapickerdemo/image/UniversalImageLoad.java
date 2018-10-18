package com.eagle.mediapickerdemo.image;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

/**
 * 重写ImageLoad 方便以后修改ImageLoad
 * <p/>
 * author: Soulwolf Created on 2015/9/10 0:09.
 * email : Ching.Soulwolf@gmail.com
 */
public final class UniversalImageLoad extends ImageLoader {

    public static final String CACHE_IMAGE_LOADER = "images";
    private volatile static UniversalImageLoad instance;

    /**
     * Returns singleton class instance
     */
    public static UniversalImageLoad getInstance() {
        if (instance == null) {
            throw new IllegalStateException("The ImageLoad not initialize!");
        }
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (UniversalImageLoad.class) {
                if (instance == null) {
                    instance = new UniversalImageLoad(context);
                }
            }
        }
    }

    protected UniversalImageLoad(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("The ImageLoad init Context be not null!");
        }
        File cacheDir = StorageUtils.getCacheDirectory(context, CACHE_IMAGE_LOADER);
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory() // 缓存显示不同大小的同一张图片
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheSize(200 * 1024 * 1024)//磁盘缓存200M
                .diskCacheFileCount(500)//缓存的File数量
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new LruMemoryCache(25 * 1024 * 1024))//bitmap的强引用
                .memoryCacheSize(25 * 1024 * 1024)//内存缓存
                .memoryCacheSizePercentage(13)// 设置内存缓存最大大小占当前应用可用内存的百分比
                        //.tasksProcessingOrder(QueueProcessingType.LIFO)
               // .defaultDisplayImageOptions(UniversalDisplayOptions.simple())//设置默认图片加载占位图
                .build();
        super.init(configuration);
    }
}
