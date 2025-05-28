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

package tisawem.gametesting.vol1.gdx

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min


class InstrumentLayout : WidgetGroup() {
    private val instruments = mutableListOf<Actor>()
    private var cols = 1
    private var rows = 1

    /**
     * 设置要显示的乐器列表
     */
    fun setInstruments(newInstruments: List<Actor>) {
        clearChildren()
        instruments.clear()
        instruments.addAll(newInstruments)
        newInstruments.forEach { addActor(it) }
        updateLayout()
    }

    /**
     * 计算最佳网格布局（尽量接近正方形）
     */
    private fun updateLayout() {
        val count = instruments.size
        // 寻找最均衡的网格布局
        var bestCols = 1
        var bestDiff = Int.MAX_VALUE

        for (c in 1..count) {
            val r = ceil(count.toDouble() / c).toInt()
            val diff = abs(c - r)
            if (diff < bestDiff) {
                bestDiff = diff
                bestCols = c
            }
        }

        cols = bestCols
        rows = ceil(count.toDouble() / cols).toInt()
        invalidate()
    }

    /**
     * 执行实际布局计算
     */
    override fun layout() {
        if (instruments.isEmpty()) return

        val totalWidth = width
        val totalHeight = height

        val cellWidth = totalWidth / cols
        val cellHeight = totalHeight / rows

        var index = 0
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (index < instruments.size) {
                    val instrument = instruments[index]

                    // 使用 setSize 而不是 setScale
                    val instrumentWidth = min(cellWidth * 0.9f, instrument.width)
                    val instrumentHeight = min(cellHeight * 0.9f, instrument.height)

                    // 保持宽高比
                    val aspectRatio = instrument.width / instrument.height
                    val finalWidth = min(instrumentWidth, instrumentHeight * aspectRatio)
                    val finalHeight = min(instrumentHeight, instrumentWidth / aspectRatio)

                    instrument.setSize(finalWidth, finalHeight)

                    // 居中放置
                    val x = j * cellWidth + (cellWidth - finalWidth) / 2
                    val y = (rows - i - 1) * cellHeight + (cellHeight - finalHeight) / 2

                    instrument.setPosition(x, y)
                    instrument.setScale(1f) // 重置缩放

                    index++
                }
            }
        }
    }
}
class OverlayInstrumentLayout : WidgetGroup() {
    private val melodicSection = InstrumentLayout()


    init {
        // 将钢琴键盘部分设置为填满父容器
        melodicSection.setFillParent(true)
        addActor(melodicSection)
    }

    /**
     * 设置旋律乐器（钢琴键盘）
     */
    fun setMelodicInstruments(instruments: List<Actor>) {
        melodicSection.setInstruments(instruments)
    }


    override fun layout() {
        super.layout()

        // 确保钢琴键盘部分占满整个区域
        melodicSection.setSize(width, height)
        melodicSection.validate()


    }
}
