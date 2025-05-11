package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une personne morale (organisation ou groupe industriel), héritant de Entite.
 * Elle peut contenir d'autres entités comme membres.
 * @see Entite
 */
public class PersonneMorale extends Entite {
    private List<Entite> membres;

    /**
     * Constructeur de la personne morale.
     *
     * @param nom Nom de la personne morale
     */
    public PersonneMorale(String nom) {
        super(nom);
        this.membres = new ArrayList<>();
    }

    /**
     * Ajoute un membre à cette personne morale.
     *
     * @param e Entité à ajouter (personne physique ou morale)
     */
    public void ajouterMembre(Entite e) {
        membres.add(e);
    }

    /**
     * Retourne la liste des membres de cette personne morale.
     *
     * @return liste des entités membres
     */
    public List<Entite> getMembres() {
        return membres;
    }

    @Override
    public String toString() {
        return super.toString() + ", membres=" + membres;
    }
}
