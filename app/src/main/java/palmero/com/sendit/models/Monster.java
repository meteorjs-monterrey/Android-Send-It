package palmero.com.sendit.models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import palmero.com.sendit.ChatActivity;
import palmero.com.sendit.GameActivity;
import palmero.com.sendit.R;

/**
 * Created by rakirox on 3/2/16.
 */
public class Monster {
    private ChatHandler chatHandler;
    private String owner, name, id;
    private int totalLife, currentLife;
    private JSONArray players,messages,attacks;
    private View fragmentView;
    private Monster(){}
    public Monster(String id, JSONObject monster){
        Log.e("MORGOTH", monster.toString());
        Monster.this.owner = monster.optString("owner");
        this.name = monster.optString("name");
        this.totalLife = monster.optInt("totalLife");
        this.currentLife = monster.optInt("currentLife");
        this.players = monster.optJSONArray("players");
        this.messages = monster.optJSONArray("messages");
        this.attacks = monster.optJSONArray("attacks");
        this.id = id;
    }

    public void updateMonster(JSONObject monster) {
        Log.e("updateStuff", monster.toString());
        Iterator<String> it = monster.keys();
        while(it.hasNext()){
            String currentKey = it.next();

            if(currentKey.equals("owner")){
                this.owner = monster.optString(currentKey);
            }else if(currentKey.equals("name")){
                this.name = monster.optString(currentKey);
            }else if(currentKey.equals("totalLife")){
                this.totalLife = monster.optInt(currentKey);
            }else if(currentKey.equals("currentLife")){
                if(chatHandler != null) chatHandler.damageReceived(this);
                this.currentLife = monster.optInt(currentKey);
            }else if(currentKey.equals("players")){
                this.players = monster.optJSONArray(currentKey);
            }else if(currentKey.equals("messages")){
                this.messages = monster.optJSONArray(currentKey);
                Log.e("message",this.messages.toString());
                if(chatHandler != null) chatHandler.chatReceived(getLastMessageId(),getLastMessageEmail(),getLastMessage());
            }else if(currentKey.equals("attacks")){
                this.attacks = monster.optJSONArray(currentKey);
                if(chatHandler != null) chatHandler.attackReceived(getLastDamage(),getLastEmailDamage());
            }

        }

    }

    public void setChatHandler(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    public String getLastMessageId() {
        return messages.optJSONObject(messages.length() -1).optString("_id");
    }
    public int getLastDamage(){
        return attacks.optJSONObject(attacks.length() -1).optInt("damage");
    }
    public String getLastPlayerDamage(){
        return attacks.optJSONObject(attacks.length() -1).optString("player");
    }
    public String getLastEmailDamage(){
        return attacks.optJSONObject(attacks.length() -1).optString("email");
    }
    public String getLastMessageEmail(){
        return messages.optJSONObject(messages.length() -1).optString("player");
    }
    public String getLastMessage(){
        return messages.optJSONObject(messages.length() -1).optString("message");
    }
    public View createMonsterCard(final Context context){

//        if(fragmentView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            fragmentView = inflater.inflate(R.layout.monster_item, null, false);
//        }
        TextView tvCurrentPlayers = (TextView) fragmentView.findViewById(R.id.item_monster_current_players);
        TextView tvCurrentLife = (TextView) fragmentView.findViewById(R.id.item_monster_current_life);
        TextView tvTotalLife = (TextView) fragmentView.findViewById(R.id.item_monster_total_life);
        TextView tvMonstersName = (TextView) fragmentView.findViewById(R.id.item_monster_name);

        tvCurrentLife.setText(this.currentLife + "");
        tvTotalLife.setText(this.totalLife + "");
        tvMonstersName.setText(this.name);
        tvCurrentPlayers.setText(this.players.length() + "");
        fragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Dis dungeon is dangerous, Do you have the ba...?")
                        .setPositiveButton("Sure B*#ch!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((GameActivity)context).loadChatActivity(Monster.this);
                            }
                        })
                        .setNegativeButton("Not really..", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return fragmentView;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public int getTotalLife() {
        return totalLife;
    }

    public int getCurrentLife() {
        return currentLife;
    }

    public JSONArray getPlayers() {
        return players;
    }

    public int getPlayersCount() {
        return players.length();
    }



    public interface ChatHandler{
        void chatReceived(String playerId,String email, String message);
        void damageReceived(Monster monster);
        void attackReceived(int damage,String playerId);
    }
}
