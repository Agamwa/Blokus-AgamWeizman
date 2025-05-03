package agam.w.myproject;
import android.util.Log;

import java.util.Collections;
import java.util.Stack;


public class MyGame
{
    private int turnNumber;
    private Card[] player1, player2;
    private Stack<Card> heap;
    private Stack<Card> stock; // heap= ערימה שלוקחים ממנה קלפים, stock= ערימת זבל
    private Card player1CurrentCard;
    private Card player2CurrentCard;
    private int currentPlayerTurn = 1; // 1 לשחקן 1, 2 לשחקן 2
    private int player1Wins = 0;  // ניצחונות של שחקן 1
    private int player2Wins = 0;


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
        this.player1CurrentCard = null;
        this.player2CurrentCard = null;
    }

    public Stack<Card> getHeap() {
        return heap;
    }

    public Stack<Card> getStock() {
        return stock;
    }

    public void replace(int player, int pos, Card c)
    {
        if(player == 1)
            this.player1[pos] = c;
        else
            this.player2[pos] = c;
    }
    // זריקת קלף לערימת זבל
    public void throwToStock(Card c)
    {
        this.stock.push(c);

    }

    // פעולה המקבלת את מספר השחקן ואת מקום הקלף שבחר ומחליפה את הקלף האחרון בערימת ה"זבל" אל מקום הקלף שהשחקן בחר.
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
//פעולה שתתרחש בקבלת הקלף "הצץ"- היא מקבלת את מספר השחקן שקיבל את הקלף ואת מיקום הקלף שבחר להציץ בו ומראה לשחקן את הקלף שבחר .
    public Card takeAPeak(int playerNum, int chosenPlace)
    {
        if(playerNum == 1)
            return this.player1[chosenPlace];
        return this.player2[chosenPlace];
    }
    // פעולה שתתרחש בקבלת הקלף "החלף"- היא מקבלת את השחקן שקיבל את הקלף, את המקום של הקלף של השחקן השני שירצה להחליף איתו ואת הקלף שירצה להחליף איתו מהשחקן השני. פעולה זו מבצעת את ההחלפה בין שני הקלפים.
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
    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public void switchTurn() {
        if (currentPlayerTurn == 1)
            currentPlayerTurn = 2;
        else
            currentPlayerTurn = 1;
    }

    public Card[] getPlayer1() {
        return player1;
    }

    public Card[] getPlayer2() {
        return player2;
    }

    public void addToStock(Card card)
    {
        this.stock.push(card);
    }
    public int getPlayer1Wins() {
        return player1Wins;
    }

    public int getPlayer2Wins() {
        return player2Wins;
    }

    // פעולה לעדכון הניצחון
    public void updateWin(int winner) {
        if (winner == 1) {
            player1Wins++;
        } else if (winner == 2) {
            player2Wins++;
        }
    }
}
