package core;

import model.Entite;
import model.Participation;

import java.util.*;

/**
 * Gère les participations entre entités (médias, organisations, personnes).
 * Permet l’ajout, le transfert, et la consultation des parts détenues.
 */
public class ParticipationService {
    private final List<Participation> participations = new ArrayList<>();
    private Map<String, Entite> entitesParNom = new HashMap<>();

    /**
     * Ajoute une nouvelle participation au système.
     *
     * @param proprietaire entité détentrice
     * @param cible        entité ou média détenu
     * @param pourcentage  pourcentage de parts (0–100)
     */
    public void ajouterParticipation(Entite proprietaire, Entite cible, double pourcentage) {
        participations.add(new Participation(proprietaire, cible, pourcentage));
    }

    /**
     * Transfère un pourcentage de parts d’un vendeur à un acheteur.
     *
     * @param vendeur     entité cédante
     * @param acheteur    entité acheteuse
     * @param cible       cible de la participation
     * @param pourcentage pourcentage à transférer
     * @return true si le transfert est valide, false sinon
     */
    public boolean transfererParts(Entite vendeur, Entite acheteur, Entite cible, double pourcentage) {
        for (Participation p : participations) {
            if (p.getProprietaire().equals(vendeur) && p.getCible().equals(cible)) {
                if (p.getPourcentage() >= pourcentage) {
                    p.setPourcentage(p.getPourcentage() - pourcentage);

                    // Chercher si l’acheteur possède déjà une part
                    boolean trouvé = false;
                    for (Participation pa : participations) {
                        if (pa.getProprietaire().equals(acheteur) && pa.getCible().equals(cible)) {
                            pa.setPourcentage(pa.getPourcentage() + pourcentage);
                            trouvé = true;
                            break;
                        }
                    }

                    if (!trouvé) {
                        participations.add(new Participation(acheteur, cible, pourcentage));
                    }

                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Enregistre les entités disponibles (clé = nom).
     *
     * @param mapEntites map des entités par nom
     */
    public void setEntites(Map<String, Entite> mapEntites) {
        this.entitesParNom = mapEntites;
    }

    /**
     * Retourne une entité par son nom normalisé (minuscules + sans espaces superflus).
     *
     * @param nom nom de l’entité recherchée
     * @return entité correspondante ou null
     */
    public Entite getEntiteParNom(String nom) {
        return entitesParNom.get(nom.toLowerCase().trim());
    }

    /**
     * @return la liste de toutes les participations
     */
    public List<Participation> getParticipations() {
        return participations;
    }

        /**
     * @return la map des entités connues (par nom en minuscules)
     */
    public Map<String, Entite> getEntites() {
        return entitesParNom;
    }

    /**
     * Affiche les participations dans la console.
     */
    public void afficherParticipations() {
        for (Participation p : participations) {
            System.out.println(p);
        }
    }
}
