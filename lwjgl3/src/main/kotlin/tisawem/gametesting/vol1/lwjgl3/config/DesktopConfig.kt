/**
 *     GameTestingVol.1
 *     Copyright (C) 2020-2025 Tisawem東北項目
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */


package tisawem.gametesting.vol1.lwjgl3.config


enum class DesktopConfig(val item: String) {
    // 可读写设置项 (会在DesktopConfig.properties文件写入的配置)
    MIDIFile("MIDIFile"),
    MIDIOutputDevice("MIDI_Output_Device"),
    UsingGervill("Using_Gervill"),
    FullScreen("FullScreen"),


    // 仅读设置项 (整个程序生命周期内不会去修改的配置项)
    LanguageResourcePath("Language_Resource_Path"),
    WindowedResolution("WindowedResolution"),

    ;





    fun load()= DesktopConfigOperation.load (item)

    fun write(value:String)= DesktopConfigOperation.write(item,value)


   }

