package com.chongyu.privatechest.mixin;

import com.chongyu.privatechest.core.ChestBlockEntityNbt;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRailBlock.class)
public abstract class AbstractRailBlockMixin extends Block implements Waterloggable {
    public AbstractRailBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at=@At("HEAD"), method="getStateForNeighborUpdate")
    public void getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos , CallbackInfoReturnable<BlockState> ca) {
        BlockEntity block = world.getBlockEntity(pos.up());
        if (block != null && ((ChestBlockEntityNbt) block).privateChest$contains("private_chest_aliveandwell")) {
            world.removeBlock(pos, true);
        }
    }
}
