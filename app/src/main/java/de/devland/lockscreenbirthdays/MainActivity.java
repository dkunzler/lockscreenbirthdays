package de.devland.lockscreenbirthdays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toolbar;

import butterknife.BindView;
import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.InternalPrefs;


public class MainActivity extends Activity {

    private static final int REQUEST_CODE_INTRO = 42;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    private InternalPrefs internalPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        internalPrefs = Esperandro.getPreferences(InternalPrefs.class, this);
        if (!internalPrefs.onBoardingFinished()) {
            Intent nextActivity = new Intent(this, OnBoardingActivity.class);
            startActivityForResult(nextActivity, REQUEST_CODE_INTRO);
        } else {
            Intent nextActivity = new Intent(this, BirthdayActivity.class);
            startActivity(nextActivity);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                // Finished the intro
                internalPrefs.onBoardingFinished(true);
                Intent nextActivity = new Intent(this, BirthdayActivity.class);
                startActivity(nextActivity);
                finish();
            } else {
                // Cancelled the intro
                finish();
            }
        }
    }
}
