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
import tisawem.gametesting.vol1.midi.Score
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration





class DefaultMusicianPercussion(
    override val timeBasedSequence: TimeBasedSequence,
    override val score: Score,
    override val getPosition: () -> Duration
) : Musician , Actor(){
    /**
     * 音符与乐器的映射关系
     * @param folderName 图片所在文件夹，每个文件夹都由一个Base.png和一个Hit.png组成
     * @param notesA 角色A要响应的音符
     * @param notesB 角色B要响应的音符
     * @param aboveBody 图层顺序，false是在Body.png下方，true是在Body.png上方
     */
    enum class Percussion(
        val folderName: String,
        val notesA: ByteArray,
        val notesB: ByteArray,
        val aboveBody: Boolean
    ) {
        BassDrum("BassDrum", byteArrayOf(35), byteArrayOf(36), false),
        HiHat("HiHat", byteArrayOf(42), byteArrayOf(44), true),
        RimShot("RimShot", byteArrayOf(37, 61, 64), byteArrayOf(60, 62, 63), true),
        Snare("Snare", byteArrayOf(38), byteArrayOf(40), true),
        Tom("Tom", byteArrayOf(41, 45, 47), byteArrayOf(43, 48, 50), false),
        TopCymbal("TopCymbal", byteArrayOf(49, 52, 57), byteArrayOf(51, 53, 59), false);
    }

    companion object {
        const val PICTURE_WEIGHT = 794f
        const val PICTURE_HEIGHT = 1123f
        const val IMAGE_BASE_DIRECTORY = "Musician/DefaultMusician_Percussion"
        // 音符最少演奏时长
        fun getLeastPerformLength() = (Gdx.graphics.deltaTime.toDouble() * 2).toDuration(DurationUnit.SECONDS)
    }

    val bodyTexture = Texture("$IMAGE_BASE_DIRECTORY/body.png")

    // 为每个打击乐器加载Base和Hit纹理
    private val percussionTextures = mutableMapOf<Percussion, Pair<Texture, Texture>>()

    init {
        // 加载所有打击乐器的纹理
        Percussion.entries.forEach { percussion ->
            val baseTexture = Texture("$IMAGE_BASE_DIRECTORY/${percussion.folderName}/Base.png")
            val hitTexture = Texture("$IMAGE_BASE_DIRECTORY/${percussion.folderName}/Hit.png")
            percussionTextures[percussion] = baseTexture to hitTexture
        }
        // 设置默认尺寸，但允许后续被布局系统修改
        setSize(PICTURE_WEIGHT * 2, PICTURE_HEIGHT)
    }

    // 预过滤：只保留打击乐相关的音符
    private val relevantArcs by lazy {
        score.arcs.filter { arc ->
            Percussion.entries.any { percussion ->
                percussion.notesA.contains(arc.note) ||
                    percussion.notesB.contains(arc.note)
            }
        }.sortedBy { it.startTime } // 按开始时间排序，便于提前退出
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val currentPosition = getPosition()

        // 使用当前Actor的实际尺寸，而不是固定尺寸
        val actualWidth = width
        val actualHeight = height
        val halfWidth = actualWidth / 2f

        // 首先绘制aboveBody为false的乐器（在body下方）
        Percussion.entries.filter { !it.aboveBody }.forEach { percussion ->
            drawPercussion(batch, percussion, currentPosition, actualHeight, halfWidth)
        }

        // 绘制body（中间层）
        batch.draw(bodyTexture, x, y, halfWidth, actualHeight)
        batch.draw(bodyTexture, x + halfWidth, y, halfWidth, actualHeight)

        // 最后绘制aboveBody为true的乐器（在body上方）
        Percussion.entries.filter { it.aboveBody }.forEach { percussion ->
            drawPercussion(batch, percussion, currentPosition, actualHeight, halfWidth)
        }
    }

    private fun drawPercussion(
        batch: Batch,
        percussion: Percussion,
        currentPosition: Duration,
        totalHeight: Float,
        halfWidth: Float
    ) {
        val textures = percussionTextures[percussion] ?: return
        val (baseTexture, hitTexture) = textures

        // 检查角色A是否需要显示Hit状态
        val isHitA = isNoteActive(percussion.notesA, currentPosition)
        val textureA = if (isHitA) hitTexture else baseTexture

        // 检查角色B是否需要显示Hit状态
        val isHitB = isNoteActive(percussion.notesB, currentPosition)
        val textureB = if (isHitB) hitTexture else baseTexture

        // 绘制左边（角色A）- 使用实际尺寸
        batch.draw(textureA, x, y, halfWidth, totalHeight)

        // 绘制右边（角色B）- 使用实际尺寸
        batch.draw(textureB, x + halfWidth, y, halfWidth, totalHeight)
    }

    private fun isNoteActive(notes: ByteArray, currentPosition: Duration): Boolean {
        // 使用预过滤的音符列表
        return relevantArcs.any { arc ->
            notes.contains(arc.note) &&
                currentPosition >= arc.startTime &&
                currentPosition <= maxOf(arc.endTime, arc.startTime + getLeastPerformLength())
        }
    }

    override fun getActor(): Actor = this

    override fun dispose() {
        bodyTexture.dispose()
        // 释放所有打击乐器的纹理
        percussionTextures.values.forEach { (base, hit) ->
            base.dispose()
            hit.dispose()
        }
        percussionTextures.clear()
    }
}
