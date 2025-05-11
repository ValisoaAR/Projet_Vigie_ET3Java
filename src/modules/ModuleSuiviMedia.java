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
    private final List<String> historique = new ArrayList<>();

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
        historique.add(evenement.getContenu());

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
     * Retourne l'historique des événements traités.
     *
     * @return la liste des événements traités
     */
    public List<String> getHistorique() {
        return historique;
    }

    /**
     * Affiche l'historique des événements traités.
     */
    public void afficherHistorique() {
        historique.forEach(System.out::println);
    }
}
