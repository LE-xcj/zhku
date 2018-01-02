package com.example.zhkuapp.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhkuapp.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_imfor);
        ButterKnife.bind(this);
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

}
