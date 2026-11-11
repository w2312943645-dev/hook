此目录随构建产物注入 APK 的 META-INF/xposed/。
* java_init.list —— 入口类全名, 每行一个 (LSPosed 加载时调用其 IXposedHookLoadPackage).
* module.prop —— 本工程置于 APK 根目录 (经典布局); 有的 LSPosed 版本也读
  META-INF/xposed 下的 module.prop —— 按目标版本文档调整位置, 写入逻辑不变.
* 作用域由 LSPosed 管理器勾选, 不是 APK 内文件.
勿把 README/其他文件误命名为 .list (会被当作入口类名加载).
