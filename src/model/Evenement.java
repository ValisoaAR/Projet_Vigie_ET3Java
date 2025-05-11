package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Représente un événement survenu dans l'écosystème : rachat, publication, alerte, etc.
 * Chaque événement concerne une cible principale (un média ou une entité) et peut mentionner plusieurs entités.
 */
public class Evenement {
    private String type;
    private LocalDate date;
    private Entite cible;
    private Object source;
    private String description;
    private String contenu;
    private String typePublication;
    private List<Entite> mentions;
    private Entite acheteur;
    private Entite vendeur;
    private double pourcentage;

    /**
     * Constructeur pour un événement de type "publication".
     *
     * @param date            Date de l'événement
     * @param source          Source de l'événement (Media ou Entite)
     * @param contenu         Contenu de l'événement
     * @param typePublication Type spécifique de la publication (article, reportage, interview)
     * @param mentions        Liste des entités mentionnées dans l'événement
     */
    public Evenement(LocalDate date, Object source, String contenu, String typePublication, List<Entite> mentions) {
        this.type = "publication";
        this.date = date;
        this.source = source;
        this.contenu = contenu;
        this.typePublication = typePublication;
        this.mentions = mentions;
    }

    /**
     * Constructeur pour un événement de type "rachat".
     *
     * @param date        Date de l'événement
     * @param source      Source de l'événement (Media ou Entite)
     * @param acheteur    Entité acheteuse
     * @param vendeur     Entité vendeuse
     * @param cible       Cible principale concernée (Media ou Entite)
     * @param description Description ou résumé de l'événement
     * @param pourcentage Pourcentage des parts transférées
     */
    public Evenement(LocalDate date, Entite acheteur, Entite vendeur, Entite cible, String description, double pourcentage) {
        this.type = "rachat";
        this.date = date;
        this.acheteur = acheteur;
        this.vendeur = vendeur;
        this.cible = cible;
        this.description = description;
        this.pourcentage = pourcentage;
    }

    // Getters

    public String getType() {
        return type;
    }

    public LocalDate getDate() {
        return date;
    }

    public Object getCible() {
        return cible;
    }

    public Object getSource() {
        return source;
    }

    public String getDescription() {
        return description;
    }

    public String getContenu() {
        return contenu;
    }

    public String getTypePublication() {
        return typePublication;
    }

    public List<Entite> getMentions() {
        return mentions;
    }

    public Entite getAcheteur() {
        return acheteur;
    }

    public Entite getVendeur() {
        return vendeur;
    }

    public double getPourcentage() {
        return pourcentage;
    }

    public PersonnePhysique getPersonneImpliquee() {
        if (cible instanceof PersonnePhysique) {
            return (PersonnePhysique) cible;
        }
        return null;
    }

    public Media getMediaImplique() {
        if (source instanceof Media) {
            return (Media) source;
        }
        return null;
    }

    /**
     * Méthode pour afficher une description d'un rachat
     * @return une chaîne de caractères décrivant le rachat
     */
    public String descriptionRachat() {
        return acheteur.getNom() + " a acquis " + pourcentage + "% de " +
                        cible.getNom() + " auprès de " + vendeur.getNom();
    }
}
