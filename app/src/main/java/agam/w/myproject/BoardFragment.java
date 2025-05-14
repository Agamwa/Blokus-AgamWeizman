package agam.w.myproject;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class BoardFragment extends Fragment {


    RatATatCatViewModel game;
    ImageView[] player_1;
    ImageView[] player_2;
    ImageView drawTop;
    ImageView garbageTop;
    Button btnFinish; // Button to finish the game and declare the winner
    TextView timer;
    long timerDuration = TimeUnit.MINUTES.toMillis(1);
    long ticksInteval = 10;
    long millis = 1000;
    private TextView turnTextView; // TextView showing current turn
//    private boolean isPlayerClickEnabled = false; // Controls player card interaction

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        game = new RatATatCatViewModel();

        player_1 = new ImageView[4];
        player_2 = new ImageView[4];
        timer = view.findViewById(R.id.timerTV);

        long timerDurationMillis = TimeUnit.MINUTES.toMillis(30); // 30 דקות

        new CountDownTimer(timerDurationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                String timerText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                timer.setText(timerText);
            }

            @Override
            public void onFinish() {
                timer.setText("00:00");
                Toast.makeText(getContext(), "Time Is Over!", Toast.LENGTH_LONG).show();
                btnFinish.performClick(); // End game
            }
        }.start();

        turnTextView = view.findViewById(R.id.textViewTurn);
        btnFinish = view.findViewById(R.id.btnFinish);
        drawTop = view.findViewById(R.id.drawTop);
        garbageTop = view.findViewById(R.id.garbageTop);

        btnFinish.setOnClickListener(v -> {
            String winnerMessage = game.endGame();
            // Show all the cards
            for (int i = 0; i < player_1.length; i++)
                player_1[i].setImageResource(fromCardToImageSource(game.getPlayerCard(1, i)));
            for (int i = 0; i < player_2.length; i++)
                player_2[i].setImageResource(fromCardToImageSource(game.getPlayerCard(2, i)));

            // Show the winner
            ((GameActivity) requireActivity()).showWinnerDialog(winnerMessage);
        });

        drawTop.setOnClickListener(v -> {
            game.turn(SelectedPile.DRAW_PILE, -1, -1);
        });
        garbageTop.setOnClickListener(v -> {
            if (game.garbageIsEmpty()) Toast.makeText(getContext(), "Garbage is empty", Toast.LENGTH_SHORT).show();
            else game.turn(SelectedPile.GARBAGE_PILE, -1, -1);
        });

        for (int i = 0; i < player_1.length; i++)
        {
            int id = getResources().getIdentifier("imageViewPlayer1_" + (i + 1), "id", getActivity().getPackageName());
            player_1[i] = view.findViewById(id);
            int finalI = i;
            player_1[i].setOnClickListener(v -> {
                game.turn(null, finalI, -1);
            });
        }

        for (int i = 0; i < player_2.length ; i++)
        {
            int id = getResources().getIdentifier("imageViewPlayer2_" + (i + 1), "id", getActivity().getPackageName());
            player_2[i] = view.findViewById(id);
            int finalI = i;
            player_2[i].setOnClickListener(v -> {
                game.turn(null, -1, finalI);
            });
        }

        game.getCurrentPlayerTurnLiveData().observe(getViewLifecycleOwner(), turn -> {
            if (turn == 1) {
                turnTextView.setText("Player 1's turn");
            } else {
                turnTextView.setText("Player 2's turn");
            }
        });

        game.getSelecetedCardPlayer1().observe(getViewLifecycleOwner(), card -> {
            if (card != null)
                player_1[card].setImageResource(fromCardToImageSource(game.getPlayerCard(1, card)));
            else
                for (int i = 0; i < player_1.length; i++)
                    player_1[i].setImageResource(R.drawable.back);
        });

        game.getSelecetedCardPlayer2().observe(getViewLifecycleOwner(), card -> {
            if (card != null)
                player_2[card].setImageResource(fromCardToImageSource(game.getPlayerCard(2, card)));
            else
                for (int i = 0; i < player_2.length; i++)
                    player_2[i].setImageResource(R.drawable.back);
        });

        game.getTurnStateLiveData().observe(getViewLifecycleOwner(), turnState -> {
            if (turnState != TurnState.SELECT_PILE && turnState != TurnState.PLACE_IN_YOUR_DECK_FROM_GARBAGE) {
                drawTop.setImageResource(fromCardToImageSource(game.getTopDrawPileCardLiveData().getValue()));
            } else {
                drawTop.setImageResource(R.drawable.back);
            }
        });

        game.getTopGarbageCardLiveData().observe(getViewLifecycleOwner(), card -> {
            if (card != null)
                garbageTop.setImageResource(fromCardToImageSource(card));
        });

        return view;
    }

    public int fromCardToImageSource(Card c) {
    // Converts a Card object into the image resource ID (drawable)
        if (c instanceof SpecialCard) {
            // If the card is a special card, return its corresponding drawable
            SpecialCard sp = (SpecialCard)c;
            if(sp.getName().equals("replace"))
                return R.drawable.card_replace;
            if(sp.getName().equals("draw2"))
                return R.drawable.card_draw2;
            return R.drawable.card_peek;
        } else {
            // For regular cards, return the appropriate drawable ID based on the number
            int num = c.getNum();
            int dr = getResources().getIdentifier("card_" + num, "drawable", getActivity().getPackageName());
            return dr;
        }
    }

}