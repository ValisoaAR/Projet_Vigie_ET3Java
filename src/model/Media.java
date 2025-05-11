package model;

/**
 * Représente un média (presse, TV, radio, etc.) pouvant être détenu par une ou plusieurs entités.
 */
public class Media extends Entite {
    private String type;

    /**
     * Construit un média avec son nom et son type.
     *
     * @param nom  nom du média
     * @param type type du média (presse, TV, etc.)
     */
    public Media(String nom, String type) {
        super(nom);
        this.type = type;
    }

    /**
     * @return le type du média
     */
    public String getType() {
        return type;
    }

    /**
     * @return une représentation textuelle du média
     */
    @Override
    public String toString() {
        return "Media : " + nom + " [" + type + "]";
    }
}
