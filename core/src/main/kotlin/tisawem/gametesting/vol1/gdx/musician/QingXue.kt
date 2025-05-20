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
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.utils.Disposable
import ktx.assets.disposeSafely
import org.wysko.kmidi.midi.TimeBasedSequence
import org.wysko.kmidi.midi.TimedArc
import tisawem.gametesting.vol1.midi.Score
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


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
class QingXue(
    override val timeBasedSequence: TimeBasedSequence,
    override val score: Score,
    override val getPosition: () -> Duration
) : Musician , Disposable{

    companion object{
        const val PICTURE_WEIGHT = 1134f
        const val PICTURE_HEIGHT = 1512f
        val LEAST_PERFORM_LENGTH =0.08.toDuration(DurationUnit.SECONDS)
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
    private val arcsB = score .arcs.filter { notePercussionMapB[it.note] != null }

    inner class ActorUnit(
        private val track: List<TimedArc>,
        private val notePercussionMap: HashMap<Byte, Percussion>
    ) : Actor() {
        init {
            setSize( PICTURE_WEIGHT,  PICTURE_HEIGHT)
        }

        private var nextArcIndex = 0

        // 存储当前活动的乐器
        private val activePercussions = HashSet<Percussion>()

        // 存储当前活动的肢体
        private val activeLimbs = HashSet<Limb>()

        // 存储当前活动的弧，优先队列按结束时间排序
        private val activeArcs = PriorityQueue<TimedArc>(compareBy { it.endTime })

        // 需要移除的弧列表
        private val arcsToRemove = ArrayList<TimedArc>()

        override fun act(delta: Float) {
            super.act(delta)

            // 激活新音符
            while (nextArcIndex < track.size) {
                val arc = track[nextArcIndex]
                if (getPosition() >= arc.startTime) {
                    val percussion = notePercussionMap[arc.note]
                    if (percussion != null) {
                        activeArcs.add(arc)
                        activePercussions.add(percussion)
                        percussion.limb.forEach { limb ->
                            activeLimbs.add(limb)
                        }
                    }
                    nextArcIndex++
                } else {
                    break
                }
            }

            // 检查并移除过期音符
            arcsToRemove.clear()
            while (activeArcs.isNotEmpty()) {
                val arc = activeArcs.peek()


                // 如果音符已经播放了最小持续时间且已经结束，则标记为移除
                if (getPosition() - arc.startTime >= LEAST_PERFORM_LENGTH && getPosition() >= arc.endTime) {
                    arcsToRemove.add(activeArcs.poll())
                } else {
                    break
                }
            }

            // 如果有音符需要移除，更新活动状态
            if (arcsToRemove.isNotEmpty()) {
                // 清空活动集合
                activePercussions.clear()
                activeLimbs.clear()

                // 根据剩余活动音符重建活动集合
                activeArcs.forEach { arc ->
                    val percussion = notePercussionMap[arc.note]
                    if (percussion != null) {
                        activePercussions.add(percussion)
                        percussion.limb.forEach { limb ->
                            activeLimbs.add(limb)

                        }
                    }
                }
            }
        }

        override fun draw(batch: Batch, parentAlpha: Float) {
            val drawX = x
            val drawY = y
            val drawW = width
            val drawH = height

            // 始终绘制基础身体
            batch.draw(limbTextures[Limb.BaseBody], drawX, drawY, drawW, drawH)

            // 绘制非活动的肢体
            Limb.entries.forEach { limb ->
                if (limb != Limb.BaseBody && !activeLimbs.contains(limb)) {
                    batch.draw(limbTextures[limb], drawX, drawY, drawW, drawH)
                }
            }

            // 绘制活动乐器的Body.png（显示活动肢体状态）
            activePercussions.forEach { percussion ->
                batch.draw(percussionBodyTextures[percussion], drawX, drawY, drawW, drawH)
            }
        }
    }

    private val group = HorizontalGroup().apply {
        addActor(ActorUnit(arcsA, notePercussionMapA))
        addActor(ActorUnit(arcsB, notePercussionMapB))
    }

    override fun getActor(): Actor = group

    override fun dispose() {
        limbTextures.values.forEach { it.disposeSafely() }
        percussionBodyTextures.values.forEach { it.disposeSafely() }
    }


}
