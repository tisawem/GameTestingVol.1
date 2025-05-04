package tisawem.gametesting.vol1.config

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import tisawem.gametesting.vol1.i18n.Messages.getMessages
import tisawem.gametesting.vol1.ui.swing.ExceptionDialog
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.TrayIcon
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import javax.swing.*
import javax.swing.JOptionPane.WARNING_MESSAGE
import kotlin.collections.component1
import kotlin.collections.component2


object ConfigItemToolkit {

    /**
     * Retrieves the windowed resolution as a pair of integers representing the width and height.
     *
     * This function attempts to load the `WindowedResolution` configuration value from the `config.properties` file.
     * The expected format for the configuration value is `<width>_<height>`, where both width and height are positive integers.
     * If the value is correctly formatted and valid, it is returned as a `Pair<Int, Int>`.
     *
     * If any of the following issues occur, an [ExceptionDialog] is displayed with detailed error information:
     * - A [NumberFormatException] or [IndexOutOfBoundsException] is thrown if the configuration value is not in the correct format or contains invalid numbers.
     * - A [NoSuchElementException] is thrown if the `WindowedResolution` setting is missing from the `config.properties` file.
     *
     * In case of any exception, the function logs the error, displays the dialog, and returns a default resolution of `1280x720`.
     *
     * @return A [Pair] containing the width and height of the windowed resolution, or a default value of `1280x720` if an error occurs.
     */
    fun getWindowedResolution()=try {
       val (x,y)= ConfigItem.WindowedResolution.load().split('_', ignoreCase = false, limit = 2).map { it.toInt().takeIf { number -> number>0 }?:throw NumberFormatException("范围不对") }
Pair(x,y)
    }catch (e: Throwable){
        ExceptionDialog(
            e, true, """
1、NumberFormatException，IndexOutOfBoundsException：
    config.properties文件的WindowedResolution设置项，值的文本格式，或者范围不对：
        当前设置项的值为：${ConfigItem.WindowedResolution.load()}

    正确格式为 <横向分辨率>_<纵向分辨率> ，值均为正整数。

其他错误为未知错误。此函数将返回 Pair(1280,720)。
"""
        )

        Pair(1280,720)
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
}
