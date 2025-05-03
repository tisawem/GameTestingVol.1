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


object ConfigItemToolkit {





    /**
     * 这个函数是给[com.badlogic.gdx.scenes.scene2d.ui.Slider.value]定制的
     *
     * 获得的浮点数，有很小的误差，造成小数点后的位数比较多，可以靠这个函数获得截断误差后的值，并转为字符串。
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
