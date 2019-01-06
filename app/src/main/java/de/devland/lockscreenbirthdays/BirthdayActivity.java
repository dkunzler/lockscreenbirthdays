package de.devland.lockscreenbirthdays;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;


public class BirthdayActivity extends Activity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
        Esperandro.getPreferences(DefaultPrefs.class, this).initDefaults();
        ButterKnife.bind(this);
        setActionBar(toolbar);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                if (fm.getBackStackEntryCount() == 1) {
                    getActionBar().setDisplayHomeAsUpEnabled(false);
                }
                fm.popBackStack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            if (fm.getBackStackEntryCount() == 1) {
                getActionBar().setDisplayHomeAsUpEnabled(false);
            }
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
