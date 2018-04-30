package yukon.runforwater;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Arrays;

public class LogIn extends AppCompatActivity implements
        View.OnClickListener, FacebookCallback<LoginResult> {

    private static final String TAG = "Log In Activity";
    private static final int RC_SIGN_IN = 123;

    private TextView mStatusTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private CheckBox mKenyan;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private CallbackManager mCallbackManager;


    // [START Android Lifecycle Methods]
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .build(),
                RC_SIGN_IN);

        setContentView(R.layout.log_in);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mKenyan = findViewById(R.id.typeofUser);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);

        // Firebase Refs
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // FB INIT
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentFUser = mAuth.getCurrentUser();
        updateUI(currentFUser);
    }
    // [END Android Lifecycle Methods]


    // [START AUTHENTICATION]
    private void createAccount(final String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");

                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);

                        if (user == null) {
                            Log.e("Create Account:failure", "Failed to resolve either Fuser or getCurrentUser");
                            return;
                        }

                        // Save user info to database
                        myRef = database.getReference();
                        User newUser = new User.UserBuilder(email, mKenyan.isChecked(), email).build();
                        myRef.child("Users").child(user.getUid()).setValue(newUser);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LogIn.this, String.valueOf(task.getException()),
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LogIn.this, String.valueOf(task.getException()),
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    // [START_EXCLUDE]
                    if (!task.isSuccessful()) {
                        mStatusTextView.setText(R.string.auth_failed);
                    }

                    // [END_EXCLUDE]
                });

        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Enter your email");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Enter a password");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }
    // [END AUTHENTICATION]


    // [START UI MANAGEMENT]
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));

            Intent intent = new Intent(this, LoggedInMain.class);
            Gson gS = new Gson();
            String target = gS.toJson(user);

            intent.putExtra("Firebase User", target);
            startActivity(intent);
        } else {
            mStatusTextView.setText(R.string.signed_out);
        }
    }

    @Override
    public void onBackPressed() {
    }
    // [END UI MANAGEMENT]


    // [START FB AUTH]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d(TAG, "facebook:onSuccess:" + loginResult);
        handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "facebook:onCancel");
        // [START_EXCLUDE]
        updateUI(null);
        // [END_EXCLUDE]
    }

    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "facebook:onError", error);
        // [START_EXCLUDE]
        updateUI(null);
        // [END_EXCLUDE]
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("KIERAN", String.valueOf(mAuth.getCurrentUser()));
                        final FirebaseUser Fuser = mAuth.getCurrentUser();
                        updateUI(Fuser);

                        // Save the facebook user's info to the database
                        myRef = database.getReference();
                        if (Fuser == null || mAuth.getCurrentUser() == null) {
                            Log.e("signIn:failure", "Failed to resolve either Fuser or getCurrentUser");
                            return;
                        }
                        User newUser = new User.UserBuilder(Fuser.getEmail(), mKenyan.isChecked(), mAuth.getCurrentUser().getDisplayName()).build();
                        myRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("email").setValue(newUser.getEmail());
                        myRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("kenyan").setValue(newUser.getKenyan());
                        myRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("username").setValue(newUser.getUsername());
                        myRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("FBProfileId").setValue(String.valueOf(Profile.getCurrentProfile().getId()));

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LogIn.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }
    // [END FB AUTH]
}