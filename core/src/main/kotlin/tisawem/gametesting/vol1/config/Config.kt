/**
 *     GameTestingVol.1
 *     Copyright (C) 2020-2025 Tisawem東北項目
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */


package tisawem.gametesting.vol1.config

/**
 * 这是调用[ConfigOperation]类的枚举类
 *
 * 请：
 *
 * 手动与[config.properties](core/src/main/resources/config.properties)同步配置项
 *
 * 自行归类 可读写配置项 和 仅读配置项
 *
 * 程序设计时，自觉的，保证不会写入仅读设置项
 *@param load 读取配置项
 * 如果没有读取到，整个程序会停止
 * @param write 写入配置项，返回the previous value of the specified key, or null if it did not have one.
 *
 */
enum class Config(val load: () -> String, val write: (String) -> Any?) {
    // 可读写设置项 (会在config.properties文件写入的配置)
    Language("Language"),
    MIDIFile("MIDIFile"),
    MIDIOutputDevice("MIDI_Output_Device"),
    UsingGervill("Using_Gervill"),
    FullScreen("FullScreen"),
    ScreenAdvancedTime("Screen_Advanced_Time"),


    // 仅读设置项 (整个程序生命周期内不会去修改的配置项)
    LanguageResourcePath("Language_Resource_Path"),
    DefaultInstrument("DefaultInstrument"),
    DefaultPercussion("DefaultPercussion"),
    FontLight("Font_Light"),
    FontRegular("Font_Regular"),
    FontBold("Font_Bold"),
    UISkin("UISkin"),
    WindowedResolution("WindowedResolution"),

    ;

    constructor(item: String) : this({ ConfigOperation.load (item) }, { s -> ConfigOperation.write(item, s) })
}



