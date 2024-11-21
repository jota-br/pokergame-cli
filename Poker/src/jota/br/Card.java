package jota.br;

public class Card {

    private final String face;
    private final Suit suit;
    private final int rank;

    public Card(String face, Suit suit, int rank) {
        this.face = face;
        this.suit = suit;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "%s%c(%d)".formatted(face, suit.getImage(), rank);
    }

    public String getFace() {
        return face;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }
}
