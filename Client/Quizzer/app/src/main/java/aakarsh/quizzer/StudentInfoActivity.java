package aakarsh.quizzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static aakarsh.quizzer.Constants.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentInfoActivity extends AppCompatActivity {
    Button goToSessionStarterButton;
    TextView emailView;
    String email, id;
    TextView idView, classView;
    Button performanceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        emailView = (TextView) findViewById(R.id.rawTotalPossibleField);
        goToSessionStarterButton = (Button) findViewById(R.id.goToSessionStarterButton);
        goToSessionStarterButton.setOnClickListener(goToSessionStarterButtonListener);
        idView = (TextView) findViewById(R.id.idField);
        classView = (TextView) findViewById(R.id.classField);
        performanceButton = (Button) findViewById(R.id.performanceButton);
        performanceButton.setOnClickListener(performanceListener);
        retriveStudentInformation();
        getClasses();
    }

    public View.OnClickListener goToSessionStarterButtonListener = new View.OnClickListener() {
        public void onClick (View view){
            Intent ClassListActivity = new Intent(getApplicationContext(), ClassListActivity.class);
            startActivity(ClassListActivity);

        }};
    public View.OnClickListener performanceListener = new View.OnClickListener() {
        public void onClick (View view){
            Intent ClassListActivity = new Intent(getApplicationContext(), ClassListActivityPerformance.class);
            startActivity(ClassListActivity);

        }};

    public void retriveStudentInformation(){
        RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
        StringRequest jRequest = new StringRequest(Request.Method.GET, STUDENT_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);
                            email = object.getString("email");
                            emailView.setText(email);
                            id = object.getString("_id");
                            idView.setText(id);



                        } catch (JSONException e){

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString()); //Error exists


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("x-auth", HEADER);
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(jRequest);
    }

    public void getClasses(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jRequest = new StringRequest(Request.Method.GET, CLASS_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);
                            StringBuilder sb = new StringBuilder();
                            JSONArray arrayOfClasses = object.getJSONArray("classList");
                            int arrLen = arrayOfClasses.length();
                            for(int i = 0; i < arrLen; i++){
                                sb.append(arrayOfClasses.getString(i));
                                if(i == arrLen - 1){
                                    continue;
                                }
                                sb.append(", ");
                            }
                            classView.setText(sb.toString());

                        } catch (JSONException e){

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString()); //Error exists


            }
        }) { @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("x-auth", HEADER);
            return headers;
        }};
// Add the request to the RequestQueue.
        queue.add(jRequest);
    }


}
