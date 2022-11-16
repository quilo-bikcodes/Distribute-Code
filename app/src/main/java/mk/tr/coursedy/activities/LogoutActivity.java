package mk.tr.coursedy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import mk.tr.coursedy.login.LoginActivity;

public class LogoutActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        auth.signOut();
        Toast.makeText(LogoutActivity.this, "Logout is Successful.", Toast.LENGTH_LONG).show();
        Intent logoutIntent = new Intent(LogoutActivity.this, LoginActivity.class);
        startActivity(logoutIntent);
        finish();

    }
}