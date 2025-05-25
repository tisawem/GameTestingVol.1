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

package tisawem.gametesting.vol1.lwjgl3.toolkit


import tisawem.gametesting.vol1.lwjgl3.config.DesktopConfig
import tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog
import java.awt.Graphics2D
import java.awt.image.BufferedImage

object Toolkit {

    /**
     * Retrieves the windowed resolution as a pair of integers representing the width and height.
     *
     * This function attempts to load the `WindowedResolution` configuration value from the `config.properties` file.
     * The expected format for the configuration value is `<width>_<height>`, where both width and height are positive integers.
     * If the value is correctly formatted and valid, it is returned as a `Pair<Int, Int>`.
     *
     * If any of the following issues occur, an [tisawem.gametesting.vol1.lwjgl3.swing.ExceptionDialog] is displayed with detailed error information:
     * - A [NumberFormatException] or [IndexOutOfBoundsException] is thrown if the configuration value is not in the correct format or contains invalid numbers.
     *
     * In case of any exception, the function logs the error, displays the dialog, and returns a default resolution of `1280x720`.
     *
     * @return A [Pair] containing the width and height of the windowed resolution, or a default value of `1280x720` if an error occurs.
     */
    fun getWindowedResolution() = try {
        val (x, y) = DesktopConfig.WindowedResolution.load().split('_', ignoreCase = false, limit = 2)
            .map { it.toInt().takeIf { number -> number > 0 } ?: throw NumberFormatException("范围不对") }
        Pair(x, y)
    } catch (e: Throwable) {
        ExceptionDialog(
            e, true, """
1、NumberFormatException，IndexOutOfBoundsException：
    config.properties文件的WindowedResolution设置项的值，文本格式，或者范围不对：
        当前设置项的值为：${DesktopConfig.WindowedResolution.load()}

    正确格式为 <横向分辨率>_<纵向分辨率> ，值均为正整数。

其他错误为未知错误。此函数将返回 Pair(1280,720)。
"""
        )

        Pair(1280, 720)
    }


    /**
     * 这个函数是给[com.badlogic.gdx.scenes.scene2d.ui.Slider.value]定制的
     *
     * 从滑动条获得的浮点数，有很小的浮点误差，造成小数点后的位数多，可以靠这个函数获得截断误差后的值，并转为字符串。
     *
     * Formats a given floating-point number to a string with specified rounding.
     *
     * @param value The floating-point number to be formatted.
     * @param round The number of decimal places to include in the formatted string.
     *              If less than 1, the decimal part is omitted.
     * @return A string representation of the formatted number, where the decimal part
     *         is rounded to the specified number of places or removed if [round] is less than 1.
     */
    fun getFormatedString(value: Float, round: Int): String {


        var (a, b) = value.toString().split('.')

        if (round < 1) return a

        // 如果小数部分长度不足，则补零
        while (b.length < round) {
            b += "0"
        }

        // 截取到指定的小数位数
        b = b.substring(0, round)

        return "$a.$b"

    }


    /**
     * 将输入的 BufferedImage 按照指定的 scale 缩放，并返回新的 BufferedImage
     *
     * @param image 原始图像
     * @param scale 缩放比例（例如 0.5f 表示缩小为原来的一半，2.0f 表示放大两倍）
     * @return 缩放后的 BufferedImage
     */
    fun getScaledBufferedImage(image: BufferedImage, scale: Float): BufferedImage {
        if (scale <= 0) {
            ExceptionDialog(IllegalArgumentException(), true, "图片缩放倍数必须是正数\n该函数返回 $image")
            return image
        }

        // 计算新的宽高
        val newWidth = (image.width * scale).toInt()
        val newHeight = (image.height * scale).toInt()

        // 创建一个新的 BufferedImage，类型使用原图的类型
        val scaledImage =
            BufferedImage(newWidth, newHeight, image.type.takeIf { it != 0 } ?: BufferedImage.TYPE_INT_ARGB)

        // 获取 Graphics2D
        val g2d: Graphics2D = scaledImage.createGraphics()

        // 绘制缩放后的图像
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null)
        g2d.dispose()

        return scaledImage
    }
}
