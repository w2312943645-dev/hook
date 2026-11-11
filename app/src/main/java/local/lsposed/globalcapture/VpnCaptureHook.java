package local.lsposed.globalcapture;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * Global-capture Hook — removes the accelerator tunnel's app filter so the
 * capture set degrades to the framework default (empty filter set = all apps).
 *
 * We hook the FRAMEWORK API, not the app's (obfuscated) classes:
 *   android.net.VpnService$Builder#addAllowedApplication(String)
 *   android.net.VpnService$Builder#addDisallowedApplication(String)
 * (sample decompile: com.pallas.booster.engine3.ot.a build method is the only caller site).
 *
 * Scope: com.njh.biubiu (main + :engine processes share package name).
 */
public final class VpnCaptureHook implements de.robv.android.xposed.IXposedHookLoadPackage {

    private static final String TARGET_PKG = "com.njh.biubiu";

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) {
        if (!TARGET_PKG.equals(lpparam.packageName)) return;

        Class<?> builder = XposedHelpers.findClassIfExists(
                "android.net.VpnService$Builder", lpparam.classLoader);
        if (builder == null) return;

        XC_MethodReplacement noop = new XC_MethodReplacement();
        try {
            XposedHelpers.findAndHookMethod(builder, "addAllowedApplication", String.class, noop);
            XposedHelpers.findAndHookMethod(builder, "addDisallowedApplication", String.class, noop);
        } catch (Throwable t) {
            XposedBridge.log("vpn-capture hook install failed: " + t);
        }
    }
}
