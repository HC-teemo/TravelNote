package com.nxnd.travelnote.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.nxnd.travelnote.R;
import com.nxnd.travelnote.Url;
import com.nxnd.travelnote.helper.DBHelper;
import com.nxnd.travelnote.model.StepModel;
import com.nxnd.travelnote.util.CommonUtil;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.HttpMethod;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import static com.nxnd.travelnote.util.ImageLoader.getFileByUri;

public class StepActivity extends AppCompatActivity {

    @BindView(R.id.step_topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.step_listview)
    QMUIGroupListView listView;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.add_image)
    ImageButton addImage;
    @BindView(R.id.editText)
    EditText contentEdit;

    //数据
    //日期
    private String date = "";
    //图片URI
    private List<Uri> mSelected;
    private Uri imageUri;
    //图片网络URL
    private String imgUrl;
    //内容
    private String content;
    //位置
    private String location;
    //经纬度
    private Double longitude;
    private Double latitude;

    public static final int REQUEST_CODE_CHOOSE=123;
    public static final int REQUEST_CODE_LOCATION=234;
    private QMUICommonListItemView itemLocation;
    private QMUITipDialog tipDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);
        // 沉浸式状态栏
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        initTopBar();
        initTime();
        itemLocation = listView.createItemView("所在位置");
        itemLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location));
        itemLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent( StepActivity.this,LocationActivity.class);
                startActivityForResult(i, REQUEST_CODE_LOCATION);//打开定位页面
            }
        });
        QMUICommonListItemView itemTime = listView.createItemView("记录时间");
        itemTime.setDetailText(date);
        itemTime.setImageDrawable(getResources().getDrawable(R.drawable.ic_time));
        QMUIGroupListView.newSection(this)
                .setTitle("")
                .addItemView(itemLocation,null)
                .addItemView(itemTime,null)
                .setUseDefaultTitleIfNone(false)
                .setUseTitleViewForSectionSpace(false)
                .addTo(listView);
    }


    private void initTime(){
        date = CommonUtil.getCurrTime();
    }

    private void initTopBar() {
        mTopBar.setTitle("编辑足迹");
        final QMUIAlphaImageButton back = mTopBar.addLeftBackImageButton();
        back.setColorFilter(Color.BLACK);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StepActivity.this.finish();
            }
        });
        //保存
        mTopBar.addRightTextButton(R.string.step_save,R.integer.save).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                tipDialog = new QMUITipDialog.Builder(StepActivity.this)
                            .setTipWord("保存中")
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .create();
                tipDialog.show();
                //存content
                content = contentEdit.getText().toString();
                //判断
                if(content==null||content.equals("")||location==null||location.equals("")||imageUri==null){
                    Toast.makeText(StepActivity.this,"请输入完整信息",Toast.LENGTH_LONG).show();
                    tipDialog.dismiss();
                }else {


                    File file = getFileByUri(imageUri, StepActivity.this);   //图片地址
                    RequestParams params = new RequestParams(Url.url_image);
                    params.setMultipart(true);
                    params.setAsJsonContent(true);
                    params.addBodyParameter("image", file);  //file是手机里的图片文件
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject res = new JSONObject(result);
                                boolean status = res.getBoolean("success");
                                String info = res.getString("desc");
                                if (status) {
                                    //上传成功
                                    JSONObject data = res.getJSONObject("data");
                                    imgUrl = data.getString("url");
                                    //TODO 保存数据
                                    DBHelper.getInstance().addStep(new StepModel(1,imgUrl,content,date,location,longitude.floatValue(),latitude.floatValue()));
                                    tipDialog.dismiss();
                                    Toast.makeText(StepActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                                    //返回
                                    Intent i=new Intent();
                                    //返回数据
                                    setResult(Activity.RESULT_OK,i);
                                    finish();
                                } else {
                                    tipDialog.dismiss();
                                    Toast.makeText(StepActivity.this, info, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Log.d("imgerr", e.toString());
                                tipDialog.dismiss();
                                Toast.makeText(StepActivity.this, "图片上传失败", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Log.d("imgerr", ex.toString());
                            Toast.makeText(StepActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                            tipDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                        }

                        @Override
                        public void onFinished() {
                        }

                    });
                }


            }
        });
    }

    @OnClick(R.id.add_image)
    public void addImage(){
        chooseImage();
    }

    private void chooseImage(){
        Matisse.from(StepActivity.this)
                .choose(MimeType.allOf()) // 选择 mime 的类型
                .maxSelectable(1) // 图片选择的最多数量
                .theme(R.style.Matisse_Zhihu)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f) // 缩略图的比例
                .imageEngine(new GlideEngine()) // 使用的图片加载引擎
                .forResult(REQUEST_CODE_CHOOSE); // 设置作为标记的请求码
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.i("resultInfo","requestCode"+requestCode+",resultCode"+resultCode);
        switch (requestCode){
            case REQUEST_CODE_CHOOSE:
                if (resultCode == RESULT_OK) {
                    mSelected = Matisse.obtainResult(data);
                    Log.d("Matisse", "mSelected: " + mSelected);
                    imageView.setImageURI(mSelected.get(0));

                    imageUri = mSelected.get(0);

                    imageView.setVisibility(View.VISIBLE);
                    addImage.setVisibility(View.INVISIBLE);
                }
                break;
            case REQUEST_CODE_LOCATION:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String cityname = bundle.getString("cityname");
                    LatLonPoint latlonpoint = (LatLonPoint) bundle.get("latlonpoint");
                    String snippet = bundle.getString("snippet");
                    String value = bundle.getString("value");
                    String title = bundle.getString("title");
                    String adname = bundle.getString("adname");
                    //存经纬度
                    this.latitude = latlonpoint.getLatitude();
                    this.longitude = latlonpoint.getLongitude();
                    //存位置
                    this.location = cityname+","+title;
                    itemLocation.setDetailText(cityname+","+title);
                    Log.d("backFromLocation", cityname+snippet);
                }
                break;
        }

    }



}
