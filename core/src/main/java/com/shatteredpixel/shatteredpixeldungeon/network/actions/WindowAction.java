package com.shatteredpixel.shatteredpixeldungeon.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions.WndOptionsParams;
import com.nikita22007.multiplayer.utils.text.LocalizedString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class WindowAction implements LiveStateNetworkAction {
    public final int windowId;
    public final @NotNull String type;

    protected WindowAction(int windowId, @NotNull String type) {
        this.windowId = windowId;
        this.type = type;
    }

    @Override
    public final @NotNull String actionName() {
        return "show_window";
    }

    // --- Subclasses for each Window type ---

    public static class Alchemy extends WindowAction {
        public final int energy;
        public final boolean hasToolkit;
        public final int toolkitEnergy;
        public final List<Item> inputs;
        public final List<Integer> combineCosts;
        public final List<Boolean> combineEnabled;
        public final List<Item> outputs;
        public final boolean energyAddBlinking;
        public final boolean repeatEnabled;
        public final boolean createEnergy;
        public final boolean craftedItem;

        public Alchemy(int windowId, int energy, boolean hasToolkit, int toolkitEnergy,
                       List<Item> inputs, List<Integer> combineCosts, List<Boolean> combineEnabled, List<Item> outputs,
                       boolean energyAddBlinking, boolean repeatEnabled, boolean createEnergy, boolean craftedItem) {
            super(windowId, "alchemy");
            this.energy = energy;
            this.hasToolkit = hasToolkit;
            this.toolkitEnergy = toolkitEnergy;
            this.inputs = inputs;
            this.combineCosts = combineCosts;
            this.combineEnabled = combineEnabled;
            this.outputs = outputs;
            this.energyAddBlinking = energyAddBlinking;
            this.repeatEnabled = repeatEnabled;
            this.createEnergy = createEnergy;
            this.craftedItem = craftedItem;
        }
    }

    public static class Bag extends WindowAction {
        public final LocalizedString title;
        public final List<List<Integer>> allowedItems;
        public final boolean hasListener;

        public Bag(int windowId, LocalizedString title, List<List<Integer>> allowedItems, boolean hasListener) {
            super(windowId, "wnd_bag");
            this.title = title;
            this.allowedItems = allowedItems;
            this.hasListener = hasListener;
        }
    }

    public static class ChooseSubclass extends WindowAction {
        public final String option1;
        public final String option2;

        public ChooseSubclass(int windowId, String option1, String option2) {
            super(windowId, "choose_subclass");
            this.option1 = option1;
            this.option2 = option2;
        }
    }

    public static class SpellButtonInfo {
        public final boolean info;
        public final double alpha;
        public final int tier;
        public final int icon;
        public final int spellId;
        public final LocalizedString spellShortDesc;
        public final LocalizedString spellName;

        public SpellButtonInfo(boolean info, double alpha, int tier, int icon, int spellId, LocalizedString spellShortDesc, LocalizedString spellName) {
            this.info = info;
            this.alpha = alpha;
            this.tier = tier;
            this.icon = icon;
            this.spellId = spellId;
            this.spellShortDesc = spellShortDesc;
            this.spellName = spellName;
        }
    }

    public static class ClericSpells extends WindowAction {
        public final boolean info;
        public final List<SpellButtonInfo> buttons;

        public ClericSpells(int windowId, boolean info, List<SpellButtonInfo> buttons) {
            super(windowId, "cleric_spells");
            this.info = info;
            this.buttons = buttons;
        }
    }

    public static class InfoCell extends WindowAction {
        public final LocalizedString desc;
        public final IconTitle titlebar;

        public InfoCell(int windowId, LocalizedString desc, IconTitle titlebar) {
            super(windowId, "info_cell");
            this.desc = desc;
            this.titlebar = titlebar;
        }
    }

    public static class Options extends WindowAction {
        public final WndOptionsParams params;

        public Options(int windowId, WndOptionsParams params) {
            super(windowId, "wnd_option");
            this.params = params;
        }
    }

    public static class Quest extends WindowAction {
        public final String spriteName;
        public final LocalizedString charName;
        public final LocalizedString text;

        public Quest(int windowId, String spriteName, LocalizedString charName, LocalizedString text) {
            super(windowId, "quest");
            this.spriteName = spriteName;
            this.charName = charName;
            this.text = text;
        }
    }

    public static class SadGhost extends WindowAction {
        public final int questType;
        public final Item weapon;
        public final Item armor;

        public SadGhost(int windowId, int questType, Item weapon, Item armor) {
            super(windowId, "sad_ghost");
            this.questType = questType;
            this.weapon = weapon;
            this.armor = armor;
        }
    }

    public static class TradeItem extends WindowAction {
        public final boolean selling;
        public final Item item;
        public final int price;
        public final boolean steal;
        public final int chance;
        public final int charges;

        public TradeItem(int windowId, boolean selling, Item item, int price, boolean steal, int chance, int charges) {
            super(windowId, "trade_item");
            this.selling = selling;
            this.item = item;
            this.price = price;
            this.steal = steal;
            this.chance = chance;
            this.charges = charges;
        }
    }

    public static class Wandmaker extends WindowAction {
        public final Item wand1;
        public final Item wand2;
        public final Item questItem;
        public final String questItemClass;

        public Wandmaker(int windowId, Item wand1, Item wand2, Item questItem, String questItemClass) {
            super(windowId, "wandmaker");
            this.wand1 = wand1;
            this.wand2 = wand2;
            this.questItem = questItem;
            this.questItemClass = questItemClass;
        }
    }

    public static class Guess extends WindowAction {
        public final Item item;
        public final List<Integer> icons;
        public final List<Class<? extends Item>> guessOptions;

        public Guess(int windowId, Item item, List<Integer> icons, List<Class<? extends Item>> guessOptions) {
            super(windowId, "guess");
            this.item = item;
            this.icons = icons;
            this.guessOptions = guessOptions;
        }
    }

    public static class GhostHero extends WindowAction {
        public final Item weapon;
        public final Item armor;
        public final Item rose;
        public final LocalizedString title;
        public final LocalizedString message;

        public GhostHero(int windowId, Item weapon, Item armor, Item rose, LocalizedString title, LocalizedString message) {
            super(windowId, "ghost_hero");
            this.weapon = weapon;
            this.armor = armor;
            this.rose = rose;
            this.title = title;
            this.message = message;
        }
    }
}
