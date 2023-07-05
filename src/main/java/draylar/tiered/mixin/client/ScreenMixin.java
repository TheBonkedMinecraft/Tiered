package draylar.tiered.mixin.client;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import draylar.tiered.TieredClient;
import draylar.tiered.config.ConfigInit;
import draylar.tiered.util.TieredTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public class ScreenMixin {

    @Shadow
    protected TextRenderer textRenderer;
    @Shadow
    protected ItemRenderer itemRenderer;
    @Shadow
    public int width;
    @Shadow
    public int height;

    @Inject(method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
    protected void renderTooltipMixin(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo info) {
        if (ConfigInit.CONFIG.tieredTooltip && stack.hasNbt() && stack.getNbt().contains("Tiered")) {
            String nbtString = stack.getNbt().getCompound("Tiered").asString();
            for (int i = 0; i < TieredClient.BORDER_TEMPLATES.size(); i++) {
                if (!TieredClient.BORDER_TEMPLATES.get(i).containsStack(stack) && TieredClient.BORDER_TEMPLATES.get(i).containsDecider(nbtString)) {
                    TieredClient.BORDER_TEMPLATES.get(i).addStack(stack);
                } else if (TieredClient.BORDER_TEMPLATES.get(i).containsStack(stack)) {
                    List<TooltipComponent> list = getTooltipFromItem(stack).stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
                    stack.getTooltipData().ifPresent(data -> list.add(1, TooltipComponentCallback.EVENT.invoker().getComponent(data)));
                    // stack.getTooltipData().ifPresent(data -> list.add(1, TooltipComponent.of(data)));
                    TieredTooltip.renderTieredTooltipFromComponents(matrices, list, x, y, width, height, TieredClient.BORDER_TEMPLATES.get(i), textRenderer, itemRenderer);
                    info.cancel();
                    break;
                }
            }
        }
    }

    @Shadow
    public List<Text> getTooltipFromItem(ItemStack stack) {
        return null;
    }

}
