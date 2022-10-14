package me.pronil.parkour.listener;

import me.pronil.parkour.utils.ParkourManager;
import me.pronil.parkour.utils.ParkourCounter;
import me.pronil.parkour.utils.ParkourStorer;
import me.pronil.parkour.utils.ChatHelper;
import me.pronil.parkour.utils.Formatter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParkourListener implements Listener {

    private final Map<UUID, ParkourCounter> map;
    private final ParkourManager manager;
    public ParkourListener(ParkourManager manager) {
        this.map = new HashMap<>();
        this.manager = manager;
    }

    @EventHandler
    public void onWater(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();

        if (to == null) return;
        if (!map.containsKey(player.getUniqueId())) return;
        if (to.getBlock().getType() != Material.WATER) return;

        ParkourCounter object = map.get(player.getUniqueId());
        player.sendMessage(ChatHelper.t("&aTeleported back to checkpoint!"));

        float pitch = player.getLocation().getPitch();
        float yaw = player.getLocation().getYaw();


        if (object.getCheckpoint() == null) {
            player.teleport(manager.getStart());

        } else {
            player.teleport(object.getCheckpoint());
        }


        player.setFallDistance(0.0F);
        player.getLocation().setYaw(yaw);
        player.getLocation().setPitch(pitch);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        map.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDie(PlayerDeathEvent e) {
        map.remove(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();
        boolean newRecord = false;

        if (to == null) return;

        if (!map.containsKey(player.getUniqueId())) {
            if (isSimilar(to, manager.getStart())) {
                map.put(player.getUniqueId(), new ParkourCounter(player.getUniqueId(), player.getName()));
                player.sendMessage(ChatHelper.t("&aStarted parkour!"));
            }
            return;
        }

        for (Location checkpoint : manager.getCheckpoints()) {
            if (isSimilar(to, checkpoint)) {
                ParkourCounter object = map.get(player.getUniqueId());
                object.setCheckpoint(checkpoint);
            }
        }

        if (isSimilar(to, manager.getEnd())) {

            ParkourStorer storeObject = new ParkourStorer(
                    player.getUniqueId(),
                    player.getName(),
                    map.get(player.getUniqueId()).getTime()
            );

            map.remove(player.getUniqueId());

            if (manager.getFinishedParkours().containsKey(player.getUniqueId())) {
                ParkourStorer old = manager.getFinishedParkours().get(player.getUniqueId());

                if (old.getTime() < storeObject.getTime()) {
                    player.sendMessage(ChatHelper.t("&eCongratulations! You finished in &a" + Formatter
                            .getRemaining(storeObject.getTime(), true)
                            + " &ebut it didn't count due to you having a lower time before."));
                    return;

                } else {
                    newRecord = true;
                }
            }

            manager.getFinishedParkours().put(player.getUniqueId(), storeObject);
            manager.getInstance().getDatabaseManager().save(storeObject);

            player.sendMessage(ChatHelper.t("&eCongratulations! You finished in &a" + Formatter.getRemaining(
                    storeObject.getTime(), true)
                    + "&e. " + (newRecord ? "(New record)" : ""))
            );
        }
    }


    private boolean isSimilar(Location location, Location location1) {
        return location.getWorld() == location1.getWorld() &&

                Math.abs(location.getBlockX()) == Math.abs(location1.getBlockX()) &&
                Math.abs(location.getBlockY()) == Math.abs(location1.getBlockY()) &&
                Math.abs(location.getBlockZ()) == Math.abs(location1.getBlockZ());
    }
}