package draylar.tiered.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import draylar.tiered.api.BorderTemplate;
import draylar.tiered.util.TieredTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.tooltipfix.Helper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
@Mixin(TieredTooltip.class)
public class TieredTooltipMixin {

    @Inject(method = "renderTieredTooltipFromComponents", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0))
    private static void renderTieredTooltipFromComponentsMixin(MatrixStack matrices, List<TooltipComponent> components, int x, int y, int width, int height, BorderTemplate borderTemplate,
            TextRenderer textRenderer, ItemRenderer itemRenderer, CallbackInfo info) {
                Helper.newFix(components, textRenderer, x, width);
    }

    // @ModifyVariable(method = "renderTooltipFromComponents", at = @At(value = "HEAD"), index = 2, argsOnly = true)
    // public List<TooltipComponent> makeListMutable(List<TooltipComponent> value) {
    //     return new ArrayList<>(value);
    // }

    // @Inject(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0))
    // public void fix(MatrixStack matrices, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
    //     Helper.newFix(components, textRenderer, x, width);
    // }

    // @ModifyVariable(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"), index = 7)
    // public int modifyRenderX(int value, MatrixStack matrices, List<TooltipComponent> components, int x, int y) {
    //     return Helper.shouldFlip(components, textRenderer, x);
    // }
}
