package model;

/**
 * Classe abstraite représentant une entité pouvant être une personne physique ou morale.
 * Une entité peut posséder des médias ou d'autres entités (personnes morales).
 * @see PersonnePhysique
 * @see PersonneMorale
 */
public abstract class Entite {
    protected String nom;

    /**
     * Constructeur de l'entité.
     *
     * @param nom Nom de l'entité
     */
    public Entite(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne le nom de l'entité.
     *
     * @return nom de l'entité
     */
    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{nom='" + nom + "'}";
    }
}
