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

package tisawem.gametesting.vol1.ui.gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import tisawem.gametesting.vol1.config.ConfigItem
import tisawem.gametesting.vol1.i18n.Messages.getMessages
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog
import java.io.FileNotFoundException
import java.util.*



/**
 * 使用FreeTypeFont，提供BitmapFont的便捷单例类
 */
object GdxFont {

    /**
     * 加载messages.properties，获取键（Set）集合
     *
     * 遍历键，通过getMessages()获取对应的值，把这些值拼在一起
     *
     * 再加上默认字符集，就是返回的字符集
     */
    private fun getCharacters()=
        try {
            var string = FreeTypeFontGenerator.DEFAULT_CHARS // 默认字符集
            Properties().apply {
                // 使用类加载器获取资源输入流
                val inputStream = GdxFont.javaClass.classLoader.getResourceAsStream("${ConfigItem.LanguageResourcePath.load()}.properties")
                    ?: throw FileNotFoundException()
                load(inputStream)
                inputStream.close()

                stringPropertyNames().forEach {
                    string += getMessages(it)
                }
            }
            string
        }catch (e: Throwable){
            ExceptionDialog(e,true,"""
1、FileNotFoundException
     无法找到资源文件: ${ConfigItem.LanguageResourcePath.load()}.properties

3、IOException
    if an error occurred when reading from the input stream.

4、IllegalArgumentException
    if the file messages.properties contains a malformed Unicode escape sequence.

其他错误为未知错误。该函数返回默认字符集：

${FreeTypeFontGenerator.DEFAULT_CHARS}
           """.trimIndent())
            FreeTypeFontGenerator.DEFAULT_CHARS
        }


    /**
     * 创建 BitmapFont 对象，并传入配置。
     */
    fun getBitmapFont(path: String, config: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit): BitmapFont? {
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter() // 创建字体参数
        parameter.config() // 扩展函数，变相的为parameter添加了config方法。使用传入的配置对参数进行设置
        return FreeTypeFontGenerator(Gdx.files.internal(path)).generateFont(parameter) // 生成并返回字体
    }

    /**
     * 获取 Light 样式的 BitmapFont 字体
     * 在此基础上，可以通过传入的配置调整字体的参数
     * 具体实现是基于 getBitmapFont 函数
     */
    fun getLightBitmapFont(config: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit) =
        getBitmapFont(ConfigItem.FontLight.load()) { // 加载字体
            characters = getCharacters() //设置字符集
            config(this) // 调用传入的配置函数，调整字体参数
        }

    /**
     * 获取 Regular 样式的 BitmapFont 字体
     * @see getLightBitmapFont
     */
    fun getRegularBitmapFont(config: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit) =
        getBitmapFont(ConfigItem.FontRegular.load()) {
            characters = getCharacters()
            config(this)
        }

    /**
     * 获取 Bold 样式的 BitmapFont 字体
     * @see getLightBitmapFont
     */
    fun getBoldBitmapFont(config: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit) =
        getBitmapFont(ConfigItem.FontBold.load()) {
            characters = getCharacters()
            config(this)
        }

}
