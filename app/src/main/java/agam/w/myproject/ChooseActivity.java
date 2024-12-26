package agam.w.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChooseActivity extends AppCompatActivity {
    Button btnPracticeGame, btnTwoPlayers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        btnPracticeGame = findViewById(R.id.btnPracticeGame);
        btnTwoPlayers = findViewById(R.id.btnTwoPlayers);
        btnPracticeGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActivity.this, BoardActivity.class);
                startActivity(intent);
            }
        });
       btnTwoPlayers.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(ChooseActivity.this, BoardActivity.class);
               startActivity(intent);
           }
       });
    }
}