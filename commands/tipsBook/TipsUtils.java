package org.vizzoid.raidserver.raidserver.minecraft.commands.tipsBook;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.vizzoid.raidserver.raidserver.main.meta.Utility;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;

public final class TipsUtils extends Utility {

  private static final Key KEY = new Key("TIPS_AMOUNT");
  private static final Key AMOUNT = new Key("ADVANCEMENT_AMOUNT");

  private static final int every = 3;

  public static int get(Player p) {
    return has(p) ? new Data(p).getInt(KEY) : 0;
  }

  public static boolean has(Player p) {
    return new Data(p).has(KEY);
  }

  public static void add(Player p) {
    new Data(p).set(KEY, get(p) + 1);
  }

  public static int getAmount(Player p) {
    return hasAmount(p) ? new Data(p).getInt(AMOUNT) : 0;
  }

  public static boolean hasAmount(Player p) {
    return new Data(p).has(AMOUNT);
  }

  public static void addAmount(Player p) {
    int amount = getAmount(p) + 1;
    new Data(p).set(AMOUNT, amount);

    if (amount % every == 0) {
      add(p);
    }
  }

  public static void openBook(Player p) {
    Book.Builder builder = Book.builder();
    builder.author(Component.text("Notch"));
    builder.title(Component.text("Tips"));
    for (int i = 0; i < get(p); i++) {
      builder.addPage(Component.text(Tip.values()[i].desc()));
    }
    p.openBook(builder.build());
  }

  enum Tip {
    MYSTICAL_CROPS("Crops have a chance to drop its 'Mystical' version, which is a food that will produce strange but helpful effects when consumed."),
    DROP_ESSENCE("Nether mobs have chance to 'glow'. Glowing mobs are essence carriers, and they can drop essence, a material for a new type of armor."),
    MINER_DROP("Skeletons and Zombies have a chance to spawn as a Miner, which has a pickaxe and gray armor. Kill this mob for it to drop Miner Armor."),
    ESSENCE_CRAFT("Essence is used to make armor: Ghast for Viking, which is DPS, Wither Skeletons for Swordsman, which is speed, and Magma Cubes for Tank, AKA Knight."),
    ESSENCE_CRAFT_1("Any material for armor is crafted in the same way normal armor is crafted, and in the same shape."),
    WANDERING_TRADER("Wandering traders aren't useless now! They now sell essence dropped from carriers, and some boss drops."),
    BLACKSMITH("The Blacksmith class is gained by trading 250 times with Toolsmith, Armorer, and Weaponsmith villagers, so get to trading!"),
    ESSENCE_PORTAL("Essence from carriers can be used for many things, especially armor and a summon item. This item can be used to generate a portal to a boss' arena."),
    VIKING("The Viking class has a chance of one-shotting normal mobs (not bosses or players), very good against strong mobs, but very ineffective against hordes."),
    SWORDSMAN("The Swordsman class has the ability of leaping! Use this in emergency situations, or if you need to get to high heights!"),
    KNIGHT("The Knight class has the ability of launching other mobs in the air! This is very useful, because it launches mobs, arrows, tnt, fireworks, and more!"),
    BLACKSMITH_1("The Blacksmith class is somewhat of a loose cannon, but its perk is that it gets stronger around players, villagers, and etc, making it one of the most powerful armors!"),
    MINER("The Miner class isn't very powerful, but it is incredibly good against groups, but more powerful in the mines, where it can mine large tunnels quickly!"),
    RIDER("The Rider class requires you to be constantly riding an animal, but it rewards risk. The lower health that ride is, the more powerful you become, just make sure you can dodge."),
    BLACKSMITH_2("The Blacksmith class has the ability of being able to relieve surrounding players' cooldowns! Time this right and you can wipe the field quickly!"),
    MINER_1("The Miner class is very weak, but when you die, you drop an incredibly powerful bomb that lures surrounding mobs to it, to get your payback while you're not there."),
    RIDER_1("The Rider class provides an emergency exit, if you or your ride is about to die, use the heal ability and it'll grant all others heal."),
    UPGRADE("The Viking, Swordsman, and Knight are the only upgradeable armor, and they upgrade into the Berserker, Swordmaster, and Paladin. You can think of it as how you smith Diamond armor to get Netherite."),
    UPGRADE_1("To upgrade the class armor, you need to smith it with the drops from a boss. To craft that summon, use the essence you used to make the armor."),
    BERSERKER("The Berserker armor loads its crossbows automatically, so no more manual work!"),
    SWORDMASTER("The Swordmaster armor destroys its victim on every 5th attack, so make sure you save it for the big guys!"),
    PALADIN("The Paladin is not a very ranged class, but it has the ability of summoning 3 powerful skeletons that'll fight for you!"),
    SKELETON_LIFT_FRONT("The Skeletal Spirit will lift its front legs and come to a complete stop when its ready to leap or slam, so make sure you run!"),
    MAGMA_LOWER_HEALTH("Magma Opus isn't a very strong boss on the surface, but its armor will grow as it loses health, so make sure you spare your resources."),
    SPIDER_BLIND("Blind Widow is blind. This may not seem like a tip but this is the best piece of advice you'll get."),
    SKELETON_DODGE("The Skeletal Spirit is incredibly hard to dodge when dashing, but can only run for a short amount of time, so instead of trying, RUN."),
    MAGMA_NO_RESPAWN("Magma Opus slam is very easy to dodge by jumping, but if you fail, you will go flying. Make sure that you time your jumps right!"),
    SPIDER_EXPLOSION("Blind Widow and its minions are very susceptible to loud noises, so use explosions and fireworks. If you don't have them, there is an alternative...");

    private final String desc;

    Tip(String desc) {
      this.desc = desc;
    }

    public String desc() {
      return desc;
    }
  }

}
