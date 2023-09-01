package com.danielti.shutupf2;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * @author(danielti)
 * also check my website: danielti.com
 */
@Mod(ShutUpF2.MODID)
public class ShutUpF2
{

    public static final String MODID="shutupf2";
    public ShutUpF2()
    {
        new ClientSubscriber();
    }

}
