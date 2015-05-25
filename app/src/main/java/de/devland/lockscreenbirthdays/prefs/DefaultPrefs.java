package de.devland.lockscreenbirthdays.prefs;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;
import de.devland.lockscreenbirthdays.util.Icon;

/**
 * Created by David Kunzler on 03.01.2015.
 */
@SharedPreferences
public interface DefaultPrefs extends SharedPreferenceActions {

    @Default(ofString = "2")
    String maxDaysTillBirthday();
    void maxDaysTillBirthday(String daysTillBirthday);

    @Default(ofBoolean = false)
    boolean serviceEnabled();
    void serviceEnabled(boolean serviceEnabled);

    @Default(ofBoolean = false)
    boolean serviceShowcased();
    void serviceShowcased(boolean serviceShowcased);

    String donation();
    void donation(String donation);

    @Default(ofString = "MATERIAL_CAKE")
    String icon();
    void icon(String icon);
}
