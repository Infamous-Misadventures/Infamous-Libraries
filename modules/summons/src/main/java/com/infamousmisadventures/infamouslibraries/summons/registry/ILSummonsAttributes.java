package com.infamousmisadventures.infamouslibraries.summons.registry;

import com.google.common.collect.ImmutableList;
import com.infamousmisadventures.infamouslibraries.platform.Services;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.function.Supplier;

import static com.infamousmisadventures.infamouslibraries.ILConstants.MOD_ID;
import static com.infamousmisadventures.infamouslibraries.util.ResourceLocationHelper.modLoc;

public class ILSummonsAttributes {
    private static final ObjectArrayList<Supplier<Attribute>> ATTRIBUTES = new ObjectArrayList<>();

    public static final Supplier<Attribute> SUMMON_CAP = registerAttribute("summon_cap",
            0.0D,
            0.0D,
            1024.0D);

    public static void register() {
    }

    public static ImmutableList<Supplier<Attribute>> getAttributes() {
        return ImmutableList.copyOf(ATTRIBUTES);
    }

    private static Supplier<Attribute> registerAttribute(String attributeName, double defaultValue, double minValue, double maxValue) {
        Supplier<Attribute> attribSupToRegister = registerAttribute(attributeName,
                () -> new RangedAttribute("attribute.name.generic."+ MOD_ID + "." + attributeName, defaultValue, minValue, maxValue)
                        .setSyncable(true));

        ATTRIBUTES.add(attribSupToRegister);
        return attribSupToRegister;
    }

    private static Supplier<Attribute> registerAttribute(String id, Supplier<Attribute> attribSup) {
        return Services.REGISTRAR.registerObject(modLoc(id), attribSup, BuiltInRegistries.ATTRIBUTE);
    }
}
