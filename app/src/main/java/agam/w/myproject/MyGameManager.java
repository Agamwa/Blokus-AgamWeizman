package agam.w.myproject;

import java.util.Collections;
import java.util.Stack;

public class MyGameManager {
    private Card[] player1, player2;
    private Stack<Card> drawPile;
    private Stack<Card> garbage;
    private int score1 = 0, score2 = 0;
    private String winner = "";
    public MyGameManager() {
        initializeGame();
    }

    public Stack<Card> getDrawPile() {
        return drawPile;
    }

    public Stack<Card> getGarbage() {
        return garbage;
    }

    public Card[] getPlayer1() {
        return player1;
    }

    public Card[] getPlayer2() {
        return player2;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public String getWinner() {
        return winner;
    }

    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    // isDrawPile = false, takes from the garbage
    public void placeInYourDeck(int player, boolean isDrawPile, int pos) {
        Card chosenCard;
        if (isDrawPile) {
            chosenCard = drawPile.pop();
        } else {
            chosenCard = garbage.pop();
        }
        Card removedCard = replace(player, pos, chosenCard);
        garbage.push(removedCard);
    }

    public Card replace(int player, int pos, Card c) {
        Card temp;
        if (player == 1) {
            temp = player1[pos];
            player1[pos] = c;
        } else {
            temp = player2[pos];
            player2[pos] = c;
        }
        return temp;
    }

    public void throwToGarbage(Card c) {
        garbage.push(c);
    }

    public void takeLastCardFromStock(int playerNum, int chosenPlace) {
        if (!garbage.isEmpty()) {
            Card c = garbage.pop();
            Card oldCard;
            if (playerNum == 1) {
                oldCard = player1[chosenPlace];
                player1[chosenPlace] = c;
            } else {
                oldCard = player2[chosenPlace];
                player2[chosenPlace] = c;
            }
            garbage.push(oldCard);
        }
    }

    public void addToStock(Card card) {
        garbage.push(card);
    }


    public void specialCardReplace(int pos1, int pos2) {
        Card temp = player1[pos1];
        player1[pos1] = player2[pos2];
        player2[pos2] = temp;
    }

    public void specialCardDraw2(int pos1, int pos2) {
        isDoubleTurn = true;
    }


    public String endGame() {
        int sumPlayer1 = 0;
        int sumPlayer2 = 0;
        Card[] player1Cards = player1;
        Card[] player2Cards = player2;
        for (int i = 0; i < player1Cards.length; i++)
            score1 += player1Cards[i].getNum();

        for (int i = 0; i < player2Cards.length; i++)
            score2 += player2Cards[i].getNum();

        String result;
        if (score1 < score2) {
            result = "Player 1 wins with " + sumPlayer1 + " vs " + sumPlayer2;
            winner = "Player 1";
        } else if (score2 < score1) {
            result = "Player 2 wins with " + sumPlayer2 + " vs " + sumPlayer1;
            winner = "Player 2";
        } else {
            result = "It's a tie! Both have " + sumPlayer1;
            winner = "Tie";
        }
        return result;
    }

    public boolean isPlayerTurn(int playerIndex) {
        return currentPlayerTurn == playerIndex;
    }

    public void initializeGame() {
        drawPile = new Stack<>();
        for (int i = 0; i < 3; i++) drawPile.push(new SpecialCard("replace"));
        for (int i = 0; i < 3; i++) drawPile.push(new SpecialCard("draw 2"));
        for (int i = 0; i < 3; i++) drawPile.push(new SpecialCard("peek"));

        for (int i = 0; i < 9; i++) drawPile.push(new Card(9));
        for (int i = 0; i <= 7; i++)
            for (int j = 0; j < 4; j++)
                drawPile.push(new Card(i));

        Collections.shuffle(drawPile);

        player1 = new Card[4];
        player2 = new Card[4];
        for (int i = 0; i < 4; i++) {
            player1[i] = drawPile.pop();
            player2[i] = drawPile.pop();
        }

        garbage = new Stack<>();
        this.currentPlayerTurn = 1;
    }

}
