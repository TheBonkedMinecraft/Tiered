package draylar.tiered.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "tiered")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class TieredConfig implements ConfigData {

    @Comment("Items in for example mineshaft chests get modifiers")
    public boolean lootContainerModifier = true;
    @Comment("Equipped items on entities get modifiers")
    public boolean entityItemModifier = true;
    @Comment("Crafted items get modifiers")
    public boolean craftingModifier = true;
    @Comment("Merchant items get modifiers")
    public boolean merchantModifier = true;
    @Comment("Decreases the biggest weights by this modifier")
    public float reforge_modifier = 0.9F;
    @Comment("Modify the biggest weights by this modifier per smithing level")
    public float levelz_reforge_modifier = 0.01F;
    @Comment("Modify the biggest weights by this modifier per luck")
    public float luck_reforge_modifier = 0.02F;

    @ConfigEntry.Category("client_settings")
    public boolean showReforgingTab = true;
    @ConfigEntry.Category("client_settings")
    public int xIconPosition = 0;
    @ConfigEntry.Category("client_settings")
    public int yIconPosition = 0;
    @ConfigEntry.Category("client_settings")
    public boolean tieredTooltip = true;
    @ConfigEntry.Category("client_settings")
    public boolean centerName = true;

}
