这是一个面向 Android 和 iOS 的 Kotlin Multiplatform 项目。

* [/shared](./shared/src) 存放跨平台**业务逻辑**（登录 Repository、ViewModel、`LoginBridge` 等），不包含 UI。
* [/androidApp](./androidApp/src/main/kotlin) 使用 **Compose** 实现 Android 登录界面。
* [/iosApp](./iosApp/iosApp) 使用 **SwiftUI** 实现 iOS 登录界面，通过 `Shared` 框架调用 Kotlin 逻辑。

`shared` 源码目录说明：

- [commonMain](./shared/src/commonMain/kotlin) 存放所有目标平台共用的代码。
- 其他目录存放仅针对对应平台编译的 Kotlin 代码。例如，若要在 iOS 端使用 Apple 的 API，可在 [iosMain](./shared/src/iosMain/kotlin) 中编写。

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
