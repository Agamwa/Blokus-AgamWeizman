import android.util.Log;

import java.util.Collections;
import java.util.Stack;

public class Game
{
    private int turnNumber;
    private Card[] player1, player2;
    private Stack<Card> heap, stock;
    private int sum1, sum2;
    private Card player1CurrentCard;
    private Card player2CurrentCard;

    public Game()
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

}
