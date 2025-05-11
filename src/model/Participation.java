package model;

/**
 * Représente une participation dans une entité ou un média.
 * Une entité (propriétaire) détient un pourcentage de propriété sur une autre entité ou média (cible).
 *
 * @see Entite
 * @see Media
 */
public class Participation {
    private Entite proprietaire;
    private Entite cible;
    private double pourcentage;

    /**
     * Construit une nouvelle participation.
     *
     * @param proprietaire Entité propriétaire des parts
     * @param cible        Entité ou média possédé
     * @param pourcentage  Pourcentage de parts détenues (entre 0 et 100)
     */
    public Participation(Entite proprietaire, Entite cible, double pourcentage) {
        this.proprietaire = proprietaire;
        this.cible = cible;
        this.pourcentage = pourcentage;
    }

    /**
     * Retourne l'entité propriétaire des parts.
     *
     * @return propriétaire
     */
    public Entite getProprietaire() {
        return proprietaire;
    }

    /**
     * Retourne la cible de la participation (entité ou média).
     *
     * @return cible
     */
    public Entite getCible() {
        return cible;
    }

    /**
     * Retourne le pourcentage détenu.
     *
     * @return pourcentage de propriété (entre 0 et 100)
     */
    public double getPourcentage() {
        return pourcentage;
    }

    /**
     * Définit un nouveau pourcentage de propriété.
     *
     * @param pourcentage nouveau pourcentage (entre 0 et 100)
     */
    public void setPourcentage(double pourcentage) {
        this.pourcentage = pourcentage;
    }

    /**
     * Retourne le média associé à cette participation si la cible est un média.
     *
     * @return Le média concerné, ou null si la cible n'est pas un média
     */
    public Media getMedia() {
        if (cible instanceof Media) {
            return (Media) cible;
        }
        return null;
    }

    /**
     * Affiche une description lisible de la participation.
     *
     * @return chaîne de caractères représentant la participation
     */
    @Override
    public String toString() {
        return proprietaire.getNom() + " détient " + pourcentage + "% de " + cible.getNom();
    }
}
