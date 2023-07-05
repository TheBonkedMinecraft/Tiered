package draylar.tiered.mixin;

import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import draylar.tiered.api.ModifierUtils;
import draylar.tiered.config.ConfigInit;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.math.random.Random;

@Mixin(LootTable.class)
public class LootTableMixin {

    @Inject(method = "method_331", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0))
    private static void processStacksMixin(Consumer<ItemStack> lootConsumer, ItemStack itemStack, CallbackInfo info) {
        if (ConfigInit.CONFIG.lootContainerModifier) {
            ModifierUtils.setItemStackAttribute(null, itemStack, false);
        }
    }

    @Inject(method = "method_331", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void processStacksMixin(Consumer<ItemStack> lootConsumer, ItemStack itemStack, CallbackInfo info, int i, ItemStack itemStack2) {
        if (ConfigInit.CONFIG.lootContainerModifier) {
            ModifierUtils.setItemStackAttribute(null, itemStack2, false);
        }
    }

    @Inject(method = "supplyInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void supplyInventoryMixin(Inventory inventory, LootContext context, CallbackInfo info, ObjectArrayList<ItemStack> objectArrayList, Random random, List<Integer> list,
            ObjectListIterator<ItemStack> var6, ItemStack itemStack) {
        if (ConfigInit.CONFIG.lootContainerModifier) {
            ModifierUtils.setItemStackAttribute(null, itemStack, false);
        }
    }
}
