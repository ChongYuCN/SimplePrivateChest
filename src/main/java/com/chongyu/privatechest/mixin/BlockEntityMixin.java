package com.chongyu.privatechest.mixin;

import com.chongyu.privatechest.core.ChestBlockEntityNbt;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements ChestBlockEntityNbt {
    @Unique
    @Final
    private static final String NBT_BASE_KEY = "private_chest_nbt_aliveandwell";//总的nbt

    @Mutable
    @Unique
    private NbtCompound customNbt = new NbtCompound();//分支nbt

    //写入nbt
    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void writeToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.put(NBT_BASE_KEY, customNbt);
    }

    //读取nbt
    @Inject(method = "readNbt", at = @At("RETURN"))
    private void readFromNbt(NbtCompound tag, CallbackInfo ci) {
        customNbt = tag.getCompound(NBT_BASE_KEY);
    }

    //判断分支nbt的键
    @Override
    public boolean privateChest$contains(String key) {
        return customNbt.contains(key);
    }
    //获取分支nbt的键
    @Override
    public String privateChest$getString(String key) {
        return customNbt.getString(key);
    }
    //存取一个分支nbt
    @Override
    public void privateChest$putString(String key, String value) {
        customNbt.putString(key, value);
    }
    @Override
    public void privateChest$removeString(String key) {
        customNbt.remove(key);
    }

    @Override
    public boolean privateChest$getBoolean(String key) {
        return customNbt.getBoolean(key);
    }

    @Override
    public void privateChest$putBoolean(String key, boolean value) {
        customNbt.putBoolean(key, value);
    }
}
