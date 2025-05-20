package agam.w.myproject;

public class LeaderboardItem implements Comparable<LeaderboardItem> {
    private String firstName;
    private String lastName;
    private long score;

    public LeaderboardItem(String firstName, String lastName, long score) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.score = score;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getScore() {
        return score;
    }


    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "LeaderboardItem{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public int compareTo(LeaderboardItem o) {
        return Long.compare(o.score, this.score);
    }
}
