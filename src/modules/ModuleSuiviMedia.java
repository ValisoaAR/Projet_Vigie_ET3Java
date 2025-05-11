package modules;

import core.ModuleSpecialise;
import core.Vigie;
import model.Evenement;
import model.Media;

import java.util.ArrayList;
import java.util.List;

/**
 * Module chargé de surveiller certains médias lors des rachats.
 * Déclenche une alerte si un média surveillé est la cible d’un rachat.
 */
public class ModuleSuiviMedia implements ModuleSpecialise {
    private final List<Media> mediasSurveilles;
    private final Vigie vigie;
    private final List<Evenement> historique = new ArrayList<>();

    /**
     * Construit un module pour surveiller une liste de médias.
     *
     * @param mediasSurveilles liste des médias à surveiller
     * @param vigie            instance de la Vigie à alerter
     */
    public ModuleSuiviMedia(List<Media> mediasSurveilles, Vigie vigie) {
        this.mediasSurveilles = mediasSurveilles;
        this.vigie = vigie;
    }

    /**
     * Traite un événement de rachat. Si un média surveillé est la cible,
     * une alerte est envoyée à la Vigie.
     *
     * @param evenement l’événement à analyser
     */
    @Override
    public void traiter(Evenement evenement) {
        historique.add(evenement);

        if (!evenement.getType().equalsIgnoreCase("rachat")) return;

        for (Media m : mediasSurveilles) {
            if (evenement.getCible() instanceof Media cible && cible.equals(m)) {
                String alerte = "Rachat concernant " + m.getNom() + " le "
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
        System.out.println("Historique du module de suivi de média :");
        for (Evenement e : historique) {
            System.out.println("- [" + e.getDate() + "] " + e.getContenu());
        }
    }
}
