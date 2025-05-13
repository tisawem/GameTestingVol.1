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
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.I18NBundle
import tisawem.gametesting.vol1.config.CoreConfig
import java.util.Locale


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
                    try {
                        // 构建 FileHandle 对象来加载资源
                        val fileHandle = Gdx.files.internal(CoreConfig.LanguageResourcePath.load())
                        // 加载 I18NBundle，使用指定语言或默认语言
                        bundle = I18NBundle.createBundle(fileHandle, Locale(configLanguage))
                        currentLanguage = configLanguage
                    } catch (e: Exception) {
                        Gdx.app.error("Messages", "Failed to load language bundle", e)
                        // 尝试使用默认语言
                        try {
                            val fileHandle = Gdx.files.internal(CoreConfig.LanguageResourcePath.load())
                            bundle = I18NBundle.createBundle(fileHandle, Locale.ENGLISH)
                            currentLanguage = "en"
                        } catch (e2: Exception) {
                            Gdx.app.error("Messages", "Failed to load default language bundle", e2)
                            throw RuntimeException("Could not load any language bundles", e2)
                        }
                    }
                }
            }
        }
    }

    fun getMessages(key: String): String = try {
        ensureBundle()
        bundle!!.get(key)
    } catch (e: Exception) {
        val errorMessage = when (e) {
            is GdxRuntimeException -> {
                // 通常表示找不到资源或键
                Gdx.app.error("Messages", "Resource key not found: $key", e)
                "Missing resource key: [$key]"
            }
            is NullPointerException -> {
                // Bundle未初始化
                Gdx.app.error("Messages", "Bundle not initialized", e)
                "Bundle initialization error: [$key]"
            }
            else -> {
                // 其他未知错误
                Gdx.app.error("Messages", "Unknown error when getting message for key: $key", e)
                "Unknown error: [$key]"
            }
        }


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
