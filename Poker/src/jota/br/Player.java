package jota.br;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    private int chips;
    private int score;
    private Play play;
    private List<Card> cards;
    private String type;

    public Player(String name, int chips, int score, Play play, String type) {
        this.name = name;
        this.chips = chips;
        this.score = score;
        this.play = play;
        this.cards = new ArrayList<>(2);
        this.type = type;
    }

    public void setChips(int value) {
        this.chips = value;
    }

    public void setScore(int value) {
        this.score = value;
    }

    public void setPlay(Play play) {
        this.play = play;
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public int getScore() {
        return score;
    }

    public Play getPlay() {
        return play;
    }

    public List<Card> getCards() {
        return cards;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "%s CHIPS:(%d) SCORE:(%d) PLAY:(%s) [%s] -> %s%n".formatted(name, chips, score, play, type, cards);
    }
}
