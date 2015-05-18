package de.devland.lockscreenbirthdays.prefs;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.text.MessageFormat;

import de.devland.lockscreenbirthdays.R;
import de.devland.lockscreenbirthdays.util.IabHelper;
import de.devland.lockscreenbirthdays.util.IabResult;

/**
 * Created by David Kunzler on 04.01.2015.
 */
public class SettingsFragment extends PreferenceFragment implements
        IabHelper.OnIabSetupFinishedListener {

    private static final String apiKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkjL2HBniIr4zAV/0vO7/X27jlyVGDjYgigfc+xtVAYDim/lpUuNcT4MisnYGuLbNpG8DbFAKfxesW1qgV4vfqJe/ylGgn7nrCKjZzaqv9Kmw96ssguXOCWZzY7d5M2jYgW1juX0+VI7xY9OoIOBPkSTBKryaTIpPuVc7bccXs2fTjh8D+Bd1jMHoH1pdCOwQOlanzT6117mTEgTcVgs6yuqRz1D55QeUcXFXn/1MFk27B4kFPuj2Os/6yCRY8uVwFJQKCtEK0JpQAXNLL8mu+sE2prbUNrQvv29DcPfUR35Qr3uevMQG6f8mxnp2ThJjDQYuvqt/oO9hq/Lpkg3BWwIDAQAB";

    private IabHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_main);
        bindPreferenceSummaryToValue(findPreference("maxDaysTillBirthday"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.dispose();
            helper = null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity.getActionBar() != null) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getActionBar().setTitle(R.string.action_settings);
        }

        helper = new IabHelper(getActivity(), apiKey);
        helper.startSetup(this);
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (result.isSuccess()) {
            addPreferencesFromResource(R.xml.pref_iab);
            BeerPreference donationPreference = (BeerPreference) findPreference("donation");
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener bindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value != null ? value.toString() : "";

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            }
            if (preference.getKey().equals("maxDaysTillBirthday")) {
                String format = getActivity().getResources().getString(R.string.summary_daysTillBirthday);
                String summary = MessageFormat.format(format, Integer.parseInt(stringValue));
                preference.setSummary(summary);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #bindPreferenceSummaryToValueListener
     */
    protected void bindPreferenceSummaryToValue(Preference preference) {
        if (preference != null) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(bindPreferenceSummaryToValueListener);

            // Trigger the listener immediately with the preference's
            // current value.
            bindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getAll().get(preference.getKey()));
        }
    }

}
