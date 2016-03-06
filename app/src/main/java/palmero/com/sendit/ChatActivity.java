package palmero.com.sendit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;
import palmero.com.sendit.models.ChatAdapter;
import palmero.com.sendit.models.ChatMessage;
import palmero.com.sendit.models.Monster;

public class ChatActivity extends AppCompatActivity implements MeteorCallback, Monster.ChatHandler {
    public static final String MONSTER = "MONSTER_ID";
    private Monster mMonster;
    private Meteor mMeteor;
    private EditText messageET;
    private ListView messagesContainer;
    private ChatAdapter adapter;
    private TextView tvPlayers,tvLife,tvName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        String monsterId = intent.getStringExtra(ChatActivity.MONSTER);
        mMeteor = MeteorSingleton.getInstance();
        mMeteor.setCallback(this);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("_id", monsterId);
        mMeteor.subscribe("monsters",new Object[]{values});
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        messageET = (EditText) findViewById(R.id.messageEdit);
//        loadDummyHistory();
        messageET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText) || mMonster ==null) {
                    return true;
                }
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("_id", mMonster.getId());
                String method = "";
                if(messageText.equals("/attack")){
                    method = "attack";
                }else{
                    values.put("message", messageET.getText().toString());
                    method = "message";
                }
                mMeteor.call(method,new Object[]{values});
                messageET.setText("");

                return true;
            }
        });
        messageET.setImeActionLabel("Derp", KeyEvent.KEYCODE_ENTER);
        tvPlayers = (TextView) findViewById(R.id.chat_monster_players);
        tvLife = (TextView) findViewById(R.id.chat_monster_current_life);
        tvName = (TextView) findViewById(R.id.chat_monster_name);






    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }
    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void addMessage(boolean isMe, String email, String message){
        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(isMe);
        msg.setMessage(message);
        msg.setEmail(email);
        displayMessage(msg);
    }
    private void addAttack(int damage, String playerId) {
        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(false);
        msg.setAttack(true);
        msg.setMessage(damage + "");
        msg.setEmail(playerId);
        displayMessage(msg);


    }

    @Override
    public void onConnect(boolean signedInAutomatically) {

    }

    @Override
    public void onDisconnect() {
        Log.e("onDisconnect","onDisconnect");
    }

    @Override
    public void onException(Exception e) {
        Log.e("onException","onException");
    }

    @Override
    public void onDataAdded(String collectionName, String documentId, String newValuesJson) {
        Log.e("onDataAdded",collectionName + " : " + newValuesJson);
        try {
            updateMonster(documentId, new JSONObject(newValuesJson));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void updateMonster(String documentId, JSONObject monster){
        if(mMonster ==null){
            mMonster = new Monster(documentId,monster);
            mMonster.setChatHandler(this);
        }else{
            mMonster.updateMonster(monster);
        }
        updateMonsterInterface();
    }
    private void updateMonsterInterface(){
        tvPlayers.setText(mMonster.getPlayersCount() + "");
        tvLife.setText(mMonster.getCurrentLife() + "");
        tvName.setText(mMonster.getName());
    }
    @Override
    public void onDataChanged(String collectionName, String documentId, String updatedValuesJson, String removedValuesJson) {
        Log.e("onDataChanged","onDataChanged");
        try {
            updateMonster(documentId, new JSONObject(updatedValuesJson));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        Log.e("onDataRemoved","onDataRemoved");
    }

    @Override
    public void chatReceived(String playerId, String email, String message) {
        addMessage((mMeteor.getUserId().equals(playerId)),email,message);
    }

    @Override
    public void damageReceived(Monster monster) {

    }

    @Override
    public void attackReceived(int damage,String playerId) {
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        this.findViewById(R.id.shake_it).startAnimation(shake);
        addAttack(damage, playerId);
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

}
