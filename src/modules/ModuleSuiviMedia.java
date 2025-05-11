package modules;

import core.ModuleSpecialise;
import core.Vigie;
import model.Evenement;
import model.Media;

import java.util.ArrayList;
import java.util.List;

/**
 * Module spécialisé pour surveiller certains médias lors des événements de rachat.
 * Ce module déclenche une alerte à la Vigie si un média surveillé est la cible d'un rachat.
 */
public class ModuleSuiviMedia implements ModuleSpecialise {
    /**
     * Liste des médias surveillés par ce module.
     */
    private final List<Media> mediasSurveilles;

    /**
     * Instance de la Vigie utilisée pour envoyer des alertes.
     */
    private final Vigie vigie;

    /**
     * Historique des événements traités par ce module.
     */
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
     * une alerte est envoyée à la Vigie et l'événement est ajouté à l'historique.
     *
     * @param evenement l’événement à analyser
     */
    @Override
    public void traiter(Evenement evenement) {
        // Ajoute l'événement à l'historique
        historique.add(evenement.getContenu());

        // Ne traite que les événements de type "rachat"
        if (!evenement.getType().equalsIgnoreCase("rachat")) return;

        // Vérifie si l'événement concerne un média surveillé
        for (Media m : mediasSurveilles) {
            if (evenement.getCible() instanceof Media cible && cible.equals(m)) {
                String alerte = "Rachat concernant " + m.getNom() + " le "
                        + evenement.getDate() + " : " + evenement.descriptionRachat();
                vigie.recevoirAlerte(alerte);
                break;
            }
        }
    }

    /**
     * Retourne l'historique des événements traités par ce module.
     *
     * @return la liste des événements traités
     */
    public List<String> getHistorique() {
        return historique;
    }

    /**
     * Affiche l'historique des événements traités en console.
     */
    public void afficherHistorique() {
        System.out.println("\n=== Historique des événements traités ===");
        historique.forEach(System.out::println);
    }
}
