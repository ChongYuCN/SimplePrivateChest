package com.chongyu.privatechest;

import com.chongyu.privatechest.core.Handle;
import net.fabricmc.api.ModInitializer;

public class PrivateChest implements ModInitializer {
    @Override
    public void onInitialize() {
        Handle.init();
    }
}
