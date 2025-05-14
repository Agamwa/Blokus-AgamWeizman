package agam.w.myproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;


public class RulesFragment extends Fragment {
    TextView tv;
    ImageButton speak;
    TextToSpeech textToSpeech;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rules, container, false);
        tv = view.findViewById(R.id.textTV);
        speak = view.findViewById(R.id.imageButtonSpeak);
        textToSpeech= new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!= TextToSpeech.ERROR)
                    textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textToSpeech.isSpeaking())
                    textToSpeech.stop();
                else
                {
                    String text = tv.getText().toString();
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });
        return view;
    }
}