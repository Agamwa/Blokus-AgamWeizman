package agam.w.myproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FragmentWinsTable extends Fragment {
    TextView player1WinsText,player2WinsText;
    private MyGame game;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wins_table, container, false);



        TextView player1WinsText = view.findViewById(R.id.player1Wins);
        TextView player2WinsText = view.findViewById(R.id.player2Wins);

        player1WinsText.setText("Player 1 Wins: " + game.getPlayer1Wins());
        player2WinsText.setText("Player 2 Wins: " + game.getPlayer2Wins());

        return view;
    }

    public FragmentWinsTable(MyGame game) {
        this.game = game;
    }
}