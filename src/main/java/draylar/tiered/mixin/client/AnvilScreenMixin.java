package draylar.tiered.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import draylar.tiered.TieredClient;
import draylar.tiered.config.ConfigInit;
import draylar.tiered.network.TieredClientPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.libz.api.Tab;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> implements Tab {

    @Shadow
    @Mutable
    @Final
    private PlayerEntity player;

    public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @Override
    public Class<?> getParentScreenClass() {
        return this.getClass();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && ConfigInit.CONFIG.showReforgingTab && TieredClient.isBCLibLoaded && this.isPointWithinBounds(26, -20, 22, 19, (double) mouseX, (double) mouseY))
            TieredClientPacket.writeC2SScreenPacket((int) this.client.mouse.getX(), (int) this.client.mouse.getY(), true);

        return super.mouseReleased(mouseX, mouseY, button);
    }

}
