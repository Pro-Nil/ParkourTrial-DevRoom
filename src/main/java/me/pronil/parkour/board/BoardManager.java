package me.pronil.parkour.board;

import lombok.Getter;
import lombok.Setter;
import me.pronil.parkour.Parkour;
import me.pronil.parkour.board.listener.BoardListener;
import me.pronil.parkour.board.thread.BoardThread;
import me.pronil.parkour.board.type.BoardType;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter

public class BoardManager {

    private Parkour instance;
    private BoardAdapter adapter;
    private BoardThread thread;

    private Map<UUID, Board> boards;
    private long ticks = 2;

    private final ChatColor[] chatColorCache = ChatColor.values();

    public BoardManager(Parkour instance) {
        this.instance = instance;

        this.adapter = new BoardType(instance);
        this.boards = new ConcurrentHashMap<>();
        this.thread = new BoardThread(this);

        instance.getServer().getPluginManager().registerEvents(new BoardListener(), instance);
    }
}