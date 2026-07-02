/*
 * Licensed under the GPL, Version 3.0.
 * You may obtain a copy of the Licence at:
 * https://www.gnu.org/licenses/gpl-3.0.html
 */

package com.example.examplemod.data;

import com.example.examplemod.ExampleMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ExampleMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DataEntryPoint
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        // Register data providers here, e.g.:
        // event.getGenerator().addProvider(true, new MyBlockTags(output, lookup, existingFileHelper));
    }
}
