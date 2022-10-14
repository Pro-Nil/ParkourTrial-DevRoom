package me.pronil.parkour.utils;

import lombok.Getter;
import lombok.Setter;
import me.pronil.parkour.Parkour;
import me.pronil.parkour.listener.ParkourListener;
import me.pronil.parkour.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class ParkourManager {

    private final Parkour instance;
    private final Map<UUID, ParkourStorer> finishedParkours;
    private final List<Location> checkpoints;

    private Location start;
    private Location end;
    private int checkpoint;
    private List<ParkourStorer> sorted;

    public ParkourManager(Parkour instance) {
        this.instance = instance;

        this.finishedParkours = new HashMap<>();
        this.checkpoints = new ArrayList<>();
        this.sorted = new ArrayList<>();

        this.load();
        this.sort();
        this.registerCommand();

        this.checkpoint=checkpoints.size();
        this.start = checkpoints.remove(0);
        this.end = checkpoints.remove(checkpoints.size() - 1);

        instance.getServer().getPluginManager().registerEvents(new ParkourListener(this), instance);
    }


    private void registerCommand() {
        try {

            Server bukkit = Bukkit.getServer();
            Method method = bukkit.getClass().getMethod("getCommandMap");
            ((CommandMap) method.invoke(bukkit)).register("parkour", new Command(this));

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void sort() {
        new BukkitRunnable() {
            @Override
            public void run() {
                setSorted(new ArrayList<>(finishedParkours.values())
                        .stream()
                        .sorted(Comparator.comparingLong(ParkourStorer::getTime))
                        .collect(Collectors.toList()));
            }
        }.runTaskTimer(getInstance(), 0L, 20 * 30);
    }
    

    @SuppressWarnings("unchecked")
    private void load() {

        List<Map<String, Object>> maps = (List<Map<String, Object>>) instance.getJsonFile().getValues().get("checkpointsData");

        for (Map<String, Object> map : maps) {
            checkpoints.add(new Location(
                    Bukkit.getWorld((String) map.get("worldName")),
                    (double) map.get("x"),
                    (double) map.get("y"),
                    (double) map.get("z")
            ));
        }
    }
}