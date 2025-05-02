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
    private boolean isReplaceMode = false;
    private boolean isPlayerClickEnabled = false;
    private TextView turnTextView;
    private LinearLayout layoutStock;
    private int cardWidth, cardHeight;


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
                cardWidth = heapTop.getLayoutParams().width; // שמירה על רוחב הקלף ב-heap
                cardHeight = heapTop.getLayoutParams().height; // שמירה על גובה הקלף ב-heap
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shoeDialog();
                    }
                },2000);

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
            Toast.makeText(getContext(), "Card replaced. The new card will be hidden shortly.", Toast.LENGTH_SHORT).show();
            isReplaceMode = false;
            isPlayerClickEnabled = false;
            game.switchTurn();
            updateTurnText();
            heapTop.setImageResource(R.drawable.back);
            currentDrawnCard = null;
            return;
        }

        if (isReplaceMode && index < 4)
        {
            int newIndex = index;
            Card cardToStock = game.getPlayer1()[index];
            addCardToStock(cardToStock);
            game.getPlayer1()[index] = currentDrawnCard;
            player_1[index].setImageResource(fromCardToImageSource(currentDrawnCard));
                new Handler().postDelayed(() -> {
                    player_1[newIndex].setImageResource(R.drawable.back);
                }, 2000);
            Toast.makeText(getContext(), "Card replaced. The new card will be hidden shortly.", Toast.LENGTH_SHORT).show();
            isReplaceMode = false;
            isPlayerClickEnabled = false;
            game.switchTurn();
            updateTurnText();
            heapTop.setImageResource(R.drawable.back);
            currentDrawnCard = null;
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

    public int fromCardToImageSource(Card c)//ופכת אובייקט Card למזהה תמונה (drawable id)
    {
        if(c instanceof SpecialCard)
        {
            SpecialCard sp = (SpecialCard)c;
            if(sp.getName().equals("replace"))
                return R.drawable.card_replace;
            if(sp.getName().equals("draw_2"))
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
                }

            }
        });
        dialog.show();
    }
   private void updateTurnText() {
       int current = game.getCurrentPlayerTurn();
       turnTextView.setText("Player " + current + "'s turn");
   }

    private void addCardToStock(Card card) {
        game.addToStock(card);
        ImageView cardView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cardWidth, cardHeight); // שימוש בגודל של קלף ה-heap
        cardView.setLayoutParams(params);
        cardView.setImageResource(fromCardToImageSource(card));
        if (layoutStock.getChildCount() > 0) {
            ImageView previousCardView = (ImageView) layoutStock.getChildAt(0);
            previousCardView.setVisibility(View.GONE);
        }
        layoutStock.addView(cardView);
    }


}