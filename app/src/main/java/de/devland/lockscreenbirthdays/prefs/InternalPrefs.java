package de.devland.lockscreenbirthdays.prefs;

import de.devland.esperandro.annotations.SharedPreferences;

@SharedPreferences(name = "internal")
public interface InternalPrefs {
    boolean onBoardingFinished();

    void onBoardingFinished(boolean onBoardingFinished);
}
