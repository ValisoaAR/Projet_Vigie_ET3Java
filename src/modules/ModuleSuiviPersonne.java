package modules;

import core.ModuleSpecialise;
import core.Vigie;
import model.Evenement;
import model.PersonnePhysique;

import java.util.ArrayList;
import java.util.List;

/**
 * Module chargé de surveiller des personnes physiques dans les publications.
 * Déclenche une alerte si une personne surveillée est mentionnée.
 */
public class ModuleSuiviPersonne implements ModuleSpecialise {
    private final List<PersonnePhysique> personnesSurveillees;
    private final Vigie vigie;
    private final List<Evenement> historique = new ArrayList<>();

    /**
     * Construit un module pour surveiller une liste de personnes.
     *
     * @param personnesSurveillees liste des personnes physiques à surveiller
     * @param vigie                instance de la Vigie à alerter
     */
    public ModuleSuiviPersonne(List<PersonnePhysique> personnesSurveillees, Vigie vigie) {
        this.personnesSurveillees = personnesSurveillees;
        this.vigie = vigie;
    }

    /**
     * Traite un événement. Si une personne surveillée est mentionnée,
     * une alerte est envoyée à la Vigie.
     *
     * @param evenement l’événement à analyser
     */
    @Override
    public void traiter(Evenement evenement) {
        historique.add(evenement);

        if (!evenement.getType().equalsIgnoreCase("publication")) return;

        for (PersonnePhysique p : personnesSurveillees) {
            boolean mentionDansContenu = evenement.getContenu().toLowerCase().contains(p.getNom().toLowerCase());
            boolean cibleEstPersonne = evenement.getCible() instanceof PersonnePhysique
                    && ((PersonnePhysique) evenement.getCible()).equals(p);

            if (mentionDansContenu || cibleEstPersonne) {
                String alerte = "Publication mentionnant " + p.getNom() + " le "
                        + evenement.getDate() + " : " + evenement.getContenu();
                vigie.recevoirAlerte(alerte);
                break;
            }
        }
    }

    /**
     * Affiche l’historique des événements reçus par ce module.
     */
    public void afficherHistorique() {
        System.out.println("Historique du module de suivi de personne :");
        for (Evenement e : historique) {
            System.out.println("- [" + e.getDate() + "] " + e.getContenu());
        }
    }
}
