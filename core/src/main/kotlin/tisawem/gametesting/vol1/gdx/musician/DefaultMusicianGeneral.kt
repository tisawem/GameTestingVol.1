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
import org.wysko.kmidi.midi.TimeBasedSequence
import org.wysko.kmidi.midi.TimedArc
import tisawem.gametesting.vol1.midi.Score
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class DefaultMusicianGeneral(
    override val timeBasedSequence: TimeBasedSequence,
    override val score: Score,
    override val getPosition: () -> Duration
) : Musician, Actor() {

    override fun getActor() = this

    companion object {
        const val PICTURE_WEIGHT = 960f
        const val PICTURE_HEIGHT = 540f
        const val IMAGE_BASE_DIRECTORY = "Musician/DefaultMusician_General"

        // 音符最少演奏时长
        fun getLeastPerformLength() = (Gdx.graphics.deltaTime.toDouble() * 2).toDuration(DurationUnit.SECONDS)

        // 钢琴的有效音符范围
        const val MIN_NOTE = 21
        const val MAX_NOTE = 108
    }

    private var nextArcIndex = 0

    // 使用优先队列存储活跃弧，便于高效移除已结束的音符
    private val activeArcs = PriorityQueue<TimedArc>(compareBy { it.endTime })

    // 活跃音符计数（避免重复渲染同一个音符）
    private val activeNoteCount = IntArray(MAX_NOTE + 1) { 0 }

    private val noteTextures = mutableMapOf<Byte, Texture>().apply {
        score.arcs
            .map { it.note }
            .filter { it in MIN_NOTE..MAX_NOTE }
            .toSet()
            .forEach { note ->
                this[note] = Texture("$IMAGE_BASE_DIRECTORY/$note.png")
            }
    }

    init {
        setSize(PICTURE_WEIGHT, PICTURE_HEIGHT)
    }

    override fun act(delta: Float) {
        super.act(delta)
        // 激活新音符
        activateNewNotes()
        // 移除已结束的音符
        deactivateEndedNotes()
    }

    private fun activateNewNotes() {
        // 检查是否有新的音符需要被激活
        while (nextArcIndex < score.arcs.size) {
            val arc = score.arcs[nextArcIndex]
            if (getPosition() >= arc.startTime) {
                activeArcs.add(arc)
                if (arc.note in MIN_NOTE..MAX_NOTE) {
                    activeNoteCount[arc.note.toInt()]++
                }
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
            // 计算结束时间
            val endTime = maxOf(arc.startTime + getLeastPerformLength(), arc.endTime)

            // 只有在以下条件都满足时才移除音符：
            // 1. 音符确实已经结束（当前时间超过其结束时间）
            // 2. 音符已经演奏了至少最小时长
            if (getPosition() <= endTime) {
                break // 还不能移除这个音符或其他音符
            }

            // 移除音符，因为它已经结束且演奏了足够长的时间
            activeArcs.poll()
            if (arc.note in MIN_NOTE..MAX_NOTE) {
                activeNoteCount[arc.note.toInt()]--
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val drawX = x
        val drawY = y
        val drawW = width
        val drawH = height

        // 只绘制活跃音符，每个音符只绘制一次
        for (note in MIN_NOTE..MAX_NOTE) {
            if (activeNoteCount[note] > 0) {
                noteTextures[note.toByte()]?.let { texture ->
                    batch.draw(texture, drawX, drawY, drawW, drawH)
                }
            }
        }
    }

    override fun dispose() {
        // 释放纹理资源
        noteTextures.values.forEach { texture ->
            texture.dispose()
        }
    }
}
