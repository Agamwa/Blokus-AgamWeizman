package agam.w.myproject;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ChooseFragment extends Fragment {
    // Button to start practice game (currently not used)
    Button btnPracticeGame;
    // Button to start two-player game mode
    Button  btnTwoPlayers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.fragment_choose, container, false);
        btnTwoPlayers = view.findViewById(R.id.btnTwoPlayers);

        btnTwoPlayers.setOnClickListener(new View.OnClickListener() {
          @Override
          // Replace the current fragment with the game board (BoardFragment)
          public void onClick(View v) {
              replaceFragment(new BoardFragment());
          }});

      return view;
    }

    // This method replaces the current fragment with the one passed as an argument
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();

    }
}