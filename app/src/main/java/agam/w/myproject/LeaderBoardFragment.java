package agam.w.myproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LeaderBoardFragment extends Fragment {

    private List<LeaderboardItem> leaderboardItemList = new ArrayList<LeaderboardItem>();
    private RecyclerView rv;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leader_board, container, false);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.rvLeadeboardItems);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        LeaderboardAdapter adapter = new LeaderboardAdapter(leaderboardItemList);
        rv.setAdapter(adapter);

        firestore.collection("Users").get().addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                QuerySnapshot res = t.getResult();
                for (QueryDocumentSnapshot documentSnapshot: res) {
                    leaderboardItemList.add(
                            new LeaderboardItem(
                                    documentSnapshot.getString("firstName"),
                                    documentSnapshot.getString("lastName"),
                                    documentSnapshot.getLong("wins")
                            ));
                }

                Collections.sort(leaderboardItemList);

                adapter.notifyDataSetChanged();

            } else {
                Log.e("LeaderboardFragment", "Failed to load the leaderboard" + t.getException().getMessage());
                Toast.makeText(requireContext(), "Failed to load the leaderboard", Toast.LENGTH_SHORT).show();
            }
        });
    }
}