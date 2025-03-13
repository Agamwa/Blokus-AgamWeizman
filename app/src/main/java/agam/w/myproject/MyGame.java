package agam.w.myproject;
import android.util.Log;

import java.util.Collections;
import java.util.Stack;


public class MyGame
{
    private int turnNumber;
    private Card[] player1, player2;
    private Stack<Card> heap, stock; // heap= ערימה שלוקחים ממנה קלפים, stock= ערימת זבל
    private int sum1, sum2;
    private Card player1CurrentCard;
    private Card player2CurrentCard;

    public MyGame()
    {
        this.turnNumber = 1;
        heap = new Stack<>();
        for (int i = 0; i < 3; i++)
        {
            heap.push(new SpecialCard("replace"));
        }
        for (int i = 0; i < 3; i++)
        {
            heap.push(new SpecialCard("draw 2"));
        }
        for (int i = 0; i < 3; i++)
        {
            heap.push(new SpecialCard("peek"));
        }
        for (int i = 0; i < 9; i++)
        {
            heap.push(new Card(9));
        }
        for (int i = 0; i <= 7; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                heap.push(new Card(i));
            }
        }
        Collections.shuffle(heap);
        Log.d("cards", heap.toString());

        this.player1 = new Card[4];
        this.player2 = new Card[4];
        for (int i = 0; i < 4; i++)
        {
            player1[i] = heap.pop();
            player2[i] = heap.pop();
        }
        this.stock = new Stack<>();
        this.sum1 = 0;
        this.sum2 = 0;
        this.player1CurrentCard = null;
        this.player2CurrentCard = null;
    }



    public void replace(int player, int pos, Card c)
    {
        if(player == 1)
            this.player1[pos] = c;
        else
            this.player2[pos] = c;
    }

    public void throwToStock(Card c)
    {
        this.stock.push(c);

    }

    public void takeLastCardFromStock(int playerNum, int chosenPlace)
    {
        if(!this.stock.empty())
        {
            Card c = this.stock.pop();
            Card c1 = null;
            if(playerNum == 1)
            {
                c1 = this.player1[chosenPlace];
                this.player1[chosenPlace] = c;
            }
            else
            {
                c1 = this.player2[chosenPlace];
                this.player2[chosenPlace] = c;
            }
            this.stock.push(c1);
        }
    }

    public Card takeAPeak(int playerNum, int chosenPlace)
    {
        if(playerNum == 1)
            return this.player1[chosenPlace];
        return this.player2[chosenPlace];
    }

    public void change(int fromPlayer, int toPlayer, int fromPosition, int toPosition) {
        if (fromPlayer == 1)
        {
            Card fromCard = this.player1[fromPosition];
            this.player1[fromPlayer] = this.player2[toPosition];
            this.player2[toPosition] = fromCard;
        }
        else
        {
            Card fromCard = this.player2[fromPosition];
            this.player2[fromPlayer] = this.player1[toPosition];
            this.player1[toPosition] = fromCard;
        }

    }

}
