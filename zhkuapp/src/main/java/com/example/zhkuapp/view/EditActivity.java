package com.example.zhkuapp.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.ResultVO;
import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.dao.UploadDao;
import com.example.zhkuapp.dao.UserDao;
import com.example.zhkuapp.service.UploadService;
import com.example.zhkuapp.utils.MyProgressDialog;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.utils.NetUtil;
import com.example.zhkuapp.utils.PhotoUtil;
import com.example.zhkuapp.utils.SDCardUtil;
import com.example.zhkuapp.utils.SharePreferenceUtil;
import com.loopj.android.image.SmartImageView;

import java.io.File;
import java.io.InterruptedIOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {


    @Bind(R.id.img_photo)
    SmartImageView imgPhoto;

    @Bind(R.id.et_username)
    EditText et_Username;

    @Bind(R.id.et_sex)
    EditText et_Sex;

    @Bind(R.id.et_selfIntroduction)
    EditText et_SelfIntroduction;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private  MyProgressDialog d;

    private Button btn_picture, btn_photo, btn_cancle;

    private int select = 0;
    private final String[] OPTIONS = {"男", "女"};

    private String photoName ="";

    private Bitmap bitmap = null;

    private final int USER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        init();
    }

    public void click(View view) {

        switch (view.getId()) {
            case R.id.img_photo: {
                showDialog();
            }
            break;

            case R.id.et_sex: {
                selectSex();
            }
            break;

            case R.id.btn_sure: {
               editSure();
            }
            break;
        }
    }

    private void editSure(){

        String userName = et_Username.getText().toString().trim();
        String sex = et_Sex.getText().toString().trim();
        String selfIntroduction = et_SelfIntroduction.getText().toString().trim();

        /*
        *这里编辑信息有多种情况：
        *   1、有头像，但是没有更改
        *   3、没有头像，没有更改
        *   如果是第3,种情况，那么头像名称自然不需要
        *   但是如果是第1种情况，那么在更新single时，因为需要用到photoName，所以没有改变头像，那么photoName自然为空
        *
        *   2、有头像，有更改
        *   4、没有头像，有更改
        *   这两种情况，全局变量photoName都会得到正确的图片名称：账号.jpg
        *
        *   而且从没有头像到有头像，这样photo需要设置
        *
         */

        //原本就有头像，不管有没有改变头像，头像名都是那个
        if (null != SingleUser.getPhoto() || !"".equals(SingleUser.getPhoto()))
            photoName = SingleUser.getPhoto();

        //更新当前的single
        SingleUser.set(userName,sex,selfIntroduction,photoName);

        d = new MyProgressDialog(this,"正在修改，请稍后...");
        d.show();

        //有改变头像，那么就要更新本地与服务器那边的头像信息
        if (null != bitmap){
            //替换本地的头像
            SDCardUtil.setPicToView(bitmap,photoName,true);

            //更新服务器的头像
            UploadDao.uploadBitmap(new File(SDCardUtil.getAbsolutePath(SingleUser.getPhoto(),true)),true);
        }else{
            //这里设置头像的结果码为1，是为了能够终止下面listenResult方法里的循环
            ResultVO.setPhotoResultCode(1);
        }

        //更新sharePreference
        SharePreferenceUtil.write(this,SingleUser.single);

        //更新服务器的用户信息
        UserDao.updateUser(SingleUser.single);

        //监听异步请求返回的结果
        listenResult();
    }

    private void listenResult() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //判断异步请求是否已经返回结果了
                while(ResultVO.isDefaul(USER)){}

                //获取结果码
                int photoCode = ResultVO.getPhotoResultCode();
                int basicImforCode = ResultVO.getBasicImforCode();

                //更新ui
                Message msg = new Message();
                msg.what = photoCode;
                msg.obj = basicImforCode;
                handler.sendMessage(msg);

                //还原默认值
                ResultVO.setDefault(USER);

            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int photoCode = msg.what;
            int basicImforCode = (int) msg.obj;
            if (1 == photoCode && 1 == basicImforCode)
                MyToast.show(EditActivity.this, "修改完成");
            else if (-1 == photoCode && -1 == basicImforCode)
                MyToast.show(EditActivity.this, "修改失败");
            else if (-1 == photoCode)
                MyToast.show(EditActivity.this, "图片上传失败");
            else
                MyToast.show(EditActivity.this, "信息上传失败");
            if (null != d)
                d.dismiss();

            //设置返回结果
            Intent intent = new Intent();
            intent.putExtra("photoCode",photoCode);
            intent.putExtra("basicImforCode",basicImforCode);
            setResult(photoCode+basicImforCode,intent);
            finish();
        }
    };

    private void selectSex() {
        new AlertDialog.Builder(this)
                .setTitle("请选择")
                .setIcon(null)
                .setCancelable(false)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et_Sex.setText(OPTIONS[select]);
                    }
                })
                .setNegativeButton("取消", null)
                .setSingleChoiceItems(OPTIONS, select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select = which;
                    }
                }).create().show();
    }

    private void init() {

        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.finish();
                Toast.makeText(getApplicationContext(), "点击了返回箭头", Toast.LENGTH_LONG).show();
            }
        });

        Bitmap photo = PhotoUtil.getBitmap(SDCardUtil.getAbsolutePath(SingleUser.getPhoto(),true));

        if (null != photo) {
            imgPhoto.setImageBitmap(photo);
        } else if (NetUtil.isNetworkAvailable(MainActivity.instance)) {
            imgPhoto.setImageUrl(UserDao.getPhotoUrl(SingleUser.getUserID()));
        } else {
            //使用默认的图片
            imgPhoto.setImageBitmap(PhotoUtil.getBitmap());
        }

        if ("女".equals(SingleUser.getSex()))
            select = 1;

        et_Sex.setText(SingleUser.getSex());

        et_Username.setText(SingleUser.getUsername());

        et_SelfIntroduction.setText(SingleUser.getSelfIntroduction());

    }

    private void showDialog() {

        //获取弹出框的界面
        View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);

        //设置弹出框的样式
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);

        //将弹出框的视图加到视图群中
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //显示
        Window window = dialog.getWindow();

        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();

        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        btn_picture = (Button) window.findViewById(R.id.btn_picture);
        btn_photo = (Button) window.findViewById(R.id.btn_photo);
        btn_cancle = (Button) window.findViewById(R.id.btn_cancle);

        //从图库选择图片
        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });

        //拍照获取
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), SingleUser.getUserID() +".jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });

        //取消
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    PhotoUtil.cropPhoto(data.getData(), this);// 裁剪图片
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + File.separator+ SingleUser.getUserID() +".jpg");
                    PhotoUtil.cropPhoto(Uri.fromFile(temp), this);// 裁剪图片
                }

                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    bitmap = extras.getParcelable("data");
                    if (bitmap != null) {
                        /*
                        *设置图片的名称，先暂时保存在一个全局的临时变量
                        * 设置头像的名称，组成：用户ID + ".jpg"
                         */
                        photoName = SingleUser.getUserID() + SDCardUtil.TYPE;

                        imgPhoto.setImageBitmap(PhotoUtil.toRoundBitmap(bitmap));    // 用ImageView显示出来
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
