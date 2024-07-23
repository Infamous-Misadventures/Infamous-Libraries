package com.infamousmisadventures.infamouslibraries.summons.mixins;

import com.infamousmisadventures.infamouslibraries.followers.ILeaderDataHolder;
import com.infamousmisadventures.infamouslibraries.followers.Leader;
import com.infamousmisadventures.infamouslibraries.summons.registry.ILSummonsAttributes;
import com.infamousmisadventures.infamouslibraries.summons.summon.ISummonDataHolder;
import com.infamousmisadventures.infamouslibraries.summons.summon.Summon;
import com.infamousmisadventures.infamouslibraries.summons.summoner.ISummonerDataHolder;
import com.infamousmisadventures.infamouslibraries.summons.summoner.Summoner;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin implements ISummonDataHolder, ISummonerDataHolder {

    @Unique
    private Summon il_summons$summon = null;
    @Unique
    private Summoner il_summons$summoner = null;

    @Override
    public Summon getOrCreateSummonData() {
        if (il_summons$summon == null) {
            il_summons$summon = new Summon();
        }
        return il_summons$summon;
    }
    @Override
    public Summoner getOrCreateSummonerData() {
        Leader leader = ((ILeaderDataHolder) this).getOrCreateLeaderData();
        if (il_summons$summoner == null) {
            il_summons$summoner = new Summoner(leader);
        }
        return il_summons$summoner;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        nbt.put("SummonData", this.getOrCreateSummonData().serializeNBT());
        nbt.put("SummonerData", this.getOrCreateSummonerData().serializeNBT());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        getOrCreateSummonData().deserializeNBT(nbt.getCompound("SummonData"));
        getOrCreateSummonerData().deserializeNBT(nbt.getCompound("SummonerData"));
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void ILSummons$addModdedAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        ILSummonsAttributes.getAttributes().forEach((attributeSupplier) ->
                cir.getReturnValue().add(attributeSupplier.get())
        );
    }
}
