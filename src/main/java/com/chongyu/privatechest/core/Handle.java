package com.chongyu.privatechest.core;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Objects;

public class Handle {
    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!player.getWorld().isClient) {
                ItemStack itemStack = player.getStackInHand(hand);
                Item item = itemStack.getItem();
                Block blockH = world.getBlockState(hitResult.getBlockPos()).getBlock();

                //私人箱子锁******************玩家名字——权限*************************
                BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());
                //判断是否有已经是私人箱子
                //如果是私人箱子
                if (blockEntity != null){
                    //是否是私人容器
                    if (((ChestBlockEntityNbt) blockEntity).privateChest$contains("private_chest_aliveandwell")) {
                        //判断玩家是否为主要权限
                        if (((ChestBlockEntityNbt) blockEntity).privateChest$getString("private_chest_mainower").equals(player.getUuidAsString())) {
                            //添加团队
                            if (itemStack.getItem() == Items.NAME_TAG) {
                                if(!player.isSneaking()){
                                    //写入命名牌的名字到nbt
                                    //团队名字记录
                                    String nameTeam = ((ChestBlockEntityNbt) blockEntity).privateChest$getString("private_chest_aliveandwell");
                                    //判断是否已添加
//                                    if(!nameTeam.contains(itemStack.getName().getString())){
                                    String itemName = itemStack.getName().getString();
                                    for (ServerPlayerEntity serverPlayer : Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList()){
                                        if(serverPlayer.getName().getString().equals(itemName)){//服务器有该玩家
                                            if(!((ChestBlockEntityNbt) blockEntity).privateChest$contains(serverPlayer.getUuidAsString())){
                                                //记录uuid——Boolean
                                                ((ChestBlockEntityNbt) blockEntity).privateChest$putBoolean(serverPlayer.getUuidAsString(),true);
                                                //记录name——uuid
                                                ((ChestBlockEntityNbt) blockEntity).privateChest$putString(itemName,serverPlayer.getUuidAsString());
                                                nameTeam = nameTeam+","+itemStack.getName().getString();

                                                //移除之前的名字，再写入新的
                                                ((ChestBlockEntityNbt) blockEntity).privateChest$removeString("private_chest_aliveandwell");

                                                //记录存入玩家的名字，用以信息播报
                                                ((ChestBlockEntityNbt) blockEntity).privateChest$putString("private_chest_aliveandwell", nameTeam);

                                                itemStack.split(1);
                                                player.sendMessage(Text.of(nameTeam).copy().formatted(Formatting.LIGHT_PURPLE).append(Text.translatable("aliveandwell.privatechest.info10").formatted(Formatting.GREEN)));
                                                return ActionResult.FAIL;
                                            }
                                        }
                                    }
                                }else {
                                    //移除玩家权限
                                    //团队名字记录
                                    String nameTeam = ((ChestBlockEntityNbt) blockEntity).privateChest$getString("private_chest_aliveandwell");
                                    //判断是否已添加
                                    if(((ChestBlockEntityNbt) blockEntity).privateChest$contains(itemStack.getName().getString())) {
                                        //移除玩家权限
                                        //移除uuid——Boolean
                                        ((ChestBlockEntityNbt) blockEntity).privateChest$removeString(((ChestBlockEntityNbt) blockEntity).privateChest$getString(itemStack.getName().getString()));
                                        //移除name——uuid
                                        ((ChestBlockEntityNbt) blockEntity).privateChest$removeString(itemStack.getName().getString());
                                        //AAA,BBB,CCC,DDD
                                        nameTeam = nameTeam.replace("," + itemStack.getName().getString(), "");

                                        //移除之前的名字，再写入新的
                                        ((ChestBlockEntityNbt) blockEntity).privateChest$removeString("private_chest_aliveandwell");
                                        ((ChestBlockEntityNbt) blockEntity).privateChest$putString("private_chest_aliveandwell", nameTeam);

                                        itemStack.split(1);
                                        player.sendMessage(Text.of(nameTeam).copy().formatted(Formatting.LIGHT_PURPLE).append(Text.translatable("aliveandwell.privatechest.info10").formatted(Formatting.GREEN)));
                                        return ActionResult.FAIL;
                                    }
                                }
                            }

                            //移除私人箱子
                            if (itemStack.getItem() == Items.COAL) {
                                if (player.isSneaking()) {
                                    String nameTeam = ((ChestBlockEntityNbt) blockEntity).privateChest$getString("private_chest_aliveandwell");
//                                    //多个玩家
                                    if(nameTeam.contains(",")){
                                        //分割并提取团队名字
                                        String[] names = nameTeam.split(",");
                                        for (String name : names){
                                            //移除uuid——Boolean
                                            ((ChestBlockEntityNbt) blockEntity).privateChest$removeString(((ChestBlockEntityNbt) blockEntity).privateChest$getString(name));
                                            //移除name——uuid
                                            ((ChestBlockEntityNbt) blockEntity).privateChest$removeString(name);
                                        }
                                    }else {
                                        //移除uuid——Boolean
                                        ((ChestBlockEntityNbt) blockEntity).privateChest$removeString(((ChestBlockEntityNbt) blockEntity).privateChest$getString(nameTeam));
                                        //移除name——uuid
                                        ((ChestBlockEntityNbt) blockEntity).privateChest$removeString(nameTeam);
                                    }

                                    //移除私人箱子标记
                                    ((ChestBlockEntityNbt) blockEntity).privateChest$removeString(((ChestBlockEntityNbt) blockEntity).privateChest$getString("private_chest_mainower"));
                                    ((ChestBlockEntityNbt) blockEntity).privateChest$removeString("private_chest_mainower");
                                    ((ChestBlockEntityNbt) blockEntity).privateChest$removeString("private_chest_aliveandwell");
                                    ((ChestBlockEntityNbt) blockEntity).privateChest$removeString("private_chest_nbt_aliveandwell");

                                    itemStack.split(1);
                                    player.sendMessage(Text.translatable("aliveandwell.privatechest.info9").formatted(Formatting.GREEN));
                                    return ActionResult.FAIL;
                                }
                            }
                            return ActionResult.PASS;
                        } else {
                            //是否有权限
                            if (!((ChestBlockEntityNbt) blockEntity).privateChest$getBoolean(player.getUuidAsString())) {
                                player.sendMessage(Text.translatable(((ChestBlockEntityNbt) blockEntity).privateChest$getString("private_chest_aliveandwell")).formatted(Formatting.LIGHT_PURPLE).append(Text.translatable("aliveandwell.privatechest.info2").formatted(Formatting.YELLOW)));
                                return ActionResult.FAIL;
                            }
                            return ActionResult.PASS;
                        }
                    } else {
                        //不是私人箱子
                        //判断玩家手中是否有命名牌
                        if (itemStack.getItem() == Items.NAME_TAG) {
                            if(player.getName().equals(itemStack.getName())){
                                //写入命名牌的名字到nbt
                                //存入玩家uuid
                                ((ChestBlockEntityNbt) blockEntity).privateChest$putString("private_chest_aliveandwell", itemStack.getName().getString());
                                //权限记录
                                ((ChestBlockEntityNbt) blockEntity).privateChest$putBoolean(player.getUuidAsString(),true);
                                //主要权限
                                ((ChestBlockEntityNbt) blockEntity).privateChest$putString("private_chest_mainower",player.getUuidAsString());
                                Text name = itemStack.getName();
                                itemStack.split(1);
                                player.sendMessage(name.copy().formatted(Formatting.LIGHT_PURPLE).append(Text.translatable("aliveandwell.privatechest.info10").formatted(Formatting.GREEN)));
                                return ActionResult.FAIL;
                            }
                        }
                    }
                }

                //私人箱子旁边无法放置箱子*******************************************************************************************************************************************
                BlockPos blockPos0 = hitResult.getBlockPos();//右击位置
                BlockPos blockPos1 = blockPos0.up().south();
                BlockPos blockPos2 = blockPos0.up().north();
                BlockPos blockPos3 = blockPos0.up().west();
                BlockPos blockPos4 = blockPos0.up().east();
                BlockPos blockPos5 = blockPos0.up().up();
                BlockPos blockPos6 = blockPos0.down().down();

                BlockPos blockPos61 = blockPos0.down().north();
                BlockPos blockPos62 = blockPos0.down().south();
                BlockPos blockPos63 = blockPos0.down().west();
                BlockPos blockPos64 = blockPos0.down().east();

                BlockPos blockPos7 = blockPos0.south();
                BlockPos blockPos8 = blockPos0.north();
                BlockPos blockPos9 = blockPos0.west();
                BlockPos blockPos10 = blockPos0.east();
                BlockPos blockPos11 = blockPos0.down();
                BlockPos blockPos12 = blockPos0.up();

                BlockEntity block1 = world.getBlockEntity(blockPos1);
                BlockEntity block2 = world.getBlockEntity(blockPos2);
                BlockEntity block3 = world.getBlockEntity(blockPos3);
                BlockEntity block4 = world.getBlockEntity(blockPos4);
                BlockEntity block5 = world.getBlockEntity(blockPos5);
                BlockEntity block6 = world.getBlockEntity(blockPos6);

                BlockEntity block61 = world.getBlockEntity(blockPos61);
                BlockEntity block62= world.getBlockEntity(blockPos62);
                BlockEntity block63 = world.getBlockEntity(blockPos63);
                BlockEntity block64 = world.getBlockEntity(blockPos64);

                BlockEntity block7 = world.getBlockEntity(blockPos7);
                BlockEntity block8 = world.getBlockEntity(blockPos8);
                BlockEntity block9 = world.getBlockEntity(blockPos9);
                BlockEntity block10 = world.getBlockEntity(blockPos10);
                BlockEntity block11 = world.getBlockEntity(blockPos11);
                BlockEntity block12 = world.getBlockEntity(blockPos12);

                //判断右击位置的斜角是否有私人箱子
                BlockPos blockPosSouth = blockPos0.south();
                BlockPos blockPosNorth = blockPos0.north();
                BlockPos blockPosWest = blockPos0.west();
                BlockPos blockPosEast = blockPos0.east();
                //四周位置的三个方向位置是否为私人箱子
                BlockPos blockPosSouth1 = blockPosSouth.south();
                BlockPos blockPosSouth2 = blockPosSouth.west();
                BlockPos blockPosSouth3 = blockPosSouth.east();

                BlockPos blockPosNorth1 = blockPosNorth.north();
                BlockPos blockPosNorth2 = blockPosNorth.west();
                BlockPos blockPosNorth3 = blockPosNorth.east();

                BlockPos blockPosWest1 = blockPosWest.south();
                BlockPos blockPosWest2 = blockPosWest.west();
                BlockPos blockPosWest3 = blockPosWest.north();

                BlockPos blockPosEast1 = blockPosEast.south();
                BlockPos blockPosEast2 = blockPosEast.north();
                BlockPos blockPosEast3 = blockPosEast.east();
                //对应方块
                BlockEntity blockPosSouth1B = world.getBlockEntity(blockPosSouth1);
                BlockEntity blockPosSouth2B = world.getBlockEntity(blockPosSouth2);
                BlockEntity blockPosSouth3B = world.getBlockEntity(blockPosSouth3);

                BlockEntity blockPosNorth1B = world.getBlockEntity(blockPosNorth1);
                BlockEntity blockPosNorth2B = world.getBlockEntity(blockPosNorth2);
                BlockEntity blockPosNorth3B = world.getBlockEntity(blockPosNorth3);

                BlockEntity blockPosWest1B = world.getBlockEntity(blockPosWest1);
                BlockEntity blockPosWest2B = world.getBlockEntity(blockPosWest2);
                BlockEntity blockPosWest3B = world.getBlockEntity(blockPosWest3);

                BlockEntity blockPosEast1B = world.getBlockEntity(blockPosEast1);
                BlockEntity blockPosEast2B = world.getBlockEntity(blockPosEast2);
                BlockEntity blockPosEast3B = world.getBlockEntity(blockPosEast3);

                //不能拿着箱子放在草上。
                if (blockH instanceof PlantBlock) {
                    if (item instanceof BlockItem blockItem && blockItem.getBlock().getDefaultState().hasBlockEntity()) {
                        return ActionResult.FAIL;
                    }
                }

                //1
                if (blockPosSouth1B != null && ((ChestBlockEntityNbt) blockPosSouth1B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosSouth1B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //2
                if (blockPosSouth2B != null && ((ChestBlockEntityNbt) blockPosSouth2B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosSouth2B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //3
                if (blockPosSouth3B != null && ((ChestBlockEntityNbt) blockPosSouth3B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosSouth3B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //4
                if (blockPosNorth1B != null && ((ChestBlockEntityNbt) blockPosNorth1B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosNorth1B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //5
                if (blockPosNorth2B != null && ((ChestBlockEntityNbt) blockPosNorth2B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosNorth2B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //6
                if (blockPosNorth3B != null && ((ChestBlockEntityNbt) blockPosNorth3B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosNorth3B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //7
                if (blockPosWest1B != null && ((ChestBlockEntityNbt) blockPosWest1B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosWest1B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //8
                if (blockPosWest2B != null && ((ChestBlockEntityNbt) blockPosWest2B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosWest2B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //9
                if (blockPosWest3B != null && ((ChestBlockEntityNbt) blockPosWest3B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosWest3B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //10
                if (blockPosEast1B != null && ((ChestBlockEntityNbt) blockPosEast1B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosEast1B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //11
                if (blockPosEast2B != null && ((ChestBlockEntityNbt) blockPosEast2B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosEast2B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //12
                if (blockPosEast3B != null && ((ChestBlockEntityNbt) blockPosEast3B).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) blockPosEast3B).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }

                //==============================
                if (block1 != null && ((ChestBlockEntityNbt) block1).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block1).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                if (block2 != null && ((ChestBlockEntityNbt) block2).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block2).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                //=================================
                if (block3 != null && ((ChestBlockEntityNbt) block3).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block3).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                if (block4 != null && ((ChestBlockEntityNbt) block4).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block4).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }

                //================================

                if (block5 != null && ((ChestBlockEntityNbt) block5).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block5).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }

                if (block6 != null && ((ChestBlockEntityNbt) block6).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block6).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }

                if (block61 != null && ((ChestBlockEntityNbt) block61).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block61).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                if (block62 != null && ((ChestBlockEntityNbt) block62).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block62).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                if (block63 != null && ((ChestBlockEntityNbt) block63).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block63).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
                if (block64 != null && ((ChestBlockEntityNbt) block64).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block64).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }

                //===============================================
                if (block7 != null && ((ChestBlockEntityNbt) block7).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block7).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }

                if (block8 != null && ((ChestBlockEntityNbt) block8).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block8).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }

                if (block9 != null && ((ChestBlockEntityNbt) block9).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block9).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }

                if (block10 != null && ((ChestBlockEntityNbt) block10).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block10).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }

                if (block11 != null && ((ChestBlockEntityNbt) block11).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block11).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }

                if (block12 != null && ((ChestBlockEntityNbt) block12).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (!((ChestBlockEntityNbt) block12).privateChest$getBoolean(player.getUuidAsString())) {
                        player.sendMessage(Text.translatable("aliveandwell.privatechest.info6").formatted(Formatting.RED));
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });


        //破坏私人箱子播报
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            if (!player.getWorld().isClient) {
                BlockEntity block = world.getBlockEntity(pos);
                if (block != null && ((ChestBlockEntityNbt) block).privateChest$contains("private_chest_aliveandwell")) {
                    //判断玩家是否有权限
                    if (((ChestBlockEntityNbt)block).privateChest$getString("private_chest_mainower").equals(player.getUuidAsString())) {
                        return true;
                    }

//                    if(Objects.requireNonNull(player.getServer()).getCommandSource().hasPermissionLevel(4)){
//                        return true;
//                    }

                    if (Objects.requireNonNull(player.getServer()).getPlayerManager().isOperator(player.getGameProfile())) {
                        List<ServerPlayerEntity> list = Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayerList();
                        for (ServerPlayerEntity player1 : list) {
                            player1.sendMessage(Text.translatable("aliveandwell.privatechest.administrator").append(Text.of(player.getName().getString())).append(Text.translatable("aliveandwell.privatechest.info7")).append(Text.translatable(((ChestBlockEntityNbt) block).privateChest$getString("private_chest_aliveandwell")).formatted(Formatting.LIGHT_PURPLE)).append(Text.translatable("aliveandwell.privatechest.info8")).formatted(Formatting.YELLOW));
                        }
                        return true;
                    }

                    player.sendMessage(Text.translatable(((ChestBlockEntityNbt) block).privateChest$getString("private_chest_aliveandwell")).formatted(Formatting.LIGHT_PURPLE).append(Text.translatable("aliveandwell.privatechest.info8")).formatted(Formatting.YELLOW));
                    return false;
                }
            }
            return true;
        });
    }
}
