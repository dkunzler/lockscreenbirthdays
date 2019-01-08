package de.devland.lockscreenbirthdays;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.model.Contact;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;
import de.devland.lockscreenbirthdays.prefs.SettingsFragment;
import de.devland.lockscreenbirthdays.service.BirthdayService;

/**
 * Created by David Kunzler on 03.01.2015.
 */
public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    @BindView(R.id.serviceSwitch)
    protected Switch serviceSwitch;
    @BindView(R.id.birthdayList)
    protected RecyclerView birthdayList;
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
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?

            // No explanation needed, we can request the permission.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        1);
            }

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

        if (defaultPrefs.serviceEnabled() && !BirthdayService.isRunning) {
            BirthdayService.start(getActivity());
        }

        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(R.string.title_activity_main);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateBirthdays();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, getActivity());

        birthdayList.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            updateBirthdays();
        }

        serviceSwitch.setChecked(defaultPrefs.serviceEnabled());
        serviceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            defaultPrefs.serviceEnabled(isChecked);
            if (isChecked) {
                if (!BirthdayService.isRunning) {
                    BirthdayService.start(getActivity());
                }
            } else {
                BirthdayService.stop(getActivity());
            }
        });

        if (!defaultPrefs.serviceShowcased()) {
            try {
                ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(getActivity());
                showCaseBuilder.hideOnTouchOutside().setContentTitle(R.string.showcase_serviceTitle)
                        .withNewStyleShowcase()
                        .setStyle(R.style.ShowcaseLightTheme)
                        .setContentText(R.string.showcase_serviceText)
                        .hideOnTouchOutside()
                        .setTarget(new ViewTarget(serviceSwitch));
                showCaseBuilder.build().show();
            } catch (Exception e) {
                Log.e(TAG, "onViewCreated: failed to showcase service usage", e);
            } finally {
                defaultPrefs.serviceShowcased(true);
            }
        }
    }

    private void updateBirthdays() {
        List<Contact> birthdays = Contact.getAllContactsWithBirthdays(getActivity());
        Collections.sort(birthdays);
        birthdayList.setAdapter(new BirthdayAdapter(getActivity(), birthdays));
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
