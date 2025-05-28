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

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import ktx.assets.disposeSafely
import org.wysko.kmidi.midi.TimeBasedSequence
import org.wysko.kmidi.midi.TimedArc
import tisawem.gametesting.vol1.midi.Score
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class QingXue(
    override val timeBasedSequence: TimeBasedSequence,
    override val score: Score,
    override val getPosition: () -> Duration
) : Musician , Disposable{
    /**
     * 身体部位列表
     */
    enum class Limb(val fileName: String) {
        BaseBody("body.png"), LeftArm("leftArm.png"), RightArm("rightArm.png"), LeftLeg("leftLeg.png"), RightLeg("rightLeg.png"), Tail(
            "tail.png"
        );
    }

    /**
     * 音符与乐器的映射关系
     */
    enum class Percussion(
        val folderName: String,
        val notesA: ByteArray,
        val notesB: ByteArray,
        val limb: Array<Limb>
    ) {
        BassDrum("BassDrum", byteArrayOf(35), byteArrayOf(36), arrayOf(Limb.LeftLeg)),
        HiHat("HiHat", byteArrayOf(42), byteArrayOf(44), arrayOf(Limb.LeftLeg)),
        RimShot("RimShot", byteArrayOf(37, 61, 64), byteArrayOf(60, 62, 63), arrayOf(Limb.Tail)),
        SnareDrum("SnareDrum", byteArrayOf(38), byteArrayOf(40), arrayOf(Limb.RightArm)),
        Tom("Tom", byteArrayOf(41, 45, 47), byteArrayOf(43, 48, 50), arrayOf(Limb.LeftArm)),
        TopCymbal("TopCymbal", byteArrayOf(49, 52, 57), byteArrayOf(51, 53, 59), arrayOf(Limb.RightLeg));
    }

    companion object{
        const val PICTURE_WEIGHT = 1134f
        const val PICTURE_HEIGHT = 1512f
        val LEAST_PERFORM_LENGTH = 0.08.toDuration(DurationUnit.SECONDS)
        const val IMAGE_BASE_DIRECTORY = "Musician/QingXue"
    }

    // 加载基础肢体纹理
    private val limbTextures = Limb.entries.associateWith {
        Texture("${ IMAGE_BASE_DIRECTORY}/${it.fileName}")
    }

    // 只加载乐器的Body.png纹理
    private val percussionBodyTextures = Percussion.entries.associateWith {
        Texture("${ IMAGE_BASE_DIRECTORY}/${it.folderName}/Body.png")
    }

    // 建立音符到乐器的映射
    private val notePercussionMapA = HashMap<Byte, Percussion>().apply {
        Percussion.entries.forEach { percussion ->
            percussion.notesA.forEach { note ->
                put(note, percussion)
            }
        }
    }

    private val notePercussionMapB = HashMap<Byte, Percussion>().apply {
        Percussion.entries.forEach { percussion ->
            percussion.notesB.forEach { note ->
                put(note, percussion)
            }
        }
    }

    // 过滤有效音符
    private val arcsA = score.arcs.filter { notePercussionMapA[it.note] != null }
    private val arcsB = score.arcs.filter { notePercussionMapB[it.note] != null }

    /**
     * 单个轨道的状态管理
     */
    private class TrackState(
        val track: List<TimedArc>,
        val notePercussionMap: HashMap<Byte, Percussion>
    ) {
        var nextArcIndex = 0
        val activePercussions = HashSet<Percussion>()
        val activeLimbs = HashSet<Limb>()
        val activeArcs = PriorityQueue<TimedArc>(compareBy { it.endTime })
        val arcsToRemove = ArrayList<TimedArc>()
    }

    inner class DualTrackActor : Actor() {
        init {
            // 设置默认尺寸，但允许布局系统调整
            setSize(PICTURE_WEIGHT * 2, PICTURE_HEIGHT)
        }

        /**
         * 获取推荐的宽高比（双角色并排）
         */
        fun getRecommendedAspectRatio(): Float = (PICTURE_WEIGHT * 2) / PICTURE_HEIGHT

        /**
         * 根据给定宽度计算推荐高度
         */
        fun getRecommendedHeight(width: Float): Float = width / getRecommendedAspectRatio()

        /**
         * 根据给定高度计算推荐宽度
         */
        fun getRecommendedWidth(height: Float): Float = height * getRecommendedAspectRatio()

        private val trackStateA = TrackState(arcsA, notePercussionMapA)
        private val trackStateB = TrackState(arcsB, notePercussionMapB)

        private fun updateTrackState(trackState: TrackState) {
            // 激活新音符
            while (trackState.nextArcIndex < trackState.track.size) {
                val arc = trackState.track[trackState.nextArcIndex]
                if (getPosition() >= arc.startTime) {
                    val percussion = trackState.notePercussionMap[arc.note]
                    if (percussion != null) {
                        trackState.activeArcs.add(arc)
                        trackState.activePercussions.add(percussion)
                        percussion.limb.forEach { limb ->
                            trackState.activeLimbs.add(limb)
                        }
                    }
                    trackState.nextArcIndex++
                } else {
                    break
                }
            }

            // 检查并移除过期音符
            trackState.arcsToRemove.clear()
            while (trackState.activeArcs.isNotEmpty()) {
                val arc = trackState.activeArcs.peek()

                // 如果音符已经播放了最小持续时间且已经结束，则标记为移除
                if (getPosition() - arc.startTime >= LEAST_PERFORM_LENGTH && getPosition() >= arc.endTime) {
                    trackState.arcsToRemove.add(trackState.activeArcs.poll())
                } else {
                    break
                }
            }

            // 如果有音符需要移除，更新活动状态
            if (trackState.arcsToRemove.isNotEmpty()) {
                // 清空活动集合
                trackState.activePercussions.clear()
                trackState.activeLimbs.clear()

                // 根据剩余活动音符重建活动集合
                trackState.activeArcs.forEach { arc ->
                    val percussion = trackState.notePercussionMap[arc.note]
                    if (percussion != null) {
                        trackState.activePercussions.add(percussion)
                        percussion.limb.forEach { limb ->
                            trackState.activeLimbs.add(limb)
                        }
                    }
                }
            }
        }

        override fun act(delta: Float) {
            super.act(delta)
            updateTrackState(trackStateA)
            updateTrackState(trackStateB)
        }

        private fun drawCharacter(
            batch: Batch,
            drawX: Float,
            drawY: Float,
            drawW: Float,
            drawH: Float,
            trackState: TrackState
        ) {
            // 始终绘制基础身体
            batch.draw(limbTextures[Limb.BaseBody], drawX, drawY, drawW, drawH)

            // 绘制非活动的肢体
            Limb.entries.forEach { limb ->
                if (limb != Limb.BaseBody && !trackState.activeLimbs.contains(limb)) {
                    batch.draw(limbTextures[limb], drawX, drawY, drawW, drawH)
                }
            }

            // 绘制活动乐器的Body.png（显示活动肢体状态）
            trackState.activePercussions.forEach { percussion ->
                batch.draw(percussionBodyTextures[percussion], drawX, drawY, drawW, drawH)
            }
        }

        override fun draw(batch: Batch, parentAlpha: Float) {
            // 使用当前Actor的实际尺寸
            val actualWidth = width
            val actualHeight = height
            val characterWidth = actualWidth / 2  // 每个角色占用一半宽度
            val characterHeight = actualHeight

            // 绘制左侧角色 (轨道A)
            drawCharacter(
                batch,
                x,
                y,
                characterWidth,
                characterHeight,
                trackStateA
            )

            // 绘制右侧角色 (轨道B)
            drawCharacter(
                batch,
                x + characterWidth,
                y,
                characterWidth,
                characterHeight,
                trackStateB
            )
        }
    }

    private val actor = DualTrackActor()

    override fun getActor(): Actor = actor

    override fun dispose() {
        limbTextures.values.forEach { it.disposeSafely() }
        percussionBodyTextures.values.forEach { it.disposeSafely() }
    }
}
