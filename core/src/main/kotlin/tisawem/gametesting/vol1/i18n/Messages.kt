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

package tisawem.gametesting.vol1.i18n

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.I18NBundle
import tisawem.gametesting.vol1.config.CoreConfig
import java.util.*


object Messages {


    enum class SupportedLanguage(val locale: Locale) {
        Default(Locale.ENGLISH),
        Zh(Locale.CHINESE),
        Ja(Locale.JAPANESE);

        override fun toString(): String = locale.displayLanguage
    }

    private var bundle: I18NBundle? = null
    private var currentLanguage: String = ""
    private val lock = Any()  // 用于同步的锁

    private fun ensureBundle() {
        // 获取配置的语言,若为空白,将使用系统默认语言
        val configLanguage = CoreConfig.Language.load().ifEmpty { Locale.getDefault().language }

        if (bundle == null || currentLanguage != configLanguage) {
            synchronized(lock) { // 加锁确保线程安全
                if (bundle == null || currentLanguage != configLanguage) {  // 双重检查锁定



                        // 构建 FileHandle 对象来加载资源
                        val fileHandle = Gdx.files.internal(CoreConfig.LanguageResourcePath.load())
                        // 加载 I18NBundle，使用指定语言或默认语言
                        bundle = I18NBundle.createBundle(fileHandle, Locale.forLanguageTag(configLanguage))
                        currentLanguage = configLanguage

                }
            }
        }
    }

    fun getMessages(key: String): String = try {
        ensureBundle()
        bundle!!.get(key)
    } catch (e: Throwable) {
        Gdx.app.error("运行时抛出错误","""
1、NullPointerException
    这个最不可能抛出
    要么core模块的Messages类的Bundle字段没初始化
    要么创建I18NBundle实例时，形参有null值

2、 MissingResourceException
    给定的Gdx.files.internal目录，没有任何语言包包含 $key 这个索引项
    可以考虑 ${CoreConfig.LanguageResourcePath.load()} 这个目录是不是错了
    还是语言包文件不含这个索引项

    即将返回键名称：[$key]
        """.trimIndent(),e)


        // 返回键名称作为后备
        "[$key]"
    }

    /**
     * <LF> is Line Feed in messages.properties
     *
     * Replaces all occurrences of `<LF>` in the input string with `\n`.
     *
     * @return The processed string with `<LF>` replaced by `\n`.
     */
    fun getMessagesWithLineFeedReplace(key: String) = getMessages(key).replace("<LF>", "\n")
}


