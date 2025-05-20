# $$GameTestingVol.1$$

**Copyright (C) 2020-2025 [Tisawem東北項目](https://space.bilibili.com/367911078)**

**源代码许可证：** [GNU General Public License v3.0](LICENSES/LICENSE_GPLv3.txt) or later

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

很抱歉项目又删了又重新推送。

目前来看，这个项目的程序设计，距离稳定，还有很长的路要走。

个人原因，这个项目估计6月份又得无限期的搁置。

另外，我的开发能力确实很弱，也对各个框架陌生。

这个项目也大量的使用了语言模型生成的代码。


为了解决素材和GPL许可证冲突的问题

该项目将不会出现任何包含 `Tisawem東北項目` Logo 和 `Renkween` 形象的素材。Legacy文件夹也随即删除。

第一个Release，由于包含了Renkween素材，也会随即删除。

之后的一段时间，我再研究素材的问题。
## 该项目使用的包，框架，及主要功能

| 包，框架                                                                                      | 主要功能                                            | 许可证                                                                                                  |
|-------------------------------------------------------------------------------------------|-------------------------------------------------|------------------------------------------------------------------------------------------------------|
| [libGDX](https://libgdx.com/), [KTX](https://libktx.github.io/)                           | 渲染画面                                            | LibGDX：[Apache 2.0](LICENSES/LICENSE_Apache%202.0.txt) KTX：[CC0 1.0](LICENSES/LICENSE_CC0%201.0.txt) |
| [Swing](https://docs.oracle.com/javase/8/docs/technotes/guides/swing/index.html)          | 桌面端UI框架                                         | 取决于使用的JDK/JRE                                                                                        |
| [org.wysko.kmidi](https://github.com/wyskoj/kmidi)                                        | 解析MIDI文件数据。                                     | [Apache 2.0](LICENSES/LICENSE_Apache%202.0.txt)                                                      |
| [Java Sound API](https://docs.oracle.com/javase/8/docs/technotes/guides/sound/index.html) | 将MIDI序列发送至MIDI OUT设备；播放Wave音频；内置合成器方案(Gervill)。 | 取决于使用的JDK/JRE                                                                                        |
| [org.jjazz.fluidsynthjava](https://github.com/jjazzboss/FluidSynthJava)                   | 内置合成器的方案，将MIDI序列转为Wave音频，由Java Sound API进行播放。   | [LGPL 2.1](LICENSES/LICENSE_LGPL%20v2.1.txt)                                                         |
| [arrow-kt](https://arrow-kt.io/)                                                          | 提供Either和Tuple类型                                | [Apache 2.0](LICENSES/LICENSE_Apache%202.0.txt)                                                      |
| [ICU4J](https://icu.unicode.org/home)                                                     | 提供国际化支持                                         | [Unicode License V3](LICENSES/LICENSE_Unicode%20License%20V3.txt)                                    |
| [Kotlin Reflect](https://kotlinlang.org/)                                                 | Kotlin反射支持                                      | [Apache 2.0](LICENSES/LICENSE_Apache%202.0.txt)                                                      |

以及 Java™ SE 和 Kotlin 的标准库。

对 Java™ SE 版本要求最低是23。

## `assets`文件夹的说明

[assets](assets)文件夹，存放图片，音频，字体等素材，不存放源代码。

请留意每个文件夹下的许可证

| 文件或文件夹                                | 描述                                                                    |
|:--------------------------------------|:----------------------------------------------------------------------|
| [DefaultAssets](assets/DefaultAssets) | 存放与GPL许可证兼容的缺省素材                                                      |
| [Font](assets/Font)                   | 存放字体文件，该项目使用[思源黑体](https://github.com/adobe-fonts/source-han-sans)    |
| [Musician](assets/Musician)           | 存放角色的素材                                                               |
| [ui](assets/ui)                       | 使用[gdx-liftoff](https://github.com/libgdx/gdx-liftoff)创建项目时，添加的GUI资源。 |

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
