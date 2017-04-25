package aakarsh.quizzer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static aakarsh.quizzer.Constants.ADD_CLASS;
import static aakarsh.quizzer.Constants.CLASS_LIST;
import static aakarsh.quizzer.Constants.CLASS_NAME;
import static aakarsh.quizzer.Constants.HEADER;
import static aakarsh.quizzer.Constants.TEACHER_EMAIL;

public class ClassListActivityPerformance extends AppCompatActivity {

    ListView lview;
    ArrayAdapter listAdapter;
    ArrayList<String> classList = new ArrayList<>();
    ArrayList<String> instructorList = new ArrayList<>();
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        lview = (ListView) findViewById(R.id.lview);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabListener);
        getClasses();
    }

    public View.OnClickListener fabListener = new View.OnClickListener() {
        public void onClick(View view) {
            createDialog();
        }
    };

    private void createDialog(){
        LayoutInflater inflater = LayoutInflater.from(ClassListActivityPerformance.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);
        final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);
        final EditText teacherEditText = (EditText)subView.findViewById(R.id.dialogEditText2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a Class");
        builder.setMessage("The class name is something along the lines of \"CS 246\"");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String className = subEditText.getText().toString().trim();
                String teacherEmail = teacherEditText.getText().toString().trim();
                postClasses(className, teacherEmail);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }


    public void getClasses(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jRequest = new StringRequest(Request.Method.GET, CLASS_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arrayOfClasses = object.getJSONArray("classList");
                            for(int i = 0; i < arrayOfClasses.length(); i++){
                                classList.add(arrayOfClasses.getString(i));
                            }

                            JSONArray arrayOfInstructors = object.getJSONArray("instructorList");
                            for(int i = 0; i < arrayOfInstructors.length(); i++){
                                instructorList.add(arrayOfInstructors.getString(i));
                            }

                            generateList();

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

    public void postClasses(String className, String teacherEmail){
        JSONObject classInfo = new JSONObject();
        try {
            classInfo.put("className", className);
            classInfo.put("teacher", teacherEmail);
        }catch (Exception e){

        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest addClassQueue = new JsonObjectRequest(Request.Method.PUT, ADD_CLASS, classInfo, ///JS (object goes right after url)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // System.out.println(response);
                        finish();
                        startActivity(getIntent());
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

        queue.add(addClassQueue); //Add class
    }


    public void generateList(){

        listAdapter = new ArrayAdapter<String>(ClassListActivityPerformance.this, android.R.layout.simple_list_item_1, classList);
        lview.setAdapter(listAdapter);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CLASS_NAME = classList.get(position);
                TEACHER_EMAIL = instructorList.get(position);
                    Intent ClassSummary = new Intent(getApplicationContext(), ClassSummaryActivity.class);
                    startActivity(ClassSummary);


            }

        });
    }

}
