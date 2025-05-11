package model;

import java.util.List;
import core.ParticipationService;

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
     * Retourne les participations associées à ce média.
     *
     * @param participationService Le service de participation.
     * @return Liste des participations pour ce média.
     */
    public List<Participation> getParticipations(ParticipationService participationService) {
        return participationService.getProprietaires(this);
    }

    /**
     * @return une représentation textuelle du média
     */
    @Override
    public String toString() {
        return "Media : " + nom + " [" + type + "]";
    }
}
