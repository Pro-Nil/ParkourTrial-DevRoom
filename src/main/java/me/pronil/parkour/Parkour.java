package me.pronil.parkour;

import lombok.Getter;
import me.pronil.parkour.board.BoardManager;
import me.pronil.parkour.utils.ParkourManager;
import me.pronil.parkour.database.DatabaseManager;
import me.pronil.parkour.utils.JsonFile;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Parkour extends JavaPlugin {

    @Getter
    private static Parkour instance;

    private JsonFile jsonFile;
    private BoardManager boardManager;
    private ParkourManager parkourManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        this.jsonFile = new JsonFile(this, getDataFolder(), "locations.json");
        this.boardManager = new BoardManager(this);
        this.parkourManager = new ParkourManager(this);
        this.databaseManager = new DatabaseManager(this);
    }

    @Override
    public void onDisable() {
        parkourManager.getFinishedParkours().forEach((uuid, object) -> databaseManager.save(object));
    }
}