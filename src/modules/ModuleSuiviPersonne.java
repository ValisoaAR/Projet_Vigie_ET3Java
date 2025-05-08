package modules;

import core.ModuleSpecialise;
import core.Vigie;
import model.Evenement;
import model.PersonnePhysique;

import java.util.List;

/**
 * Module spécialisé chargé de surveiller les publications concernant
 * une ou plusieurs personnes physiques. Il déclenche une alerte si
 * une publication mentionne une personne surveillée ou la cible directement.
 *
 * @see PersonnePhysique
 * @see Evenement
 */
public class ModuleSuiviPersonne implements ModuleSpecialise {
    private List<PersonnePhysique> personnesSurveillees;
    private Vigie vigie; // Référence vers la vigie pour lui envoyer les alertes

    /**
     * Construit un module de suivi de publications pour des personnes physiques.
     *
     * @param personnesSurveillees liste des personnes à surveiller
     * @param vigie                instance de la vigie à alerter
     */
    public ModuleSuiviPersonne(List<PersonnePhysique> personnesSurveillees, Vigie vigie) {
        this.personnesSurveillees = personnesSurveillees;
        this.vigie = vigie;
    }

    /**
     * Traite un événement. Si c'est une publication qui concerne une personne surveillée,
     * une alerte est envoyée à la vigie.
     *
     * @param evenement l'événement à analyser
     */
    @Override
    public void traiter(Evenement evenement) {
        if (!evenement.getType().equalsIgnoreCase("publication")) {
            return;
        }

        for (PersonnePhysique p : personnesSurveillees) {
            boolean mentionDansContenu = evenement.getContenu().toLowerCase().contains(p.getNom().toLowerCase());
            boolean cibleEstPersonne = evenement.getCible() instanceof PersonnePhysique
                    && ((PersonnePhysique) evenement.getCible()).equals(p);

            if (mentionDansContenu || cibleEstPersonne) {
                String alerte = "ALERTE : " + p.getNom() + " a été mentionné dans une publication le "
                        + evenement.getDate() + " : " + evenement.getContenu();
                vigie.recevoirAlerte(alerte);
                break;
            }
        }
    }
}
