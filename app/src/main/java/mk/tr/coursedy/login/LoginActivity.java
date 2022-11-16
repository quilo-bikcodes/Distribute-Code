package mk.tr.coursedy.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mk.tr.coursedy.R;
import mk.tr.coursedy.activities.MainActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    TextView register;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_edit_email);
        password = findViewById(R.id.login_edit_password);
        btnLogin = findViewById(R.id.login_btn_login);
        auth = FirebaseAuth.getInstance();

        register = findViewById(R.id.login_txt_reg);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progrss bar

                String mail = email.getText().toString();
                String pass = password.getText().toString();

                if(TextUtils.isEmpty(mail)||TextUtils.isEmpty(pass))
                {
                    Toast.makeText(LoginActivity.this, "Please fill all the fields.", Toast.LENGTH_LONG).show();
                }
                else{
                    auth.signInWithEmailAndPassword(mail,pass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful())
                                    {
                                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = db.getReference().child("Users").child(auth.getCurrentUser().getUid());

                                        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                            @Override
                                            public void onSuccess(DataSnapshot dataSnapshot) {
                                                //progressbar.dismis();
                                                Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(LoginActivity.this,"Login Failed\n"+e,Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    else{
                                        //pb.Dismis
                                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                }


            }
        });

    }


}
