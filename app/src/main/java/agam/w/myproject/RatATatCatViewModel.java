package agam.w.myproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RatATatCatViewModel extends ViewModel {
    private MyGame myGame;

    public enum TurnState {
        SELECT_PILE,
        PLACE_IN_YOUR_DECK,
        PEEK, // Choose any card
        DRAW2_CHOOSE_FIRST_CARD,
        FIRST_CARD_IN_YOUR_DECK,
        DRAW2_CHOOSE_SECOND_CARD,
        SECOND_CARD_IN_YOUR_DECK,
        SPECIAL_REPLACE_CHOOSE_YOUR_CARD,
        SPECIAL_REPLACE_CHOOSE_OPPONENT_CARD,
        SHOW_CARD_IDLE,
    }

    private enum SelectedPile {
        DRAW_PILE,
        GARBAGE_PILE
    }
    private final MutableLiveData<Integer> currentPlayerTurnLiveData = new MutableLiveData<>();
    private final MutableLiveData<TurnState> turnStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<SelectedPile> selectedPileLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> selecetedCardPlayer1 = new MutableLiveData<>();
    private final MutableLiveData<Integer> selecetedCardPlayer2 = new MutableLiveData<>();
    private final MutableLiveData<String> winnerLiveData = new MutableLiveData<>();
    private final MutableLiveData<Card> topDrawPileCardLiveData = new MutableLiveData<>();
    private final MutableLiveData<Card> topGarbageCardLiveData = new MutableLiveData<>();
    public RatATatCatViewModel() {
        myGame = new MyGame();

        currentPlayerTurnLiveData.setValue(1);
        turnStateLiveData.setValue(TurnState.SELECT_PILE);

        selectedPileLiveData.setValue(null);
        selecetedCardPlayer1.setValue(null);
        selecetedCardPlayer2.setValue(null);

        winnerLiveData.setValue(null);

        topDrawPileCardLiveData.setValue(myGame.getDrawPile().peek());
        topGarbageCardLiveData.setValue(null);
    }

    // implement turn function

    public LiveData<Integer> getCurrentPlayerTurnLiveData() {
        return currentPlayerTurnLiveData;
    }

}
