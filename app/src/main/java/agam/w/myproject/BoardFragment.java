package agam.w.myproject;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Stack;


public class BoardFragment extends Fragment implements View.OnClickListener {


    MyGameManager game = new MyGameManager(); // Main game object that manages the game state
    ImageView[] player_1;
    ImageView[] player_2;
    Stack<Card> gameHeap;// Central heap of cards
    ImageView heapTop;  // ImageView showing the top card of the heap
    Card currentDrawnCard;// Save the currently drawn card from the heap
    Stack<Card>gameStock;//A pile of discarded cards
    Button btnFinish; // Button to finish the game and declare the winner
    private boolean isReplaceMode = false; // Indicates if replace mode is active
    private boolean isPlayerClickEnabled = false; // Controls player card interaction
    private TextView turnTextView;//  // TextView showing current turn
    private LinearLayout layoutStock;// // Layout for the stock/draw pile
    private int cardWidth, cardHeight;
    private int selectedSelfIndex = -1;// Index of selected player's card
    private boolean isWaitingForSecondPick = false;
    private boolean isReplaceSpecialActive = false;
    private boolean isPeekActive = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        player_1 = new ImageView[4];
        player_2 = new ImageView[4];
        gameHeap  = game.getDrawPile();
        gameStock = game.getGarbage();
        View view = inflater.inflate(R.layout.fragment_board, container, false);
        turnTextView =view.findViewById(R.id.textViewTurn);
        layoutStock = view.findViewById(R.id.layoutStock);
        btnFinish = view.findViewById(R.id.btnFinish);
        btnFinish.setEnabled(false);
        if (game.getCurrentPlayerTurn() == 1) {
            btnFinish.setEnabled(true);
        } else if (game.getCurrentPlayerTurn() == 2) {
            btnFinish.setEnabled(true);
        }

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String winnerMessage = game.endGame();
                Toast.makeText(getContext(), winnerMessage, Toast.LENGTH_LONG).show();
                heapTop.setEnabled(false);
            }
        });



        layoutStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeCardFromStock();

            }
        });

        for (int i = 0; i < player_1.length; i++)
        {
            int id = getResources().getIdentifier("imageViewPlayer1_" + (i + 1), "id", getActivity().getPackageName());
            player_1[i] = view.findViewById(id);
            player_1[i].setOnClickListener(this);
        }

        for (int i = 0; i < player_2.length ; i++)
        {
            int id = getResources().getIdentifier("imageViewPlayer2_" + (i + 1), "id", getActivity().getPackageName());
            player_2[i] = view.findViewById(id);
            player_2[i].setOnClickListener(this);
        }


        heapTop = view.findViewById(R.id.imageViewHeap);
        heapTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDrawnCard = gameHeap.pop();
                int is = fromCardToImageSource(currentDrawnCard);
                heapTop.setImageResource(is);
                heapTop.setEnabled(false);
                // Check if the drawn card is a special card
                if (currentDrawnCard instanceof SpecialCard) {
                    SpecialCard special = (SpecialCard) currentDrawnCard;
                    String name = special.getName();
                    // Handle specific special cards based on their name
                    if (name.equals("peek"))
                    {
                        handlePeekCard();
                    }
                    else if (name.equals("draw2"))
                    {
                        handleDraw2Card();
                    }
                    else if (name.equals("replace"))
                    {
                        Toast.makeText(getContext(), "Pick one of your cards, then one of your opponent's", Toast.LENGTH_SHORT).show();
                        isReplaceSpecialActive = true;
                        isPlayerClickEnabled = true;
                    }
                } else {
                    // If the card is not special, show a dialog after a short delay
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showDialog();
                        }
                    }, 2000);// Delay of 2000 milliseconds (2 seconds)
                }
            }
        });



        return view;
    }

    @Override
    public void onClick(View v)
    {
        // Check if the player is allowed to click
        if (!isPlayerClickEnabled) {
            Toast.makeText(getContext(), "Please draw a card from the heap first", Toast.LENGTH_SHORT).show();
            return;
        }
        // Determine which card ImageView was clicked based on its ID
        int id = v.getId();
        int index = 0;
        if(id == R.id.imageViewPlayer1_2)
            index = 1;
        else if(id == R.id.imageViewPlayer1_3)
            index = 2;
        else if(id == R.id.imageViewPlayer1_4)
            index = 3;
        else if(id == R.id.imageViewPlayer2_1)
            index = 4;
        else if(id == R.id.imageViewPlayer2_2)
            index = 5;
        else if(id == R.id.imageViewPlayer2_3)
            index = 6;
        else if(id == R.id.imageViewPlayer2_4)
            index = 7;

        // Determine which player clicked based on the card index
        int clickedPlayer;
        if (index < 4) {
            clickedPlayer = 1;
        } else {
            clickedPlayer = 2;
        }
        // Check again if player is allowed to click
        if (!isPlayerClickEnabled) {
            Toast.makeText(getContext(), "Please draw a card from the heap first", Toast.LENGTH_SHORT).show();
            return;
        }
        // Handle Replace Special Card logic
        if (isReplaceSpecialActive) {
            int playerTurn = game.getCurrentPlayerTurn();
            int playerIndex;

            // הגדרת אינדקס השחקן בהתאם לתור
            if (playerTurn == 1) {
                playerIndex = index;
            } else {
                playerIndex = index - 4;
            }

            if (!isWaitingForSecondPick) {
                // בחירה ראשונה - חייב להיות קלף מהצד של השחקן
                boolean isSelectingOwnCard = (playerTurn == 1 && index < 4) || (playerTurn == 2 && index >= 4);
                if (isSelectingOwnCard) {
                    selectedSelfIndex = playerIndex;
                    isWaitingForSecondPick = true;
                    Toast.makeText(getContext(), "Now pick opponent's card to switch with", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(getContext(), "Pick a card from your own side first", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                // בחירה שנייה - חייב להיות קלף מהצד של היריב
                boolean isSelectingOpponentCard = (playerTurn == 1 && index >= 4) || (playerTurn == 2 && index < 4);
                if (isSelectingOpponentCard) {
                    int opponentIndex;
                    Card[] playerCards;
                    Card[] opponentCards;

                    // הגדרת קלפים של השחקן והיריב בהתאם לתור
                    if (playerTurn == 1) {
                        opponentIndex = index - 4;
                        playerCards = game.getPlayer1();
                        opponentCards = game.getPlayer2();
                    } else {
                        opponentIndex = index;
                        playerCards = game.getPlayer2();
                        opponentCards = game.getPlayer1();
                    }

                    // בדיקה שהאינדקסים תקינים לפני ביצוע ההחלפה
                    if (selectedSelfIndex >= 0 && selectedSelfIndex < playerCards.length &&
                            opponentIndex >= 0 && opponentIndex < opponentCards.length) {

                        // ביצוע החלפת קלפים
                        Card temp = playerCards[selectedSelfIndex];
                        playerCards[selectedSelfIndex] = opponentCards[opponentIndex];
                        opponentCards[opponentIndex] = temp;

                        // עדכון תמונות הקלפים
                        if (playerTurn == 1) {
                            if (player_1 != null && player_1[selectedSelfIndex] != null) {
                                player_1[selectedSelfIndex].setImageResource(fromCardToImageSource(game.getPlayer1()[selectedSelfIndex]));
                            }
                            if (player_2 != null && player_2[opponentIndex] != null) {
                                player_2[opponentIndex].setImageResource(fromCardToImageSource(game.getPlayer2()[opponentIndex]));
                            }
                        } else {
                            if (player_2 != null && player_2[selectedSelfIndex] != null) {
                                player_2[selectedSelfIndex].setImageResource(fromCardToImageSource(game.getPlayer2()[selectedSelfIndex]));
                            }
                            if (player_1 != null && player_1[opponentIndex] != null) {
                                player_1[opponentIndex].setImageResource(fromCardToImageSource(game.getPlayer1()[opponentIndex]));
                            }
                        }

                        // הסתרת קלפים לאחר 2 שניות
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (playerTurn == 1) {
                                if (player_1 != null && player_1[selectedSelfIndex] != null) {
                                    player_1[selectedSelfIndex].setImageResource(R.drawable.back);
                                }
                                if (player_2 != null && player_2[opponentIndex] != null) {
                                    player_2[opponentIndex].setImageResource(R.drawable.back);
                                }
                            } else {
                                if (player_2 != null && player_2[selectedSelfIndex] != null) {
                                    player_2[selectedSelfIndex].setImageResource(R.drawable.back);
                                }
                                if (player_1 != null && player_1[opponentIndex] != null) {
                                    player_1[opponentIndex].setImageResource(R.drawable.back);
                                }
                            }
                        }, 2000); // השהייה של 2 שניות

                        // הודעת הצלחה
                        Toast.makeText(getContext(), "Cards switched!", Toast.LENGTH_SHORT).show();

                        // איפוס משתנים למצב התחלתי
                        selectedSelfIndex = -1;
                        isWaitingForSecondPick = false;
                        isReplaceSpecialActive = false;
                        isPlayerClickEnabled = false;

                        // מעבר לתור הבא
                        game.switchTurn();
                        updateTurnText();

                        // איפוס תמונת ערימת הקלפים
                        heapTop.setImageResource(R.drawable.back);
                        currentDrawnCard = null;
                    } else {
                        Toast.makeText(getContext(), "Invalid card selection", Toast.LENGTH_SHORT).show();
                        resetReplaceSpecialState();
                        return;
                    }

                    // הפעלת מחדש את האפשרות ללחוץ על ערימת הקלפים
                    heapTop.setEnabled(true);
                    return;
                } else {
                    Toast.makeText(getContext(), "Pick a card from your opponent", Toast.LENGTH_SHORT).show();
                }
            }
        }




        // Check if the player who clicked is the current player
        if (clickedPlayer != game.getCurrentPlayerTurn()) {
            Toast.makeText(getContext(), "Not your turn!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Handle Replace Mode from heap when replacing a card from player 2's side
        else if (isReplaceMode && index >= 4) {
            int realIndex = index - 4;
            // Take the ols card from player2 and put it in the stock
            Card cardToStock = game.getPlayer2()[realIndex];
            addCardToStock(cardToStock);
            // Replace the old card with the drawn card
            game.getPlayer2()[realIndex] = currentDrawnCard;
            // Show the drawn card temporarily
            player_2[realIndex].setImageResource(fromCardToImageSource(currentDrawnCard));
            new Handler().postDelayed(() -> {
                player_2[realIndex].setImageResource(R.drawable.back);
            }, 2000);// 2-second delay to let the player see the card
            Toast.makeText(getContext(), "Card replaced", Toast.LENGTH_SHORT).show();// Notify the user and reset the state
            isReplaceMode = false;
            isPlayerClickEnabled = false;
            // Switch turns and reset heap
            game.switchTurn();
            updateTurnText();
            heapTop.setImageResource(R.drawable.back);
            currentDrawnCard = null;
            heapTop.setEnabled(true);
            return;
        }
// Handle case when player is either replacing or taking from stock
        if ((isReplaceMode || isTakingFromStock) && index >= 4) {
            int realIndex = index - 4;
            Card cardToStock = game.getPlayer2()[realIndex];
            game.getPlayer2()[realIndex] = currentDrawnCard;
            player_2[realIndex].setImageResource(fromCardToImageSource(currentDrawnCard));
            new Handler().postDelayed(() -> player_2[realIndex].setImageResource(R.drawable.back), 2000);
            Toast.makeText(getContext(), "Card replaced", Toast.LENGTH_SHORT).show();
            if (isTakingFromStock) {
                game.getGarbage().pop();
            } else {
                addCardToStock(cardToStock);
            }

            resetAfterMove();
            return;
        }


        if (isPeekActive) {
            int realIndex;
            ImageView selectedCardView;
            Card selectedCard;

            if (index < 4) {
                // Player 1's card
                realIndex = index;
                selectedCardView = player_1[realIndex];
                selectedCard = game.getPlayer1()[realIndex];
            } else {
                // Player 2's card
                realIndex = index - 4;
                selectedCardView = player_2[realIndex];
                selectedCard = game.getPlayer2()[realIndex];
            }
            // Temporarily show the peeked card
            selectedCardView.setImageResource(fromCardToImageSource(selectedCard));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedCardView.setImageResource(R.drawable.back);// Hide card after peek
                    isPeekActive = false;
                    isPlayerClickEnabled = false;
                    addCardToStock(currentDrawnCard);
                    currentDrawnCard = null;
                    heapTop.setImageResource(R.drawable.back);
                    game.switchTurn();
                    updateTurnText();
                    heapTop.setEnabled(true);
                }
            }, 2000); // Delay of 2 seconds

            return;
        }



        if ((isReplaceMode || isTakingFromStock) && index < 4) {
            int newIndex = index;
            Card cardToStock = game.getPlayer1()[index];
            game.getPlayer1()[index] = currentDrawnCard;
            player_1[index].setImageResource(fromCardToImageSource(currentDrawnCard));

            new Handler().postDelayed(() -> player_1[newIndex].setImageResource(R.drawable.back), 2000);

            Toast.makeText(getContext(), "Card replaced", Toast.LENGTH_SHORT).show();

            if (isTakingFromStock) {
                game.getGarbage().pop(); // Remove used stock card
            } else {
                addCardToStock(cardToStock);// Return replaced card to stock
            }

            resetAfterMove();
            return;
        }
        if(index < 4)
        { // Player 1
            Card c = game.getPlayer1()[index];
            if(c instanceof SpecialCard)
            {
                SpecialCard sp = (SpecialCard)c;
                if(sp.getName().equals("replace"))
                {

                    player_1[index].setImageResource(R.drawable.card_replace);
                }
                if(sp.getName().equals("draw_2"))
                    player_1[index].setImageResource(R.drawable.card_draw2);
                if(sp.getName().equals("peek"))
                    player_1[index].setImageResource(R.drawable.card_peek);
                // Drop the card and draw a new one to replace
                Card heapHead = this.gameHeap.pop();
                Card carToDrop = fromImageSourseToCard(player_1[index]);
                player_1[index].setImageResource(fromCardToImageSource(heapHead));
                game.addToStock(carToDrop);
            }
            else
            {
                // Player 2
                int num = c.getNum();
                int dr = getResources().getIdentifier("card_" + num, "drawable", getActivity().getPackageName());
                player_1[index].setImageResource(dr);

            }
        }
        else
        {
            // player 2
            index -= 4;
            Card c = game.getPlayer2()[index];
            // Check if the card is a special card
            if(c instanceof SpecialCard)
            {
                SpecialCard sp = (SpecialCard)c;
                if(sp.getName().equals("replace"))
                    player_2[index].setImageResource(R.drawable.card_replace);
                if(sp.getName().equals("draw_2"))
                    player_2[index].setImageResource(R.drawable.card_draw2);
                if(sp.getName().equals("peek"))
                    player_2[index].setImageResource(R.drawable.card_peek);
            }
            else
            {
                // If it's a normal card, get its number
                int num = c.getNum();
                //  Get the drawable ID for the corresponding card imag
                int dr = getResources().getIdentifier("card_" + num, "drawable", getActivity().getPackageName());
                // Set the image for the card based on its number
                player_2[index].setImageResource(dr);

            }
        }




    }

    private Card fromImageSourseToCard(ImageView imageView)// Converts an ImageView (which shows a card image) back to a Card object)
    {

        Drawable drawable = imageView.getDrawable();
        if(drawable == getResources().getDrawable(R.drawable.card_peek))
        {
            return new SpecialCard("peek");
        }
        else if(drawable == getResources().getDrawable(R.drawable.card_draw2))
        {
            return new SpecialCard("draw2");
        }
        else  if(drawable == getResources().getDrawable(R.drawable.card_replace))
        {
            return new SpecialCard("replace");
        }
        else
            // Loop through regular cards (0–9)
            for (int i = 0; i <= 9; i++)
            {
                // Get the drawable ID for "card_i"
                int id = getResources().getIdentifier("card_"+i, "drawable", getContext().getPackageName());
                Drawable dr = getResources().getDrawable(id);
                // Compare drawable states to find a match
                if(drawable.getConstantState().equals(dr.getConstantState()))
                    return new Card(i);
            }
        return null;

    }

    public int fromCardToImageSource(Card c)// Converts a Card object into the image resource ID (drawable)
    {
        if(c instanceof SpecialCard)
        {
            // If the card is a special card, return its corresponding drawable
            SpecialCard sp = (SpecialCard)c;
            if(sp.getName().equals("replace"))
                return R.drawable.card_replace;
            if(sp.getName().equals("draw2"))
                return R.drawable.card_draw2;
            return R.drawable.card_peek;
        }
        else
        {
            // For regular cards, return the appropriate drawable ID based on the number
            int num = c.getNum();
            int dr = getResources().getIdentifier("card_" + num, "drawable", getActivity().getPackageName());
            return dr;
        }
    }
    // Displays a dialog to the player after drawing a card from the heap,
    // allowing them to choose between replacing a card or throwing it away
    public void showDialog()
    {
        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);// Dialog cannot be dismissed
        dialog.setContentView(R.layout.choose_dialog);
        dialog.setTitle("choose your next move");
        RadioButton replaceRb = dialog.findViewById(R.id.radioButtonReplace);
        RadioButton throwRb = dialog.findViewById(R.id.radioButtonThrow);
        Button btn = dialog.findViewById(R.id.buttonDone);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the user chooses to replace a card
                if(replaceRb.isChecked())
                {

                    LayoutInflater inflater = getLayoutInflater();
                    View customToast = inflater.inflate(R.layout.custom_toast, null);
                    Toast toast = new Toast(getContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(customToast);
                    toast.show();
                    isReplaceMode = true;
                    isPlayerClickEnabled = true;
                    dialog.dismiss();
                }
                else
                {
                    // If the user chooses to throw the card away
                    addCardToStock(currentDrawnCard);
                    heapTop.setImageResource(R.drawable.back);// Reset heap image
                    isPlayerClickEnabled = false;
                    game.switchTurn(); // Switch to next player's turn
                    updateTurnText();// Update turn text display
                    dialog.dismiss();
                    heapTop.setEnabled(true);// Allow drawing next turn
                }

            }
        });
        dialog.show();
    }
    // Handles the logic for when a "peek" special card is played
    private void handlePeekCard() {
        Toast.makeText(getContext(), "Pick a card to peek at", Toast.LENGTH_SHORT).show();
        isPlayerClickEnabled = true;
        isPeekActive = true;
    }

    // Updates the text display that shows which player's turn it is
    private void updateTurnText() {
        int current = game.getCurrentPlayerTurn();
        turnTextView.setText("Player " + current + "'s turn");
    }
    // Adds a card to the visible stock area in the UI
    private void addCardToStock(Card card) {
        game.addToStock(card);
        layoutStock.removeAllViews();
        // Create new ImageView for the added card
        ImageView cardView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cardWidth, cardHeight);
        cardView.setLayoutParams(params);
        cardView.setImageResource(fromCardToImageSource(card));
        layoutStock.addView(cardView); // Add to layout
    }
    // Handles the effect of the "draw2" card - player draws two more cards
    private void handleDraw2Card() {
        Toast.makeText(getContext(), "Drawing 2 more cards...", Toast.LENGTH_SHORT).show();

        for (int i = 0; i < 2; i++) {
            if (!gameHeap.isEmpty()) {
                Card extraCard = gameHeap.pop();
                game.addToStock(extraCard);
            }
        }
        // Wait 2 seconds before switching turn and resetting UI
        new Handler().postDelayed(() -> {
            game.switchTurn();
            updateTurnText();
            heapTop.setImageResource(R.drawable.back);
            currentDrawnCard = null;
            isPlayerClickEnabled = false;
        }, 2000);
        heapTop.setEnabled(true);// Allow next draw
    }
    private boolean isTakingFromStock = false;
    // Lets the player take a card from the stock (instead of the heap)
    private void takeCardFromStock() {
        if (!game.getGarbage().isEmpty()) {
            currentDrawnCard = game.getGarbage().peek();
            isReplaceMode = true;
            isTakingFromStock = true;
            isPlayerClickEnabled = true;

            LayoutInflater inflater = getLayoutInflater();
            View customToast = inflater.inflate(R.layout.custom_toast, null);
            Toast toast = new Toast(getContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(customToast);
            toast.show();
        } else {
            Toast.makeText(getContext(), "Stock is empty or you already drew", Toast.LENGTH_SHORT).show();
        }
    }
    // Resets game state after using a special "replace" card
    private void resetReplaceSpecialState() {
        isReplaceSpecialActive = false;
        isWaitingForSecondPick = false;
        selectedSelfIndex = -1;
        isPlayerClickEnabled = false;
        currentDrawnCard = null;
        heapTop.setImageResource(R.drawable.back);
        heapTop.setEnabled(true);
        game.switchTurn();
        updateTurnText();
    }

    // Resets general game state after any move
    private void resetAfterMove() {
        isReplaceMode = false;
        isTakingFromStock = false;
        isPlayerClickEnabled = false;
        currentDrawnCard = null;
        heapTop.setImageResource(R.drawable.back);
        heapTop.setEnabled(true);
        game.switchTurn();
        updateTurnText();
    }

}