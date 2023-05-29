package com.example.a82.activity;

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

import com.example.a82.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText email_view,password_view,password_confirmed;
    Button create_acc_button;
    TextView back_Loin_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email_view = findViewById(R.id.register_username);
        password_view = findViewById(R.id.register_password);
        password_confirmed = findViewById(R.id.register_comfirmed_password);
        create_acc_button = findViewById(R.id.creat_account);
        back_Loin_button = findViewById(R.id.back_to_login);

        //back to login\
        back_Loin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        //create new account
        create_acc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String email = email_view.getText().toString();
        String password1 = password_view.getText().toString();
        String password2 = password_confirmed.getText().toString();
        if(TextUtils.isEmpty(email)){
            email_view.setError("email address is requied!");
            return;
        }

        if(TextUtils.isEmpty(password1)){
            password_view.setError("password is requied");
            return;
        }
        if(TextUtils.isEmpty(password2)){
            password_confirmed.setError("confirmed password is requied");
            return;
        }
        if(!password1.equals(password2)){
            password_view.setError("please enter two same password");
            password_confirmed.setError("please enter two same password");
            return;
        }else{
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email,password1)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"successful to create an account", Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                            }else{
                                Toast.makeText(RegisterActivity.this,"sorry, please try again, error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
}