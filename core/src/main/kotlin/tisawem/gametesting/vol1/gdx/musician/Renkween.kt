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
import kotlin.collections.forEach
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Renkween(
    override val timeBasedSequence: TimeBasedSequence,//如果不响应其他MIDI事件，这个形参暂无用处
    override val score: Score, override val getPosition: () -> Duration
) : Musician,Actor(){
    override fun getActor()=this


    companion object {
        const val PICTURE_WEIGHT = 1123f
        const val PICTURE_HEIGHT = 768f
        const val IMAGE_BASE_DIRECTORY = "Musician/Renkween"
        //音符最少演奏时长
        fun getLeastPerformLength() = (Gdx.graphics.deltaTime.toDouble()*2).toDuration(DurationUnit.SECONDS)

        // 钢琴的有效音符范围
        const val MIN_NOTE = 21
        const val MAX_NOTE = 108
    }

    private var nextArcIndex = 0
    // 使用优先队列存储活跃弧，便于高效移除已结束的音符
    private val activeArcs = PriorityQueue<TimedArc>(compareBy { it.endTime })


    // 活跃音符计数（避免重复渲染同一个音符）
    private val activeNoteCount = IntArray(MAX_NOTE + 1) { 0 }

    // 为了避免重复加载，从 track.arcs 中提取所有音高，加载 body 与 pointer 图片
    private val noteTextures = mutableMapOf<Byte, Pair<Texture, Texture>>().apply {
        score.arcs

            .map { it.note }
            .filter { it in MIN_NOTE..MAX_NOTE }
            .toSet()
            .forEach { note ->
                this[note] = Pair(
                    Texture("$IMAGE_BASE_DIRECTORY/$note/body.png"),
                    Texture("$IMAGE_BASE_DIRECTORY/$note/pointer.png")
                )
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
        // Check each note that should potentially be removed
        while (activeArcs.isNotEmpty()) {
            val arc = activeArcs.peek()

            // Calculate the end time
            val endTime = maxOf(arc.startTime + getLeastPerformLength(),arc.endTime)

            // Only remove the note if:
            // 1. It has actually ended (currentTick passed its end)
            // 2. It has played for at least the minimum duration
            if (getPosition() <= endTime) {
                break // Don't remove this note or any others yet
            }

            // Remove the note since it has ended and played long enough
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
                noteTextures[note.toByte()]?.let { (body, pointer) ->
                    batch.draw(body, drawX, drawY, drawW, drawH)
                    batch.draw(pointer, drawX, drawY, drawW, drawH)
                }
            }
        }
    }

    override fun dispose() {
        // 释放所有纹理资源
        noteTextures.values.forEach { (body, pointer) ->
            body.disposeSafely()
            pointer.disposeSafely()

        }
    }

}
