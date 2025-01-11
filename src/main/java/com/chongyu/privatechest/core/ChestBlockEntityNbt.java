package com.chongyu.privatechest.core;

public interface ChestBlockEntityNbt {
    boolean privateChest$contains(String key);

    String privateChest$getString(String key);

    void privateChest$putString(String key, String value);
    void privateChest$removeString(String key);

    boolean privateChest$getBoolean(String key);

    void privateChest$putBoolean(String key, boolean value);
}
