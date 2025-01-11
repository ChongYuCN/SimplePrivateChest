package com.chongyu.privatechest.mixin;

import com.chongyu.privatechest.core.ChestBlockEntityNbt;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin extends BlockEntity  {

    @Shadow
    @Nullable
    private Text customName;

    public LockableContainerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(at=@At("HEAD"), method="getName", cancellable = true)
    public void getName(CallbackInfoReturnable<Text> cir) {
        ChestBlockEntityNbt chestBlockEntityNbt = (ChestBlockEntityNbt) (Object) this;
        if(chestBlockEntityNbt.privateChest$contains("private_chest_aliveandwell")){
            String nameTeam = chestBlockEntityNbt.privateChest$getString("private_chest_aliveandwell");
            cir.setReturnValue(this.customName != null ? this.customName.copy().append(Text.of(Formatting.BOLD+"-").copy().formatted(Formatting.YELLOW)).copy().append(Text.of(Formatting.LIGHT_PURPLE+nameTeam))
                    : this.getContainerName().copy().append(Text.of(Formatting.BOLD+"-").copy().formatted(Formatting.YELLOW)).copy().append(Text.of(Formatting.LIGHT_PURPLE+nameTeam)));
       }
    }

    @Shadow
    protected abstract Text getContainerName();
}
