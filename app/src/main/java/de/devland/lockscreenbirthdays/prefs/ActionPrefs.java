package de.devland.lockscreenbirthdays.prefs;


import java.util.HashMap;

import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by deekay on 28.06.2015.
 */
@SharedPreferences(name = "actionPrefs")
public interface ActionPrefs {
    HashMap<Integer, Long> dismissedBirthdays();

    void dismissedBirthdays(HashMap<Integer, Long> dismissedBirthdays);
}
