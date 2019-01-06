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
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class OnBoardingActivity extends IntroActivity {

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
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Permissions")
                .description("This app needs the permission to read contacts to function properly.")
                .image(R.drawable.ic_contacts)
                .permission(Manifest.permission.READ_CONTACTS)
                .build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
            batterySlide = new SimpleSlide.Builder()
                    .title("Battery Optimizations")
                    .description("Please turn off battery optimizations for this app to make sure it stays in the background and can remind you of your contact's birthdays.")
                    .buttonCtaLabel("Turn off Battery Optimizations")
                    .buttonCtaClickListener(__ -> {
                        startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName())));
                    })
                    .canGoForward(false)
                    .build();
            addSlide(batterySlide);

            setNavigationPolicy(new NavigationPolicy() {
                @SuppressLint("NewApi")
                @Override
                public boolean canGoForward(int position) {
                    if (position == 2) {
                        return powerManager.isIgnoringBatteryOptimizations(getPackageName());
                    }
                    return true;
                }

                @Override
                public boolean canGoBackward(int position) {
                    return true;
                }
            });
        }
    }
}
