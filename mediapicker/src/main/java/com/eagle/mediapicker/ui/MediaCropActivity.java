package com.eagle.mediapicker.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.eagle.mediapicker.MediaPicker;
import com.eagle.mediapicker.R;
import com.eagle.mediapicker.bean.MediaItem;
import com.eagle.mediapicker.view.CropImageView;

import java.io.File;
import java.util.ArrayList;

public class MediaCropActivity extends MediaBaseActivity implements View.OnClickListener, CropImageView.OnBitmapSaveCompleteListener {

    private CropImageView mCropImageView;
    private Bitmap mBitmap;
    private boolean mIsSaveRectangle;
    private int mOutputX;
    private int mOutputY;
    private ArrayList<MediaItem> mMediaItems;
    private MediaPicker mediaPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        mediaPicker = MediaPicker.getInstance();

        //初始化View
        findViewById(R.id.btn_back).setOnClickListener(this);
        TextView btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_ok.setText(getString(R.string.complete));
        btn_ok.setOnClickListener(this);
        TextView tv_pic = (TextView) findViewById(R.id.tv_pic);
        tv_pic.setText(getString(R.string.photo_crop));
        mCropImageView = (CropImageView) findViewById(R.id.cv_crop_image);
        mCropImageView.setOnBitmapSaveCompleteListener(this);

        //获取需要的参数
        mOutputX = mediaPicker.getOutPutX();
        mOutputY = mediaPicker.getOutPutY();
        mIsSaveRectangle = mediaPicker.isSaveRectangle();
        mMediaItems = mediaPicker.getSelectedMedias();
        String imagePath = mMediaItems.get(0).path;

        mCropImageView.setFocusStyle(mediaPicker.getStyle());
        mCropImageView.setFocusWidth(mediaPicker.getFocusWidth());
        mCropImageView.setFocusHeight(mediaPicker.getFocusHeight());

        //缩放图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeFile(imagePath, options);
        mCropImageView.setImageBitmap(mBitmap);

//        mCropImageView.setImageURI(Uri.fromFile(new File(imagePath)));
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = width / reqWidth;
            } else {
                inSampleSize = height / reqHeight;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.btn_ok) {
            mCropImageView.saveBitmapToFile(mediaPicker.getCropCacheFolder(this), mOutputX, mOutputY, mIsSaveRectangle);
        }
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
//        Toast.makeText(ImageCropActivity.this, "裁剪成功:" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        //裁剪后替换掉返回数据的内容，但是不要改变全局中的选中数据
        mMediaItems.remove(0);
        MediaItem mediaItem = new MediaItem();
        mediaItem.path = file.getAbsolutePath();
        mMediaItems.add(mediaItem);

        Intent intent = new Intent();
        intent.putExtra(MediaPicker.EXTRA_RESULT_ITEMS, mMediaItems);
        setResult(MediaPicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
        finish();
    }

    @Override
    public void onBitmapSaveError(File file) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBitmap && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
