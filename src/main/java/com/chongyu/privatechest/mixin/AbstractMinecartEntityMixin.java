package com.chongyu.privatechest.mixin;

import com.chongyu.privatechest.core.ChestBlockEntityNbt;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {
    public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @Inject(at=@At("HEAD"), method="tick")
    private void tick(CallbackInfo ca){
        BlockEntity block = getWorld().getBlockEntity(this.getBlockPos().up());
        if (block != null && ((ChestBlockEntityNbt) block).privateChest$contains("private_chest_aliveandwell")) {
            this.remove(RemovalReason.KILLED);
        }
    }
}
