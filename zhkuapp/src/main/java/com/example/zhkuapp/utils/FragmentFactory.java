package com.example.zhkuapp.utils;


import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.frgment.ClassroomFragment;
import com.example.zhkuapp.frgment.ItemFragment;
import com.example.zhkuapp.frgment.LostFragment;
import com.example.zhkuapp.frgment.MessageFragment;
import com.example.zhkuapp.frgment.PersonlFragment;
import com.example.zhkuapp.frgment.PickFragment;

/**
 * Created by chujian on 2018/1/5.
 */

public class FragmentFactory {
    private static ItemFragment item = null;
    private static ClassroomFragment classroom = null;
    private static MessageFragment message = null;
    private static PersonlFragment personl = null;

    private static LostFragment lost = null;
    private static PickFragment pick = null;

    private static final int ITEM = 0;
    private static final int CLASSROM = 1;
    private static final int MESSAGE = 2;
    private static final int PERSONL = 3;


    public static Fragment getFragment(int index){

        Fragment fragment = null;
        switch (index){

            case ITEM:{
                if (null == item)
                    item = new ItemFragment();
                fragment = item;
            }break;

            case CLASSROM:{
                if (null == classroom)
                    classroom = new ClassroomFragment();
                fragment = classroom;
            }break;

            case MESSAGE:{
                if (null == message){
                    message = new MessageFragment();
                    Log.e("this message case","new ok");
                }

                fragment = message;

            }break;

            case PERSONL:{
                if (null == personl){
                    personl= new PersonlFragment(MainActivity.instance);
                }
                Log.e("pseronl fragmentfactory","本来是想执行楼下来初始化");
                //personl.initPersonl(SingleUser.single);
                fragment = personl;
            }break;
        }
        Log.e("返回的fragment是： ",index+"");
        return fragment;
    }

    public static Fragment getFragment(boolean checked){
        Fragment fragment = null;
        if(checked){
            if (null == pick)
                pick = new PickFragment();
            fragment = pick;
        }else{
            if (null == lost)
                lost = new LostFragment();
            fragment = lost;
        }
        return fragment;
    }
}
