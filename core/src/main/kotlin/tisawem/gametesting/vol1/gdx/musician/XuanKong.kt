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

package tisawem.gametesting.vol1.gdx.musician


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import ktx.assets.disposeSafely
import org.wysko.kmidi.midi.TimeBasedSequence
import org.wysko.kmidi.midi.TimedArc
import tisawem.gametesting.vol1.midi.Score


import java.util.PriorityQueue
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class XuanKong(
    override val timeBasedSequence: TimeBasedSequence,
    override val score: Score,
    override val getPosition: () -> Duration

) : Actor(), Musician {
    companion object {
        const val PICTURE_WEIGHT = 945f
        const val PICTURE_HEIGHT = 945f
        const val IMAGE_BASE_DIRECTORY = "Musician/XuanKong"
        //音符最少演奏时长
        fun getLeastPerformLength() = (Gdx.graphics.deltaTime.toDouble()*2).toDuration(DurationUnit.SECONDS)

        //有效音符范围 (钢琴音高范围)
        val RIGHT_LEG_RANGE = Pair(21, 42)
        val LEFT_LEG_RANGE = Pair(43, 64)
        val LEFT_ARM_RANGE = Pair(65, 86)
        val RIGHT_ARM_RANGE = Pair(87, 108)

        //出现某一边没有腿时，显示的默认图片
        const val DEFAULT_LEFT_ARM = 66
        const val DEFAULT_RIGHT_ARM = 88
        const val DEFAULT_LEFT_LEG = 43
        const val DEFAULT_RIGHT_LEG = 42
    }

    /**
     * 放在最底层
     */
    private val tail = Texture("$IMAGE_BASE_DIRECTORY/tail.png")

    /**
     * 被手臂和腿夹着
     */
    private val body = Texture("$IMAGE_BASE_DIRECTORY/body.png")

    /**
     * 放在最顶层
     */
    private val pants = Texture("$IMAGE_BASE_DIRECTORY/pants.png")

    /**
     * 手臂图层
     */
    private val leftArms = mutableMapOf<Int, Texture>()
    private val rightArms = mutableMapOf<Int, Texture>()

    /**
     * 腿图层
     */
    private val leftLegs = mutableMapOf<Int, Texture>()
    private val rightLegs = mutableMapOf<Int, Texture>()

    // 跟踪下一个要处理的音符索引
    private var nextArcIndex = 0

    // 使用优先队列存储活跃弧，按结束时间排序
    private val activeArcs = PriorityQueue<TimedArc>(compareBy { it.endTime })

    // 活跃的肢体集合
    private val activeRightLegs = mutableSetOf<Int>()
    private val activeLeftLegs = mutableSetOf<Int>()
    private val activeLeftArms = mutableSetOf<Int>()
    private val activeRightArms = mutableSetOf<Int>()

    init {
        setSize(PICTURE_WEIGHT, PICTURE_HEIGHT)

        // 收集轨道中用到的所有音符音高
        val usedNotes = mutableSetOf<Int>()

        // 添加默认音符（确保默认肢体图片被加载）
        usedNotes.add(DEFAULT_LEFT_ARM)
        usedNotes.add(DEFAULT_RIGHT_ARM)
        usedNotes.add(DEFAULT_LEFT_LEG)
        usedNotes.add(DEFAULT_RIGHT_LEG)

        // 添加轨道中的所有音符
        score.arcs.forEach {
            usedNotes.add(it.note.toInt())
        }

        // 只加载用到的音符对应的图片
        usedNotes.forEach { note ->
            when (note) {
                in LEFT_ARM_RANGE.first..LEFT_ARM_RANGE.second ->
                    leftArms[note] = Texture("$IMAGE_BASE_DIRECTORY/Arm/$note.png")

                in RIGHT_ARM_RANGE.first..RIGHT_ARM_RANGE.second ->
                    rightArms[note] = Texture("$IMAGE_BASE_DIRECTORY/Arm/$note.png")

                in LEFT_LEG_RANGE.first..LEFT_LEG_RANGE.second ->
                    leftLegs[note] = Texture("$IMAGE_BASE_DIRECTORY/Leg/$note.png")

                in RIGHT_LEG_RANGE.first..RIGHT_LEG_RANGE.second ->
                    rightLegs[note] = Texture("$IMAGE_BASE_DIRECTORY/Leg/$note.png")
            }
        }
    }

    override fun act(delta: Float) {
        super.act(delta)

        // 激活新音符
        activateNewNotes()

        // 移除已结束的音符
        deactivateEndedNotes()

        // 更新活跃的肢体
        updateActiveLimbs()
    }

    private fun activateNewNotes() {
        // 检查是否有新的音符需要被激活
        while (nextArcIndex < score.arcs.size) {
            val arc = score.arcs[nextArcIndex]
            if (getPosition() >= arc.startTime) {
                activeArcs.add(arc)
                nextArcIndex++
            } else {
                break
            }
        }
    }

    private fun deactivateEndedNotes() {
        // 检查每个可能需要移除的音符
        while (activeArcs.isNotEmpty()) {
            val arc = activeArcs.peek()

            // 计算基于音符开始时间加上最小持续时间的最小结束时间
            val minimumEndTime = arc.startTime + getLeastPerformLength()

            // 只有当以下两个条件都满足时才移除音符：
            // 1. 音符实际已经结束（currentTick 超过了其结束时间）
            // 2. 音符至少播放了最小持续时间
            if (getPosition() <= arc.endTime || getPosition() < minimumEndTime) {
                break // 暂时不移除这个音符或任何其他音符
            }

            // 移除已经结束并且播放足够长时间的音符
            activeArcs.poll()
        }
    }

    private fun updateActiveLimbs() {
        // 清空所有活跃肢体集合
        activeRightLegs.clear()
        activeLeftLegs.clear()
        activeLeftArms.clear()
        activeRightArms.clear()

        // 检查所有活跃音符，将对应的肢体添加到活跃集合中
        for (arc in activeArcs) {
            when (val note = arc.note.toInt()) {
                in RIGHT_LEG_RANGE.first..RIGHT_LEG_RANGE.second -> {
                    activeRightLegs.add(note)
                }
                in LEFT_LEG_RANGE.first..LEFT_LEG_RANGE.second -> {
                    activeLeftLegs.add(note)
                }
                in LEFT_ARM_RANGE.first..LEFT_ARM_RANGE.second -> {
                    activeLeftArms.add(note)
                }
                in RIGHT_ARM_RANGE.first..RIGHT_ARM_RANGE.second -> {
                    activeRightArms.add(note)
                }
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val drawX = x
        val drawY = y
        val drawW = width
        val drawH = height

        // 根据图层顺序绘制：
        // 1. tail (底层)
        batch.draw(tail, drawX, drawY, drawW, drawH)

        // 2. 手臂 (body 下方)
        if (activeLeftArms.isEmpty()) {
            // 如果没有左臂活跃，则显示默认左臂
            leftArms[DEFAULT_LEFT_ARM]?.let { texture ->
                batch.draw(texture, drawX, drawY, drawW, drawH)
            }
        } else {
            // 否则显示所有活跃的左臂
            activeLeftArms.forEach { note ->
                leftArms[note]?.let { texture ->
                    batch.draw(texture, drawX, drawY, drawW, drawH)
                }
            }
        }

        if (activeRightArms.isEmpty()) {
            // 如果没有右臂活跃，则显示默认右臂
            rightArms[DEFAULT_RIGHT_ARM]?.let { texture ->
                batch.draw(texture, drawX, drawY, drawW, drawH)
            }
        } else {
            // 否则显示所有活跃的右臂
            activeRightArms.forEach { note ->
                rightArms[note]?.let { texture ->
                    batch.draw(texture, drawX, drawY, drawW, drawH)
                }
            }
        }

        // 3. body (中间层)
        batch.draw(body, drawX, drawY, drawW, drawH)

        // 4. 腿 (body 上方)
        if (activeLeftLegs.isEmpty()) {
            // 如果没有左腿活跃，则显示默认左腿
            leftLegs[DEFAULT_LEFT_LEG]?.let { texture ->
                batch.draw(texture, drawX, drawY, drawW, drawH)
            }
        } else {
            // 否则显示所有活跃的左腿
            activeLeftLegs.forEach { note ->
                leftLegs[note]?.let { texture ->
                    batch.draw(texture, drawX, drawY, drawW, drawH)
                }
            }
        }

        if (activeRightLegs.isEmpty()) {
            // 如果没有右腿活跃，则显示默认右腿
            rightLegs[DEFAULT_RIGHT_LEG]?.let { texture ->
                batch.draw(texture, drawX, drawY, drawW, drawH)
            }
        } else {
            // 否则显示所有活跃的右腿
            activeRightLegs.forEach { note ->
                rightLegs[note]?.let { texture ->
                    batch.draw(texture, drawX, drawY, drawW, drawH)
                }
            }
        }

        // 5. pants (顶层)
        batch.draw(pants, drawX, drawY, drawW, drawH)
    }

    override fun dispose() {
        // 释放所有纹理资源
        tail.disposeSafely()
        body.disposeSafely()
        pants.disposeSafely()

        leftArms.values.forEach { it.disposeSafely() }
        rightArms.values.forEach { it.disposeSafely() }
        leftLegs.values.forEach { it.disposeSafely() }
        rightLegs.values.forEach { it.disposeSafely() }
    }

    override fun getActor () = this
}
