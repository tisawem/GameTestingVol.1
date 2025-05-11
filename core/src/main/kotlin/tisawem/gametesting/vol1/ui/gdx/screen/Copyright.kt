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


package tisawem.gametesting.vol1.ui.gdx.screen

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen

class Copyright(val game: KtxGame<KtxScreen>) {
    val batch= SpriteBatch()
    val viewport= FitViewport(3840f,2160f)

    companion object{
        const val SOFTWARE_COPYRIGHT="""
            GameTestingVol.1

            Version 1.1

            Source Code:
            Copyright (C) 2020-2025 Tisawem東北項目
            License is under the GNU GENERAL PUBLIC LICENSE Version 3 or Later.

            Perform Seat (冯熙) Character and Related Imagery:
            Copyright (C) 2020-2025 Tisawem東北項目
            License is under the Creative Commons Attribution-ShareAlike 4.0 International Public License.
        """

        const val THIRD_LIBRARIES_LICENSE="""
            GameTestingVol.1 使用的类库，及其许可证：



        """
    }


}
