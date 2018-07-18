package com.example.benedictlutab.sidelinetg.modules.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.modules.signup.accountKitActivity;
import com.example.benedictlutab.sidelinetg.modules.signup.signupActivity;
import com.example.benedictlutab.sidelinetg.modules.viewHome.homeActivity;
import com.example.benedictlutab.sidelinetg.modules.welcomeUser.entranceActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class loginActivity extends AppCompatActivity
{
    @BindView(R.id.etRecoverAccount) EditText etRecoverAccount;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;

    private String ROLE = "Task Giver";
    private String message, response_code;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_login);

        ButterKnife.bind(this);

        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/ralewayRegular.ttf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        // Make uneditable.
        etRecoverAccount.setFocusable(false);
    }

    @OnClick(R.id.btnLogin)
    public void loginUser()
    {
        Log.e("loginUser:", "START!");
        // Get route obj.
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText("");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_LOGIN,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        swalDialog.hide();
                        // Showing response message coming from server.
                        Log.e("RESPONSE: ", ServerResponse);

                        // Fetch JSON Response
                        try
                        {
                            JSONArray jsonArray = new JSONArray(ServerResponse);

                            for(int x = 0; x < jsonArray.length(); x++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(x);
                                message          = jsonObject.getString("message");
                                response_code    = jsonObject.getString("response_code");

                                Log.e("Fetch jsonArray:", message + response_code);
                            }

                            if(message.equals("Invalid email or password") && response_code.equals("ERROR"))
                            {
                                TastyToast.makeText(getApplicationContext(), message, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                            }
                            else if(message.equals("Account does not exists.") && response_code.equals("ERROR"))
                            {
                                TastyToast.makeText(getApplicationContext(), message, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                            }
                            else if(response_code.equals("SUCCESS"))
                            {
                                // Store USER_ID to SharedPreferences
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("USER_ID", message);
                                editor.apply();

                                // Go to homeActivity
                                Intent intent = new Intent(loginActivity.this, homeActivity.class);
                                finish();
                                startActivity(intent);
                                Log.e("loginUser:", "SUCCESS!" + message);
                            }

                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                            Log.e("loginUser (CATCH): ", e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        // Showing error message if something goes wrong.
                        Log.e("Error Response:", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                // Sending all registration fields to 'Parameter'.
                Parameter.put("email", etEmail.getText().toString());
                Parameter.put("password", etPassword.getText().toString());
                Parameter.put("role", ROLE);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(loginActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }
}
