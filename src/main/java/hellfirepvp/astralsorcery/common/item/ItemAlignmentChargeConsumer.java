/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemAlignmentChargeConsumer
 * Created by HellFirePvP
 * Date: 27.12.2016 / 21:52
 */
public interface ItemAlignmentChargeConsumer extends ItemAlignmentChargeRevealer {

    default public void drainCharge(EntityPlayer player, double charge) {
        if(player.isCreative()) return;
        ResearchManager.modifyAlignmentCharge(player, -charge);
    }

    default public double getCharge(EntityPlayer player, Side side) {
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if(progress == null) return 0.0D;
        return progress.getAlignmentCharge();
    }

    default public boolean hasAtLeastCharge(EntityPlayer player, Side side, double required) {
        return player.isCreative() || getCharge(player, side) >= required;
    }

}
