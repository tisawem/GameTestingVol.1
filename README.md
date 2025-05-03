# $$GameTestingVol.1$$

**Copyright (C) 2020-2025 [Tisawem東北項目](https://space.bilibili.com/367911078)**

**源代码许可证：** [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html) or later

* $$目录$$

<!-- TOC -->
* [$$GameTestingVol.1$$](#gametestingvol1)
  * [介绍](#介绍)
  * [该项目使用的包，框架，及主要功能](#该项目使用的包框架及主要功能)
  * [`assets`文件夹的说明](#assets文件夹的说明)
  * [平台](#平台)
  * [Gradle](#gradle)
<!-- TOC -->

***

## 介绍

计划做一个MIDI事件可视化的程序，类似[midis2jam2](https://midis2jam2.xyz/)软件的超级缩水版。

我在犹豫，还要不要继续开发......

5月份挺迷茫的......

## 该项目使用的包，框架，及主要功能

| 包，框架                                                                                      | 主要功能                                          |
|-------------------------------------------------------------------------------------------|-----------------------------------------------|
| [libGDX](https://libgdx.com/), [KTX](https://libktx.github.io/)                           | 渲染画面                                          |
| [org.wysko.kmidi](https://github.com/wyskoj/kmidi)                                        | 解析MIDI文件数据。                                   |
| [Java Sound API](https://docs.oracle.com/javase/8/docs/technotes/guides/sound/index.html) | 将MIDI序列发送至MIDI OUT设备，或者Gervill；播放Wave音频。      |
| [org.jjazz.fluidsynthjava](https://github.com/jjazzboss/FluidSynthJava)                   | 内置合成器的方案，将MIDI序列转为Wave音频，由Java Sound API进行播放。 |
| [arrow-kt](https://arrow-kt.io/)                                                          | 提供Either和Tuple类型                              |
| [ICU4J](https://icu.unicode.org/home)                                                     | 提供国际化支持                                       |
| [Kotlin Reflect](https://kotlinlang.org/)                                                 | Kotlin反射支持                                    |

以及 Java™ SE 和 Kotlin 的标准库。

对Java发行版的版本要求最低是23。

## `assets`文件夹的说明

[assets](assets)文件夹，存放图片，音频，字体等素材，不存放源代码。

请留意每个文件夹下的许可证

| 文件或文件夹                        | 描述                                                                    |
|:------------------------------|:----------------------------------------------------------------------|
| [CCLicense](assets/CCLicense) | 存放[Creative Commons](https://creativecommons.org/)的许可证                |
| [Font](assets/Font)           | 存放字体文件，该项目使用[思源黑体](https://github.com/adobe-fonts/source-han-sans)    |
| [Musician](assets/Musician)   |                                                                       |
| [ui](assets/ui)               | 使用[gdx-liftoff](https://github.com/libgdx/gdx-liftoff)创建项目时，添加的GUI资源。 |

[Musician](assets/Musician) 文件夹，存放着版权素材。

素材绑定的代码，开源。**但是，素材不开源。** 原因是：

* 代码：GPL许可证下随便玩。
* 素材的人物形象：我的！我的！不共享！严格保护！

点击查看[Renkween素材的许可证](assets/Musician/Renkween/LICENSE.MD)

所以，最好准备自己的素材。

## 平台

该项目针对桌面端进行开发

目前程序在 Windows 11 24H2 和 Debian 12.10 操作系统上进行测试。

* `core`: 主要模块，包含所有平台共享的应用逻辑。
* `lwjgl3`: 使用LWJGL3的主要桌面平台；在旧版文档中称为“desktop”。

## Gradle

这个项目使用 [Gradle](https://gradle.org/) 管理依赖关系。  
已经包含了 Gradle 包装器，因此你可以通过 `gradlew.bat` 或 `./gradlew` 命令运行 Gradle 任务。  
常用的 Gradle 任务和标志：

* `--continue`：使用此标志时，错误不会阻止任务的执行。
* `--daemon`：此标志会使用 Gradle 守护进程来运行选定的任务。
* `--offline`：使用此标志时，将使用缓存的依赖档案。
* `--refresh-dependencies`：此标志强制验证所有依赖项。对快照版本特别有用。
* `build`：构建每个项目的源代码和归档文件。
* `cleanEclipse`：删除 Eclipse 项目数据。
* `cleanIdea`：删除 IntelliJ 项目数据。
* `clean`：删除存储编译类和构建档案的 `build` 文件夹。
* `eclipse`：生成 Eclipse 项目数据。
* `idea`：生成 IntelliJ 项目数据。
* `lwjgl3:jar`：构建应用程序的可执行 JAR 文件，文件位于 `lwjgl3/build/libs` 目录下。
* `lwjgl3:run`：启动应用程序。
* `test`：运行单元测试（如果有）。

请注意，大多数不特定于单个项目的任务可以使用 `name:` 前缀运行，其中 `name` 应替换为特定项目的 ID。  
例如，`core:clean` 仅删除 `core` 项目的 `build` 文件夹。
