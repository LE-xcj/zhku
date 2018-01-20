package com.example.zhkuapp.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.dao.UploadDao;
import com.example.zhkuapp.pojo.User;
import com.example.zhkuapp.service.RegisService;
import com.example.zhkuapp.service.UpdateUserService;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.utils.PhotoUtil;
import com.example.zhkuapp.utils.SDCardUtil;
import com.example.zhkuapp.utils.SharePreferenceUtil;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SetImforActivity extends AppCompatActivity {

    @Bind(R.id.skip)
    TextView skip;
    @Bind(R.id.img_photo)
    ImageView img_Photo;
    @Bind(R.id.et_username)
    EditText et_Username;
    @Bind(R.id.et_sex)
    EditText et_Sex;
    @Bind(R.id.et_selfIntroduction)
    EditText et_SelfIntroduction;

    @Bind(R.id.et_email)
    EditText et_Email;
    @Bind(R.id.btn_sure)
    Button btnSure;

    private int select = 0;
    private final String[] OPTIONS = {"男", "女"};

    private Button btn_picture, btn_photo, btn_cancle;
    private Bitmap head;        // 头像Bitmap

    private String photoName = "";

    public static SetImforActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_imfor);
        ButterKnife.bind(this);

        instance = this;

        et_Sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSex();
            }
        });
    }

    @Override
    protected void onDestroy() {
        //将新注册填写的信息，保存在sharePreference里
        SharePreferenceUtil.write(this,SingleUser.single);
        super.onDestroy();
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.skip: {
                SingleUser.setDefault();
                finish();
            }break;

            case R.id.btn_sure: {

                //这里的四个属性值是从用户输入获取的
                String userName = et_Username.getText().toString();
                String sex = et_Sex.getText().toString();
                String email = et_Email.getText().toString();
                String selfIntroduction = et_SelfIntroduction.getText().toString();


                //向服务器设置用户信息
                if (UpdateUserService.usernameIsNull(userName))
                    MyToast.show(this,"请输入用户名");
                else{

                    /*
                    *设置当前用户信息，single
                    * 注意：photo是一个未知的参数
                    * 因为：用户可以不设置头像，也可以设置了头像有跳过设置
                     */
                    SingleUser.set(userName,sex,email,selfIntroduction,photoName);

                    //向服务器那边更新用户信息
                    UpdateUserService.update(SingleUser.single);
                    Log.e("this is SetImforActivit", "上一句是向服务器更新用户信息");

                    if (null != head){
                        // 将头像图片保存在SD卡中,photo的名字已经包括扩展名了
                        SDCardUtil.setPicToView(head,SingleUser.getPhoto(),true);
                        Log.e("this selfImforActivity"," 上一句是将用户的头像保存在SD卡里");

                        //上传到服务器
                        UploadDao.uploadBitmap(new File(SDCardUtil.getAbsolutePath(SingleUser.getPhoto(),true)),true);
                        Log.e("this selfImforActivity "," 上一句是将图片上传到服务器");
                    }

                    MyToast.show(this,"设置成功");
                    finish();       //显示最底的MainActivity
                }
            }break;

            case R.id.img_photo: {
                showDialog();
            }break;
        }

    }

    @Override
    public void onBackPressed() {
        SingleUser.setDefault();
    }

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


    //--------------------------------------------------------\\

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
                    PhotoUtil.cropPhoto(data.getData(), instance);// 裁剪图片
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + File.separator+ SingleUser.getUserID() +".jpg");
                    PhotoUtil.cropPhoto(Uri.fromFile(temp), instance);// 裁剪图片
                }

                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /*
                        *设置图片的名称，先暂时保存在一个全局的临时变量
                        * 设置头像的名称，组成：用户ID + ".jpg"
                         */
                        photoName = SingleUser.getUserID() + SDCardUtil.TYPE;

                        img_Photo.setImageBitmap(PhotoUtil.toRoundBitmap(head));    // 用ImageView显示出来
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
