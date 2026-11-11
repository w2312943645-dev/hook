package de.robv.android.xposed;
import java.lang.reflect.Method;
public final class XposedHelpers {
    private XposedHelpers() { }
    public static Class<?> findClassIfExists(String name, ClassLoader cl) {
        if (name == null) return null;
        ClassLoader c = (cl != null) ? cl : ClassLoader.getSystemClassLoader();
        try { return Class.forName(name, true, c); } catch (Throwable t) { return null; }
    }
    public static XC_MethodHook findAndHookMethod(Class<?> clazz, String methodName, Object... args) {
        if (clazz == null || methodName == null) throw new NullPointerException("findAndHookMethod: clazz/methodName");
        Class<?>[] ps = new Class<?>[args == null ? 0 : args.length];
        for (int i = 0; i < ps.length; i++) {
            if (!(args[i] instanceof Class<?>)) throw new IllegalArgumentException("arg["+i+"] must be Class");
            ps[i] = (Class<?>) args[i];
        }
        try { Method m = clazz.getDeclaredMethod(methodName, ps); m.setAccessible(true); return new HookHandle(clazz, methodName, m); }
        catch (Throwable t) { throw new IllegalStateException("findAndHookMethod failed", t); }
    }
    private static final class HookHandle extends XC_MethodHook {
        HookHandle(Class<?> c, String m, Method mm) { }
    }
}
