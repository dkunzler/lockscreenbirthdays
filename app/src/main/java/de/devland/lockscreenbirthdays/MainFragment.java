package de.devland.lockscreenbirthdays;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;

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

        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
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
}
