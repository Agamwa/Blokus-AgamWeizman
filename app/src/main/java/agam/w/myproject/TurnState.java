package agam.w.myproject;

public enum TurnState {
    SELECT_PILE, // Select the draw pile or the garbage pile
    PLACE_IN_YOUR_DECK, // If regular card is selected, choose a place it in your deck (or discard it)
    PLACE_IN_YOUR_DECK_FROM_GARBAGE,
    SPECIAL_PEEK, // If special peek card is selected, choose a what card to show
    SPECIAL_DRAW2_CHOOSE_FIRST_CARD, // If special draw2 card is selected, choose a card to draw from the draw pile
    SPECIAL_FIRST_CARD_IN_YOUR_DECK, // Select the place of the first card from the draw 2
    SPECIAL_DRAW2_CHOOSE_SECOND_CARD,  // Select the second card
    SECOND_CARD_IN_YOUR_DECK,  // Select the place of the second card from the draw 2
    SPECIAL_REPLACE_CHOOSE_YOUR_CARD, // If special replace card is selected, choose a card from your deck
    SPECIAL_REPLACE_CHOOSE_OPPONENT_CARD, // Next choose a card from your opponent's deck
    SHOW_CARD_IDLE, // Idle state when showing a card
}
