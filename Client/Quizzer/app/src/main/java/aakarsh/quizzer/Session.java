package aakarsh.quizzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static aakarsh.quizzer.Constants.*;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Session extends AppCompatActivity {

    HashMap<String, String> questionOptionsMap = new HashMap<String, String>();
    private Socket socket;
    private String answer = "";
    private String CORRECT_ANSWER = ""; //provided by the server via socket event.
    int POINTS = 0; //points student earned from answering.
    TextView answerChoice, sessionLabel;
    ProgressBar bar;
    Button A, B, C, D;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        answerChoice = (TextView) findViewById(R.id.answerTextView);
        sessionLabel = (TextView) findViewById(R.id.sessionLabel);
        sessionLabel.setVisibility(View.INVISIBLE);
        bar = (ProgressBar) findViewById(R.id.bar);
        bar.setVisibility(View.VISIBLE);
        A = (Button) findViewById(R.id.A);
        B = (Button) findViewById(R.id.B);
        C = (Button) findViewById(R.id.C);
        D = (Button) findViewById(R.id.D);
        A.setVisibility(View.INVISIBLE);
        B.setVisibility(View.INVISIBLE);
        C.setVisibility(View.INVISIBLE);
        D.setVisibility(View.INVISIBLE);
        A.setOnClickListener(optionAListener);
        B.setOnClickListener(optionBListener);
        C.setOnClickListener(optionCListener);
        D.setOnClickListener(optionDListener);
        System.out.println(JOINED_ROOM);
        try {
            socket = IO.socket(BASE_URL);
            SocketConnection();
            //joinConnection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void SocketConnection() throws URISyntaxException {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {



            @Override
            public void call(Object... args) {

                JSONObject dataToSendToJoin = new JSONObject();

                try{
                    dataToSendToJoin.put("room", JOINED_ROOM);
                    dataToSendToJoin.put("classname", CLASS_NAME);
                } catch(JSONException e){

                }

                socket.emit("join", dataToSendToJoin);
            }

        }).on("successJoiningRoom", new Emitter.Listener() { //Joined session successfully.

            @Override
            public void call(Object... args) {
                turnOffBar();
                JSONObject obj = (JSONObject) args[0];
                String objS = "";
                try {
                    objS = obj.getString("server");
                    System.out.println(objS);
                    makeToast(objS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }).on("failedJoiningRoom", new Emitter.Listener() { //instructor takes away question

            @Override
            public void call(Object... args) {
                turnOffBar();
                JSONObject obj = (JSONObject) args[0];
                String objS = "";
                try {
                    objS = obj.getString("server");
                    System.out.println(objS);
                    makeToast(objS);
                    socket.disconnect();
                    Intent JoinSessionActivity = new Intent(getApplicationContext(), JoinSession.class);
                    startActivity(JoinSessionActivity);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }).on("receiveQuestion", new Emitter.Listener() { //receive question

            @Override
            public void call(Object... args) {
                final JSONObject obj = (JSONObject) args[0];
                itemsVisible();


            }

        }).on("closeQuestion", new Emitter.Listener() { //instructor takes away question

            @Override
            public void call(Object... args) {
                final JSONObject response = (JSONObject) args[0];
                try {
                    itemsInvisible();
                    questionOptionsMap.clear();
                    CORRECT_ANSWER = response.getString("correct_answer").toLowerCase();
                    sendAnswerToServer();
                    makeToast(response.getString("server"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*JSONObject obj = new JSONObject();
                try {
                    obj.put("answer", answer);
                    obj.put("header", HEADER);
                    obj.put("room", JOINED_ROOM);
                    socket.emit("sendAnswer", obj);
                    questionOptionsMap.clear();
                    itemsInvisible();

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/


            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

            }

        });
        socket.connect();
    }
    @Override
    public void onBackPressed() {
        JSONObject disconnectObj = new JSONObject();
        try {
            disconnectObj.put("room", JOINED_ROOM);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("disconnectStudent", disconnectObj);
        System.out.println("Back button pressed");
        socket.disconnect();
        super.onBackPressed();
        return;
    }


    public void joinConnection(){
           /* JSONObject obj = new JSONObject();
            try {
                obj.put("room", "ROOM1");
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

    }

    public void sendAnswerToServer(){
        //put request
        if(answer.equals("")){
            POINTS = 0;
        }
        else if(answer.toLowerCase().equals(CORRECT_ANSWER)){
            POINTS = 2;
        } else{
            POINTS = 1;
        }
        JSONObject answerInfo = new JSONObject();
        try {
            answerInfo.put("answer", answer);
            answerInfo.put("points", POINTS);
            answerInfo.put("className", CLASS_NAME.toLowerCase().replaceAll("\\s",""));
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest answerQuestionQueue = new JsonObjectRequest(Request.Method.PUT,QUESTION_ANSWERED, answerInfo, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }) { @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("x-auth", HEADER);
            return headers;
        }};

        queue.add(answerQuestionQueue); //Answer Question
    }

    public View.OnClickListener optionAListener = new View.OnClickListener() {
        public void onClick (View view){
            answer = "A";
            answerChoice.setText(answer);
        }};

    public View.OnClickListener optionBListener = new View.OnClickListener() {
        public void onClick (View view){
            answer = "B";
            answerChoice.setText(answer);
        }};

    public View.OnClickListener optionCListener = new View.OnClickListener() {
        public void onClick (View view){
            answer = "C";
            answerChoice.setText(answer);
        }};

    public View.OnClickListener optionDListener = new View.OnClickListener() {
        public void onClick (View view){
            answer = "D";
            answerChoice.setText(answer);
        }};


    //UI THREADS

    public void makeToast(final String toMakeToastWith){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toMakeToastWith, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void turnOffBar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void itemsVisible(){
        /*questionOptionsMap.put("A", obj.getString("A"));
        questionOptionsMap.put("B", obj.getString("B"));
        questionOptionsMap.put("C", obj.getString("C"));
        questionOptionsMap.put("D", obj.getString("D"));*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                A.setVisibility(View.VISIBLE);
                B.setVisibility(View.VISIBLE);
                C.setVisibility(View.VISIBLE);
                D.setVisibility(View.VISIBLE);
                sessionLabel.setVisibility(View.VISIBLE);
                answerChoice.setVisibility(View.VISIBLE);
            }
        });
    }

    public void itemsInvisible(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                A.setVisibility(View.INVISIBLE);
                B.setVisibility(View.INVISIBLE);
                C.setVisibility(View.INVISIBLE);
                D.setVisibility(View.INVISIBLE);
                sessionLabel.setVisibility(View.INVISIBLE);
                answerChoice.setText("");
                answer = "";
                answerChoice.setVisibility(View.INVISIBLE);
            }
        });


    }


}
