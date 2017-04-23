package aakarsh.quizzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import static aakarsh.quizzer.Constants.*;

public class JoinSession extends AppCompatActivity {

    Button join;
    EditText roomID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_session);
        System.out.println(CLASS_NAME);
        roomID =(EditText) findViewById(R.id.roomText);
        join = (Button) findViewById(R.id.joinButton);
        join.setOnClickListener(joinListener);
    }


    public View.OnClickListener joinListener = new View.OnClickListener() {
        public void onClick (View view){
            JOINED_ROOM = roomID.getText().toString();
            Intent SessionRoom = new Intent(getApplicationContext(), Session.class);
            startActivity(SessionRoom);
            finish();

        }};
}
