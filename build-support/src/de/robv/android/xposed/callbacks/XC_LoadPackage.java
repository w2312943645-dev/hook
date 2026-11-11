package de.robv.android.xposed.callbacks;
import java.io.File;
import java.lang.ClassLoader;
public class XC_LoadPackage {
    public static class LoadPackageParam {
        public final String packageName;
        public final ClassLoader classLoader;
        public final File appDir;
        public final boolean isFirstApk;
        public LoadPackageParam(String packageName, ClassLoader classLoader) { this(packageName, classLoader, null, true); }
        public LoadPackageParam(String packageName, ClassLoader classLoader, File appDir, boolean isFirstApk) {
            this.packageName = packageName; this.classLoader = classLoader; this.appDir = appDir; this.isFirstApk = isFirstApk;
        }
        public String getPackageName() { return packageName; }
        public ClassLoader getClassLoader() { return classLoader; }
    }
}
