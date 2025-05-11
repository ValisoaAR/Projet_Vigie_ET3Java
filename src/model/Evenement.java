package model;

import java.time.LocalDate;

/**
 * Représente un événement survenu dans l'écosystème : rachat, publication, alerte, etc.
 * Chaque événement concerne une cible (un média ou une entité) et contient un contenu descriptif.
 *
 * @see Entite
 * @see Media
 */
public class Evenement {
    private String type;
    private LocalDate date;
    private Object cible;
    private String contenu;

    /**
     * Construit un nouvel événement.
     *
     * @param type     Type de l'événement (ex: "rachat", "publication", etc.)
     * @param date     Date de l'événement
     * @param cible    Cible concernée (Media ou Entite)
     * @param contenu  Description ou contenu de l'événement
     */
    public Evenement(String type, LocalDate date, Object cible, String contenu) {
        this.type = type;
        this.date = date;
        this.cible = cible;
        this.contenu = contenu;
    }

    /**
     * @return le type de l'événement
     */
    public String getType() {
        return type;
    }

    /**
     * @return la date de l'événement
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @return la cible de l'événement (Media ou Entite)
     */
    public Object getCible() {
        return cible;
    }

    /**
     * @return le contenu ou la description de l'événement
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * Définit la cible de l’événement.
     *
     * @param cible entité concernée
     */
    public void setCible(Entite cible) {
        this.cible = cible;
    }


    @Override
    public String toString() {
        return "Evenement{type='" + type + "', date=" + date + ", cible=" + cible + ", contenu='" + contenu + "'}";
    }
}
