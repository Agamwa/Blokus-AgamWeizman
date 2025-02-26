
public class Card
{
    private int num;

    public Card(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString()
    {
        return "[" + this.num +"]";
    }

}
