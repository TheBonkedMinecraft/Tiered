package draylar.tiered.reforge.widget;

import org.jetbrains.annotations.Nullable;

import draylar.tiered.TieredClient;
import draylar.tiered.network.TieredClientPacket;
import net.libz.api.InventoryTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AnvilTab extends InventoryTab {

    public AnvilTab(Text title, @Nullable Identifier texture, int preferedPos, Class<?>... screenClasses) {
        super(title, texture, preferedPos, screenClasses);
    }

    @Override
    public void onClick(MinecraftClient client) {
        if (!TieredClient.isBCLibLoaded) {
            TieredClientPacket.writeC2SScreenPacket((int) client.mouse.getX(), (int) client.mouse.getY(), false);
        }
    }

}
