package xyz.lncvrt.speedlimitplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SpeedLimitPlugin extends JavaPlugin implements Listener {
    private final Map<UUID, Location> lastPos = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastTime = new ConcurrentHashMap<>();
    private static final double MAX_SPEED = 75.0;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
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
