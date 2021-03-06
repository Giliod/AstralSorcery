/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileNetworkSkybound
 * Created by HellFirePvP
 * Date: 02.08.2016 / 17:35
 */
public abstract class TileNetworkSkybound extends TileNetwork {

    protected boolean doesSeeSky = false;

    @Override
    public void update() {
        super.update();

        if((ticksExisted & 15) == 0) {
            updateSkyState(world.canSeeSky(getPos()));
        }
    }

    protected void updateSkyState(boolean seesSky) {
        this.doesSeeSky = seesSky;
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

}
