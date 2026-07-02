/*
 * Licensed under the GPL, Version 3.0.
 * You may obtain a copy of the Licence at:
 * https://www.gnu.org/licenses/gpl-3.0.html
 */

package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ExampleMod.MOD_ID)
public final class ExampleMod
{
    public static final String MOD_ID = "examplemod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ExampleMod()
    {
        LOGGER.info("Hello from {}!", MOD_ID);
    }
}
