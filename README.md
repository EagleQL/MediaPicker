<<<<<<< HEAD
# MediaPicker
###Android自定义相册，完全仿微信UI，实现了拍照、图片选择（单选/多选）、 裁剪 、旋转、等功能。
###实现了视频拍摄、选择功能

该项目参考了：

* [https://github.com/pengjianbo/GalleryFinal](https://github.com/pengjianbo/GalleryFinal) 
* [https://github.com/easonline/AndroidImagePicker](https://github.com/easonline/AndroidImagePicker)
* [https://gitee.com/z8806c/ImagePicker](https://gitee.com/z8806c/ImagePicker)



## 1.功能和参数含义

<table>
  <tdead>
    <tr>
      <th align="center">配置参数</th>
      <th align="center">参数含义</th>
    </tr>
  </tdead>
  <tbody>
    <tr>
      <td align="center">multiMode</td>
      <td align="center">图片选着模式，单选/多选</td>
    </tr>
    <tr>
      <td align="center">selectLimit</td>
      <td align="center">多选限制数量，默认为9</td>
    </tr>
    <tr>
      <td align="center">showCamera</td>
      <td align="center">选择照片时是否显示拍照按钮</td>
    </tr>
    <tr>
      <td align="center">crop</td>
      <td align="center">是否允许裁剪（单选有效）</td>
    </tr>
    <tr>
      <td align="center">style</td>
      <td align="center">有裁剪时，裁剪框是矩形还是圆形</td>
    </tr>
    <tr>
      <td align="center">focusWidth</td>
      <td align="center">矩形裁剪框宽度（圆形自动取宽高最小值）</td>
    </tr>
    <tr>
      <td align="center">focusHeight</td>
      <td align="center">矩形裁剪框高度（圆形自动取宽高最小值）</td>
    </tr>
    <tr>
      <td align="center">outPutX</td>
      <td align="center">裁剪后需要保存的图片宽度</td>
    </tr>
    <tr>
      <td align="center">outPutY</td>
      <td align="center">裁剪后需要保存的图片高度</td>
    </tr>
    <tr>
      <td align="center">isSaveRectangle</td>
      <td align="center">裁剪后的图片是按矩形区域保存还是裁剪框的形状，例如圆形裁剪的时候，该参数给true，那么保存的图片是矩形区域，如果该参数给fale，保存的图片是圆形区域</td>
    </tr>
    <tr>
      <td align="center">imageLoader</td>
      <td align="center">需要使用的图片加载器，自需要实现ImageLoader接口即可</td>
    </tr>
  </tbody>
</table>

## 2.代码参考
### 1.首先配置图片选择器，一般初始化配置一次就可以
```java
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
		imagePicker.setShowCamera(true);  //显示拍照按钮
		imagePicker.setCrop(true);        //允许裁剪（单选才有效）
		imagePicker.setSaveRectangle(true); //是否按矩形区域保存
		imagePicker.setSelectLimit(9);    //选中数量限制
		imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
		imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
		imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
	}
```
### 2.以上配置完成后，在适当的方法中开启相册，例如点击按钮时
```java
	public void onClick(View v) {
            Intent intent = new Intent(this, ImageGridActivity.class);
            // 此处是数组表示相册显示得到菜单，可以根据数组元素的顺序显示图片、视频、音频菜单
            intent.putExtra("mode",new int[] {MediaDataSource.PIC, MediaDataSource.VIDEO});
            startActivityForResult(intent, IMAGE_PICKER);  
        }
    }
```
### 3.重写`onActivityResult`方法,回调结果
```java
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> medias = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                MyAdapter adapter = new MyAdapter(medias);
                gridView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }
```
=======


