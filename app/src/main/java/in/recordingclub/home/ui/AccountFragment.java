package in.recordingclub.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import in.recordingclub.MainActivity;
import in.recordingclub.admin.ChangeUserRole;
import in.recordingclub.admin.DetailsManager;
import in.recordingclub.admin.GiveSubscription;
import in.recordingclub.admin.SendNotification;
import in.recordingclub.admin.audio_books_manager.AudioBooksManagerActivity;
import in.recordingclub.admin.m_profiles_manager.M_Profiles_Manager_Activity;
import in.recordingclub.admin.newspaper_manager.NewspaperManagerActivity;
import in.recordingclub.databinding.AccountFragmentBinding;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private AccountFragmentBinding binding;
    private AppInitializer initializer;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AccountFragmentBinding.inflate(inflater, container, false);
        initializer = new AppInitializer(getContext(), (AppCompatActivity) getActivity());
        getAccountInfo();
        binding.accountAdminBtnNewspaperManager.setOnClickListener(this::onClick);
        binding.accountAdminBtnAudioBooksManager.setOnClickListener(this::onClick);
        binding.sendNotificationBtnAdmin.setOnClickListener(this::onClick);
        binding.mProfilesManager.setOnClickListener(this::onClick);
        binding.accountChangeRoleBtn.setOnClickListener(this::onClick);
        binding.logoutAccount1.setOnClickListener(this::onClick);
        binding.adminDetailsManager.setOnClickListener(this::onClick);
        binding.adminGiveSubscription.setOnClickListener(this::onClick);
        binding.tv3.setVisibility(View.GONE);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);
        return binding.getRoot();
    }

    private void getAccountInfo() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Loading", "Loading profile ...");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().account_get_info, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        int user_role = jsonObject.getInt("user_role");
                        if (user_role == 0) {
                            int date = jsonObject.getInt("exp_d");
                            int month = jsonObject.getInt("exp_m");
                            int year = jsonObject.getInt("exp_y");
                            String expireValidity = "Subscription Valid Till : " + date + "/" + month + "/" + year;
                            binding.tv3.setText(expireValidity);
                            binding.tv3.setVisibility(View.VISIBLE);
                        }
                        if (user_role == 1) {
                            binding.accountAdminPanel.setVisibility(View.VISIBLE);

                            binding.logoutAccount1.setVisibility(View.GONE);
                        } else {
                            binding.accountAdminPanel.setVisibility(View.GONE);

                        }
                        binding.accountTv1.setText(firebaseUser.getDisplayName());
                        binding.accountTv2.setText(firebaseUser.getEmail());
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getContext(), "Using account: " + firebaseUser.getDisplayName() + ", " + firebaseUser.getEmail() + ".", Toast.LENGTH_SHORT).show();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    initializer.getAppFunctions().setAlertDialogForResponseError();
                    firebaseAuth.signOut();
                    startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    getActivity().finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                initializer.getAppFunctions().setAlertDialogForServerError();
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_email_address", firebaseUser.getEmail());
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }


    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.adminDetailsManager.getId()) {
            startActivity(new Intent(getContext(), DetailsManager.class));
        }
        if (id == binding.accountAdminBtnNewspaperManager.getId()) {
            startActivity(new Intent(getContext(), NewspaperManagerActivity.class));
        } else if (id == binding.accountAdminBtnAudioBooksManager.getId()) {
            startActivity(new Intent(getContext(), AudioBooksManagerActivity.class));
        } else if (id == binding.sendNotificationBtnAdmin.getId()) {
            startActivity(new Intent(getContext(), SendNotification.class));
        } else if (id == binding.mProfilesManager.getId()) {
            startActivity(new Intent(getContext(), M_Profiles_Manager_Activity.class));
        } else if (id == binding.logoutAccount1.getId()) {
            logAccount();
        } else if (id == binding.accountChangeRoleBtn.getId()) {
            startActivity(new Intent(getContext(), ChangeUserRole.class));
        } else if (id == binding.adminGiveSubscription.getId()) {
            startActivity(new Intent(getContext(), GiveSubscription.class));
        }
    }

    public void logAccount() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.signOut();
        firebaseUser = null;
        mClient.signOut();
        Toast.makeText(getContext(), "Logged Out Successfully.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
