package model;

/**
 * Représente un média détenu par une entité (personne physique ou morale).
 *
 * @see Entite
 */
public class Media {
    private int id;
    private String nom;
    private Entite proprietaire;

    /**
     * Construit un média avec son identifiant, nom et propriétaire.
     *
     * @param id          identifiant unique du média
     * @param nom         nom du média
     * @param proprietaire entité propriétaire
     */
    public Media(int id, String nom, Entite proprietaire) {
        this.id = id;
        this.nom = nom;
        this.proprietaire = proprietaire;
    }

    /**
     * @return l'identifiant du média
     */
    public int getId() {
        return id;
    }

    /**
     * @return le nom du média
     */
    public String getNom() {
        return nom;
    }

    /**
     * @return le propriétaire du média
     */
    public Entite getProprietaire() {
        return proprietaire;
    }

    @Override
    public String toString() {
        return "Media{id=" + id + ", nom='" + nom + "', proprietaire=" + proprietaire.getNom() + "}";
    }

    /**
     * Vérifie l'égalité entre deux objets Media.
     * Deux médias sont considérés égaux s'ils ont le même identifiant.
     *
     * @param obj objet à comparer
     * @return true si égalité, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Media media = (Media) obj;
        return id == media.id;
    }

    /**
     * @return code de hachage basé sur l'identifiant
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
