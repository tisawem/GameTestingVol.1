# $$GameTestingVol.1$$

**Copyright (C) 2020-2025 [Tisawem東北項目](https://space.bilibili.com/367911078)**

**源代码许可证：** [GNU General Public License v3.0](LICENSES/LICENSE_GPLv3.txt) or later

* $$目录$$

<!-- TOC -->
* [$$GameTestingVol.1$$](#gametestingvol1)
  * [介绍](#介绍)
  * [该项目使用的包，框架，及主要功能](#该项目使用的包框架及主要功能)
  * [`assets`文件夹的说明](#assets文件夹的说明)
  * [`LICENSES`文件夹的说明](#licenses文件夹的说明)
  * [平台](#平台)
  * [Gradle](#gradle)
<!-- TOC -->

***

## 介绍

计划做一个MIDI事件可视化的程序，类似[midis2jam2](https://midis2jam2.xyz/)软件的超级缩水版。
 
目前来看，这个项目的程序设计，距离稳定，还有很长的路要走。
尤其是GDX部分的代码，急需解决的就是布局。

另外，我的开发能力确实很弱，也对各个框架陌生，这个项目也大量的使用了语言模型生成的代码。

我画了一个很简陋的角色，以CC BY 4.0许可证授予该项目使用。

[GameTestingVol.1 Default Musician](assets/Musician/DefaultMusician_General/README.md)

讽刺的是，这个项目自始至终都是为了漫展无料服务，如果它不能给我带来广泛的关注和私信，我也就没有维护的动力。

至少而言，现在可以继续做的：把Lwjgl3端的ProcessedMIDIData.kt，放到core端。Lwjgl3端只负责将MIDI文件传给core端，提供控制后端MIDI序列播放的接口。

长远而言，可以尝试重写core端代码，引入新功能，通过FFI引入别的有意思的东西。

但，继续开发的期望极低，发完无料后，没人私信我，Github上也没人提issue，那我还是忙点别的吧。

我也真心受够了，是真的受够了。


## 该项目使用的包，框架，及主要功能

| 包，框架                                                                                           | 主要功能                                            | 许可证                                                                                                                                                                  |
|------------------------------------------------------------------------------------------------|-------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [libGDX](https://libgdx.com/)<br/>[KTX](https://libktx.github.io/)                             | 渲染画面                                            | LibGDX：[Apache 2.0](LICENSES/LICENSE_Apache%202.0.txt) KTX：[CC0 1.0](LICENSES/LICENSE_CC0%201.0.txt)                                                                 |
| [Swing](https://docs.oracle.com/javase/8/docs/technotes/guides/swing/index.html)               | 桌面端UI框架                                         | 取决于使用的JDK/JRE                                                                                                                                                        |
| [org.wysko.kmidi](https://github.com/wyskoj/kmidi)                                             | 解析MIDI文件数据。                                     | [Apache 2.0](LICENSES/LICENSE_Apache%202.0.txt)                                                                                                                      |
| [Java Sound API](https://docs.oracle.com/javase/8/docs/technotes/guides/sound/index.html)      | 将MIDI序列发送至MIDI OUT设备；播放Wave音频；内置合成器方案(Gervill)。 | 取决于使用的JDK/JRE                                                                                                                                                        |
| [org.jjazz.fluidsynthjava](https://github.com/jjazzboss/FluidSynthJava)                        | 内置合成器的方案，将MIDI序列转为Wave音频，由Java Sound API进行播放。   | [LGPL 2.1](LICENSES/LICENSE_LGPL%20v2.1.txt)                                                                                                                         |
| [arrow-kt](https://arrow-kt.io/)                                                               | 提供Either和Tuple类型                                | [Apache 2.0](LICENSES/LICENSE_Apache%202.0.txt)                                                                                                                      |
| [ICU4J](https://icu.unicode.org/home)                                                          | 提供国际化支持                                         | [Unicode License V3](LICENSES/LICENSE_Unicode%20License%20V3.txt)                                                                                                    |
| [Logback Classic Module](https://logback.qos.ch/)<br/>[SLF4J API Module](http://www.slf4j.org) | 日志支持                                            | Logback Classic Module: [EPL 1.0](LICENSES/LICENSE_EPL%201.0.txt) [LGPL 2.1](LICENSES/LICENSE_LGPL%20v2.1.txt)<br/>SLF4J API Module: [MIT](LICENSES/LICENSE_MIT.txt) |
| [Kotlin Reflect](https://kotlinlang.org/)                                                      | Kotlin反射支持                                      | [Apache 2.0](LICENSES/LICENSE_Apache%202.0.txt)                                                                                                                      |

以及 Java™ SE 和 Kotlin 的标准库。

## `assets`文件夹的说明

[assets](assets)文件夹，存放图片，音频，字体等素材，不存放源代码。

请留意每个文件夹下的许可证

| 文件或文件夹                                | 描述                                                                    |
|:--------------------------------------|:----------------------------------------------------------------------|
| [DefaultAssets](assets/DefaultAssets) | 存放与GPL许可证兼容的缺省素材                                                      |
| [Font](assets/Font)                   | 存放字体文件，该项目使用[思源黑体](https://github.com/adobe-fonts/source-han-sans)    |
| [Musician](assets/Musician)           | 存放角色的素材                                                               |
| [ui](assets/ui)                       | 使用[gdx-liftoff](https://github.com/libgdx/gdx-liftoff)创建项目时，添加的GUI资源。 |

## `LICENSES`文件夹的说明
[LICENSES](LICENSES)，存放许可证副本，包括本项目使用的GPLv3许可证。

可以在[LICENSES](LICENSES)文件夹内，找到各类库使用的许可证的副本。

## 平台

该项目针对桌面端进行开发，桌面端要求Java发行版版本最低是23。

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
