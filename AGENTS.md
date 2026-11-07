# AGENTS.md — GuguSlimefunLib 操作宪章

> 本文件是 AI 编程助手与本仓库开发者的**操作契约**。当本文与代码冲突时，以代码为准并更新本文。

## TL;DR

1. 本仓库是 **Slimefun 附属开发基础库**（非玩法插件，本身不注册游戏内容），Java 21 / Gradle Kotlin DSL，所有改动必须通过 `./gradlew clean build` 验收后才能提交。
2. 本库是其他附属的**硬依赖**：任何破坏二进制兼容的 API 改动（删方法、改签名、改 relocate 路径）都会连带毁掉下游附属。
3. 禁止 `git push -f`、禁止提交未编译验证的代码。

## 1. 项目身份与边界

| 维度 | 事实 |
| --- | --- |
| 定位 | Slimefun4 附属的**前置库插件**：提供机器基类、嵌套分类、指南菜单、NBT/颜色等公共能力 |
| 入口类 | `me.ddggdd135.guguslimefunlib.GuguSlimefunLib`（`extends JavaPlugin implements SlimefunAddon`） |
| 版本目标 | Java 21 字节码（`options.release = 21`）、Paper 1.21.11 API、Slimefun 2025.1、`api-version: 1.16` |
| 上游 | `TimetownDev/GuguSlimefunLib`（pull.yml 同步），本仓库 `balugaq/GuguSlimefunLib` |
| AI 职责范围 | 功能代码、Bug 修复、构建/CI/文档维护 |
| AI 不负责 | 发布版本、推送（由开发者执行） |

### 1.1 代码包地图

| 包 | 职责 |
| --- | --- |
| `api` | `AEMenu`（带返回键的指南菜单）、`CustomCraftingOperation`、`ItemHashMap` / `ItemHashSet`、`InitializeProvider` / `InitializeSafeProvider` |
| `api.abstracts` | `AbstractMachineBlock`（机器基类：ticking + 能耗 + 配方 + 槽位）、`TickingBlock` |
| `api.interfaces` | `InventoryBlock`（输入/输出槽契约）、`Integration` |
| `items` | `AdvancedCustomItemStack`、`AdvancedNestedItemGroup`（可增删子分类）、`ItemKey`、`ItemStackCache`、`ItemType`、`MenuItems` |
| `libraries.colors` | CMI 颜色代码移植（`CMIChatColor` 等） |
| `libraries.version` | `MinecraftPlatform` / `Version` 跨版本判断 |
| `listeners` | `InventoryListener`——指南内点击机器配方项时打开双列输入/输出预览菜单 |
| `commands` | `ServerUUID`（`/server_uuid` 命令） |
| `nms` / `tasks` / `utils` | `ItemNMS`、`AbstractArmorTask`、反射/物品/Potion/UUID 工具 |

## 2. 技术栈与结构（事实核对）

- **构建**：Gradle Kotlin DSL + Shadow 插件（`com.gradleup.shadow` 9.6.1）；产物 `build/libs/GuguSlimefunLib-<version>.jar`。
- **Shaded 依赖**（`implementation`，会打入 jar 并 relocate）：NBT-API（`com.github.TimetownDev:Item-NBT-API` → `...guguslimefunlib.libraries.nbtapi`）、matlib（`com.github.m1919810:matlib` → `...guguslimefunlib.libraries.matlib`）。**shadowJar 不用 minimize()**——库代码可能被下游反射访问，裁剪有风险。
- **compileOnly**：paper-api、Slimefun4（JitPack `2025.1`）、Lombok 1.18.38。
- **版本目录**：所有依赖坐标集中在 `gradle/libs.versions.toml`，`build.gradle.kts` 只用 `libs.xxx` 引用。
- **plugin.yml**：`version: '${version}'` 占位符由 `processResources` 展开（Gradle 迁移后已从 Maven 的 `${project.version}` 改过，勿改回）。
- **CI**：`build.yml`（JDK 21，push/PR 到 master）、`release.yml`（tag `v<version>`，版本号读自 `gradle.properties`）、`test-compatibility.yml`（Paper 1.20.1 与 26.2 启动测试，依赖插件只装 Slimefun4）。
- **JitPack**：`jitpack.yml` 用 openjdk21 + `./gradlew clean build -x test`。

## 3. 决策树

- **当新增公共 API 时**：放在对应包（机器相关 → `api.abstracts`，物品相关 → `items`，工具 → `utils`）；这是库，新 API 要考虑下游怎么用，javadoc 写清契约。
- **当修改 `AbstractMachineBlock` / `TickingBlock` 时**：先 grep 下游用法（JitPack 上依赖本库的附属），确认没有二进制兼容破坏；子类已依赖 `getBorderIn()` / `getInputSlots()` / `getMachineRecipes()` 等方法签名。
- **当新增 shaded 依赖时**：坐标进 `libs.versions.toml`，scope 用 `implementation`，并必须在 `shadowJar` 加对应 `relocate`（防止与本库/下游其他插件冲突）。
- **当修改 relocate 路径时**：视为**破坏性变更**，必须大版本号并在 README 标注——下游代码 import 的是 relocate 后的包名。
- **当修改 `InventoryListener` 时**：它靠 `PersistentDataContainer` 键（`slimefun_item` / `recipe_index`）与 `AbstractMachineBlock` 强耦合，两处要一起改。
- **当需要新增命令/权限时**：先改 `plugin.yml` 的 `commands` / `permissions`，再在主类注册 executor。
- **当构建失败时**：读报错定位（依赖解析 / Lombok / relocation 冲突），修复后重跑，禁止跳过任务硬绕。

## 4. 红线

1. **禁止破坏二进制兼容**：删除/改签名公共方法、改 relocate 包名，都会毁掉下游附属。
2. **禁止 `git push -f` 或改写共享历史。**
3. **禁止未构建验证就提交**：`./gradlew clean build` 通过是提交门槛。
4. **禁止把 `plugin.yml` 的 version 占位符改回 `${project.version}`**（那是 Maven 时代的写法，Gradle 下会变成字面量导致 Bukkit 加载失败）。
5. **禁止在库内注册游戏内容**（物品/机器/分类的注册留给下游附属），本库只提供能力。

## 5. 质量规范

- 跟随现有风格：Palantir 格式（历史 pom 配置了 spotless palantirJavaFormat）、4 空格缩进、Lombok `@Getter` 等。
- 每个改动保持可编译的完整状态，不留死代码。

## 6. 常用命令

| 场景 | 命令 |
| --- | --- |
| 标准验收构建 | `./gradlew clean build` |
| 快速编译检查 | `./gradlew compileJava` |
| 依赖树 | `./gradlew dependencies` |
| 产物位置 | `build/libs/GuguSlimefunLib-<version>.jar` |

## 7. Git 工作流

- 本地 `master` 直接开发；推送与打 tag 由开发者执行。
- 提交信息 Conventional Commits：`feat:` / `fix:` / `docs:` / `refactor:` / `chore:`。
- 提交前自检：构建通过、未触红线、只提交相关文件（`build/`、`.gradle/` 已在 .gitignore）。

## 8. 文档更新契约

发现本文与代码不一致时必须主动指出并更新本文；新约定先提议、经开发者确认后写入。
