package com.chongyu.privatechest.mixin;

import com.chongyu.privatechest.core.ChestBlockEntityNbt;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityExplosionBehavior.class)
public class EntityExplosionBehaviorMixin {
    @Shadow @Final private  Entity entity;

    @Inject(at = @At("HEAD"), method = "canDestroyBlock", cancellable = true)
    public void canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power, CallbackInfoReturnable<Boolean> ca) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null && ((ChestBlockEntityNbt) blockEntity).privateChest$contains("private_chest_aliveandwell")) {
            ca.setReturnValue(false);
        }

    }
}
