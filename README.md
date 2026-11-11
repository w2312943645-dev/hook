# 漂呗（biubiu）加速器 —— 全局捕获 Hook 模块

研究/逆向伴随模块: 在你有权控制的设备与样本上开发; 行为边界见 §6–§7。
**v0.1 无运行开关**: 勾中模块即全局捕获; 退勾/移除即回退。

## 1. 问题 → 机制 → 本模块做了什么
加速器把“捕获哪些 App”做成数据: profile → `TunConfig`(Parcelable) → 建隧道时写进
`VpnService.Builder` 的 allowed/disallowed 名单; 空名单 = 框架默认**全局捕获**
(引擎日志印过字面 `"TUN >> Enable GLOBAL APPS vpn access!"`, 说明该退化分支是设计内的)。

已反编译样本(`resources/`+`sources/`,约 36k 文件)坐标:
- 全树唯一定名施加点 `com.pallas.booster.engine3.ot.a` 构建方法
  (batch-26/ot/a.java,反编译行约 322–352;`builder.add*Application`)。
- 引擎服务 `SpeedupEngine3Service`(主进程)+ `SpeedupEngine3ServiceSA`(":engine" 进程)。

本模块钩**框架**方法 `android.net.VpnService$Builder#addAllowedApplication/addDisallowedApplication`
→ 名单决策缺席 → 隧道生而全局捕获。**换不到运行中隧道**:过滤集在 `establish()` 固化;
想改捕获面得按框架语义重建隧道/重启服务进程 — VpnService 的性质,不是模块缺陷。

## 2. 结构
```
lsposed-global-capture/
├── app/  build.gradle.kts(AGP 8.7.3, compileSdk 35) + AndroidManifest + VpnCaptureHook.java
├── build-support/  compile-only Xposed stub + make-xposed-stub.sh + README
├── module/META-INF/xposed/java_init.list + module.prop
├── scripts/inject-xposed-meta.sh
├── settings.gradle.kts  google()/mavenCentral()  (失败即显式报错)
├── gradle.properties  · .gitignore  · README.md
```

## 3. 构建(env: JDK17+，网络到你执行的仓库)
```bash
# (0) stub
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-arm64 bash build-support/make-xposed-stub.sh
# (1) build — wrapper included (Gradle 8.14.3), JDK 17+
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-arm64 ./gradlew :app:assembleDebug --stacktrace
# (2) inject module meta (⚠ breaks any signature → re-sign!)  zipalign+apksigner from build-tools
./scripts/inject-xposed-meta.sh app/build/outputs/apk/debug/app-debug.apk
# (3) LSPosed: install, scope = com.njh.biubiu, restart target process(es)
```
`gradle` on PATH on this box is a manual install (§5); the wrapper (8.14.3)
bundled here pins the version for CI — bootstrapping is only needed if you
regenerate: `gradle wrapper --gradle-version 8.14.3`.

## 4. 生效/回退/边界
- 钩框架面,他们 App 私有类不动; 对客户端改版更耐。
- 意图注册面:`module.prop` + `META-INF/xposed/java_init.list`; 作用域由管理器勾("com.njh.biubiu")。
- 注入流程与常见变体见 scripts/inject-xposed-meta.sh 头注释。
- 边界: 系统级隧道/撤销语义、省电策略、多 VPN 意图与本模块正邪无关;
  客户端任务层策略开关(如 `only_ping_one_game` 类动态配置**不是捕获面**,别混淆)。
- 若运行时名单落在别处(native JNI 选路/新 builder 封装), 本模块不覆盖:排查顺序 native→序列化面→下发面。

## 5. 构建环境·本机实况(2026-11-10, Ubuntu 24.04 aarch64 容器,挂载 sdcard)
| 项 | 实况 |
| JDK | OpenJDK17 `/usr/lib/jvm/java-17-openjdk-arm64` |
| Gradle | 手动 **8.14.3** → `/opt/gradle-dist/gradle-8.14.3`,`gradle` 软链 /usr/local/bin; 源 `services.gradle.org` 200(约137MB,长时间传断点续传手法见 `.tmp` 曾用脚本,成果已解压) |
| zip/unzip | ✓(apt) |
| Android SDK | **本会话已装**（cmdline-tools 12.0 + platforms;android-35 + build-tools 34.0.0/35.0.0；ANDROID_HOME=~/android-sdk）；CI 侧由 workflow 装。sdkmanager 首跑需接受 licenses。 |
| 仓库源 | repo.maven.apache.org / maven.google.com(301重定向)正常; **github/raw.githubusercontent 与 aliyun 本机不可达** —— 若你的构建需要,要先解决出站网络 |
| 其他 | 容器盘余量充足;Android 工具链按需自备密钥/签名(ks 敏感,不入库) |

> 本会话观察: 工具面写入曾多次产出 **0 字节文件**(写成功但不落盘),shell 写这一面可靠(已底探针验证)。凡需真字节者一律走 shell 并 `wc -c` 复核。

## 6. 诚实边界
- 迷你逆向模块:行为视为对**框架过滤施加**的移除;**不是**协议层/账号层功能,可用性受宿主版本与系统约束影响;
- 单测/静态校验撑不起真机结论:装入 LSPosed + 目标包 + 看日志/对照为准;
- 未/禁碰:广告过滤改动、服务端交互模拟、私人数据;样本与设备归属由使用者负责。

## 7. 動手前自查清单(避免半小时白搭)
- [ ] JDK17 / Gradle 与 `gradle :app:assembleDebug` 对仓库可解析
- [ ] Android SDK 已装;`ANDROID_HOME` 与 `sdkmanager --version` 通
- [ ] 能联网拉 AGP/依赖(本仓解析走 google()+mavenCentral())
- [ ] stub 已造;改过钩调用面 → 重造并重编译
- [ ] zipalign/apksigner 在 build-tools 上;keystore 自备