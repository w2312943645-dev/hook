# build-support/ — compile-only Xposed host stub

`app/build.gradle.kts` compiles against `compileOnly(files("../../build-support/xposed-stub.jar"))`.

Why the stub exists:
- the module references host-provided FQNs (`de.robv.android.xposed.*`,
  `de.robv.android.xposed.callbacks.XC_LoadPackage`);
- a build that dragged a real host jar in as a normal dependency risks shipping
  sharable FQN structures into the APK that the LSPosed host also defines;
- `compileOnly` keeps the stub out of package output: runtime classes are the host's,
  always.

Surface mirrored (only what the module calls):
- `XposedBridge.log(String)`
- `XposedHelpers.findClassIfExists(String, ClassLoader)`
- `XposedHelpers.findAndHookMethod(Class, String, Object...)`
- `XC_MethodHook` / `XC_MethodReplacement`
- `IXposedHookLoadPackage.handleLoadPackage(XC_LoadPackage.LoadPackageParam)`
- `XC_LoadPackage.LoadPackageParam` fields used by the module.

Build: `bash build-support/make-xposed-stub.sh` — javac --release 17 + jar;
JDK: `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-arm64` (this box).
Regenerate after any hook-call change; keep surface minimal.
