package com.project.android_kidstories;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.project.android_kidstories.Api.Api;
import com.project.android_kidstories.Api.Responses.BaseResponse;
import com.project.android_kidstories.Api.Responses.loginRegister.DataResponse;
import com.project.android_kidstories.DataStore.Repository;
import com.project.android_kidstories.Model.User;
import com.project.android_kidstories.Views.main.MainActivity;
import com.project.android_kidstories.sharePref.SharePref;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.security.MessageDigest;

public class RegisterActivity extends AppCompatActivity {

    public static final String USER_TOKEN_KEY = "USER_TOKEN_KEY";

    private static final String TAG = "RegisterActivity";
    public static final int LOGIN_TEXT_REQUEST_CODE = 11;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String AUTH_TYPE = "rerequest";

    EditText emailET;
    EditText phone;
    EditText firstName;
    EditText lastName;
    EditText password, confirmPassword;
    Button regFacebook, regGoogle, SignUp;
    TextView loginText;
    ProgressBar progressBar;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_TEXT_REQUEST_CODE) {
            finish();
        }
    }


    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                firstName.setText("");
                lastName.setText("");
                emailET.setText("");
                Toast.makeText(RegisterActivity.this, "User Logged Out", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        printHashKey(this);
        checkLoginStatus();

        progressBar = findViewById(R.id.reg_progress_bar);
        phone = findViewById(R.id.reg_contact);
        password = findViewById(R.id.reg_password);
        firstName = findViewById(R.id.reg_first_name);
        lastName = findViewById(R.id.reg_last_name);
        emailET = findViewById(R.id.reg_email);
        confirmPassword = findViewById(R.id.reg_confirm_password);

//        regFacebook = findViewById(R.id.reg_facebook);
//        regGoogle = findViewById(R.id.reg_google);
        SignUp = findViewById(R.id.sign_up_button);
        loginText = findViewById(R.id.create_act);

        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
//
//        regFacebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LoginManager.getInstance().setAuthType(AUTH_TYPE)
//                        .logInWithReadPermissions(RegisterActivity.this, Arrays.asList(EMAIL));
//                facebookLogin();
//            }
//        });

        // if user is already registered
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(RegisterActivity.this, LoginActivity.class), LOGIN_TEXT_REQUEST_CODE);
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });
    }

    private void loaduserprofile(AccessToken newAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String First_Name = object.getString("First_Name");
                    String Last_Name = object.getString("Last_Name");
                    String email = object.getString("email");
                    String id = object.getString("id");

                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    emailET.setText(email);
                    firstName.setText(First_Name);
                    lastName.setText(Last_Name);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "First_Name,Last_Name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            // user already signed in
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }
    }

    public void facebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(RegisterActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onSuccess: " + loginResult);
                setResult(RESULT_OK);
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
                /*call : loginResult.getAccessToken().getUserId() to get userId and save to database;*/
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(RegisterActivity.this, "Error " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



     /*  email = findViewById(R.id.reg_email);
        phone = findViewById(R.id.reg_contact);
        fullName = findViewById(R.id.reg_full_name);
        password = findViewById(R.id.reg_password);
        btn = findViewById(R.id.sign_up_button);
        progressBar =  findViewById(R.id.reg_progress_bar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });

        googleSignInButton = findViewById(R.id.reg_google);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.web_client_id))
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        String idToken = account.getIdToken();
                    /*
                      send this id token to server using HTTPS
                     */

//                    } catch (ApiException e) {
//                        // The ApiException status code indicates the detailed failure reason.
//                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//                    }
//                    break;
//            }
//
//    }

    private void signInUser() {
        String email_string = emailET.getText().toString();
        String phone_string = phone.getText().toString();
        String firstName_string = firstName.getText().toString();
        String lastName_string = lastName.getText().toString();
        String password_string = password.getText().toString();

        //validating text fields

        if (TextUtils.isEmpty(email_string) || (!Patterns.EMAIL_ADDRESS.matcher(email_string).matches())) {
            emailET.setError("Please enter a valid email address");
            return;
        }

        if (TextUtils.isEmpty(phone_string) || (!Patterns.PHONE.matcher(phone_string).matches())) {
            phone.setError("Please enter a valid phone number");
            return;
        }

        if (TextUtils.isEmpty(firstName_string) || TextUtils.isEmpty(password_string)) {
            firstName.setError("Please enter a valid phone number");
            password.setError("Enter a password");
            return;
        }
        if (TextUtils.isEmpty(lastName_string) || TextUtils.isEmpty(password_string)) {
            lastName.setError("Please enter a valid phone number");
        }

        if (password_string.length() < 8) {
            password.setError("Password must be at least 8 characters");
        }


        progressBar.setVisibility(View.VISIBLE);

        Repository repository = Repository.getInstance(this.getApplication());
        Api api = repository.getStoryApi();

        User user = new User(firstName_string, lastName_string, email_string);
        user.setPhoneNumber(phone_string);
        user.setPassword(password_string);
        api.registerUser(user)
                .enqueue(new Callback<BaseResponse<DataResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<DataResponse>> call, Response<BaseResponse<DataResponse>> response) {
                        if (response.isSuccessful()) {
                            //TODO: Save User locally
                            try {
                                String token = response.body().getData().getToken();
                                SharePref.getINSTANCE(RegisterActivity.this)
                                        .setString(USER_TOKEN_KEY, token);

                                launchMainActivity();
                            } catch (NullPointerException e) {
                                Toast.makeText(getApplicationContext(), "Problem retrieving user token", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Problem with registration, email already registered", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<DataResponse>> call, Throwable t) {
                        if (t.getMessage() != null) {
                            Log.e(TAG, t.getMessage());
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void launchMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Getting app hash key for facebook login registration
    private static void printHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey: " + hashKey + "=");
            }
        } catch (Exception e) {
            Log.e(TAG, "printHashKey: Error: " + e.getMessage());
        }
    }
}

