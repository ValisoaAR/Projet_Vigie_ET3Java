package core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.Entite;
import model.Participation;

/**
 * Gère l'ensemble des participations entre entités et médias.
 * Permet d'ajouter, consulter et transférer des parts de propriété.
 *
 * @see Participation
 */
public class ParticipationService {
    private List<Participation> participations;

    /**
     * Initialise un gestionnaire vide de participations.
     */
    public ParticipationService() {
        this.participations = new ArrayList<>();
    }

    /**
     * Ajoute une nouvelle participation.
     *
     * @param proprietaire Entité qui détient les parts
     * @param cible        Entité ou média possédé
     * @param pourcentage  Pourcentage détenu (entre 0 et 100)
     */
    public void ajouterParticipation(Entite proprietaire, Entite cible, double pourcentage) {
        participations.add(new Participation(proprietaire, cible, pourcentage));
    }

    /**
     * Transfère une partie des parts d'une entité vers une autre.
     *
     * @param vendeur     Entité cédante
     * @param acheteur    Entité acheteuse
     * @param cible       Cible de la participation
     * @param pourcentage Pourcentage à transférer
     * @return true si le transfert est cohérent et effectué, false sinon
     */
    public boolean transfererParts(Entite vendeur, Entite acheteur, Entite cible, double pourcentage) {
        for (Participation p : participations) {
            if (p.getProprietaire().equals(vendeur) && p.getCible().equals(cible)) {
                if (p.getPourcentage() >= pourcentage) {
                    p.setPourcentage(p.getPourcentage() - pourcentage);

                    boolean acheteurExiste = false;
                    for (Participation pa : participations) {
                        if (pa.getProprietaire().equals(acheteur) && pa.getCible().equals(cible)) {
                            pa.setPourcentage(pa.getPourcentage() + pourcentage);
                            acheteurExiste = true;
                            break;
                        }
                    }

                    if (!acheteurExiste) {
                        participations.add(new Participation(acheteur, cible, pourcentage));
                    }

                    return true;
                }
            }
        }
        return false; // Rachat incohérent
    }

    /**
     * @return toutes les participations enregistrées
     */
    public List<Participation> getParticipations() {
        return participations;
    }

    /**
     * Retourne les participations détenues par une entité donnée.
     *
     * @param proprietaire Entité concernée
     * @return liste des participations
     */
    public List<Participation> getParticipationsParProprietaire(Entite proprietaire) {
        return participations.stream()
                .filter(p -> p.getProprietaire().equals(proprietaire))
                .collect(Collectors.toList());
    }

    /**
     * Retourne toutes les participations visant une cible donnée.
     *
     * @param cible entité ou média possédé
     * @return liste des participations sur cette cible
     */
    public List<Participation> getParticipationsSurCible(Entite cible) {
        return participations.stream()
                .filter(p -> p.getCible().equals(cible))
                .collect(Collectors.toList());
    }

    /**
     * Affiche toutes les participations dans la console.
     */
    public void afficherParticipations() {
        for (Participation p : participations) {
            System.out.println(p);
        }
    }
}
