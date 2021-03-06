//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package twilightforest.client.texture.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import team.chisel.ctm.client.util.CTMLogic;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CTMLogicEast extends CTMLogic {
	public static CTMLogicEast getInstance() {
		return new CTMLogicEast();
	}

	@Override
	public boolean isConnected(IBlockAccess world, BlockPos current, BlockPos connection, EnumFacing dir, IBlockState state) {
		BlockPos difference = current.subtract(connection);

		return super.isConnected(world, current, current.add(new BlockPos(difference.getZ(), -difference.getX(), difference.getY())), dir, state);
	}
}
