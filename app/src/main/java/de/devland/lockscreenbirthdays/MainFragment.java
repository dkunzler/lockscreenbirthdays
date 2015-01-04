package de.devland.lockscreenbirthdays;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;
import de.devland.lockscreenbirthdays.prefs.SettingsFragment;

/**
 * Created by David Kunzler on 03.01.2015.
 */
public class MainFragment extends Fragment {

    @InjectView(R.id.serviceSwitch)
    protected Switch serviceSwitch;
    private DefaultPrefs defaultPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity.getActionBar() != null) {
            activity.getActionBar().setTitle(R.string.title_activity_main);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, getActivity());

        serviceSwitch.setChecked(defaultPrefs.serviceEnabled());
        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                defaultPrefs.serviceEnabled(isChecked);
                Intent service = new Intent(getActivity().getApplicationContext(), BirthdayService.class);
                if (isChecked) {
                    getActivity().startService(service);
                } else {
                    getActivity().stopService(service);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getFragmentManager().
                    beginTransaction().
                    replace(R.id.container, new SettingsFragment()).
                    addToBackStack("settings").
                    commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
