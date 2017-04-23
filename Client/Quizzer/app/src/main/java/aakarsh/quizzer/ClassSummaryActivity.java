package aakarsh.quizzer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
        classNameView.setText(CLASS_NAME);
        retrieveClassInfo();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    public void retrieveClassInfo(){
        JSONObject classInfo = new JSONObject();
        try {
            classInfo.put("className", CLASS_NAME.toUpperCase());
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

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
                            scoreField.setText(score);
                            teacherField.setText(response.getString("teacher"));
                            rawTotalField.setText(String.valueOf(totalAsked));
                            totalField.setText(String.valueOf(totalPointsPossible));
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

        queue.add(addClassQueue); //Get class info

    }


}
