package palmero.com.sendit;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import palmero.com.sendit.models.Monster;

/**
 * A placeholder fragment containing a simple view.
 */
public class GameActivityFragment extends Fragment {
    private LinearLayout mMonstersContainer;
    public GameActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_game, container, false);
        mMonstersContainer = (LinearLayout) fragmentView.findViewById(R.id.monsters_container);
        return fragmentView;

    }

    public void updateMonsters(LinkedHashMap<String, Monster> monsters){
        mMonstersContainer.removeAllViews();
        Collection<Monster> colMonsters = monsters.values();
        for (Monster monster: colMonsters) {
            mMonstersContainer.addView(monster.createMonsterCard(getActivity()));
        }
    }
}
