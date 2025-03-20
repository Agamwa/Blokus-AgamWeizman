package agam.w.myproject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;


public class BoardFragment extends Fragment implements View.OnClickListener{


    MyGame game = new MyGame();
    ImageView[] player_1;
    ImageView[] player_2 = new ImageView[4];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        player_1 = new ImageView[4];
        for (int i = 0; i < player_1.length ; i++)
        {
            // imageViewPlayer1_2
            int id = getResources().getIdentifier("imageViewPlayer1_" + (i + 1), "id", getActivity().getPackageName());
            player_1[i].findViewById(id);
            // player_1[i].setOnClickListener(this);
        }

         /*
        for (int i = 0; i < player_2.length ; i++)
        {
            // imageViewPlayer1_2
            int id = getResources().getIdentifier("imageViewPlayer2_" + (i + 1), "id", getActivity().getPackageName());
            player_2[i].findViewById(id);
            player_2[i].setOnClickListener(this);
        }
*/

        View view = inflater.inflate(R.layout.fragment_board, container, false);
        return view;
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if(id == R.id.imageViewPlayer1_1)
        {
            Card c = game.getPlayer1()[0];
            if(c instanceof SpecialCard)
            {
                SpecialCard sp = (SpecialCard)c;
                if(sp.getName().equals("replace"))
                    player_1[0].setImageResource(R.drawable.card_replace);
                if(sp.getName().equals("draw_2"))
                    player_1[0].setImageResource(R.drawable.card_draw2);
                if(sp.getName().equals("peek"))
                    player_1[0].setImageResource(R.drawable.card_peek);
            }
            else
            {
                int num = c.getNum();
                int dr = getResources().getIdentifier("card_" + num, "drawable", getActivity().getPackageName());
                player_1[0].setImageResource(dr);

            }
        }
    }
}