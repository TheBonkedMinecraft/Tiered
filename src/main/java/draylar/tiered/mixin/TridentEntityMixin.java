package draylar.tiered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import draylar.tiered.util.AttributeHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {

    public TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "onEntityHit", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/damage/DamageSource;trident(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;)Lnet/minecraft/entity/damage/DamageSource;"), ordinal = 0)
    private float onEntityHitMixin(float original) {
        if (this.getOwner() instanceof ServerPlayerEntity) {
            return AttributeHelper.getExtraRangeDamage((PlayerEntity) this.getOwner(), original);
        }
        return original;
    }
}
