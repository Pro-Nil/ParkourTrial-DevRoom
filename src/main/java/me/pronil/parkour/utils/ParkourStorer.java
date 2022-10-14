package me.pronil.parkour.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter

public class ParkourStorer {

    private UUID player;
    private String playerName;
    private long time;

    public ParkourStorer(UUID player, String playerName, long time) {
        this.player = player;
        this.playerName = playerName;
        this.time = time;
    }
}