/**
 *     GameTestingVol.1
 *     Copyright (C) 2020-2025 Tisawem東北項目
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package tisawem.gametesting.vol1.ui.gdx

import ktx.app.KtxGame
import ktx.app.KtxScreen
import tisawem.gametesting.vol1.ui.gdx.screen.HomePageIDLE


/**
 * KtxGame，主要使用它的切换屏幕功能，以及不用在create方法里面初始化资源
 *
 * 通常情况下，各个屏幕只有[Game]实例是互相传来传去
 *
 * 配置读写在[tisawem.gametesting.vol1.config.Config]操作
 *
 * 通常，各个Screen在切换屏幕时，会销毁自己的实例，这很重要
 */
class Game : KtxGame<KtxScreen>() {
    override fun create() {
        SwitchGraphicsMode.setWindowedModeFromConfigItem()

        addScreen<HomePageIDLE>(HomePageIDLE(this))
        setScreen<HomePageIDLE>()
    }
}
