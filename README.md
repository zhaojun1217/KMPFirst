这是一个面向 Android 和 iOS 的 Kotlin Multiplatform 项目。

* [/iosApp](./iosApp/iosApp) 包含 iOS 应用。即使使用 Compose Multiplatform 共享 UI，仍需要这个入口来启动 iOS 应用，也可以在这里添加 SwiftUI 代码。

* [/shared](./shared/src) 用于在 Compose Multiplatform 应用之间共享的代码，包含以下子目录：
  - [commonMain](./shared/src/commonMain/kotlin) 存放所有目标平台共用的代码。
  - 其他目录存放仅针对对应平台编译的 Kotlin 代码。例如，若要在 iOS 端使用 Apple 的 CoreCrypto，可在 [iosMain](./shared/src/iosMain/kotlin) 中编写；若需编写 Desktop（JVM）相关代码，则使用 [jvmMain](./shared/src/jvmMain/kotlin)。

### 运行应用

可使用 IDE 工具栏运行配置中的选项，也可执行以下命令：

- Android：`./gradlew :androidApp:assembleDebug`
- iOS：在 Xcode 中打开 [/iosApp](./iosApp) 目录并运行。

### 运行测试

可在 IDE 编辑器行号旁点击运行按钮，或执行以下 Gradle 任务：

- Android 测试：`./gradlew :shared:testAndroidHostTest`
- iOS 测试：`./gradlew :shared:iosSimulatorArm64Test`

### 文档

- [微信支付 APP 支付](./docs/wechat-pay-app.md) — 接入说明与官方文档索引

---

了解更多：[Kotlin Multiplatform 入门](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
