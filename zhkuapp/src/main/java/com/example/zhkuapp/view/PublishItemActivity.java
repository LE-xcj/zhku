package com.example.zhkuapp.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.ItemContainer;
import com.example.zhkuapp.dao.ResultVO;
import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.pojo.Item;
import com.example.zhkuapp.service.ItemService;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.utils.NetUtil;
import com.example.zhkuapp.utils.PhotoUtil;
import com.example.zhkuapp.utils.SDCardUtil;
import com.example.zhkuapp.utils.UUIDUtil;

import java.io.File;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PublishItemActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_lostitem)
    EditText etLostitem;
    @Bind(R.id.et_place)
    EditText etPlace;
    @Bind(R.id.et_time)
    EditText etTime;
    @Bind(R.id.et_contactname)
    EditText etContactname;
    @Bind(R.id.et_call)
    EditText etCall;
    @Bind(R.id.et_description)
    EditText etDescription;
    @Bind(R.id.itemPhoto)
    ImageView img_itemPhoto;

    //发布帖子的用户id
    private String userID;

    /*
    *0代表丢失东西帖子
    * 1代表捡到东西帖子
     */
    private int type;

    private final int LOST = 0;

    private Button btn_picture, btn_photo, btn_cancle;
    private Bitmap itemPhoto;

    private final int ITEM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_item);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        //处理左上角的返回键
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        type = intent.getIntExtra("type", LOST);

        //设置标题
        if (LOST == type) {
            toolbar.setTitle("我丢了东西");
        } else {
            toolbar.setTitle("我捡到了东西");
        }

    }

    /*
    *处理点击事件
    * 三个点击事件：
    *   1、发布按钮
    *   2、选择item图片
    *   3、选择丢失时间
     */
    public void click(View view) {

        switch (view.getId()) {
            case R.id.publish: {
                //发布帖子
                publishItem();
            }break;

            case R.id.itemPhoto: {
                //显示图片选择
                showDialog();
            }break;

            case R.id.et_time: {
                //显示时间选择器
                showDateDialog();
            }break;

        }
    }


    //发布帖子
    private void publishItem() {

        //网络有问题
        if (!NetUtil.isNetworkConnected(this)){
            MyToast.show(this,"网络有问题");
            return;
        }

        String itemName = etLostitem.getText().toString().trim();

        //如果帖子的物品名称为空，则不可以发布，其余为空都可以
        if (ItemService.strEmpty(itemName)){
            MyToast.show(this, "物品名称不能为空");
            return;
        }

        String lostTime = etTime.getText().toString().trim();
        String place = etPlace.getText().toString().trim();
        String contact = etContactname.getText().toString().trim();
        String call = etCall.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        //设置item的图片名称,uuid + .jpg
        String photoName = "";

        //这里item的图片名称已经包括扩展名了
        if (null != itemPhoto)
            photoName = UUIDUtil.getUuid() + SDCardUtil.TYPE;

        Item item = new Item();

        /*
        *设置发起帖子用户的数据
        * 三个属性数据
        *   1、用户的id
        *   2、用户名
        *   3、用户的头像
        *
        *   其实，服务器那边只需要userID就可以了，
        *   但是因为涉及到本地更新帖子列表需要，所以就包用户名与头像属性也设置了
        *
        *   至于帖子的id，只有在对帖子进行update或delete的操作时，才需要帖子的id
        *   而更新帖子和删除帖子都是要进入个人动态的界面才能操作，到时再从服务器那边获取
        *   然后还有刷新一下本地
         */
        item.setUserID(userID);
        item.setUserName(SingleUser.getUsername());
        item.setUserPhoto(SingleUser.getPhoto());

        /*
        *设置帖子的数据
        * 帖子的属性一共需要设置：
        *   1、物品名称
        *   2、丢失时间
        *   3、丢失地点
        *   4、联系人
        *   5、联系方式
        *   6、物品详情
        *   7、帖子类型
        *   8、物品图片名称
        *
        *
        *   至于帖子的id是自增的
        *   帖子的状态分两种：
        *       1、未解决   （0）
        *       2、已解决   （1）
        *   因为发布帖子，已经默认这是未解决的，所以在这里不设置帖子的状态
        *   在数据库那边设置默认值为：0，也就是未解决
        *
        *   发表日期：在服务器那边设置
         */
        item.setItemName(itemName);
        item.setLostTime(lostTime);
        item.setLostPlace(place);
        item.setContactName(contact);
        item.setContactWay(call);
        item.setDescription(description);
        item.setType(type);
        item.setPhoto(photoName);

        /*
        *进行数据的操作
        * 分两个方向：
        *   1、向服务器那边进行数据的操作
        *       a）、向数据库插入数据
        *       b）、上传图片
        *
        *   2、向本地进行数据的操作
        *       a）、向item容器插入一个item
        *       b）、将图片保存在SD卡
        *
         */
        ItemService.publishItem(item,itemPhoto);

        //监听返回结果
        listenResult(item);


    }


    private void listenResult(final Item item) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //监听请求的结果
                while (ResultVO.isDefaul(ITEM)){}

                //获取请求结果
                int itemPublishCode = ResultVO.getPublishItemCode();
                int itemPhotoCode = ResultVO.getItemPhotoCode();

                //还原默认值
                ResultVO.setDefault(ITEM);

                //如果发布成功，将发布的帖子添加到帖子容器中
                /*if (ItemService.canPublish(itemPublishCode,itemPhotoCode)){

                    //item是否为空
                    if (null != item){

                        //帖子是什么类型
                       if (LOST == type){
                           Log.e("this is publishActiviy"," add lost ");
                           ItemContainer.addLost(item);
                       }else{
                           Log.e("this is publishActiviy"," add pcik ");
                           ItemContainer.addPick(item);
                       }

                    }

                }*/

                Message msg = new Message();
                msg.what = itemPublishCode;
                msg.obj = itemPhotoCode;
                handler.sendMessage(msg);

            }
        }).start();
    }

    /*
    * 在这里只有两种状态码：
    * 1：成功
    * -1：失败
    * */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            int itemPublishCode = msg.what;
            int itemPhotoCode = (int) msg.obj;

            //设置返回结果
            Intent intent = new Intent();
            intent.putExtra("itemPublishCode",itemPublishCode);
            intent.putExtra("itemPhotoCode",itemPhotoCode);

            //返回
            setResult(type,intent);

            //结束该activity
            finish();
        }
    };



    //日期选择器
    private void showDateDialog() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String month = "" + monthOfYear;
                String day = "" + dayOfMonth;

                //月份的取值0~11，所以需要加个1
                ++monthOfYear;

                //而且显示的数字如果小于10，那么就不会补0，所以需要判断是否需要补0
                if (10 > monthOfYear)
                    month = "0" + monthOfYear;
                if (10 > dayOfMonth)
                    day = "0" + dayOfMonth;

                etTime.setText(year + "-" + month + "-" + day);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        //显示dialog
        datePickerDialog.show();
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
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), SingleUser.getUserID() + ".jpg")));
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
                    File temp = new File(Environment.getExternalStorageDirectory() + File.separator + SingleUser.getUserID() + ".jpg");
                    PhotoUtil.cropPhoto(Uri.fromFile(temp), this);// 裁剪图片
                }

                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    itemPhoto = extras.getParcelable("data");
                    if (itemPhoto != null) {
                        img_itemPhoto.setImageBitmap(PhotoUtil.toRoundBitmap(itemPhoto));    // 用ImageView显示出来
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
