package de.devland.lockscreenbirthdays.prefs;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by David Kunzler on 03.01.2015.
 */
@SharedPreferences
public interface DefaultPrefs extends SharedPreferenceActions {

    @Default(ofInt = 2)
    int maxDaysTillBirthday();
    void maxDaysTillBirthday(int daysTillBirthday);

    @Default(ofBoolean = false)
    boolean serviceEnabled();
    void serviceEnabled(boolean serviceEnabled);
}
