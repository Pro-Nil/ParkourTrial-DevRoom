package me.pronil.parkour.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Setter
public class ParkourCounter {

    private UUID player;
    private String playerName;
    private Location checkpoint;
    private long start;

    public ParkourCounter(UUID player, String playerName) {
        this.player = player;
        this.playerName = playerName;
        this.checkpoint = null;
        this.start = System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - start;
    }
}