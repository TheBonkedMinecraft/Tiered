package draylar.tiered.api;

import draylar.tiered.Tiered;
import draylar.tiered.config.ConfigInit;
import net.libz.util.SortList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

import org.jetbrains.annotations.Nullable;

public class ModifierUtils {

    /**
     * Returns the ID of a random attribute that is valid for the given {@link ItemStack} in {@link Identifier} form.
     * <p>
     * If there is no valid attribute for the given {@link ItemStack}, null is returned.
     *
     * @param item {@link ItemStack} to generate a random attribute for
     * @return id of random attribute for item in {@link Identifier} form, or null if there are no valid options
     */
    @Nullable
    public static Identifier getRandomAttributeIDFor(@Nullable PlayerEntity playerEntity, ItemStack item, boolean reforge) {
        List < Identifier > potentialAttributes = new ArrayList < > ();
        List < Integer > attributeWeights = new ArrayList < > ();

        // collect all valid attributes for the given item and their weights

        Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
        if (attribute.isValid(Registry.ITEM.getId(item.getItem())) && (attribute.getWeight() > 0 || reforge)) {
            potentialAttributes.add(new Identifier(attribute.getID()));
            attributeWeights.add(reforge ? attribute.getWeight() + 1 : attribute.getWeight());
        }
        });
        if (potentialAttributes.size() == 0) {
            return null;
        }

        if (reforge && attributeWeights.size() > 2) {
            SortList.concurrentSort(attributeWeights, attributeWeights, potentialAttributes);
            int maxWeight = attributeWeights.get(attributeWeights.size() - 1);
            for (int i = 0; i < attributeWeights.size(); i++) {
                if (attributeWeights.get(i) > maxWeight / 2) {
                    attributeWeights.set(i, (int)(attributeWeights.get(i) * ConfigInit.CONFIG.reforge_modifier));
                }
            }
        }
        // Luck
        if (playerEntity != null) {
            int luckMaxWeight = Collections.max(attributeWeights);
            for (int i = 0; i < attributeWeights.size(); i++) {
                if (attributeWeights.get(i) > luckMaxWeight / 3) {
                    attributeWeights.set(i, (int)(attributeWeights.get(i) * (1.0f - ConfigInit.CONFIG.luck_reforge_modifier * playerEntity.getLuck())));
                }
            }
        }
        //if (item.getDefaultStack().hasNbt() && item.getDefaultStack().getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) {
        Identifier tier = new Identifier(item.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
        List < String > names;
        names = List.of(new String[] {
                "common",
                "uncommon",
                "rare",
                "epic",
                "legendary",
                "unique",
                "unique"
        });
        var tempRarity = tier.toString();
        String rarity;
        if (tempRarity.contains("minecraft")) {
            rarity = "common";
        } else {
            rarity = tier.getPath().split("_")[0];
        }
        var nextTier = names.get(names.indexOf(rarity) == 5 ? names.indexOf(rarity) : names.indexOf(rarity) + 1);
        var target = new ArrayList <Identifier>();
        var current = new ArrayList <Identifier>();
        for (Identifier potentialAttribute: potentialAttributes) {
            if (Objects.equals(potentialAttribute.getPath().split("_")[0], nextTier)) {
                target.add(potentialAttribute);
            } else if (Objects.equals(potentialAttribute.getPath().split("_")[0], rarity)) {
                current.add(potentialAttribute);
            }
        }

        try{
            Random rand = new Random();

            int min = 0;
            int max = 100;
            int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);

            return random_int <= 10 ? target.get(rand.nextInt(target.size())) : current.get(rand.nextInt(current.size()));
        } catch (Exception ignored) {}
        return null;
    }

    public static void setItemStackAttribute(@Nullable PlayerEntity playerEntity, ItemStack stack, boolean reforge) {
        Identifier potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(playerEntity, stack, reforge);
        // found an ID
        if (potentialAttributeID != null) {
            stack.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).putString(Tiered.NBT_SUBTAG_DATA_KEY, potentialAttributeID.toString());

            HashMap < String, Object > nbtMap = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(new Identifier(potentialAttributeID.toString())).getNbtValues();

            // add durability nbt
            List < AttributeTemplate > attributeList = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(new Identifier(potentialAttributeID.toString())).getAttributes();
            for (int i = 0; i < attributeList.size(); i++) {
                if (attributeList.get(i).getAttributeTypeID().equals("tiered:generic.durable")) {
                    if (nbtMap == null) {
                        nbtMap = new HashMap < String, Object > ();
                    }
                    nbtMap.put("durable", (double) Math.round(attributeList.get(i).getEntityAttributeModifier().getValue() * 100.0) / 100.0);
                    break;
                }
            }

            // add nbtMap
            if (nbtMap != null) {
                NbtCompound nbtCompound = stack.getNbt();
                for (HashMap.Entry < String, Object > entry: nbtMap.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    // json list will get read as ArrayList class
                    // json map will get read as linkedtreemap
                    // json integer is read by gson -> always double
                    if (value instanceof String) {
                        nbtCompound.putString(key, (String) value);
                    } else if (value instanceof Boolean) {
                        nbtCompound.putBoolean(key, (boolean) value);
                    } else if (value instanceof Double) {
                        if ((double) Math.abs((double) value) % 1.0 < 0.0001D) {
                            nbtCompound.putInt(key, (int) Math.round((double) value));
                        } else {
                            nbtCompound.putDouble(key, Math.round((double) value * 100.0) / 100.0);
                        }
                    }
                }
                stack.setNbt(nbtCompound);
            }
        }
    }

    public static void removeItemStackAttribute(ItemStack itemStack) {
        if (itemStack.hasNbt() && itemStack.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) {

            Identifier tier = new Identifier(itemStack.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            if (Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier) != null) {
                HashMap < String, Object > nbtMap = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier).getNbtValues();
                List < String > nbtKeys = new ArrayList < > ();
                if (nbtMap != null) {
                    nbtKeys.addAll(nbtMap.keySet().stream().toList());
                }

                List < AttributeTemplate > attributeList = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier).getAttributes();
                for (int i = 0; i < attributeList.size(); i++) {
                    if (attributeList.get(i).getAttributeTypeID().equals("tiered:generic.durable")) {
                        nbtKeys.add("durable");
                        break;
                    }
                }

                if (!nbtKeys.isEmpty()) {
                    for (int i = 0; i < nbtKeys.size(); i++) {
                        if (!nbtKeys.get(i).equals("Damage")) {
                            if (!nbtKeys.get(i).contains("tiered")) {
                                itemStack.getNbt().remove(nbtKeys.get(i));
                            }
                        }
                    }
                }
            }
            //itemStack.removeSubNbt(Tiered.NBT_SUBTAG_KEY);
        }
    }

    private ModifierUtils() {
        // no-op
    }
}