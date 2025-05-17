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
        // Initialize the TextToSpeech engine with context and listener
        textToSpeech= new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                // If initialization is successful (no error)
                if(i!= TextToSpeech.ERROR)
                    // Set the language for speech output to English
                    textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
        // Set an OnClickListener for the speak button
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the TTS engine is currently speaking, stop it
                if(textToSpeech.isSpeaking())
                    textToSpeech.stop();
                else
                {
                    // Otherwise, get the text from the TextView
                    String text = tv.getText().toString();
                    // Speak the text immediately, clearing any queued utterances
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });
        return view;
    }
}