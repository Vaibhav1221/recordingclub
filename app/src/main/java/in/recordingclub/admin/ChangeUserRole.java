package in.recordingclub.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.R;

public class ChangeUserRole extends AppCompatActivity {

    AppInitializer initializer;
    TextInputLayout textInputLayout;
    TextInputEditText textInputEditText;
    Spinner drawpdown;
    Button changeButton;
    ArrayAdapter<String> arrayAdapter;
    String selectedRole, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Change User Role");
        setContentView(R.layout.change_user_role);
        initializer = new AppInitializer(getApplicationContext(), ChangeUserRole.this);
        textInputLayout = findViewById(R.id.et_change_1);
        textInputEditText = findViewById(R.id.user_roll_email);
        drawpdown = findViewById(R.id.spinner_roll);
        changeButton = findViewById(R.id.change_roll_btn);
        changeButton.setEnabled(false);
        textInputLayout.setHint("Email Address:");
        String[] roleList = {"Select User Role", "Admin", "Free User", "User"};
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, roleList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    changeButton.setEnabled(false);
                    return false;
                } else {
                    return true;
                }
            }
        };
        drawpdown.setAdapter(arrayAdapter);
        drawpdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = String.valueOf(parent.getItemAtPosition(position));
                changeButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                changeButton.setEnabled(false);
            }
        });
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = textInputEditText.getText().toString();
                setRole();
            }
        });
    }

    private void setRole() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please wait", "Changing user role.");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().setUserRole, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("msg");
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(ChangeUserRole.this, msg, Toast.LENGTH_SHORT).show();
                        textInputEditText.setText("");
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(ChangeUserRole.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    e.printStackTrace();
                    Toast.makeText(ChangeUserRole.this, "Error in networking : " + e, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(ChangeUserRole.this, "Error in server connection.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_email_address", userEmail);
                params.put("user_role", selectedRole);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

}