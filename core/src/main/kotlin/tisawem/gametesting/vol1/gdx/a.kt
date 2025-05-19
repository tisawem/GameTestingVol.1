package tisawem.gametesting.vol1.gdx


import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.Layout
import kotlin.math.ceil
import kotlin.math.sqrt

/**
 * 一个自定义布局，用于将Actor均匀排列在网格中，并自动缩放以适应可用空间。
 * 会自动计算最佳网格尺寸（例如4x4或5x3），优先选择更方形的布局而非长条形。
 */
class GridLayout : WidgetGroup() {

    private var padding = 5f  // 单元格之间的间距

    /**
     * 设置单元格之间的间距
     */
    fun setPadding(padding: Float): GridLayout {
        this.padding = padding
        invalidateHierarchy()
        return this
    }

    /**
     * 根据Actor数量计算最佳网格维度
     * 尽量让网格接近正方形，优先选择宽度大于高度的布局
     */
    private fun calculateGridDimensions(count: Int): Pair<Int, Int> {
        if (count <= 0) return Pair(0, 0)
        if (count == 1) return Pair(1, 1)

        // 从接近正方形的网格开始
        val sqrt = sqrt(count.toFloat())

        // 尝试找到最平衡的网格（优先选择宽大于高的布局）
        var bestCols = ceil(sqrt).toInt()
        var bestRows = ceil(count.toFloat() / bestCols).toInt()

        // 尝试其他可能的配置
        for (cols in bestCols downTo 2) {
            val rows = ceil(count.toFloat() / cols).toInt()

            // 检查这个配置是否更好（更平衡或更宽）
            if (cols >= rows && cols * rows >= count) {
                bestCols = cols
                bestRows = rows
            }
        }

        return Pair(bestCols, bestRows)
    }

    override fun layout() {
        val count = children.size
        if (count == 0) return

        val (cols, rows) = calculateGridDimensions(count)
        if (cols == 0 || rows == 0) return

        // 计算单元格尺寸（考虑间距）
        val availableWidth = width - (padding * (cols + 1))
        val availableHeight = height - (padding * (rows + 1))
        val cellWidth = availableWidth / cols
        val cellHeight = availableHeight / rows

        // 确保最小单元格尺寸
        if (cellWidth <= 0 || cellHeight <= 0) return

        var index = 0
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if (index >= count) break

                val actor = children[index]

                // 计算单元格位置
                val cellX = padding + col * (cellWidth + padding)
                // LibGDX的Y坐标从底部开始，需要反转行计算
                val cellY = height - (padding + (row + 1) * cellHeight + row * padding)

                // 获取Actor的自然尺寸
                var actorWidth = 0f
                var actorHeight = 0f

                // 尝试获取布局Actor的首选尺寸
                if (actor is Layout) {
                    actor.validate()
                    actorWidth = actor.prefWidth
                    actorHeight = actor.prefHeight
                }

                // 如果不是布局或没有首选尺寸，使用当前尺寸
                if (actorWidth <= 0) actorWidth = actor.width
                if (actorHeight <= 0) actorHeight = actor.height

                // 如果仍然没有尺寸，使用默认值
                if (actorWidth <= 0) actorWidth = 50f
                if (actorHeight <= 0) actorHeight = 50f

                // 计算缩放比例以适应单元格（留一些边距）
                val margin = 0.9f  // 使用单元格大小的90%来提供一些间距
                val scaleX = (cellWidth * margin) / actorWidth
                val scaleY = (cellHeight * margin) / actorHeight
                val scale = minOf(scaleX, scaleY)  // 使用较小的缩放比例以保持宽高比

                // 缩放Actor
                actor.setScale(scale)

                // 计算位置以将Actor居中放置在单元格中
                val scaledWidth = actorWidth * scale
                val scaledHeight = actorHeight * scale

                val actorX = cellX + (cellWidth - scaledWidth) / 2
                val actorY = cellY + (cellHeight - scaledHeight) / 2

                // 定位Actor
                actor.setPosition(actorX, actorY)

                index++
            }
        }
    }

    override fun sizeChanged() {
        super.sizeChanged()
        invalidateHierarchy()
    }

    override fun childrenChanged() {
        super.childrenChanged()
        invalidateHierarchy()
    }
}
