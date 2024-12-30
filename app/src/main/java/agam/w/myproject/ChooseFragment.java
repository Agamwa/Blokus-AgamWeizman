package agam.w.myproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ChooseFragment extends Fragment {
    Button btnPracticeGame, btnTwoPlayers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.fragment_choose, container, false);
      btnPracticeGame = view.findViewById(R.id.btnPracticeGame);
      btnTwoPlayers = view.findViewById(R.id.btnTwoPlayers);

      return view;
    }
}