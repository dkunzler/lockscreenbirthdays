package de.devland.lockscreenbirthdays;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class OnBoardingActivity extends IntroActivity {

    private static final int POSITION_BATTERY = 2;

    private SimpleSlide batterySlide;

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        addSlide(new SimpleSlide.Builder()
                .title("Welcome")
                .description("This app shows the birthdays of your contacts as notification after turning on your device.")
                .image(R.mipmap.ic_launcher_cupcake)
                .background(R.color.md_blue_500)
                .backgroundDark(R.color.primary_dark)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Permissions")
                .description("This app needs the permission to read contacts to function properly.")
                .image(R.drawable.ic_contacts)
                .permission(Manifest.permission.READ_CONTACTS)
                .background(R.color.md_red_300)
                .backgroundDark(R.color.primary_dark)
                .build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
            batterySlide = new SimpleSlide.Builder()
                    .title("Battery Optimizations")
                    .description("Please turn off battery optimizations for this app to make sure it stays in the background and can remind you of your contact's birthdays.")
                    .image(R.drawable.ic_battery)
                    .buttonCtaLabel("Turn off Battery Optimizations")
                    .buttonCtaClickListener(__ -> {
                        startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName())));
                    })
                    .background(R.color.md_green_700)
                    .backgroundDark(R.color.primary_dark)
                    .canGoForward(false)
                    .build();
            addSlide(batterySlide);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getCurrentSlidePosition() == POSITION_BATTERY) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                // we probably came from the dialog to whitelist our app and it worked -> Move on
                setResult(RESULT_OK);
                finish();
            }
        }

    }
}
