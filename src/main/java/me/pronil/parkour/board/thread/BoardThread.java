package me.pronil.parkour.board.thread;

import me.pronil.parkour.board.Board;
import me.pronil.parkour.board.BoardManager;
import me.pronil.parkour.board.entry.BoardEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
public class BoardThread extends Thread {

    private final BoardManager boardManager;


    public BoardThread(BoardManager boardManager) {
        super("Azurite - BoardThread");
        this.boardManager = boardManager;
        this.start();
    }

    @Override
    public void run() {
        while (true) {

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                try {

                    tick(player);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {

                Thread.sleep(boardManager.getTicks() * 50);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void tick(Player player) {
        Board board = this.boardManager.getBoards().get(player.getUniqueId());

        if (board == null) return;

        Scoreboard scoreboard = board.getScoreboard();
        Objective objective = board.getObjective();

        if (scoreboard == null || objective == null) return;

        List<String> newLines = this.boardManager.getAdapter().getLines(player);

        if (newLines == null || newLines.isEmpty()) {
            board.getEntries().forEach(BoardEntry::remove);
            board.getEntries().clear();
        } else {
            if (newLines.size() > 15) {
                newLines = newLines.subList(0, 15);
            }

            Collections.reverse(newLines);

            if (board.getEntries().size() > newLines.size()) {
                for (int i = newLines.size(); i < board.getEntries().size(); i++) {
                    BoardEntry entry = board.getEntryAtPosition(i);

                    if (entry != null) {
                        entry.remove();
                    }
                }
            }

            int cache = 1;
            for (int i = 0; i < newLines.size(); i++) {
                BoardEntry entry = board.getEntryAtPosition(i);


                String line = ChatColor.translateAlternateColorCodes('&', newLines.get(i));


                if (entry == null) {
                    entry = new BoardEntry(board, line, i);
                }


                entry.setText(line);
                entry.setup();
                entry.send(cache++);
            }
        }

        if (player.getScoreboard() != scoreboard) {
            player.setScoreboard(scoreboard);
        }
    }
}