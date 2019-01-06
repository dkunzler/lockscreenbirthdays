package de.devland.lockscreenbirthdays;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;
import de.devland.lockscreenbirthdays.prefs.InternalPrefs;


public class MainActivity extends Activity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InternalPrefs internalPrefs = Esperandro.getPreferences(InternalPrefs.class, this);
        Intent nextActivity;
        if (!internalPrefs.onBoardingFinished()) {
            nextActivity = new Intent(this, OnBoardingActivity.class);
        } else {
            nextActivity = new Intent(this, BirthdayActivity.class);
        }

        startActivity(nextActivity);
        finish();
    }
}
