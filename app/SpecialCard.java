

public class SpecialCard extends Card
{
    private String name;

    public SpecialCard(String name) {
        super(10);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "[" + this.name +"]";
    }

}
