package model;

/**
 * Classe abstraite représentant une entité pouvant être une personne physique ou morale.
 * Une entité peut posséder des médias ou d'autres entités (personnes morales).
 * @see PersonnePhysique
 * @see PersonneMorale
 */
public abstract class Entite {
    protected int id;
    protected String nom;

    /**
     * Constructeur de l'entité.
     *
     * @param id  Identifiant unique de l'entité
     * @param nom Nom de l'entité
     */
    public Entite(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    /**
     * Retourne l'identifiant de l'entité.
     *
     * @return identifiant de l'entité
     */
    public int getId() {
        return id;
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
        return getClass().getSimpleName() + "{id=" + id + ", nom='" + nom + "'}";
    }
}
