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

import com.ibm.icu.util.ULocale
import com.ibm.icu.util.UResourceBundle
import tisawem.gametesting.vol1.config.ConfigItem
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog
import java.util.MissingResourceException


/**
 * A utility object responsible for managing and retrieving localized messages based on the application's language configuration.
 *
 * This object ensures that the appropriate message bundle is loaded according to the configured or system-default language.
 * It provides thread-safe initialization and retrieval of messages using keys. If a message key is not found,
 * it returns a placeholder in the format `[key]` and logs an error dialog for debugging purposes.
 *
 * The `SupportedLanguage` enum defines the languages supported by the application, each associated with a specific locale.
 * The language configuration is managed through the `ConfigItem.Language` property, which persists the selected language
 * in the application's configuration file. If no language is explicitly set, the system's default language is used.
 *
 * The `ensureBundle` method initializes or reloads the resource bundle when the language configuration changes.
 * This method employs a double-checked locking mechanism to ensure thread safety during bundle initialization.
 *
 * Note: This object relies on the `ConfigItem.LanguageResourcePath` property to locate the resource bundle files.
 * Missing or invalid resources will trigger an error dialog to assist in diagnosing issues.
 */
object Messages {
    enum class SupportedLanguage(val locale: ULocale) {
        Default(ULocale.ENGLISH), Zh(ULocale.CHINESE), Ja(ULocale.JAPANESE);
    }

    private var bundle: UResourceBundle? = null
    private var currentLanguage: String = ""
    private val lock = Any()  // 用于同步的锁

    private fun ensureBundle() {
        // 获取配置的语言，若为空白，将使用系统默认语言
        val configLanguage = ConfigItem.Language.load().ifEmpty { ULocale.getDefault().language }


        if (bundle == null || currentLanguage != configLanguage) {
            synchronized(lock) { // 加锁确保线程安全
                if (bundle == null || currentLanguage != configLanguage) {  // 双重检查锁定
                 /* 使用类加载器加载资源
                          UResourceBundle.getBundleInstance可以自动处理，它有以下行为：
                          如果configLanguage不是受支持的语言代码，就使用系统语言，若系统语言不支持，就使用messages.properties
                          当ConfigLanguage为空白时，将直接使用messages.properties
                          */

                        bundle = UResourceBundle.getBundleInstance(
                            ConfigItem.LanguageResourcePath.load(), configLanguage, Messages.javaClass.classLoader
                        )
                        currentLanguage = configLanguage

                }
            }
        }
    }


    fun getMessages(key: String): String = try {
        ensureBundle()
        bundle!!.getString(key)

    } catch (e: Throwable) {
        ExceptionDialog(
            e,
            true,
            """
1、MissingResourceException
    a.  ${ConfigItem.LanguageResourcePath.load()}
        该类路径下没有messages.properties文件

    b.  messages.properties 文件 没有 $key 对应的字符串.

以下错误需要检查源代码，它们不应该抛出。
    1、NullPointerException
        Bundle未初始化。

    2、ClassCastException
        $key 不是字符串

其他错误为未知错误。该函数返回：
“[$key]”""".trimIndent()
        )
        "[$key]"
    }

}
