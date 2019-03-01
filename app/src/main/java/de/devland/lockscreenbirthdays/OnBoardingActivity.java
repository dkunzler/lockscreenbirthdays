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

import de.devland.lockscreenbirthdays.util.ForwardConditionedSlide;

public class OnBoardingActivity extends IntroActivity {

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.slide_title_welcome)
                .description(R.string.slide_desc_welcome)
                .image(R.drawable.ic_cupcake_large)
                .background(R.color.md_blue_500)
                .backgroundDark(R.color.primary_dark)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.slide_title_permissions)
                .description(R.string.slide_desc_permissions)
                .image(R.drawable.ic_contacts)
                .permission(Manifest.permission.READ_CONTACTS)
                .background(R.color.md_red_300)
                .backgroundDark(R.color.primary_dark)
                .build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && powerManager != null && !powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
            addSlide(new ForwardConditionedSlide.ForwardConditionedBuilder()
                    .canGoForward(() -> powerManager.isIgnoringBatteryOptimizations(getPackageName()))
                    .title(R.string.slide_title_battery)
                    .description(R.string.slide_desc_battery)
                    .image(R.drawable.ic_battery)
                    .buttonCtaLabel(R.string.slide_btn_battery)
                    .buttonCtaClickListener(__ -> startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()))))
                    .background(R.color.md_green_700)
                    .backgroundDark(R.color.primary_dark)
                    .build());
        }
    }

}
