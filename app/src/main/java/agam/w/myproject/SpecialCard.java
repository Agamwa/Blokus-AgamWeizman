package agam.w.myproject;

import androidx.annotation.NonNull;

public class SpecialCard extends Card {
    public enum CardType {
        PEEK,
        DRAW2,
        REPLACE,
    }
    private CardType type;

    public SpecialCard(CardType type) {
        super(10);
        this.type = type;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString()
    {
        return "[ " + this.num + " - " + this.type + " ]";
    }

}
