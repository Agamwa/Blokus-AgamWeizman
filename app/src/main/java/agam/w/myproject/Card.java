package agam.w.myproject;

import androidx.annotation.NonNull;

public class Card
{
    protected  int num;

    public Card() {
    }

    public Card(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @NonNull
    @Override
    public String toString()
    {
        return "[" + this.num + "]";
    }

}
