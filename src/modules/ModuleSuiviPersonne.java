package modules;

import core.ModuleSpecialise;
import core.Vigie;
import model.Evenement;
import model.Media;
import model.PersonnePhysique;
import model.Participation;
import core.ParticipationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module chargé de surveiller des personnes physiques dans les publications.
 * Déclenche une alerte si une personne surveillée est mentionnée ou si une publication
 * est faite par un média détenu par une personne surveillée.
 */
public class ModuleSuiviPersonne implements ModuleSpecialise {
    /**
     * Liste des personnes physiques surveillées par ce module.
     */
    private final List<PersonnePhysique> personnesSurveillees;

    /**
     * Instance de la Vigie utilisée pour envoyer des alertes.
     */
    private final Vigie vigie;

    /**
     * Historique des événements traités par ce module.
     */
    private final List<Evenement> historique = new ArrayList<>();

    /**
     * Map pour suivre le nombre de mentions par média.
     */
    private final Map<Media, Integer> mentionsParMedia = new HashMap<>();

    /**
     * Service pour obtenir les participations des médias.
     */
    private final ParticipationService participationService;

    /**
     * Construit un module pour surveiller une liste de personnes.
     *
     * @param personnesSurveillees liste des personnes physiques à surveiller
     * @param vigie                instance de la Vigie à alerter
     * @param participationService service pour obtenir les participations des médias
     */
    public ModuleSuiviPersonne(List<PersonnePhysique> personnesSurveillees, Vigie vigie, ParticipationService participationService) {
        this.personnesSurveillees = personnesSurveillees;
        this.vigie = vigie;
        this.participationService = participationService;
    }

    /**
     * Traite un événement. Si une personne surveillée est mentionnée ou si une publication
     * est faite par un média détenu par une personne surveillée, une alerte est envoyée à la Vigie.
     *
     * @param evenement l’événement à analyser
     */
    @Override
    public void traiter(Evenement evenement) {
        // Ajoute l'événement à l'historique
        historique.add(evenement);

        // Ne traite que les événements de type "publication"
        if (!"publication".equalsIgnoreCase(evenement.getType())) {
            return;
        }

        for (PersonnePhysique personne : personnesSurveillees) {
            boolean mentionDansContenu = evenement.getContenu().toLowerCase().contains(personne.getNom().toLowerCase());
            boolean mentionDansListe = evenement.getMentions().contains(personne);
            boolean possedeMedia = false;
            if (evenement.getSource() instanceof Media media) {
                List<Participation> participations = participationService.getProprietaires(media);
                for (Participation participation : participations) {
                    if (participation.getProprietaire().equals(personne)) {
                        possedeMedia = true;
                        break;
                    }
                }
            }

            // Envoie une alerte selon les conditions
            if (mentionDansContenu) {
                vigie.recevoirAlerte(String.format("Publication concernant %s le %s : %s",
                        personne.getNom(), evenement.getDate(), evenement.getContenu()));
            }
            if (mentionDansListe) {
                vigie.recevoirAlerte(String.format("Publication mentionnant %s le %s : %s",
                        personne.getNom(), evenement.getDate(), evenement.getContenu()));
            }
            if (possedeMedia) {
                if (evenement.getSource() instanceof Media media) {
                    mentionsParMedia.merge(media, 1, Integer::sum);
                    vigie.recevoirAlerte(String.format("Publication par un média détenu par %s : %s",
                            personne.getNom(), evenement.getContenu()));
                }
            }
        }
    }

    /**
     * Retourne l'historique des événements traités par ce module.
     *
     * @return la liste des événements traités
     */
    public List<Evenement> getHistorique() {
        return historique;
    }

    /**
     * Affiche l'historique des événements traités en console.
     */
    public void afficherHistorique() {
        System.out.println("\n=== Historique des publications concernant les personnes surveillées ===");
        for (Evenement evenement : historique) {
            System.out.println("- " + evenement.getDate() + " : " + evenement.getDescription());
        }
    }

    /**
     * Affiche le pourcentage de mentions par média pour les personnes surveillées.
     */
    public void afficherPourcentageMentionsParMedia() {
        System.out.println("\n=== Pourcentage de mentions par média ===");
        int totalMentions = mentionsParMedia.values().stream().mapToInt(Integer::intValue).sum();
        for (Map.Entry<Media, Integer> entry : mentionsParMedia.entrySet()) {
            double pourcentage = (entry.getValue() * 100.0) / totalMentions;
            System.out.printf("- %s : %.2f%%\n", entry.getKey().getNom(), pourcentage);
        }
    }
}
