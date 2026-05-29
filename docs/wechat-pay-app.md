# 微信支付 APP 支付

> 官方文档：[产品介绍](https://pay.weixin.qq.com/doc/v3/merchant/4013070158)  
> 文档索引：[商户平台 llms.txt](https://pay.weixin.qq.com/doc/v3/merchant/llms.txt)

## 产品概述

APP 支付为商户在自有 App 内提供微信支付收款能力。用户在商户 App 下单后跳转微信完成验密支付，再返回商户 App 查看结果。

适用场景：电商、游戏、生活服务等独立 App。

## 准入条件

1. **主体类型**：个体工商户、企业、事业单位、政府机关、社会组织
2. **应用要求**：[已认证](https://kf.qq.com/faq/170824URbmau170824r2uY7j.html)的[微信开放平台](https://open.weixin.qq.com)账号，且已创建**移动应用**类型 AppID
3. **商户号**：在微信支付商户平台开通 APP 支付权限，并将 AppID 与商户号绑定

## 支付流程

```
用户下单 → 商户后端 APP 下单 API → 获得 prepay_id
         → 客户端 OpenSDK 调起支付 → 微信收银台
         → 用户支付/取消 → 返回商户 App（onResp 回调）
         → 商户后端查单 / 接收支付回调 → 更新订单状态
```

| 步骤 | 说明 |
|------|------|
| 1 | 用户在商户 App 选购商品并确认支付 |
| 2 | 跳转微信支付验密页（密码/指纹，可选零钱或银行卡） |
| 3 | 支付成功后微信展示成功页，可点「返回商家」 |
| 4 | 返回商户 App，展示订单支付结果 |
| 5 | 用户可在微信「我的账单」中查看明细 |

**重要**：客户端 `onResp` 回调不可靠，订单最终状态必须以后端**查单**和**支付成功回调**为准。

## 官方文档索引

### 接入指引

| 文档 | 链接 |
|------|------|
| 产品介绍 | https://pay.weixin.qq.com/doc/v3/merchant/4013070158 |
| 开发接入准备 | https://pay.weixin.qq.com/doc/v3/merchant/4015478291 |
| 开发指引 | https://pay.weixin.qq.com/doc/v3/merchant/4013070176 |
| OpenSDK 接入指南 | https://pay.weixin.qq.com/doc/v3/merchant/4013289321 |
| 申请 APP 支付权限 | https://pay.weixin.qq.com/doc/v3/merchant/4013070174 |
| 管理商户号绑定的 AppID | https://pay.weixin.qq.com/doc/v3/merchant/4013289251 |
| 支付回调和查单实现指引 | https://pay.weixin.qq.com/doc/v3/merchant/4012075249 |
| 常见问题 | https://pay.weixin.qq.com/doc/v3/merchant/4013070182 |

### API 列表

| API | 链接 |
|-----|------|
| APP 下单 | https://pay.weixin.qq.com/doc/v3/merchant/4013070347 |
| APP 调起支付 | https://pay.weixin.qq.com/doc/v3/merchant/4013070351 |
| 微信支付订单号查询订单 | https://pay.weixin.qq.com/doc/v3/merchant/4013070354 |
| 商户订单号查询订单 | https://pay.weixin.qq.com/doc/v3/merchant/4013070356 |
| 关闭订单 | https://pay.weixin.qq.com/doc/v3/merchant/4013070360 |
| 支付成功回调通知 | https://pay.weixin.qq.com/doc/v3/merchant/4013070368 |
| 退款申请 | https://pay.weixin.qq.com/doc/v3/merchant/4013070371 |
| APP 调起支付签名 | https://pay.weixin.qq.com/doc/v3/merchant/4012365340 |

### 微信开放平台 OpenSDK

| 平台 | 链接 |
|------|------|
| Android | https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html |
| iOS | https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/iOS.html |
| 鸿蒙 | https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/ohos.html |

## 核心接口说明

### 1. APP 下单（服务端）

```
POST https://api.mch.weixin.qq.com/v3/pay/transactions/app
```

关键请求字段：

| 字段 | 说明 |
|------|------|
| `appid` | 开放平台移动应用 AppID（需与商户号绑定） |
| `mchid` | 商户号 |
| `description` | 商品描述（≤127 字符，会显示在微信账单） |
| `out_trade_no` | 商户订单号（6–32 位，商户号下唯一） |
| `notify_url` | 支付结果回调地址 |
| `amount.total` | 金额，单位**分** |
| `time_expire` | 可选，支付截止时间（RFC3339）；不传则默认 7 天有效 |

响应：`prepay_id`（有效期 **2 小时**，过期需重新下单）

### 2. APP 调起支付（客户端）

商户后端下单拿到 `prepay_id` 后，由客户端通过 OpenSDK `sendReq` 拉起微信支付。

`PayReq` 参数：

| 参数 | 说明 |
|------|------|
| `appId` | 下单时的应用 ID |
| `partnerId` | 商户号 mchid |
| `prepayId` | 下单接口返回的 prepay_id |
| `packageValue` / `package` | 固定值 `Sign=WXPay`（Android 用 packageValue，iOS 用 package） |
| `nonceStr` | 随机字符串（≤32 位） |
| `timeStamp` | **秒级** Unix 时间戳 |
| `sign` | RSA 签名，见[APP 调起支付签名](https://pay.weixin.qq.com/doc/v3/merchant/4012365340.md) |

`onResp` 错误码：

| errCode | 含义 | 处理 |
|---------|------|------|
| `0` | 成功 | 调用后端查单确认 |
| `-1` | 错误 | 检查签名、AppID、SDK 配置 |
| `-2` | 用户取消 | 展示取消状态 |

## KMP 项目接入建议

本项目为 Kotlin Multiplatform（Android + iOS），微信支付需**前后端分离 + 平台原生 SDK**：

```
┌─────────────┐     prepay_id + sign     ┌─────────────┐
│  shared     │ ◄─────────────────────── │  商户后端    │
│  (业务/UI)  │                          │  (下单/查单) │
└──────┬──────┘                          └─────────────┘
       │ expect/actual
   ┌───┴───┐
   ▼       ▼
androidApp  iosApp
(OpenSDK)   (OpenSDK)
```

### 推荐模块划分

| 层级 | 位置 | 职责 |
|------|------|------|
| 共享业务 | `shared/commonMain` | 订单状态、支付结果 UI、`expect` 支付接口 |
| Android 实现 | `shared/androidMain` 或 `androidApp` | 集成微信 OpenSDK、`WXPayEntryActivity` |
| iOS 实现 | `shared/iosMain` 或 `iosApp` | CocoaPods 集成、URL Scheme / Universal Link |
| 服务端 | 独立后端 | APP 下单、签名、查单、回调验签（**不可放在客户端**） |

### Android 注意事项

- 必须实现 `包名.wxapi.WXPayEntryActivity`，否则支付后可能无法返回 App
- Android 13+：Manifest 中去除 `WXPayEntryActivity` 的 `intent-filter`，避免 Intent 被屏蔽

```xml
<activity
    android:name=".wxapi.WXPayEntryActivity"
    android:exported="true"
    android:launchMode="singleTask"
    android:taskAffinity="填写你的包名"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

### iOS 注意事项

- 按[ iOS 接入指引](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/iOS.html) 配置 **URL Scheme**
- 通过 CocoaPods 集成时务必配置 Universal Link，否则可能出现校验失败

## 订单状态

| trade_state | 含义 |
|-------------|------|
| `NOTPAY` | 未支付 |
| `SUCCESS` | 支付成功（终态） |
| `CLOSED` | 已关闭（终态） |
| `REFUND` | 转入退款（终态） |

- 未支付订单默认 7 天后自动关闭，也可调用关单 API 主动关闭
- `prepay_id` 超过 2 小时需重新下单

## 结算与提现

- [收款资金结算规则](https://kf.qq.com/faq/140225MveaUz1504092YFjeM.html)
- [收款资金提现](https://kf.qq.com/faq/161223NJBr2u161223Mfeqei.html)
