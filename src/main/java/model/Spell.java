package model;


import hibernate.Connector;

import javax.persistence.*;

@Entity
public class Spell extends Card {

    // only hibernate use this constructor
    public Spell() {}

    public Spell(String name,String description,
                int price,ClassOfCard classOfCard,
                Rarity rarity,int manaFrz){
        super(name, description,price, classOfCard, rarity, manaFrz);
    }
    @Override
    public void delete() {
        Connector connector=Connector.getConnector();
        connector.delete(this);
    }

    @Override
    public void saveOrUpdate() {
        Connector connector=Connector.getConnector();
        connector.saveOrUpdate(this.classOfCard);
        connector.saveOrUpdate(this);
    }

    @Override
    public void load() {

    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public String toString() {
        return "Spell{" +
                "classOfCard=" + classOfCard +
                ", rarity=" + rarity +
                ", manaFrz=" + manaFrz +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}