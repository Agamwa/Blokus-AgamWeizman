package agam.w.myproject;
import static agam.w.myproject.ClickedCard.GARBAGE;
import static agam.w.myproject.ClickedCard.NO_CARD;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class BoardFragment extends Fragment {


    RatATatCatViewModel game; // ViewModel managing game logic and state
    ImageView[] player_1;// ImageView array for player 1's cards
    ImageView[] player_2; // ImageView array for player 2's cards
    ImageView drawTop;  // ImageView representing the top card of the draw pile
    ImageView garbageTop;   // ImageView representing the top card of the garbage pile
    Button btnFinish; // Button to finish the game and declare the winner
    TextView timer; // TextView showing the countdown timer
    private TextView turnTextView; //TextView displaying whose turn it is
    private TextView turnStateTextView; //TextView displaying the current turn state
    HashMap<TurnState, String> turnStateMap = new HashMap<>();

    CountDownTimer countDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        initBoardFragment(view);

        // Observe current player's turn and update UI accordingly
        game.getCurrentPlayerTurnLiveData().observe(getViewLifecycleOwner(), turn -> {
            if (turn == 1) {
                turnTextView.setText("Player 1's turn");
            } else {
                turnTextView.setText("Player 2's turn");
            }
        });

        // Observe selected card for player 1 and update card image or show back if null
        game.getSelecetedCardPlayer1().observe(getViewLifecycleOwner(), card -> {
            if (card != null)
                player_1[card.ordinal()].setImageResource(fromCardToImageSource(game.getPlayerCard(1, card.ordinal())));
            else
                for (int i = 0; i < player_1.length; i++)
                    player_1[i].setImageResource(R.drawable.back);
        });
        // Observe selected card for player 2 and update card image or show back if null
        game.getSelecetedCardPlayer2().observe(getViewLifecycleOwner(), card -> {
            if (card != null)
                player_2[card.ordinal()].setImageResource(fromCardToImageSource(game.getPlayerCard(2, card.ordinal())));
            else
                for (int i = 0; i < player_2.length; i++)
                    player_2[i].setImageResource(R.drawable.back);
        });
        // Observe the turn state to update the draw pile card image (show card or back)
        game.getTurnStateLiveData().observe(getViewLifecycleOwner(), turnState -> {
            if (turnState != TurnState.SELECT_PILE && turnState != TurnState.PLACE_IN_YOUR_DECK_FROM_GARBAGE) {
                drawTop.setImageResource(fromCardToImageSource(game.getTopDrawPileCardLiveData().getValue()));
            } else {
                drawTop.setImageResource(R.drawable.back);
            }

            turnStateTextView.setText(turnStateMap.get(turnState));
        });
        // Observe the top card on garbage pile and update UI image
        game.getTopGarbageCardLiveData().observe(getViewLifecycleOwner(), card -> {
            if (card != null) {
                garbageTop.setImageResource(fromCardToImageSource(card));
            }
            else
                garbageTop.setImageResource(0);
        });

        // Observe if the player can currently play; disable UI elements visually when false
        game.getCanPlay().observe(getViewLifecycleOwner(), canPlay -> {
            if (!canPlay) {
                for (int i = 0; i < player_1.length; i++)
                    player_1[i].setAlpha(0.5f);
                for (int i = 0; i < player_2.length; i++)
                    player_2[i].setAlpha(0.5f);
                drawTop.setAlpha(0.5f);
                garbageTop.setAlpha(0.5f);
            } else {
                for (int i = 0; i < player_1.length; i++)
                    player_1[i].setAlpha(1f);
                for (int i = 0; i < player_2.length; i++)
                    player_2[i].setAlpha(1f);
                drawTop.setAlpha(1f);
                garbageTop.setAlpha(1f);
            }
        });
        
        game.getTopDrawPileCardLiveData().observe(getViewLifecycleOwner(), card -> {
            if (card == null) endGame();

        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        countDownTimer.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }

    private void initBoardFragment(View view) {
        game = new RatATatCatViewModel();

        player_1 = new ImageView[4];
        player_2 = new ImageView[4];
        timer = view.findViewById(R.id.timerTV);
        // Set up a countdown timer of 30 minutes, updating UI every second
        long timerDurationMillis = TimeUnit.MINUTES.toMillis(30);
        countDownTimer = new CountDownTimer(timerDurationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Calculate minutes and seconds remaining and update timer TextView
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                String timerText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                timer.setText(timerText);
            }

            @Override
            public void onFinish() {
                // When time finishes, show message and trigger end game logic
                timer.setText("00:00");
                Toast.makeText(getContext(), "Time Is Over!", Toast.LENGTH_LONG).show();
                endGame();
            }
        };

        turnTextView = view.findViewById(R.id.textViewTurn);
        turnStateTextView = view.findViewById(R.id.textViewTurnState);
        btnFinish = view.findViewById(R.id.btnFinish);
        drawTop = view.findViewById(R.id.drawTop);
        garbageTop = view.findViewById(R.id.garbageTop);

        // Set listener for finish button to show all cards and winner dialog
        btnFinish.setOnClickListener(v -> {
            endGame();
        });
        // Draw pile click triggers a turn with action on the draw pile
        drawTop.setOnClickListener(v -> {
            game.turn(SelectedPile.DRAW_PILE, NO_CARD, NO_CARD);
        });
        // Garbage pile click triggers a turn with action on the garbage pile
        garbageTop.setOnClickListener(v -> {
            game.turn(SelectedPile.GARBAGE_PILE, GARBAGE, GARBAGE);
        });
        // Initialize player 1 card ImageViews and set click listeners for card selection
        for (int i = 0; i < player_1.length; i++)
        {
            int id = getResources().getIdentifier("imageViewPlayer1_" + (i + 1), "id", getActivity().getPackageName());
            player_1[i] = view.findViewById(id);
            ClickedCard c = ClickedCard.values()[i];
            player_1[i].setOnClickListener(v -> {
                game.turn(null, c, NO_CARD);
            });
        }
        // Initialize player 2 card ImageViews and set click listeners for card selection
        for (int i = 0; i < player_2.length ; i++)
        {
            int id = getResources().getIdentifier("imageViewPlayer2_" + (i + 1), "id", getActivity().getPackageName());
            player_2[i] = view.findViewById(id);
            ClickedCard c = ClickedCard.values()[i];
            player_2[i].setOnClickListener(v -> {
                game.turn(null, NO_CARD, c);
            });
        }

        turnStateMap.put(TurnState.SELECT_PILE, "Pick a card!");
        turnStateMap.put(TurnState.PLACE_IN_YOUR_DECK, "Choose a place in your deck!\n (Or throw into the garbage)");
        turnStateMap.put(TurnState.PLACE_IN_YOUR_DECK_FROM_GARBAGE, "Choose a place in your deck!");
        turnStateMap.put(TurnState.SPECIAL_REPLACE_CHOOSE_YOUR_CARD, "Choose a your own card to replace!");
        turnStateMap.put(TurnState.SPECIAL_REPLACE_CHOOSE_OPPONENT_CARD, "Choose an opponent card to replace!");
        turnStateMap.put(TurnState.SPECIAL_PEEK, "Peek at any card!");
        turnStateMap.put(TurnState.SPECIAL_DRAW2_CHOOSE_FIRST_CARD, "You got a Draw 2 card!");
        turnStateMap.put(TurnState.FIRST_CARD_IN_YOUR_DECK, "Choose a place in your deck for the first card!");
        turnStateMap.put(TurnState.SECOND_CARD_IN_YOUR_DECK, "Choose a place in your deck for the second card!");
    }

    private void endGame() {
        String winnerMessage = game.endGame();
        // Reveal all player cards on the UI
        for (int i = 0; i < player_1.length; i++)
            player_1[i].setImageResource(fromCardToImageSource(game.getPlayerCard(1, i)));
        for (int i = 0; i < player_2.length; i++)
            player_2[i].setImageResource(fromCardToImageSource(game.getPlayerCard(2, i)));
        // Show winner dialog using GameActivity method
        ((GameActivity) requireActivity()).showWinnerDialog(winnerMessage);
    }

    //Converts a Card object into its corresponding drawable resource ID
    //Handles both regular cards and special cards (replace, draw2, peek)
    public int fromCardToImageSource(Card c) {
    // Converts a Card object into the image resource ID (drawable)
        if (c instanceof SpecialCard) {
            // If the card is a special card, return its corresponding drawable
            SpecialCard sp = (SpecialCard)c;
            if(sp.getType() == SpecialCard.CardType.REPLACE)
                return R.drawable.card_replace;
            if(sp.getType() == SpecialCard.CardType.DRAW2)
                return R.drawable.card_draw2;
            return R.drawable.card_peek;
        } else {
            // For normal cards, get drawable resource by card number
            int num = c.getNum();
            int dr = getResources().getIdentifier("card_" + num, "drawable", getActivity().getPackageName());
            return dr;
        }
    }

    public void resetGame() {
        game.init();
    }

}