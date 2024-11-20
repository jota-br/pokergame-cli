package jota.br;

import java.util.ArrayList;
import java.util.List;

public class Combinations {
    private List<Integer> ranks = new ArrayList<>(13);
    private List<Suit> suits = new ArrayList<>(4);
    private List<Integer> duplicatedFaces = new ArrayList<>(List.of(0,0,0,0,0,0,0,0,0,0,0,0,0));
    private boolean hasFiveSuits = false;
    private boolean hasSequence = false;
    private boolean hasAce = false;

    public List<Integer> getRanks() {
        return ranks;
    }

    public List<Suit> getSuits() {
        return suits;
    }

    public List<Integer> getDuplicatedFaces() {
        return duplicatedFaces;
    }

    public boolean hasFiveSuits() {
        return hasFiveSuits;
    }

    public void setHasFiveSuits(boolean hasFiveSuits) {
        this.hasFiveSuits = hasFiveSuits;
    }

    public boolean hasSequence() {
        return hasSequence;
    }

    public void setHasSequence(boolean hasSequence) {
        this.hasSequence = hasSequence;
    }

    public boolean hasAce() {
        return hasAce;
    }

    public void setHasAce(boolean hasAce) {
        this.hasAce = hasAce;
    }
}
