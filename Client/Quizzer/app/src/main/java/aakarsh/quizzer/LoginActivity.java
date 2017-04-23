package aakarsh.quizzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static aakarsh.quizzer.Constants.*;

public class LoginActivity extends AppCompatActivity {

    Button signUp, logIn;
    EditText email, password;
    String emailStr, passwordStr;
    ProgressBar bar;
    AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signUp = (Button) findViewById(R.id.signUp);
        logIn = (Button) findViewById(R.id.logIn);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setVisibility(View.INVISIBLE);
        email = (EditText) findViewById(R.id.emailField);
        password = (EditText) findViewById(R.id.passwordField);
        logIn.setOnClickListener(logInListener);
        signUp.setOnClickListener(signUpListener);

    }

    public View.OnClickListener signUpListener = new View.OnClickListener() {
        public void onClick (View view){
            emailStr = email.getText().toString();
            passwordStr = password.getText().toString();
            //teacherRegister();
            bar.setVisibility(View.VISIBLE);
            studentRegister();

        }};

    public View.OnClickListener logInListener = new View.OnClickListener() {
        public void onClick (View view){
            emailStr = email.getText().toString();
            passwordStr = password.getText().toString();
            bar.setVisibility(View.VISIBLE);
            studentLogin();

        }};

    public void teacherRegister(){

        JSONObject logInInfo = new JSONObject();
        try {
            logInInfo.put("email", emailStr);
            logInInfo.put("password", passwordStr);
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest teacherSignUpReq = new JsonObjectRequest(SIGNUP_TEACHER, logInInfo, ///JS (object goes right after url)
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
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                HEADER = response.headers.get("x-auth");
                System.out.println(HEADER.toString());
                return super.parseNetworkResponse(response);
            }};

        queue.add(teacherSignUpReq); //LOG IN

    }

    public void studentRegister(){

        JSONObject logInInfo = new JSONObject();
        try {
            logInInfo.put("email", emailStr);
            logInInfo.put("password", passwordStr);
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest studentSignUpReq = new JsonObjectRequest(SIGNUP_STUDENT, logInInfo, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        bar.setVisibility(View.INVISIBLE);
                        Intent StudentInfo = new Intent(getApplicationContext(), StudentInfoActivity.class);
                        startActivity(StudentInfo);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "An account with the email already exists", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                HEADER = response.headers.get("x-auth");
                System.out.println(HEADER.toString());
                return super.parseNetworkResponse(response);
            }};

        queue.add(studentSignUpReq); //SIGN UP STUDENT

    }

    public void teacherLogin(){

    }

    public void studentLogin() {

        JSONObject logInInfo = new JSONObject();
        try {
            logInInfo.put("email", emailStr);
            logInInfo.put("password", passwordStr);
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest studentSignUpReq = new JsonObjectRequest(LOGIN_STUDENT, logInInfo, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        bar.setVisibility(View.INVISIBLE);
                        Intent StudentInfo = new Intent(getApplicationContext(), StudentInfoActivity.class);
                        startActivity(StudentInfo);
                        finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                bar.setVisibility(View.INVISIBLE);
                int errorCode = error.networkResponse.statusCode;
                if(errorCode == 401){
                    Toast.makeText(getApplicationContext(), "Unauthorized: Incorrect password or email", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), "Bad request", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                HEADER = response.headers.get("x-auth");
                System.out.println(HEADER.toString());
                return super.parseNetworkResponse(response);
            }};

        queue.add(studentSignUpReq); //SIGN UP STUDENT
    }

}
