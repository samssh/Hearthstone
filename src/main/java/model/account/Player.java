package model.account;

import hibernate.SaveAble;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import model.main.Card;
import model.main.CardDetails;
import model.main.Hero;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@ToString
public class Player implements SaveAble {
    @Id
    @Setter
    @Getter
    private String userName;
    @Column
    @Setter
    @Getter
    private String password;
    @Column
    @Setter
    @Getter
    private long creatTime;
    @Column
    @Setter
    @Getter
    private int coin;
    @Column
    @Getter
    @Setter
    private int selectedDeckIndex;
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @Setter
    @Getter
    @JoinTable(name = "Player_Card")
    private Map<Card, CardDetails> cards;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @Setter
    @Getter
    @JoinTable(name = "Player_Hero")
    private List<Hero> heroes;
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
    @Setter
    @Getter
    private List<Deck> decks;

    {
        cards = new HashMap<>();
        heroes = new ArrayList<>();
        decks = new ArrayList<>();
//        gameHistories = new ArrayList<>();
    }

    public Player() {
    }

    public Player(String userName, String password, long creatTime,
                  int coin, int selectedDeckIndex,
                  Map<Card, CardDetails> cards, List<Hero> heroes, List<Deck> decks) {
        this.userName = userName;
        this.password = password;
        this.creatTime = creatTime;
        this.coin = coin;
        this.selectedDeckIndex = selectedDeckIndex;
        this.cards = cards;
        this.heroes = heroes;
        this.decks = decks;
    }

    public void addCard(Card card) {
        if (cards.containsKey(card)) cards.get(card).vRepeatedTimes(1);
        else cards.put(card, new CardDetails(1));
    }

    public void removeCard(Card card) {
        cards.get(card).vRepeatedTimes(-1);
        if (cards.get(card).getRepeatedTimes() == 0) cards.remove(card);
        for (Deck deck : decks) deck.removeCard(card);
    }

    public int numberOfCard(Card card) {
        if (cards.containsKey(card))
            return cards.get(card).getRepeatedTimes();
        return 0;
    }

    public Deck getSelectedDeck() {
        return decks.size() > selectedDeckIndex ? decks.get(selectedDeckIndex) : null;
    }

    @PostLoad
    void postLoad() {
        this.cards = new HashMap<>(this.cards);
        this.decks = new ArrayList<>(this.decks);
        this.heroes = new ArrayList<>(this.heroes);
    }
}