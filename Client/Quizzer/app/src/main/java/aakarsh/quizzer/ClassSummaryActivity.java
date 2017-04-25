package aakarsh.quizzer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static aakarsh.quizzer.Constants.HEADER;
import static aakarsh.quizzer.Constants.*;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class ClassSummaryActivity extends AppCompatActivity {

    TextView classNameView;
    String score, teacher, classname;
    TextView scoreField, totalField, rawCorField, rawTotalField, teacherField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_summary);
        classNameView = (TextView) findViewById(R.id.classNameView);
        scoreField = (TextView) findViewById(R.id.scoreField);
        totalField = (TextView) findViewById(R.id.possibleScorefield);
        rawCorField = (TextView) findViewById(R.id.rawCorrectField);
        rawTotalField = (TextView) findViewById(R.id.rawTotalPossibleField);
        teacherField = (TextView) findViewById(R.id.teachField);
        classNameView.setText(CLASS_NAME.toUpperCase());
        retrieveClassInfo();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    public void retrieveClassInfo(){
        JSONObject classInfo = new JSONObject();
        try {
            classInfo.put("className", CLASS_NAME);
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        String getURL = ATTENDANCE_SUMMARY + "?course=" + CLASS_NAME + "&instructor=" + TEACHER_EMAIL;
        Log.d("REQUEST", getURL);
        StringRequest attendanceRequest = new StringRequest(Request.Method.GET, getURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("REQUEST", response);
                        if(response.equals("nill")){
                            Toast.makeText(getApplicationContext(), "You are not enrolled in this section", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject resp = new JSONObject(response);
                            String totalAsked = String.valueOf(resp.getInt("attendance"));
                            String totalPointsPossible = String.valueOf((2*resp.getInt("attendance")));
                            rawTotalField.setText(String.valueOf(totalAsked));
                            totalField.setText(String.valueOf(totalPointsPossible));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error fetching profile", Toast.LENGTH_SHORT).show();
            }
        });



        JsonObjectRequest addClassQueue = new JsonObjectRequest(Request.Method.POST, CLASS_SUMMARY, classInfo, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            teacher = response.getString("teacher");
                            score = String.valueOf(response.getInt("score"));
                            int totalAsked = response.getInt("total"); //total ASKED
                            int totalPointsPossible = response.getInt("total")*2;
                            JSONArray answers = response.getJSONArray("answers");
                            //System.out.println("TotalAsked: " + totalAsked + " total score: " + score);
                            //Log.v("TG", String.valueOf(totalAsked));
                            //Toast.makeText(getApplicationContext(), String.valueOf(totalAsked), Toast.LENGTH_SHORT).show();
                            scoreField.setText(score);
                            teacherField.setText(response.getString("teacher"));
                            //rawTotalField.setText(String.valueOf(totalAsked));
                            //totalField.setText(String.valueOf(totalPointsPossible));
                            rawCorField.setText(String.valueOf((response.getInt("score") - response.getInt("score")%2)/2));

                            int len = answers.length();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

        queue.add(attendanceRequest);
        queue.add(addClassQueue); //Get class info

    }


}
