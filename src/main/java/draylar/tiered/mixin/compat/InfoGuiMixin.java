package draylar.tiered.mixin.compat;

import java.text.DecimalFormat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import draylar.tiered.config.ConfigInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.gui.InfoGui;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(InfoGui.class)
public class InfoGuiMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 0), ordinal = 0)
    private Text translatableText1Mixin(Text original) {
        return Text.translatable("text.tiered.smithing_info_1_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.levelz_reforge_modifier * 100));
    }

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 0), ordinal = 1)
    private Text translatableText1BMixin(Text original) {
        return Text.translatable("text.tiered.smithing_info_1_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.levelz_reforge_modifier * 100));
    }

}
