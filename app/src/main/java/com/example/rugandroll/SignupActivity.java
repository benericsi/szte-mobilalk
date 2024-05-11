package com.example.rugandroll;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private static final String PREFS_NAME = MainActivity.class.getPackage().toString();
    private static final int SECRET = 99;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    EditText userNameET;
    EditText emailET;
    EditText passwordET;
    EditText passwordAgainET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int secret = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret != 99) {
            finish();
        }

        userNameET = findViewById(R.id.usernameEditText);
        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);
        passwordAgainET = findViewById(R.id.passwordAgainEditText);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        emailET.setText(email);
        passwordET.setText(password);
        passwordAgainET.setText(password);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void signup(View view) {
        String userName = userNameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordAgain = passwordAgainET.getText().toString();
        if (userName.isEmpty()) {
            userNameET.setError("A felhasználónév nem lehet üres");
            return;
        }

        if (email.isEmpty()) {
            emailET.setError("Az email nem lehet üres");
            return;
        }

        if (password.isEmpty()) {
            passwordET.setError("A jelszó nem lehet üres");
            return;
        }

        if (passwordAgain.isEmpty()) {
            passwordAgainET.setError("A jelszó nem lehet üres");
            return;
        }

        if (!password.equals(passwordAgain)) {
            passwordET.setError("A jelszavak nem egyeznek");
            passwordAgainET.setError("A jelszavak nem egyeznek");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()  {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startShopping();
                    } else {
                        Toast.makeText(SignupActivity.this, "Sikertelen regisztráció: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    public void cancel(View view) {
        finish();
    }

    private void startShopping () {
        Intent intent = new Intent(this, ShopListActivity.class);
        startActivity(intent);
    }
}