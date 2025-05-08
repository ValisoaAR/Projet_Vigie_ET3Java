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
     * @return l'entité propriétaire des parts
     */
    public Entite getProprietaire() {
        return proprietaire;
    }

    /**
     * @return l'entité ou le média ciblé par cette participation
     */
    public Entite getCible() {
        return cible;
    }

    /**
     * @return le pourcentage de propriété détenu
     */
    public double getPourcentage() {
        return pourcentage;
    }

    /**
     * Définit un nouveau pourcentage de propriété.
     *
     * @param pourcentage nouveau pourcentage (0 à 100)
     */
    public void setPourcentage(double pourcentage) {
        this.pourcentage = pourcentage;
    }

    @Override
    public String toString() {
        return proprietaire.getNom() + " détient " + pourcentage + "% de " + cible.getNom();
    }
}
