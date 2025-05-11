package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une personne physique dans le système.
 */
public class PersonnePhysique extends Entite {
    /**
     * Liste des participations de la personne dans différents médias.
     */
    private final List<Participation> participations = new ArrayList<>();

    /**
     * Constructeur pour créer une personne physique.
     *
     * @param nom Nom de la personne
     */
    public PersonnePhysique(String nom) {
        super(nom);
    }

    /**
     * Ajoute une participation à la liste des participations de la personne.
     *
     * @param participation La participation à ajouter
     */
    public void ajouterParticipation(Participation participation) {
        participations.add(participation);
    }

    /**
     * Vérifie si la personne possède des parts dans un média donné.
     *
     * @param media Le média à vérifier
     * @return true si la personne possède des parts dans le média, false sinon
     */
    public boolean possedeMedia(Media media) {
        for (Participation participation : participations) {
            if (participation.getMedia().equals(media)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne la liste des participations de la personne.
     *
     * @return La liste des participations
     */
    public List<Participation> getParticipations() {
        return participations;
    }
}
