package com.example.zhkuapp.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhkuapp.R;
import com.example.zhkuapp.utils.PhotoUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    private final String[] OPTIONS = {"男","女"};
    private int select = 0;

    private Button btn_picture, btn_photo, btn_cancle;
    private Bitmap head;// 头像Bitmap
    private static String path = "/sdcard/myHead/";// sd路径

    public static SetImforActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_imfor);
        ButterKnife.bind(this);

        instance = this;
    }

    public void click(View view){
        switch (view.getId()){
            case R.id.skip:{
                finish();
            }break;

            case R.id.et_sex:{
                selectSex();
            }break;

            case R.id.btn_sure:{

            }break;

            case R.id.img_photo:{
                showDialog();
            }break;
        }
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
                .setNegativeButton("取消",null)
                .setSingleChoiceItems(OPTIONS, select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select = which;
                    }
                }).create().show();
    }


    //--------------------------------------------------------\\

    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });
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
                    PhotoUtil.cropPhoto(data.getData(),instance);// 裁剪图片
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                    PhotoUtil.cropPhoto(Uri.fromFile(temp),instance);// 裁剪图片
                }

                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        PhotoUtil.setPicToView(head,path);// 保存在SD卡中
                        img_Photo.setImageBitmap(PhotoUtil.toRoundBitmap(head));// 用ImageView显示出来
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    };

}
