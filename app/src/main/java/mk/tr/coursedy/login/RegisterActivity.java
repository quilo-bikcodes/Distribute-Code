package mk.tr.coursedy.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import mk.tr.coursedy.R;
import mk.tr.coursedy.models.RegisterUser;
import mk.tr.coursedy.models.User;

public class RegisterActivity extends AppCompatActivity {

    EditText mail, password, confirm_pass;
    Button register;
    TextView login;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mail = findViewById(R.id.reg_edit_email);
        password = findViewById(R.id.reg_edit_password);
        confirm_pass = findViewById(R.id.reg_edit_confirm_pass);
        register = findViewById(R.id.reg_btn_register);
        login = findViewById(R.id.reg_txt_login);

        auth= FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressBar = new ProgressBar(RegisterActivity.this);
                String email = mail.getText().toString();
                String pass = password.getText().toString();
                String confirmpass = confirm_pass.getText().toString();
                String type = "";

                if(TextUtils.isEmpty(email)){
                    mail.setError("Cannot leave Mail ID empty");
                }
                else{
                    String[] split = new String[0];
                    split = email.split("@");
                    String domain = split[1];
                    if (domain.equals("vitstudent.ac.in")){
                        type = "Student";
                    }
                    else if(domain.equals("protonmail.com")){
                        type = "Faculty";
                    }
                }

                if(TextUtils.isEmpty(pass)||TextUtils.isEmpty(confirmpass)){
                    if(TextUtils.isEmpty(pass)){
                        password.setError("Enter a password");
                    }
                    else{
                        confirm_pass.setError("Passwords do not match!");
                    }
                }
                else if(!pass.equals(confirmpass)){
                    confirm_pass.setError("Passwords do not match!");
                }
                else{
                    addUserToDb(email,pass,type);
                }
            }
        });

    }
    private void addUserToDb(final String email, String password, final String type){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            FirebaseUser currentUser = auth.getCurrentUser();
                            assert currentUser != null;
                            String id = currentUser.getUid();
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            reference = db.getReference().child("Users").child(id);
                            String name = currentUser.getDisplayName();
                            String picUrl = "";


                            RegisterUser user = new RegisterUser();
                            user.setId(id);
                            user.setNameSurname(name);
                            user.setEmail(email);
                            user.setPictureUrl(picUrl);
                            user.setType(type);

                            reference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //progressbar.dismiss();
                                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this,"Posted are Failed\n"+e,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        else{
                            //progressbar.dismiss();
                            Toast.makeText(RegisterActivity.this,
                                    "Registration failed.", Toast.LENGTH_LONG).show();
                        }


                    }
                });

    }
}
