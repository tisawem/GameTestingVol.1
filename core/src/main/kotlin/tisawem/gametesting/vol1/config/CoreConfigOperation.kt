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

package tisawem.gametesting.vol1.config

import java.util.Properties


/**
 * 读取[config.properties](core/src/main/resources/config.properties)
 *
 * 暂不计划能指定读取配置文件，将配置写入文件
 *
 * 建议使用ConfigItem.kt进行配置
 */


object CoreConfigOperation {
    // 定义一个锁对象，用于同步配置操作
    private val lock = Any()

    /**
     * 该字段由平台特定实现进行初始化
     *
     *      lwjgl3 -> tisawem/gametesting/vol1/lwjgl3/config/Lwjgl3Launcher.kt
     */
      var configProperties: Properties?=null


    // 读取配置，确保返回非空值；如果 key 不存在则抛出异常
    fun load (key: String): String = synchronized(lock) {
        configProperties!!.getProperty(key)
            ?: throw NoSuchElementException("""
配置项 $key 不存在

由于 GameTestingVol.1 严重依赖配置项
正确读取到配置，程序才能正常工作。
            """)
    }

    // 写入配置到内存中
    fun write(key: String, value: String): Any? = synchronized(lock) {
        configProperties!!.setProperty(key, value)
    }

    // 清除指定的配置项
    fun remove(key: String): Any? = synchronized(lock) {
        configProperties!!.remove(key)
    }

    // 检查配置项是否存在
    fun contains(key: String): Boolean = synchronized(lock) {
        configProperties!!.containsKey(key)
    }
}

