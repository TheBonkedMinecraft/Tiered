package draylar.tiered;

import draylar.tiered.api.BorderTemplate;
import draylar.tiered.api.PotentialAttribute;
import draylar.tiered.api.ReforgeItem;
import draylar.tiered.data.AttributeDataLoader;
import draylar.tiered.data.ReforgeItemDataLoader;
import draylar.tiered.data.TooltipBorderLoader;
import draylar.tiered.network.TieredClientPacket;
import draylar.tiered.reforge.ReforgeScreen;
import draylar.tiered.reforge.ReforgeScreenHandler;
import draylar.tiered.reforge.widget.AnvilTab;
import draylar.tiered.reforge.widget.ReforgeTab;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.libz.registry.TabRegistry;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class TieredClient implements ClientModInitializer {

    // map for storing attributes before logging into a server
    public static final Map<Identifier, PotentialAttribute> CACHED_ATTRIBUTES = new HashMap<>();
    public static final List<ReforgeItem> CACHED_REFORGE_ITEM = new ArrayList<>();

    public static final List<BorderTemplate> BORDER_TEMPLATES = new ArrayList<BorderTemplate>();

    private static final Identifier ANVIL_TAB_ICON = new Identifier("tiered:textures/gui/anvil_tab_icon.png");
    private static final Identifier REFORGE_TAB_ICON = new Identifier("tiered:textures/gui/reforge_tab_icon.png");

    public static final boolean isBCLibLoaded = FabricLoader.getInstance().isModLoaded("bclib");

    @Override
    public void onInitializeClient() {
        registerAttributeSyncHandler();
        registerReforgeItemSyncHandler();
        HandledScreens.<ReforgeScreenHandler, ReforgeScreen>register(Tiered.REFORGE_SCREEN_HANDLER_TYPE, ReforgeScreen::new);
        TieredClientPacket.init();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new TooltipBorderLoader());
        TabRegistry.registerOtherTab(new AnvilTab(Text.translatable("container.repair"), ANVIL_TAB_ICON, 0, AnvilScreen.class), AnvilScreen.class);
        TabRegistry.registerOtherTab(new ReforgeTab(Text.translatable("screen.tiered.reforging_screen"), REFORGE_TAB_ICON, 1, ReforgeScreen.class), AnvilScreen.class);
    }

    public static void registerAttributeSyncHandler() {
        ClientPlayNetworking.registerGlobalReceiver(Tiered.ATTRIBUTE_SYNC_PACKET, (client, play, packet, packetSender) -> {
            // save old attributes
            CACHED_ATTRIBUTES.putAll(Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes());
            Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().clear();

            // for each id/attribute pair, load it
            int size = packet.readInt();
            for (int i = 0; i < size; i++) {
                Identifier id = new Identifier(packet.readString());
                PotentialAttribute pa = AttributeDataLoader.GSON.fromJson(packet.readString(), PotentialAttribute.class);
                Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().put(id, pa);
            }
        });
    }

    public static void registerReforgeItemSyncHandler() {
        ClientPlayNetworking.registerGlobalReceiver(Tiered.REFORGE_ITEM_SYNC_PACKET, (client, play, packet, packetSender) -> {
            // save old attributes
            CACHED_REFORGE_ITEM.clear();
            CACHED_REFORGE_ITEM.addAll(Tiered.REFORGE_ITEM_DATA_LOADER.getReforgeItems());
            Tiered.REFORGE_ITEM_DATA_LOADER.getReforgeItems().clear();

            // for each id/attribute pair, load it
            int size = packet.readInt();
            for (int i = 0; i < size; i++) {
                ReforgeItem reforgeItem = ReforgeItemDataLoader.GSON.fromJson(packet.readString(), ReforgeItem.class);
                Tiered.REFORGE_ITEM_DATA_LOADER.getReforgeItems().add(reforgeItem);
            }
        });
    }
}
