package me.pronil.parkour.board.type;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.pronil.parkour.Parkour;
import me.pronil.parkour.board.BoardAdapter;
import me.pronil.parkour.utils.ChatHelper;
import me.pronil.parkour.utils.Formatter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BoardType implements BoardAdapter {

    private final Parkour instance;
    private final RegionContainer container;

    public BoardType(Parkour instance) {
        this.instance = instance;
        this.container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public String getTitle(Player player) {
        return ChatHelper.t("&a&lParkour");
    }


    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));
        Location location = player.getLocation();

        if (regionManager == null) return null;

        ProtectedRegion region = regionManager.getRegion("parkour_region");

        if (region == null) return null;

        if (region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
            lines.add("&7&m---------------------");
            lines.add("&aBest Attempt: " + getBestAttempt(player.getUniqueId()));
            lines.add("");
            lines.add("&aLeaderboard:");
            lines.add("&2#1 - " + getPlayer(1) + " - Time: " + getTime(1));
            lines.add("&6#2 - " + getPlayer(2) + " - Time: " + getTime(2));
            lines.add("&8#3 - " + getPlayer(3) + " - Time: " + getTime(3));
            lines.add("&8#4 - " + getPlayer(4) + " - Time: " + getTime(4));
            lines.add("&8#5 - " + getPlayer(5) + " - Time: " + getTime(5));
            lines.add("&7&m---------------------");
        }

        return ChatHelper.t(lines);
    }

    private String getBestAttempt(UUID uuid) {
        if (!instance.getParkourManager().getFinishedParkours().containsKey(uuid)) {
            return "None";
        }

        return Formatter.getRemaining(
                instance.getParkourManager().getFinishedParkours().get(uuid).getTime(),
                true
        );
    }

    private String getTime(int index) {
        try {

            return Formatter.getRemaining(
                    instance.getParkourManager().getSorted().get(index - 1).getTime(),
                    true
            );

        } catch (IndexOutOfBoundsException e) {
            return "0s";
        }
    }

    private String getPlayer(int index) {
        try {

            return instance.getParkourManager().getSorted().get(index - 1).getPlayerName();

        } catch (IndexOutOfBoundsException e) {
            return "Empty";
        }
    }
}