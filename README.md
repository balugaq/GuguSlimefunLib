# GuguSlimefunLib

为 Slimefun 附属提供机器基类、嵌套分类与配方预览界面的前置库，装上即可支撑相关附属运行。

GuguSlimefunLib 是一个面向 **Slimefun 附属开发者**的基础库插件：它本身不新增游戏内容，而是把附属开发中反复出现的样板能力（机器方块、能耗、配方展示、分类菜单、NBT/颜色处理等）沉淀成可复用的 API。

## 功能一览

- **机器基类**：`AbstractMachineBlock` / `TickingBlock`——一个抽象类搞定 ticking、能耗（`EnergyNetComponent`）、配方处理（`MachineProcessor`）与输入输出槽管理。
- **高级分类**：`AdvancedNestedItemGroup`——支持动态增删子分类的嵌套物品分类。
- **指南界面**：`AEMenu`——带返回键的 Slimefun 指南菜单，配合 `InventoryListener` 支持机器配方的双列输入/输出预览。
- **物品工具**：`AdvancedCustomItemStack`、`ItemKey`、`ItemStackCache`、`MenuItems` 等。
- **集成库**：内嵌并 relocate 的 NBT-API 与 matlib，CMI 颜色代码支持（`CMIChatColor`），跨版本工具（`MinecraftPlatform` / `Version`）。

## 前置要求

- Paper 服务端（`api-version: 1.16+`）
- [Slimefun4](https://github.com/SlimefunGuguProject/Slimefun4)（**硬依赖**）
- Java 21+

## 安装

1. 安装 Slimefun4；
2. 将 `GuguSlimefunLib-<version>.jar` 放入 `plugins/` 目录；
3. 重启服务器。依赖本库的附属请一并放入。

## 命令

| 命令 | 权限 | 说明 |
| --- | --- | --- |
| `/server_uuid` | `guguslimefunlib.server_uuid`（默认 OP） | 获取当前服务器 UUID |

## 开发者接入

在附属项目中通过 JitPack 引入（以 [JitPack](https://jitpack.io/#balugaq/GuguSlimefunLib) 上的最新 tag 为准）：

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.balugaq:GuguSlimefunLib:<version>")
}
```

本库自带的 NBT-API 与 matlib 已 relocate 到 `me.ddggdd135.guguslimefunlib.libraries.*`，不会与其他插件的版本冲突。

## 构建

```bash
./gradlew clean build
```

产物位于 `build/libs/GuguSlimefunLib-<version>.jar`。构建需要 Java 21+（`options.release = 21`，JDK 21 及以上均可编译）。

## 兼容性

CI（`test-compatibility.yml`）默认在 Paper 1.20.1 与 26.2 上启动服务器验证插件正常启用/禁用。

## 反馈

[Issues](https://github.com/balugaq/GuguSlimefunLib/issues)

## 许可

[MIT License](LICENSE) © 时光镇开发组
