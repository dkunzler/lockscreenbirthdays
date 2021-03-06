package de.devland.lockscreenbirthdays.util;

import android.content.ComponentName;
import android.content.pm.PackageManager;

import de.devland.lockscreenbirthdays.App;
import de.devland.lockscreenbirthdays.R;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by deekay on 25.05.2015.
 */
@RequiredArgsConstructor
public enum Icon {
    CAKE(".launcher.Cake", R.drawable.ic_stat_torte_notif, R.mipmap.ic_launcher_cake),
    CUPCAKE(".launcher.Cupcake", R.drawable.ic_stat_cupcake_notif, R.mipmap.ic_launcher_cupcake),
    MATERIAL_CAKE(".launcher.MaterialCake", R.drawable.ic_stat_torte_notif, R.mipmap.ic_launcher_material_cake),
    MATERIAL_CUPCAKE(".launcher.MaterialCupcake", R.drawable.ic_stat_cupcake_notif, R.mipmap.ic_launcher_material_cupcake);

    private static final String PACKAGE_NAME = "de.devland.lockscreenbirthdays";

    private final String activityName;
    @Getter
    private final int notificationIconId;
    @Getter
    private final int iconId;

    public void set() {
        App app = App.getInstance();
        PackageManager packageManager = app.getPackageManager();
        for (Icon icon : Icon.values()) {
            packageManager.setComponentEnabledSetting(
                    new ComponentName(app, PACKAGE_NAME + icon.activityName),
                    this == icon ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
