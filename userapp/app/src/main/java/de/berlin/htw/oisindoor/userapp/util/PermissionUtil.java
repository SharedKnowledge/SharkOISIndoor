package de.berlin.htw.oisindoor.userapp.util;

import android.os.Build;

/**
 * Created by Max on 07.01.2016.
 */
public class PermissionUtil {
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
