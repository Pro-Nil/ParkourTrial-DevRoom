package me.pronil.parkour.board.entry;

import lombok.Setter;
import me.pronil.parkour.board.Board;
import me.pronil.parkour.utils.ChatHelper;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class BoardEntry {

    private final Board board;
    private Team team;

    @Setter
    private String text, identifier;


    public BoardEntry(Board board, String text, int position) {
        this.board = board;
        this.text = text;
        this.identifier = this.board.getUniqueIdentifier(position);

        this.setup();
    }


    public void setup() {
        final Scoreboard scoreboard = this.board.getScoreboard();

        if (scoreboard == null) {
            return;
        }

        String teamName = this.identifier;


        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        Team team = scoreboard.getTeam(teamName);


        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }


        if (!team.getEntries().contains(this.identifier)) {
            team.addEntry(this.identifier);
        }


        if (!this.board.getEntries().contains(this)) {
            this.board.getEntries().add(this);
        }

        this.team = team;
    }


    public void send(int position) {
        String[] split = ChatHelper.splitTeamText(text);
        this.team.setPrefix(split[0]);
        this.team.setSuffix(split[1]);

        this.board.getObjective().getScore(this.identifier).setScore(position);
    }


    public void remove() {
        this.board.getIdentifiers().remove(this.identifier);
        this.board.getScoreboard().resetScores(this.identifier);
    }

}
