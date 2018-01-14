package com.example.zhkuapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.MessageContainer;
import com.example.zhkuapp.utils.MessageAdaptor;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.utils.NetUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity implements EMMessageListener {

    @Bind(R.id.chat_toolbar)
    Toolbar chatToolbar;

    @Bind(R.id.chat_recycleview)
    RecyclerView chatRecycleview;

    @Bind(R.id.et_message)
    EditText etMessage;
    @Bind(R.id.btn_send)
    Button btnSend;

    //当前对话的用户id
    private String chatObject = "";

    private String userName = "";

    private List<EMMessage> messages;

    // 消息监听器
    private EMMessageListener mMessageListener;

    // 当前会话对象
    private EMConversation mConversation;

    private MessageAdaptor myAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        init();

        initConversation();

        initButton();

        initEdittext();
    }

    //获取聊天对象的id
    private void init() {

        //获取当前对话的用户id
        Intent intent = getIntent();
        chatObject = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");

        //返回按钮事件监听
        chatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置聊天标题
        chatToolbar.setTitle("与"+userName+"聊天");

        mMessageListener = this;

        //当前聊天对象的消息记录存储
        messages = new ArrayList<>();


        //布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        chatRecycleview.setLayoutManager(linearLayoutManager);

        //设置适配器
        myAdaptor = new MessageAdaptor(messages);
        chatRecycleview.setAdapter(myAdaptor);
    }


    //发送按钮事件监听
    private void initButton() {

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isNetworkConnected(ChatActivity.this))
                    MyToast.show(ChatActivity.this, "网络有问题！");
                else {
                    String message = etMessage.getText().toString();
                    sendMessage(message);
                }
            }
        });

    }


    /**
     * 初始化会话对象，并且根据需要加载更多消息
     * 加载与某个用户的历史消息记录
     */
    private void initConversation() {

        /**
         * 初始化会话对象，这里有三个参数么，
         * 第一个表示会话的当前聊天的 useranme 或者 groupid
         * 第二个是绘画类型可以为空
         * 第三个表示如果会话不存在是否创建
         */
        mConversation = EMClient.getInstance().chatManager().getConversation(chatObject, null, true);


        if (null != mConversation){

            // 设置当前会话为已读
            mConversation.markAllMessagesAsRead();

            //获取最后一条消息记录
            EMMessage lastMessage = mConversation.getLastMessage();

            //如果是第一次会话
            if (null != lastMessage){

                //获取最后一条消息的id
                String msgID = lastMessage.getMsgId();

                //获取消息记录的总数
                int allMsgCount = mConversation.getAllMsgCount();

                //从最后一条数据开始加载所有消息记录，但是不包括最后一条消息记录
                List<EMMessage> temp = mConversation.loadMoreMsgFromDB(msgID,allMsgCount);

                //添加消息记录集
                addMessages(temp);

                //添加最后一条消息
                addMessage(lastMessage);

                //更新
                updateChatView();
            }

        }

    }


    //定位到底部
    private void toBottom() {
        int position = myAdaptor.getItemCount();
        if ( position > 0){
            chatRecycleview.smoothScrollToPosition(position-1);
        }
    }

    //添加消息
    private void addMessage(EMMessage message){
        if (null != message)
            messages.add(message);
    }

    //添加消息群集
    private void addMessages(List<EMMessage> list){
        if (null != list)
            messages.addAll(list);
    }

    //更新消息列表
    private void updateChatView() {
        myAdaptor.notifyDataSetChanged();
        toBottom();
    }

    //发送消息
    private void sendMessage(String msg) {

        //清空输入框内容
        etMessage.setText("");

        // 创建一条新消息，第一个参数为消息内容，第二个为接受者username
        EMMessage message = EMMessage.createTxtSendMessage(msg, chatObject);

        //向消息列表添加消息
        addMessage(message);
        //updateChatView();

        // 调用发送消息的方法
        EMClient.getInstance().chatManager().sendMessage(message);

        // 为消息设置回调
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                // 消息发送成功，打印下日志，正常操作应该去刷新ui
                updateChatView();
            }

            @Override
            public void onError(int i, String s) {
                // 消息发送失败，打印下失败的信息，正常操作应该去刷新ui
                updateChatView();
            }

            @Override
            public void onProgress(int i, String s) {
                // 消息发送进度，一般只有在发送图片和文件等消息才会有回调，txt不回调
                updateChatView();
            }

        });

    }


    //监听输入框的内容
    private void initEdittext() {
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //监听输入框的内容是否为空,空则使发送按钮不可用
                if (s.length() == 0)
                    btnSend.setEnabled(false);
                else
                    btnSend.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        // 添加消息监听
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 自定义实现Handler，主要用于刷新UI操作
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateChatView();
        }
    };


    /**
     * --------------------------------- Message Listener -------------------------------------
     * 环信消息监听主要方法
     */
    /**
     * 收到新消息
     *
     * @param list 收到的新消息集合
     */

    @Override
    public void onMessageReceived(List<EMMessage> list) {

        if (null != list){
            //MessageContainer.addMessage(list);
            addMessages(list);
            Message msg = new Message();
            handler.sendMessage(msg);
        }

    }

    /**
     * 收到新的 CMD 消息
     *
     * @param list
     */
    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        for (int i = 0; i < list.size(); i++) {
            // 透传消息
            EMMessage cmdMessage = list.get(i);
            EMCmdMessageBody body = (EMCmdMessageBody) cmdMessage.getBody();
            Log.i("lzan13", body.action());
        }
    }


    /**
     * 收到新的已读回执
     *
     * @param list 收到消息已读回执
     */
    @Override
    public void onMessageRead(List<EMMessage> list) {

    }


    /**
     * 收到新的发送回执
     * TODO 无效 暂时有bug
     *
     * @param list 收到发送回执的消息集合
     */
    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }


    /**
     * 消息的状态改变
     *
     * @ message 发生改变的消息
     * @ object  包含改变的消息
     */
    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }
}
