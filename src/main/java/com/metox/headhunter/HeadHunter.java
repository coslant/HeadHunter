package com.metox.headhunter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Oyuncu oldurunce kurbanin kafasi duser, uzerinde katilinin adi yazar.
 * Istege bagli olarak mob kafalari da duser.
 */
public class HeadHunter extends JavaPlugin implements Listener, TabExecutor {

    private final Random random = new Random();

    /** Mob turu -> {1.13+ materyal, 1.8 SKULL_ITEM data degeri} */
    private static final Map<String, Object[]> MOB_HEADS = new HashMap<String, Object[]>();

    static {
        MOB_HEADS.put("SKELETON", new Object[]{"SKELETON_SKULL", (short) 0});
        MOB_HEADS.put("WITHER_SKELETON", new Object[]{"WITHER_SKELETON_SKULL", (short) 1});
        MOB_HEADS.put("ZOMBIE", new Object[]{"ZOMBIE_HEAD", (short) 2});
        MOB_HEADS.put("CREEPER", new Object[]{"CREEPER_HEAD", (short) 4});
        MOB_HEADS.put("ENDER_DRAGON", new Object[]{"DRAGON_HEAD", null});
        MOB_HEADS.put("PIGLIN", new Object[]{"PIGLIN_HEAD", null});
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        if (getCommand("headhunter") != null) {
            getCommand("headhunter").setExecutor(this);
            getCommand("headhunter").setTabCompleter(this);
        }
        getLogger().info("HeadHunter aktif - algilanan surum 1." + Compat.MINOR
                + (Compat.PATCH > 0 ? "." + Compat.PATCH : ""));
    }

    // ------------------------------------------------------------------
    // Oyuncu olumu
    // ------------------------------------------------------------------

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!getConfig().getBoolean("enabled", true)) return;

        Player victim = e.getEntity();
        if (isDisabledWorld(victim.getWorld().getName())) return;

        Player killer = victim.getKiller();
        if (killer == null && getConfig().getBoolean("only-player-kills", true)) return;
        if (killer != null && killer.getUniqueId().equals(victim.getUniqueId())
                && !getConfig().getBoolean("count-suicide", false)) return;

        if (!roll(getConfig().getDouble("chance", 100.0))) return;

        String killerName = killer != null
                ? killer.getName()
                : getConfig().getString("unknown-killer", "Bilinmeyen");

        ItemStack head = buildPlayerHead(victim, killerName);
        if (head == null) return;

        deliver(head, killer, victim.getLocation());

        String announce = getConfig().getString("messages.announce", "");
        if (announce != null && !announce.isEmpty() && killer != null) {
            getServer().broadcastMessage(Compat.color(
                    fill(announce, victim.getName(), killerName)));
        }
    }

    // ------------------------------------------------------------------
    // Mob olumu (istege bagli)
    // ------------------------------------------------------------------

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        if (!getConfig().getBoolean("enabled", true)) return;
        if (!getConfig().getBoolean("mob-heads.enabled", false)) return;
        if (e instanceof PlayerDeathEvent) return;

        LivingEntity ent = e.getEntity();
        if (ent == null || isDisabledWorld(ent.getWorld().getName())) return;

        Player killer = ent.getKiller();
        if (killer == null) return;

        String type = typeName(ent);
        if (type == null || !MOB_HEADS.containsKey(type)) return;
        if (!roll(getConfig().getDouble("mob-heads.chance", 5.0))) return;

        ItemStack head = buildMobHead(type);
        if (head == null) return;

        deliver(head, killer, ent.getLocation());
    }

    private String typeName(Entity ent) {
        try {
            return ent.getType().name();
        } catch (Throwable t) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // Kafa uretimi
    // ------------------------------------------------------------------

    private ItemStack buildPlayerHead(Player victim, String killerName) {
        ItemStack head = Compat.blankHead();
        if (head.getType() == Material.STONE) return null;

        ItemMeta meta = head.getItemMeta();
        if (meta == null) return head;

        Compat.applySkullOwner(meta, victim, victim.getName());

        String name = getConfig().getString("name", "&e%victim_in% Kafasi");
        meta.setDisplayName(Compat.color(fill(name, victim.getName(), killerName)));

        List<String> raw = getConfig().getStringList("lore");
        if (!raw.isEmpty()) {
            List<String> lore = new ArrayList<String>();
            for (String s : raw) lore.add(Compat.color(fill(s, victim.getName(), killerName)));
            meta.setLore(lore);
        }
        head.setItemMeta(meta);
        return head;
    }

    @SuppressWarnings("deprecation")
    private ItemStack buildMobHead(String type) {
        Object[] spec = MOB_HEADS.get(type);
        if (spec == null) return null;

        ItemStack head;
        Material modern = Compat.material((String) spec[0]);
        if (modern != null) {
            head = new ItemStack(modern, 1);
        } else {
            Short data = (Short) spec[1];
            Material legacy = Compat.material("SKULL_ITEM");
            if (legacy == null || data == null) return null;
            head = new ItemStack(legacy, 1, data.shortValue());
        }

        ItemMeta meta = head.getItemMeta();
        if (meta != null) {
            String name = getConfig().getString("mob-heads.name", "&e%mob% Kafasi");
            meta.setDisplayName(Compat.color(name.replace("%mob%", pretty(type))));
            List<String> raw = getConfig().getStringList("mob-heads.lore");
            if (!raw.isEmpty()) {
                List<String> lore = new ArrayList<String>();
                for (String s : raw) lore.add(Compat.color(s.replace("%mob%", pretty(type))));
                meta.setLore(lore);
            }
            head.setItemMeta(meta);
        }
        return head;
    }

    private String pretty(String enumName) {
        String s = enumName.toLowerCase(Locale.ENGLISH).replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        boolean up = true;
        for (char c : s.toCharArray()) {
            sb.append(up ? Character.toUpperCase(c) : c);
            up = c == ' ';
        }
        return sb.toString();
    }

    /** Kafayi katile verir ya da yere birakir. */
    private void deliver(ItemStack head, Player killer, Location at) {
        if (getConfig().getBoolean("give-to-killer", false) && killer != null && killer.isOnline()) {
            Map<Integer, ItemStack> left = killer.getInventory().addItem(head);
            for (ItemStack rem : left.values()) {
                killer.getWorld().dropItemNaturally(killer.getLocation(), rem);
            }
            return;
        }
        if (at != null && at.getWorld() != null) {
            at.getWorld().dropItemNaturally(at, head);
        }
    }

    // ------------------------------------------------------------------
    // Yardimcilar
    // ------------------------------------------------------------------

    private boolean roll(double chance) {
        if (chance >= 100.0) return true;
        if (chance <= 0.0) return false;
        return random.nextDouble() * 100.0 < chance;
    }

    private boolean isDisabledWorld(String world) {
        for (String w : getConfig().getStringList("disabled-worlds")) {
            if (w != null && w.equalsIgnoreCase(world)) return true;
        }
        return false;
    }

    /** %victim_in% gibi ekli kaliplar once degistirilir (digerinin parcasi olduklari icin). */
    private String fill(String s, String victim, String killer) {
        String date;
        try {
            date = new SimpleDateFormat(getConfig().getString("date-format", "dd.MM.yyyy HH:mm"))
                    .format(new Date());
        } catch (Throwable t) {
            date = "";
        }
        return s.replace("%victim_in%", TR.genitive(victim))
                .replace("%killer_in%", TR.genitive(killer))
                .replace("%victim%", victim)
                .replace("%killer%", killer)
                .replace("%date%", date);
    }

    // ------------------------------------------------------------------
    // Komut
    // ------------------------------------------------------------------

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("headhunter.reload")) {
                sender.sendMessage(Compat.color(getConfig().getString(
                        "messages.no-permission", "&cBunun icin yetkin yok.")));
                return true;
            }
            reloadConfig();
            sender.sendMessage(Compat.color(getConfig().getString(
                    "messages.reloaded", "&aHeadHunter ayarlari yeniden yuklendi.")));
            return true;
        }
        sender.sendMessage(Compat.color("&7/" + label + " reload"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase(Locale.ENGLISH))) {
            return new ArrayList<String>(Arrays.asList("reload"));
        }
        return Collections.emptyList();
    }
}
