package agam.w.myproject;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RatATatCatViewModel extends ViewModel {
    private static final String TAG = "RatATatCatViewModel";

    private MyGameManager myGame;

    private final MutableLiveData<Integer> currentPlayerTurnLiveData = new MutableLiveData<>(); // Current player turn
    private final MutableLiveData<TurnState> turnStateLiveData = new MutableLiveData<>(); // Current turn state
    private final MutableLiveData<SelectedPile> selectedPileLiveData = new MutableLiveData<>(); // Draw pile or garbage pile
    private final MutableLiveData<Integer> selecetedCardPlayer1 = new MutableLiveData<>(); // From 0 - 3
    private final MutableLiveData<Integer> selecetedCardPlayer2 = new MutableLiveData<>(); // From 0 - 3
    private final MutableLiveData<Card> topDrawPileCardLiveData = new MutableLiveData<>();
    private final MutableLiveData<Card> topGarbageCardLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> canPlay = new MutableLiveData<>();
    public RatATatCatViewModel() {
        myGame = new MyGameManager();

        currentPlayerTurnLiveData.setValue(1);
        turnStateLiveData.setValue(TurnState.SELECT_PILE);

        selectedPileLiveData.setValue(null);
        selecetedCardPlayer1.setValue(null);
        selecetedCardPlayer2.setValue(null);

        topDrawPileCardLiveData.setValue(myGame.getDrawPile().peek());
        topGarbageCardLiveData.setValue(null);

        canPlay.setValue(true);
    }

    public void turn(SelectedPile selectedPile, int clickedCardPlayer1, int clickedCardPlayer2) {
        if (!canPlay.getValue()) return;

        switch (turnStateLiveData.getValue()) {
            case SELECT_PILE:
                // Validate the required parameters
                if (selectedPile == null) return;

                Log.d(TAG, "player: " + currentPlayerTurnLiveData.getValue() + "turn: SELECT_PILE = " + selectedPile.toString() + " clickedCardPlayer1 = " + clickedCardPlayer1 + " clickedCardPlayer2 = " + clickedCardPlayer2);
                if (selectedPile == SelectedPile.DRAW_PILE) {
                    selectedPileLiveData.setValue(selectedPile);
                    if (topDrawPileCardLiveData.getValue() instanceof SpecialCard) {
                        SpecialCard specialCard = (SpecialCard) topDrawPileCardLiveData.getValue();
                        if (specialCard.getName().equals("replace")) {
                            turnStateLiveData.setValue(TurnState.SPECIAL_REPLACE_CHOOSE_YOUR_CARD);
                        } else if (specialCard.getName().equals("peek")) {
                            turnStateLiveData.setValue(TurnState.SPECIAL_PEEK);
                        } else if (specialCard.getName().equals("draw 2")) {
                            turnStateLiveData.setValue(TurnState.SPECIAL_DRAW2_CHOOSE_FIRST_CARD);
                        }
                    } else { // Regular card
                        turnStateLiveData.setValue(TurnState.PLACE_IN_YOUR_DECK);
                    }
                } else if (selectedPile == SelectedPile.GARBAGE_PILE) {
                    if (topGarbageCardLiveData.getValue() instanceof SpecialCard) return;
                    selectedPileLiveData.setValue(selectedPile);
                    turnStateLiveData.setValue(TurnState.PLACE_IN_YOUR_DECK_FROM_GARBAGE);
                }
//                    turnStateLiveData.setValue(TurnState.SHOW_CARD_IDLE);
//                    // hides after 2 seconds
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            turnStateLiveData.setValue(TurnState.PLACE_IN_YOUR_DECK);
//                        }
//                    })
                break;
            case PLACE_IN_YOUR_DECK_FROM_GARBAGE:
            case PLACE_IN_YOUR_DECK:
                // Validate the required parameters
                if (currentPlayerTurnLiveData.getValue() == 1 && (clickedCardPlayer1 < 0 || clickedCardPlayer1 > 4)) return;
                if (currentPlayerTurnLiveData.getValue() == 2 && (clickedCardPlayer2 < 0 || clickedCardPlayer2 > 4)) return;
                if (selectedPileLiveData.getValue() == SelectedPile.GARBAGE_PILE && (clickedCardPlayer1 == 4 || clickedCardPlayer2 == 4)) return;

                // 0 - 3 is the deck cards, and 4 is the garbage
                if (clickedCardPlayer1 == 4 || clickedCardPlayer2 == 4) {
                    throwToGarbage(myGame.getDrawPile().pop());
                } else {
                    if (currentPlayerTurnLiveData.getValue() == 1) {
                        placeInYourDeck(1,
                                selectedPileLiveData.getValue() == SelectedPile.DRAW_PILE,
                                clickedCardPlayer1);
                        selecetedCardPlayer1.setValue(clickedCardPlayer1);
                    } if (currentPlayerTurnLiveData.getValue() == 2) {
                        placeInYourDeck(2,
                                selectedPileLiveData.getValue() == SelectedPile.DRAW_PILE,
                                clickedCardPlayer2);
                        selecetedCardPlayer2.setValue(clickedCardPlayer2);
                    }
                }

                canPlay.setValue(false);
                turnStateLiveData.setValue(TurnState.SELECT_PILE);

                // In 2 seconds, reset the selected cards
                new Handler().postDelayed(() -> {
                    selecetedCardPlayer1.setValue(null);
                    selecetedCardPlayer2.setValue(null);
                    // Switch to the next player's turn
                    currentPlayerTurnLiveData.setValue(currentPlayerTurnLiveData.getValue() == 1 ? 2 : 1);
                    // Reset the turn state
                    canPlay.setValue(true);
                }, 2000);

                break;
            case SPECIAL_PEEK:
                // Validate the required parameters
                if ((clickedCardPlayer1 < 0 || clickedCardPlayer1 > 3) && (clickedCardPlayer2 < 0 || clickedCardPlayer2 > 3)) return;
                if (selecetedCardPlayer1.getValue() != null) return;
                if (selecetedCardPlayer2.getValue() != null) return;

                if (clickedCardPlayer1 >= 0 && clickedCardPlayer1 <= 3)
                    selecetedCardPlayer1.setValue(clickedCardPlayer1);
                else if (clickedCardPlayer2 >= 0 && clickedCardPlayer2 <= 3)
                    selecetedCardPlayer2.setValue(clickedCardPlayer2);

                // In 2 seconds, reset the turn state
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        turnStateLiveData.setValue(TurnState.SELECT_PILE);
                    }
                }, 2000);

                break;
            case SPECIAL_DRAW2_CHOOSE_FIRST_CARD:
                break;
                // Handle other cases as needed
            default:
                break;
        }
    }

    // isDrawPile = false, takes from the garbage
    public void placeInYourDeck(int player, boolean isDrawPile, int pos) {
        myGame.placeInYourDeck(player, isDrawPile, pos);

        topDrawPileCardLiveData.setValue(myGame.getDrawPile().peek());
        topGarbageCardLiveData.setValue(myGame.getGarbage().peek());

        selectedPileLiveData.setValue(null);
    }

    public void throwToGarbage(Card c) {
        myGame.throwToGarbage(c);
        topGarbageCardLiveData.setValue(c);
    }

    public LiveData<Integer> getCurrentPlayerTurnLiveData() {
        return currentPlayerTurnLiveData;
    }

    public LiveData<TurnState> getTurnStateLiveData() {
        return turnStateLiveData;
    }

    public LiveData<SelectedPile> getSelectedPileLiveData() {
        return selectedPileLiveData;
    }

    public LiveData<Integer> getSelecetedCardPlayer1() {
        return selecetedCardPlayer1;
    }

    public LiveData<Integer> getSelecetedCardPlayer2() {
        return selecetedCardPlayer2;
    }

    public LiveData<Card> getTopDrawPileCardLiveData() {
        return topDrawPileCardLiveData;
    }

    public LiveData<Card> getTopGarbageCardLiveData() {
        return topGarbageCardLiveData;
    }

    public void selectPile(SelectedPile selectedPile) {
        selectedPileLiveData.setValue(selectedPile);
    }

    public Card getPlayerCard(int player, int pos) {
        if (player == 1)
            return myGame.getPlayer1()[pos];
        return myGame.getPlayer2()[pos];
    }

    public String endGame() {
        return myGame.endGame();
    }

    public boolean garbageIsEmpty() {
        return myGame.getGarbage().isEmpty();
    }
}
