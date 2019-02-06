package yukon.runforlife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.facebook.CallbackManager;
import com.facebook.login.Login;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.Arrays;

public class LogIn extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

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

        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent = new Intent(this, LoggedInMain.class);
                Gson gS = new Gson();
                String target = gS.toJson(user);

                intent.putExtra("Firebase User", target);
                startActivity(intent);
                // ...
            } else {
                // Sign in failed.
                // Do nothing
            }
        }
    }
}