package jota.br;

import java.util.*;

public class PokerGame {

    private int round;
    private int pot;
    private int bigBlind;
    private int smallBlind;
    private List<Player> players;
    private List<Player> inRound;
    private List<Card> tableCards;
    private Deck deck;

    private Comparator<Card> sortCards = Comparator.comparing(Card::getRank).thenComparing(Card::getSuit);

    public PokerGame(int bigBlind, int smallBlind) {
        this.round = 0;
        this.pot = 0;
        this.bigBlind = bigBlind;
        this.smallBlind = smallBlind;
        this.players = new ArrayList<>(23);
        this.inRound = new ArrayList<>(23);
        this.tableCards = new ArrayList<>(5);
        this.deck = new Deck();
    }

    public void addPlayer(int players, int cpus) {

        for (int i = 0; i < players; i++) {
            this.players.add(new Player("PLAYER" + i, 10000, 0, Play.NONE, "PLAYER"));
        }

        for (int i = 0; i < cpus; i++) {
            this.players.add(new Player("CPU" + i, 10000, 0, Play.NONE, "CPU"));
        }
    }

    public void resetInRoundList() {
        this.inRound.clear();
        for (Player p : this.getPlayers()) {
            this.getInRound().add(p);
        }
    }

    public void payBlinds() {

        System.out.println("-".repeat(50));
        System.out.printf("BETS AND SKIPS%n");

        for (Player p : this.players) {

            if ((this.inRound.get(1).equals(p) || this.inRound.getFirst().equals(p)) && this.round == 0) {

                int value = (this.inRound.getFirst().equals(p)) ? this.getBigBlind() : this.getSmallBlind();
                chargeBlind(p, value);
                messages("BET", p);
                continue;
            }

            if (p.getType().equals("CPU")) {

                if (this.getInRound().size() == 1) {
                    System.out.println("-".repeat(50));
                    return;
                }

                if (cpuBet(p)) {

                    chargeBlind(p, this.getBigBlind());
                    messages("BET", p);
                } else {

                    messages("SKIP", p);
                    this.inRound.remove(p);
                }
            } else {
                
                Scanner scanner = new Scanner(System.in);
                while (true) {

                    printDeck(this.getTableCards());
                    System.out.print(p);
                    messages("PLAYER_BET", p);
                    String response = scanner.nextLine();
                    if (response.equalsIgnoreCase("Y")) {
                        
                        chargeBlind(p, this.getBigBlind());
                        messages("BET", p);
                        break;
                    } else {
                        
                        this.inRound.remove(p);
                        messages("SKIP", p);
                        break;
                    }
                }
            }
        }

        if (this.round == 0) {

            if (this.players.get(1).getType().equals("CPU")) {
                chargeBlind(this.players.get(1), this.getSmallBlind());
            } else {
                chargeBlind(this.players.get(1), this.getSmallBlind());
            }

            Collections.rotate(this.players, -1);
        }

        System.out.println("-".repeat(50));
    }
    
    public void chargeBlind(Player player, int value) {
        player.setChips(player.getChips() - this.getBigBlind());
        this.pot += value;
    }

    public void messages(String msgCode, Player p) {
        switch (msgCode.toUpperCase()) {
            case "SKIP" -> System.out.print("SKIPPED THE ROUND --> " + p);
            case "BET" -> System.out.print("PLACED A BET --> " + p);
            case "PLAYER_BET" -> System.out.print("WRITE Y TO PAY BLIND OF (" + this.bigBlind + ") OR ANY CHARACTER TO SKIP THE ROUND: ");
        }
    }

    public boolean cpuBet(Player cpu) {

        Random random = new Random();
        int bound = random.nextInt(25);

        if (this.getRound() == 0) {

            if (cpu.getScore() < 6 && cpu.getPlay().equals(Play.NONE)) {
                return bound > 18;
            } else if (cpu.getScore() < 9 && cpu.getPlay().equals(Play.NONE)) {
                return bound > 15;
            } else {
                return bound > 6;
            }

        } else {

            if (cpu.getPlay().equals(Play.NONE)) {
                return false;
            } else {
                if (cpu.getPlay().ordinal() == 0 && cpu.getScore() < 9) {
                    return false;
                } else if (cpu.getPlay().ordinal() > 1) {
                    return bound > 15;
                } else if (cpu.getPlay().ordinal() > 2) {
                    return bound > 10;
                } else if (cpu.getPlay().ordinal() > 3) {
                    return bound > 5;
                } else {
                    return true;
                }
            }
        }
    }

    public void dealCardsToPlayers() {

        this.getPlayers().forEach(p -> p.getCards().clear());

        for (int i = 0; i < 2; i++) {
            this.players.forEach(p -> {
                p.getCards().add(this.deck.getDeck().getFirst());
                this.deck.getDeck().removeFirst();
            });
        }
    }

    public void dealTableCards(List<Card> deck) {

        switch (this.round) {
            case 1 -> {
                this.tableCards.addAll(List.of(deck.getFirst(), deck.get(1), deck.get(2)));
                deck.removeAll(List.of(deck.getFirst(), deck.get(1), deck.get(2)));
            }
            case 2, 3 -> {
                this.tableCards.add(deck.getFirst());
                deck.removeFirst();
            }
        }
    }

    public void evalHand() {

        this.tableCards.sort(this.sortCards);
        this.players.forEach(p -> {
            p.getCards().sort(this.sortCards);

            Combinations combinations = new Combinations();

            this.tableCards.forEach(c -> {
                combinations.getRanks().add(c.getRank());
                combinations.getSuits().add(c.getSuit());
            });
            p.getCards().forEach(c -> {
                combinations.getRanks().add(c.getRank());
                combinations.getSuits().add(c.getSuit());
            });

            p.getCards().forEach(c -> {
                if (c.getRank() == 12) {
                    combinations.setHasAce(true);
                }
                if (combinations.getDuplicatedFaces().get(c.getRank()) == 0) {
                    combinations.getDuplicatedFaces().set(c.getRank(), Collections.frequency(combinations.getRanks(), c.getRank()));
                }

                if (!combinations.hasFiveSuits() && Collections.frequency(combinations.getSuits(), c.getSuit()) == 5) {
                    combinations.setHasFiveSuits(true);
                }
            });

            combinations.getRanks().sort(Comparator.naturalOrder());
            for (int i = 0; i < combinations.getRanks().size() - 1; i++) {
                if (combinations.getRanks().get(i) + 1 == combinations.getRanks().get(i + 1)) {
                    combinations.setHasSequence(true);
                } else {
                    combinations.setHasSequence(false);
                    break;
                }
            }

            if ((combinations.hasSequence() && combinations.hasFiveSuits()) || (combinations.hasSequence() && combinations.hasFiveSuits())) {
                if (combinations.hasAce()) {
                    p.setPlay(Play.ROYAL_FLUSH);
                } else {
                    p.setPlay(Play.STRAIGHT_FLUSH);
                }
            } else if (combinations.getDuplicatedFaces().contains(4)) {
                p.setPlay(Play.FOUR_OF_A_KIND);
            } else if (combinations.getDuplicatedFaces().contains(3) && combinations.getDuplicatedFaces().contains(2)) {
                p.setPlay(Play.FULL_HOUSE);
            } else if (combinations.hasFiveSuits()) {
                p.setPlay(Play.FLUSH);
            } else if (combinations.hasSequence()) {
                p.setPlay(Play.STRAIGHT);
            } else if (combinations.getDuplicatedFaces().contains(3)) {
                p.setPlay((Play.THREE_OF_A_KIND));
            } else if (Collections.frequency(combinations.getDuplicatedFaces(), 2) == 2) {
                p.setPlay(Play.TWO_PAIRS);
            } else if (combinations.getDuplicatedFaces().contains(2)) {
                p.setPlay(Play.ONE_PAIR);
            } else {
                p.setPlay(Play.HIGH_CARD);
            }

            p.setScore(p.getCards().getFirst().getRank() + p.getCards().getLast().getRank());
        });
    }

    public List<Player> getWinner() {

        List<Player> winner = new ArrayList<>(1);
        for (Player p : this.getInRound()) {

            if (winner.isEmpty()) {
                winner.add(p);
                continue;
            }

            if (p.getPlay().ordinal() > winner.getFirst().getPlay().ordinal()) {

                winner.set(0, p);
            } else if (p.getPlay().ordinal() == winner.getFirst().getPlay().ordinal()) {

                if (p.getCards().getLast().getRank() > winner.getFirst().getCards().getLast().getRank()) {

                    winner.set(0, p);
                } else if (p.getCards().getLast().getRank() == winner.getFirst().getCards().getLast().getRank()) {

                    if (p.getCards().getFirst().getRank() > winner.getFirst().getCards().getFirst().getRank()) {
                        winner.set(0, p);
                    } else if (p.getCards().getFirst().getRank() == winner.getFirst().getCards().getFirst().getRank()) {
                        winner.add(p);
                    }
                }
            }
        }
        if (winner.size() == 1) {
            winner.getFirst().setChips(this.getPot() + winner.getFirst().getChips());
        } else {
            int potValue = this.getPot() / winner.size();
            winner.forEach(p -> p.setChips(potValue + p.getChips()));
        }
        this.pot = 0;
        winner.forEach(p -> System.out.println("WINNER IS ---> " + p));
        return winner;
    }

    public void startGame() {

        Random random = new Random();
        this.addPlayer(0, 23);

        long startTime = 0;
        long endTime = 0;

        while (round <= 5) {
            switch(this.getRound()) {
                case 0 -> {

                    this.getPlayers().removeIf(p -> p.getChips() < this.getBigBlind());
                    this.deck.getStandardDeck();
                    Collections.shuffle(this.deck.getDeck());
                    Collections.rotate(this.deck.getDeck(), random.nextInt(20, 30));

                    this.resetInRoundList();
                    this.dealCardsToPlayers();

                    this.evalHand();
                    this.payBlinds();

                    this.printPlayers();
                    this.round++;
                }
                case 1, 2, 3 -> {

                    endTime = System.currentTimeMillis();
                    if (endTime - startTime <= 5000) {
                        continue;
                    }
                    startTime = System.currentTimeMillis();
                    this.deck.getStandardDeck();
                    Collections.shuffle(this.deck.getDeck());
                    Collections.rotate(this.deck.getDeck(), random.nextInt(20, 30));

                    this.dealTableCards(this.deck.getDeck());

                    this.evalHand();
                    this.printPlayers();
                    this.printDeck(this.getTableCards());
                    this.round++;
                }
                case 4 -> {

                    startTime = System.currentTimeMillis();
                    this.getWinner();
                    this.getTableCards().clear();
                    this.round++;
                }
                case 5 -> {

                    endTime = System.currentTimeMillis();
                    if (endTime - startTime >= 5000) {
                        if (this.getPlayers().size() > 1) {
                            this.round = 0;
                        } else {
                            this.round++;
                        }
                        startTime = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    public void printDeck(List<Card> deck) {
        System.out.println("-".repeat(50));
        System.out.printf("ROUND (%d)%n", this.getRound());
        System.out.println(deck);
        System.out.println("-".repeat(50));
    }

    public void printPlayers() {

        System.out.println("PLAYERS IN THIS ROUND");
        this.inRound.forEach(System.out::print);
    }

    public int getRound() {
        return round;
    }

    public int getPot() {
        return pot;
    }

    public int getBigBlind() {
        return bigBlind;
    }

    public int getSmallBlind() {
        return smallBlind;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getInRound() {
        return inRound;
    }

    public List<Card> getTableCards() {
        return tableCards;
    }
}
