package agam.w.myproject;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;


public class BoardFragment extends Fragment implements View.OnClickListener {


    MyGame game = new MyGame();//ובייקט המשחק הראשי שמנהל את מצב המשחק.
    ImageView[] player_1;
    ImageView[] player_2;
    Stack<Card> gameHeap;// ערימת הקלפים המרכזית של המשחק.
    ImageView heapTop;//התמונה של הקלף העליון בערימה.
    Card currentDrawnCard; // שומר את הקלף שנשלף מהערימה
    Stack<Card>gameStock;
    Button btnFinish;
    private boolean isReplaceMode = false;
    private boolean isPlayerClickEnabled = false;
    private TextView turnTextView;
    private LinearLayout layoutStock;
    private int cardWidth, cardHeight;
    private int selectedSelfIndex = -1;
    private boolean isWaitingForSecondPick = false;
    private boolean isReplaceSpecialActive = false;
    private boolean isPeekActive = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        player_1 = new ImageView[4];
        player_2 = new ImageView[4];
        gameHeap  = game.getHeap();
        gameStock = game.getStock();
        View view = inflater.inflate(R.layout.fragment_board, container, false);
        turnTextView =view.findViewById(R.id.textViewTurn);
        layoutStock = view.findViewById(R.id.layoutStock);
        btnFinish = view.findViewById(R.id.btnFinish);
        btnFinish.setEnabled(false);
        if (game.getCurrentPlayerTurn() == 1) {
            btnFinish.setEnabled(true);  // שחקן 1 יכול ללחוץ על finish
        } else if (game.getCurrentPlayerTurn() == 2) {
            btnFinish.setEnabled(true);  // שחקן 2 יכול ללחוץ על finish
        }

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game.getCurrentPlayerTurn() == 1) {
                    int sumPlayer1 = 0;
                    int sumPlayer2 = 0;

                    Card[] player1Cards = game.getPlayer1();
                    Card[] player2Cards = game.getPlayer2();

                    for (int i = 0; i < player1Cards.length; i++) {
                        Card c = player1Cards[i];
                        if (c instanceof SpecialCard) continue;
                        sumPlayer1 += c.getNum();
                    }

                    for (int i = 0; i < player2Cards.length; i++) {
                        Card c = player2Cards[i];
                        if (c instanceof SpecialCard) continue;
                        sumPlayer2 += c.getNum();
                    }

                    String result;
                    if (sumPlayer1 < sumPlayer2) {
                        result = "Player 1 wins with " + sumPlayer1 + " vs " + sumPlayer2;
                    } else if (sumPlayer2 < sumPlayer1) {
                        result = "Player 2 wins with " + sumPlayer2 + " vs " + sumPlayer1;
                    } else {
                        result = "It's a tie! Both have " + sumPlayer1;
                    }

                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                    for (int i = 0; i < player_1.length; i++) {
                        Card c = game.getPlayer1()[i];
                        player_1[i].setImageResource(fromCardToImageSource(c));
                    }

                    for (int i = 0; i < player_2.length; i++) {
                        Card c = game.getPlayer2()[i];
                        player_2[i].setImageResource(fromCardToImageSource(c));
                    }

                    heapTop.setEnabled(false);
                } else if (game.getCurrentPlayerTurn() == 2) {
                    int sumPlayer1 = 0;
                    int sumPlayer2 = 0;

                    Card[] player1Cards = game.getPlayer1();
                    Card[] player2Cards = game.getPlayer2();

                    for (int i = 0; i < player1Cards.length; i++) {
                        Card c = player1Cards[i];
                        if (c instanceof SpecialCard) continue;
                        sumPlayer1 += c.getNum();
                    }

                    for (int i = 0; i < player2Cards.length; i++) {
                        Card c = player2Cards[i];
                        if (c instanceof SpecialCard) continue;
                        sumPlayer2 += c.getNum();
                    }

                    String result;
                    if (sumPlayer1 < sumPlayer2) {
                        result = "Player 1 wins with " + sumPlayer1 + " vs " + sumPlayer2;
                    } else if (sumPlayer2 < sumPlayer1) {
                        result = "Player 2 wins with " + sumPlayer2 + " vs " + sumPlayer1;
                    } else {
                        result = "It's a tie! Both have " + sumPlayer1;
                    }

                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                    for (int i = 0; i < player_1.length; i++) {
                        Card c = game.getPlayer1()[i];
                        player_1[i].setImageResource(fromCardToImageSource(c));
                    }

                    for (int i = 0; i < player_2.length; i++) {
                        Card c = game.getPlayer2()[i];
                        player_2[i].setImageResource(fromCardToImageSource(c));
                    }

                    heapTop.setEnabled(false);
                }
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
                cardWidth = heapTop.getLayoutParams().width; // שמירה על רוחב הקלף ב-heap
                cardHeight = heapTop.getLayoutParams().height; // שמירה על גובה הקלף ב-heap
                if (currentDrawnCard instanceof SpecialCard) {
                    SpecialCard special = (SpecialCard) currentDrawnCard;
                    String name = special.getName();

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
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shoeDialog();
                        }
                    }, 2000);
                }
            }
        });



        return view;
    }

    @Override
    public void onClick(View v)//מופעלת כאשר שחקן לוחץ על קלף לפי ה-ID של הלחיצה, מזוהה המיקום של הקלף. אם מדובר בקלף מיוחד (כמו "peek", "draw 2", או "replace"), מציגה את הקלף המתאים ומבצעת פעולה של החלפה או שימוש לפי ההיגיון שלך. אם הקלף רגיל, מציגה אותו בהתאם למספר שלו.
    {
        if (!isPlayerClickEnabled) {
            Toast.makeText(getContext(), "Please draw a card from the heap first", Toast.LENGTH_SHORT).show();
            return;
        }
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

        int clickedPlayer;
        if (index < 4) {
            clickedPlayer = 1;
        } else {
            clickedPlayer = 2;
        }
        if (!isPlayerClickEnabled) {
            Toast.makeText(getContext(), "Please draw a card from the heap first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isReplaceSpecialActive) {
            int playerTurn = game.getCurrentPlayerTurn();
            int playerIndex;

            if (playerTurn == 1) {
                playerIndex = index;
            } else {
                playerIndex = index - 4;
            }

            if (!isWaitingForSecondPick) {
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
                boolean isSelectingOpponentCard = (playerTurn == 1 && index >= 4) || (playerTurn == 2 && index < 4);
                if (isSelectingOpponentCard) {
                    int opponentIndex;
                    Card[] playerCards;
                    Card[] opponentCards;

                    if (playerTurn == 1) {
                        opponentIndex = index - 4;
                        playerCards = game.getPlayer1();
                        opponentCards = game.getPlayer2();
                    } else {
                        opponentIndex = index;
                        playerCards = game.getPlayer2();
                        opponentCards = game.getPlayer1();
                    }

                    if (selectedSelfIndex >= 0 && selectedSelfIndex < playerCards.length
                            && opponentIndex >= 0 && opponentIndex < opponentCards.length) {

                        // החלפת קלפים
                        Card temp = playerCards[selectedSelfIndex];
                        playerCards[selectedSelfIndex] = opponentCards[opponentIndex];
                        opponentCards[opponentIndex] = temp;

                        // עדכון התמונות וההיגיון אחר כך

                        if (playerTurn == 1) {
                            player_1[selectedSelfIndex].setImageResource(fromCardToImageSource(game.getPlayer1()[selectedSelfIndex]));
                            player_2[opponentIndex].setImageResource(fromCardToImageSource(game.getPlayer2()[opponentIndex]));
                        } else {
                            player_2[selectedSelfIndex].setImageResource(fromCardToImageSource(game.getPlayer2()[selectedSelfIndex]));
                            player_1[opponentIndex].setImageResource(fromCardToImageSource(game.getPlayer1()[opponentIndex]));
                        }

                        // להחזיר את התמונות ל-back לאחר 2 שניות
                        new Handler().postDelayed(() -> {
                            if (playerTurn == 1) {
                                player_1[selectedSelfIndex].setImageResource(R.drawable.back);
                                player_2[opponentIndex].setImageResource(R.drawable.back);
                            } else {
                                player_2[selectedSelfIndex].setImageResource(R.drawable.back);
                                player_1[opponentIndex].setImageResource(R.drawable.back);
                            }
                        }, 2000);

                        // הודעת הצלחה
                        Toast.makeText(getContext(), "Cards switched!", Toast.LENGTH_SHORT).show();

                        // איפוס משתנים
                        selectedSelfIndex = -1;
                        isWaitingForSecondPick = false;
                        isReplaceSpecialActive = false;
                        isPlayerClickEnabled = false;

                        // סיבוב משחק
                        game.switchTurn();
                        updateTurnText();
                        heapTop.setImageResource(R.drawable.back);
                        currentDrawnCard = null;
                    } else {
                        Toast.makeText(getContext(), "Invalid card selection", Toast.LENGTH_SHORT).show();
                        resetReplaceSpecialState();
                        return;
                    }

                    heapTop.setEnabled(true);
                    return;
                } else {
                    Toast.makeText(getContext(), "Pick a card from your opponent", Toast.LENGTH_SHORT).show();
                }
            }
        }




        if (clickedPlayer != game.getCurrentPlayerTurn()) {
            Toast.makeText(getContext(), "Not your turn!", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (isReplaceMode && index >= 4) {
            int realIndex = index - 4;
            Card cardToStock = game.getPlayer2()[realIndex];
            addCardToStock(cardToStock);
            game.getPlayer2()[realIndex] = currentDrawnCard;
            player_2[realIndex].setImageResource(fromCardToImageSource(currentDrawnCard));
            new Handler().postDelayed(() -> {
                player_2[realIndex].setImageResource(R.drawable.back);
            }, 2000);
            Toast.makeText(getContext(), "Card replaced", Toast.LENGTH_SHORT).show();
            isReplaceMode = false;
            isPlayerClickEnabled = false;
            game.switchTurn();
            updateTurnText();
            heapTop.setImageResource(R.drawable.back);
            currentDrawnCard = null;
            heapTop.setEnabled(true);
            return;
        }

        if ((isReplaceMode || isTakingFromStock) && index >= 4) {
            int realIndex = index - 4;
            Card cardToStock = game.getPlayer2()[realIndex];
            game.getPlayer2()[realIndex] = currentDrawnCard;
            player_2[realIndex].setImageResource(fromCardToImageSource(currentDrawnCard));
            new Handler().postDelayed(() -> player_2[realIndex].setImageResource(R.drawable.back), 2000);
            Toast.makeText(getContext(), "Card replaced", Toast.LENGTH_SHORT).show();
            if (isTakingFromStock) {
                game.getStock().pop();
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
                realIndex = index;
                selectedCardView = player_1[realIndex];
                selectedCard = game.getPlayer1()[realIndex];
            } else {
                realIndex = index - 4;
                selectedCardView = player_2[realIndex];
                selectedCard = game.getPlayer2()[realIndex];
            }

            selectedCardView.setImageResource(fromCardToImageSource(selectedCard));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedCardView.setImageResource(R.drawable.back);
                    isPeekActive = false;
                    isPlayerClickEnabled = false;
                    addCardToStock(currentDrawnCard);
                    currentDrawnCard = null;
                    heapTop.setImageResource(R.drawable.back);
                    game.switchTurn();
                    updateTurnText();
                    heapTop.setEnabled(true);
                }
            }, 2000);

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
                game.getStock().pop(); // באמת מסיר אותו רק אחרי שימוש
            } else {
                addCardToStock(cardToStock);
            }

            resetAfterMove();
            return;
        }
        if(index < 4)
        {
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
                Card heapHead = this.gameHeap.pop();
                Card carToDrop = fromImageSourseToCard(player_1[index]);
                player_1[index].setImageResource(fromCardToImageSource(heapHead));
                game.addToStock(carToDrop);
            }
            else
            {
                int num = c.getNum();
                int dr = getResources().getIdentifier("card_" + num, "drawable", getActivity().getPackageName());
                player_1[index].setImageResource(dr);

            }
        }
        else
        {
            index -= 4;
            Card c = game.getPlayer2()[index];
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
                int num = c.getNum();
                int dr = getResources().getIdentifier("card_" + num, "drawable", getActivity().getPackageName());
                player_2[index].setImageResource(dr);

            }
        }




    }

    private Card fromImageSourseToCard(ImageView imageView)//מתרגמת תמונה של קלף (ImageView) חזרה לאובייקט Card
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
           for (int i = 0; i <= 9; i++)
           {
               int id = getResources().getIdentifier("card_"+i, "drawable", getContext().getPackageName());
               Drawable dr = getResources().getDrawable(id);
               if(drawable.getConstantState().equals(dr.getConstantState()))
                   return new Card(i);
           }
           return null;

    }

    public int fromCardToImageSource(Card c)//הופכת אובייקט Card למזהה תמונה (drawable id)
    {
        if(c instanceof SpecialCard)
        {
            SpecialCard sp = (SpecialCard)c;
            if(sp.getName().equals("replace"))
                return R.drawable.card_replace;
            if(sp.getName().equals("draw2"))
                return R.drawable.card_draw2;
            return R.drawable.card_peek;
        }
        else
        {
            int num = c.getNum();
            int dr = getResources().getIdentifier("card_" + num, "drawable", getActivity().getPackageName());
            return dr;
        }
    }

    public void shoeDialog()//פותחת תיבת דיאלוג לבחירת פעולה לאחר שליפת קלף מהערימה לזרוק אותו או להחליף אותו
    {
        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.choose_dialog);
        dialog.setTitle("choose your next move");
        RadioButton replaceRb = dialog.findViewById(R.id.radioButtonReplace);
        RadioButton throwRb = dialog.findViewById(R.id.radioButtonThrow);
        Button btn = dialog.findViewById(R.id.buttonDone);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    addCardToStock(currentDrawnCard);
                    heapTop.setImageResource(R.drawable.back);
                    isPlayerClickEnabled = false;
                    game.switchTurn();
                    updateTurnText();
                    dialog.dismiss();
                    heapTop.setEnabled(true);
                }

            }
        });
        dialog.show();
    }
    private void handlePeekCard() {
        Toast.makeText(getContext(), "Pick a card to peek at", Toast.LENGTH_SHORT).show();
        isPlayerClickEnabled = true;
        isPeekActive = true;  // נכנס למצב peek
    }


    private void updateTurnText() {
       int current = game.getCurrentPlayerTurn();
       turnTextView.setText("Player " + current + "'s turn");
   }

    private void addCardToStock(Card card) {
        game.addToStock(card);
        layoutStock.removeAllViews();
        ImageView cardView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cardWidth, cardHeight);
        cardView.setLayoutParams(params);
        cardView.setImageResource(fromCardToImageSource(card));
        layoutStock.addView(cardView);
    }




    private void handleDraw2Card() {
        Toast.makeText(getContext(), "Drawing 2 more cards...", Toast.LENGTH_SHORT).show();

        for (int i = 0; i < 2; i++) {
            if (!gameHeap.isEmpty()) {
                Card extraCard = gameHeap.pop();
                game.addToStock(extraCard);
            }
        }

        new Handler().postDelayed(() -> {
            game.switchTurn();
            updateTurnText();
            heapTop.setImageResource(R.drawable.back);
            currentDrawnCard = null;
            isPlayerClickEnabled = false;
        }, 2000);
        heapTop.setEnabled(true);
    }
    private boolean isTakingFromStock = false;

    private void takeCardFromStock() {
        if (!game.getStock().isEmpty()) {
            currentDrawnCard = game.getStock().peek();
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