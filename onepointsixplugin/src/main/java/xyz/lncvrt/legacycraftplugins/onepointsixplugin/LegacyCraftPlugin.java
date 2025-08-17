package xyz.lncvrt.legacycraftplugins.onepointsixplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;
import xyz.lncvrt.legacycraftplugins.onepointsixplugin.commands.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class LegacyCraftPlugin extends JavaPlugin implements Listener {
    private final Map<UUID, Location> lastPos = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastTime = new ConcurrentHashMap<>();
    private static final double MAX_SPEED = 75.0;

    @Override
    public void onEnable() {
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("rules").setExecutor(new RulesCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    int players = Arrays.stream(getServer().getOnlinePlayers()).toArray().length;
                    int ping = ((CraftPlayer) player).getHandle().ping;
                    String playersString = ChatColor.GOLD + "Players: " + players;
                    String pingString = ChatColor.GOLD + "Ping: " + ping + "ms";
                    if (playersString.length() > 16) {
                        playersString = playersString.substring(0, 16);
                    }
                    if (pingString.length() > 16) {
                        pingString = pingString.substring(0, 16);
                    }

                    ScoreboardManager manager = getServer().getScoreboardManager();
                    Scoreboard board = manager.getNewScoreboard();

                    Objective objective = board.registerNewObjective(ChatColor.GOLD + "LegacyCraft", "customsb");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                    Score score1 = objective.getScore(getServer().getOfflinePlayer(playersString));
                    score1.setScore(1);
                    Score score2 = objective.getScore(getServer().getOfflinePlayer(pingString));
                    score2.setScore(0);

                    player.setScoreboard(board);
                }
            }
        }, 0L, 20L);
    }

    @EventHandler
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            player.setScoreboard(getServer().getScoreboardManager().getNewScoreboard());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        Location to = e.getTo();
        lastPos.put(id, to);
        lastTime.put(id, System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        Location to = e.getTo();
        Location from = e.getFrom();

        UUID id = e.getPlayer().getUniqueId();
        long now = System.currentTimeMillis();

        if (!to.getWorld().equals(from.getWorld())) {
            lastPos.put(id, to);
            lastTime.put(id, now);
            return;
        }

        Location lastLocation = lastPos.get(id);
        Long lastTimestamp = lastTime.get(id);
        if (lastLocation == null || lastTimestamp == null || !lastLocation.getWorld().equals(to.getWorld())) {
            lastPos.put(id, to);
            lastTime.put(id, now);
            return;
        }

        double dist = lastLocation.distance(to);
        double elapsed = (now - lastTimestamp) / 1000.0;
        if (elapsed <= 0.05) elapsed = 0.05;

        double speed = dist / elapsed;

        if (speed > MAX_SPEED) {
            e.setCancelled(true);
            e.getPlayer().teleport(lastLocation);
            e.getPlayer().sendMessage(ChatColor.RED + "You are going " + (int) speed + "BPS! Please slow down and go below " + (int) MAX_SPEED + " BPS.");
            return;
        }

        lastPos.put(id, to);
        lastTime.put(id, now);
    }
}
