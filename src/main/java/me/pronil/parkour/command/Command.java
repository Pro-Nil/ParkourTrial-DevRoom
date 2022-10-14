package me.pronil.parkour.command;

import me.pronil.parkour.utils.ParkourManager;
import me.pronil.parkour.utils.ChatHelper;
import me.pronil.parkour.utils.JsonFile;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Command extends BukkitCommand {

    private final ParkourManager manager;

    public Command(ParkourManager manager) {
        super("parkour");
        this.manager = manager;
        this.setPermission("trial.parkour");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatHelper.t("&cYou can only run this command as a player!"));
            return true;
        }

        if (!sender.hasPermission("trial.parkour")) {
            sender.sendMessage(ChatHelper.t("Insufficient Permission."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatHelper.t("&7&m------------------"));
            sender.sendMessage(ChatHelper.t("&9&lParkour Command"));
            sender.sendMessage(ChatHelper.t("&7- /parkour setstart &8- &fSet to current location."));
            sender.sendMessage(ChatHelper.t("&7- /parkour setend &8- &fSet to current location."));
            sender.sendMessage(ChatHelper.t("&7- /parkour clearcheckpoints &8- &fClear checkpoints"));
            sender.sendMessage(ChatHelper.t("&7- /parkour createcheckpoint &8- &fCreate checkpoints"));
            sender.sendMessage(ChatHelper.t("&7&m------------------"));
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();
        JsonFile file = manager.getInstance().getJsonFile();
        List<Map<String, Object>> toSet = new ArrayList<>();

        switch (args[0].toLowerCase()) {
            case "setstart":
                toSet.add(serialize(location));
                toSet.addAll(manager.getCheckpoints().stream().map(this::serialize).collect(Collectors.toList()));
                toSet.add(serialize(manager.getEnd()));


                file.getValues().replace("checkpointsData", toSet);


                manager.getInstance().getJsonFile().save();
                manager.setStart(location);

                player.sendMessage(ChatHelper.t("&aUpdated the start of parkour."));
                return true;

            case "setend":
                toSet.add(serialize(manager.getStart()));
                toSet.addAll(manager.getCheckpoints().stream().map(this::serialize).collect(Collectors.toList()));
                toSet.add(serialize(location));


                file.getValues().replace("checkpointsData", toSet);

                manager.getInstance().getJsonFile().save();
                manager.setEnd(location);

                player.sendMessage(ChatHelper.t("&aUpdated the end of parkour."));
                return true;

            case "clearcheckpoints":
                toSet.add(serialize(manager.getStart()));
                toSet.add(serialize(manager.getEnd()));


                file.getValues().replace("checkpointsData", toSet);


                manager.getInstance().getJsonFile().save();
                manager.getCheckpoints().clear();

                player.sendMessage(ChatHelper.t("&aCleared all checkpoints."));
                return true;

            case "createcheckpoint":
                toSet.add(serialize(manager.getStart()));
                toSet.addAll(manager.getCheckpoints().stream().map(this::serialize).collect(Collectors.toList()));
                toSet.add(serialize(location));
                toSet.add(serialize(manager.getEnd()));


                file.getValues().replace("checkpointsData", toSet);


                manager.getInstance().getJsonFile().save();
                manager.getCheckpoints().add(location);

                player.sendMessage(ChatHelper.t("&aCreated a check point."));
                return true;
        }

        sender.sendMessage(ChatHelper.t("&7&m------------------"));
        sender.sendMessage(ChatHelper.t("&9&lParkour Command"));
        sender.sendMessage(ChatHelper.t("&7- /parkour setstart &8- &fSet to current location."));
        sender.sendMessage(ChatHelper.t("&7- /parkour setend &8- &fSet to current location."));
        sender.sendMessage(ChatHelper.t("&7- /parkour clearcheckpoints &8- &fClear checkpoints"));
        sender.sendMessage(ChatHelper.t("&7- /parkour createcheckpoint &8- &fCreate checkpoints"));
        sender.sendMessage(ChatHelper.t("&7&m------------------"));
        return false;
    }

    private Map<String, Object> serialize(Location location) {
        Map<String, Object> map = new HashMap<>();

        map.put("worldName", location.getWorld().getName());
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());

        return map;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Arrays.asList("setstart", "setend", "createcheckpoint", "clearcheckpoints");
        }

        return super.tabComplete(sender, alias, args);
    }
}