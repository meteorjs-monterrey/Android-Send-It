package palmero.com.sendit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;
import palmero.com.sendit.models.Monster;

public class GameActivity extends AppCompatActivity implements MeteorCallback{
    private Meteor mMeteor;
    Map<String, Monster> mMonsters;
    GameActivityFragment fragmentMonsters;
    private String monsterSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeteor.call("newMonster");
            }
        });
        fragmentMonsters = (GameActivityFragment) this.getSupportFragmentManager().findFragmentById(R.id.fragment_monsters);
        mMeteor = MeteorSingleton.getInstance();
        mMeteor.setCallback(this);
        monsterSubscription = mMeteor.subscribe("monsters");
        mMonsters = new LinkedHashMap<>();
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
        if(collectionName.equals("monsters")){
            try {
                mMonsters.put(documentId,new Monster(documentId,new JSONObject(newValuesJson)));
                fragmentMonsters.updateMonsters((LinkedHashMap<String, Monster>) mMonsters);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDataChanged(String collectionName, String documentId, String updatedValuesJson, String removedValuesJson) {
        Log.e("onDataChanged","onDataChanged");
        try {
            mMonsters.get(documentId).updateMonster(new JSONObject(updatedValuesJson));
            fragmentMonsters.updateMonsters((LinkedHashMap<String, Monster>) mMonsters);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        Log.e("onDataRemoved","onDataRemoved");
        mMonsters.remove(documentID);
        fragmentMonsters.updateMonsters((LinkedHashMap<String, Monster>) mMonsters);
    }
    class CustomResultListener implements  ResultListener{
        Monster monster;
        public CustomResultListener(Monster monster) {
            this.monster = monster;
        }

        @Override
        public void onSuccess(String s) {}
        @Override
        public void onError(String s, String s1, String s2) {}
    }
    public void loadChatActivity(Monster monster) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("_id", monster.getId());
        mMeteor.call("JoinDungeon", new Object[]{values},new CustomResultListener(monster) {
            @Override
            public void onSuccess(String s) {
                mMeteor.unsetCallback(GameActivity.this);
                mMeteor.unsubscribe(monsterSubscription);
                Intent chatActivity = new Intent(GameActivity.this, ChatActivity.class);
                chatActivity.putExtra(ChatActivity.MONSTER, monster.getId());
                GameActivity.this.startActivity(chatActivity);
            }

            @Override
            public void onError(String s, String s1, String s2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setMessage("Sorry dis dungeon has a problem...")
                        .setPositiveButton("So what?", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
}
