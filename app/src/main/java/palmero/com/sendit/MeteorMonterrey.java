package palmero.com.sendit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MeteorMonterrey extends AppCompatActivity {
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meteor_monterrey);
        thread=  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        wait(3000);
                    }
                }
                catch(InterruptedException ex){
                }

                Intent Login = new Intent(MeteorMonterrey.this, LoginActivity.class);
                startActivity(Login);
                MeteorMonterrey.this.finish();
            }
        };

        thread.start();

    }
}
