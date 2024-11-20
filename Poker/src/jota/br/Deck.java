package jota.br;

import java.util.ArrayList;
import java.util.List;

public class Deck {

    private List<Card> deck;

    public Deck() {
        deck = new ArrayList<>(52);
    }

    private Card getNumericCard(Suit suit, int cardNumber) {

        return new Card(String.valueOf(cardNumber), suit, cardNumber - 2);
    }

    private Card getFaceCard(Suit suit, char face) {

        String faces = "JQKA";
        int faceIndex = faces.indexOf(face);
        return new Card(String.valueOf(faces.charAt(faceIndex)), suit, faceIndex + 9);
    }

    public void getStandardDeck() {

        this.deck.clear();
        for (Suit s : Suit.values()) {
            for (int i = 2; i <= 10; i++) {
                this.deck.add(getNumericCard(s, i));
            }
            for (int i = 0; i < 4; i++) {
                this.deck.add(getFaceCard(s, "JQKA".charAt(i)));
            }
        }
    }

    public void printDeck(int rows) {

        int cardPerRow = this.deck.size() / rows;
        for (int i = 0; i < rows; i++) {

            int startIndex = i * cardPerRow;
            int endIndex = startIndex + cardPerRow;

            this.deck.subList(startIndex, endIndex).forEach(c -> System.out.print(c + " "));
            System.out.println();
        }
    }

    public List<Card> getDeck() {
        return this.deck;
    }
}
