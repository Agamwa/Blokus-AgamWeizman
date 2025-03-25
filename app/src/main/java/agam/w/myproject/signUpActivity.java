package agam.w.myproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class signUpActivity extends AppCompatActivity {
    Button btnSignUp;
    TextInputLayout emailAddressTi;
    TextInputLayout passwordTi;
    TextInputLayout firstNameTi;
    TextInputLayout lastNameTi;
    TextInputLayout usernameTi;
    FirebaseAuth auth;
    FirebaseFirestore db;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        emailAddressTi = findViewById(R.id.tiSignUpEmail);
        passwordTi = findViewById(R.id.tiSignUpPassword);
        firstNameTi = findViewById(R.id.tiSignUpName);
        lastNameTi = findViewById(R.id.tiSignUpLastName);
        usernameTi = findViewById(R.id.tiSignUpUsername);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                String email = emailAddressTi.getEditText().getText().toString();
                String password = passwordTi.getEditText().getText().toString();
                if(email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(signUpActivity.this, "email and/or password can't be empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                try {
                                    String firstName = firstNameTi.getEditText().getText().toString();
                                    String lastName = lastNameTi.getEditText().getText().toString();
                                    String username = usernameTi.getEditText().getText().toString();
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("username",username );
                                    data.put("password", password);
                                    data.put("firstNme", firstName);
                                    data.put("lastName", lastName);
                                    data.put("email", email);
                                    data.put("wins", "0");
                                    db.collection("Users").document()
                                            .set(data, SetOptions.merge());
                                    DocumentReference docRef = db.collection("Users").document();
                                    docRef.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                        }
                                    });

                                } catch (Exception e) {
                                    Toast.makeText(context, "Saving Document Falied!", Toast.LENGTH_SHORT).show();
                                }
                                Intent intent = new Intent(signUpActivity.this, SignInActivity.class);
                                intent.putExtra("username", usernameTi.getEditText().getText().toString());
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                   
                }
                
            }
        });


    }
}