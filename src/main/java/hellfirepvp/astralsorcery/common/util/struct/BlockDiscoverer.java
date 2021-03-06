/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.struct;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockDiscoverer
 * Created by HellFirePvP
 * Date: 07.02.2017 / 01:09
 */
public class BlockDiscoverer {

    public static BlockArray discoverBlocksWithSameStateAround(World world, BlockPos origin, boolean onlyExposed, int cubeSize) {
        IBlockState toMatch = world.getBlockState(origin);

        Block matchBlock = toMatch.getBlock();
        int matchMeta = matchBlock.getMetaFromState(toMatch);

        BlockArray foundArray = new BlockArray();
        foundArray.addBlock(origin, toMatch);
        List<BlockPos> visited = new LinkedList<>();

        Deque<BlockPos> searchNext = new LinkedList<>();
        searchNext.addFirst(origin);

        while (!searchNext.isEmpty()) {
            Deque<BlockPos> currentSearch = searchNext;
            searchNext = new LinkedList<>();

            for (BlockPos offsetPos : currentSearch) {
                for (EnumFacing face : EnumFacing.VALUES) {
                    BlockPos search = offsetPos.offset(face);
                    if (visited.contains(search)) continue;
                    if (getCubeDistance(search, origin) > cubeSize) continue;

                    visited.add(search);

                    if (!onlyExposed || isExposedToAir(world, search)) {
                        IBlockState current = world.getBlockState(search);
                        if (current.getBlock() == matchBlock && current.getBlock().getMetaFromState(current) == matchMeta) {
                            foundArray.addBlock(search, current);
                            searchNext.add(search);
                        }
                    }

                }
            }
        }

        return foundArray;
    }

    public static int getCubeDistance(BlockPos p1, BlockPos p2) {
        return (int) MathHelper.absMax(MathHelper.absMax(p1.getX() - p2.getX(), p1.getY() - p2.getY()), p1.getZ() - p2.getZ());
    }

    /*public static List<BlockPos> getAdjacent(BlockPos pos, boolean withEdges, boolean withCorners) {
        int exp = 6 + (withEdges ? 12 : 0) + (withCorners ? 8 : 0);
        List<BlockPos> positions = new ArrayList<>(exp);
        for (EnumFacing face : EnumFacing.VALUES) positions.add(pos.offset(face));
        if (withEdges) {
            positions.add(pos.add(1, 1, 0));
            positions.add(pos.add(-1, 1, 0));
            positions.add(pos.add(0, 1, 1));
            positions.add(pos.add(0, 1, -1));
            positions.add(pos.add(-1, 0, 1));
            positions.add(pos.add(-1, 0, -1));
            positions.add(pos.add(1, 0, 1));
            positions.add(pos.add(1, 0, -1));
            positions.add(pos.add(1, -1, 0));
            positions.add(pos.add(-1, -1, 0));
            positions.add(pos.add(0, -1, 1));
            positions.add(pos.add(0, -1, -1));
        }
        if (withCorners) {
            positions.add(pos.add(-1, 1, 1));
            positions.add(pos.add(-1, 1, -1));
            positions.add(pos.add(1, 1, 1));
            positions.add(pos.add(1, 1, -1));
            positions.add(pos.add(-1, -1, 1));
            positions.add(pos.add(-1, -1, -1));
            positions.add(pos.add(1, -1, 1));
            positions.add(pos.add(1, -1, -1));
        }
        return positions;
    }*/

    public static boolean isExposedToAir(World world, BlockPos pos) {
        for (EnumFacing face : EnumFacing.VALUES) {
            if (world.isAirBlock(pos.offset(face))) return true;
        }
        return false;
    }

}
