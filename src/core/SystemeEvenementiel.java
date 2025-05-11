package core;

import model.Evenement;
import model.Entite;

import java.util.*;

/**
 * Gère la propagation des événements aux modules abonnés.
 */
public class SystemeEvenementiel {
    private final Map<String, List<ModuleSpecialise>> abonnements;
    private final ParticipationService participationService;

    /**
     * Constructeur de SystemeEvenementiel.
     *
     * @param participationService service métier pour gérer les participations
     */
    public SystemeEvenementiel(ParticipationService participationService) {
        this.abonnements = new HashMap<>();
        this.participationService = participationService;
    }

    /**
     * Enregistre un module spécialisé pour un type d’événement donné.
     *
     * @param type   le type d’événement (ex : "publication", "rachat")
     * @param module le module à abonner
     */
    public void abonner(String type, ModuleSpecialise module) {
        String key = type.toLowerCase();
        abonnements.computeIfAbsent(key, k -> new ArrayList<>()).add(module);
    }

    /**
     * Diffuse un événement aux modules abonnés.
     *
     * @param evenement l’événement à diffuser
     */
    public void diffuserEvenement(Evenement evenement) {
        // Vérifie si l'événement est un rachat
        if (evenement.getType().equalsIgnoreCase("rachat")) {
            traiterRachat(evenement);
        }

        // Notifie les modules abonnés
        List<ModuleSpecialise> modules = abonnements.getOrDefault(evenement.getType().toLowerCase(), Collections.emptyList());
        for (ModuleSpecialise module : modules) {
            module.traiter(evenement);
        }
    }

    /**
     * Traite un événement de type "rachat" et met à jour les participations.
     *
     * @param evenement l'événement de rachat
     */
    private void traiterRachat(Evenement evenement) {
        try {
            // Format attendu : "acheteur, vendeur, cible, pourcentage"
            String[] infos = evenement.getContenu().split(",");
            if (infos.length != 4) {
                System.err.println("Format invalide pour un rachat : " + evenement.getContenu());
                return;
            }

            String acheteurNom = infos[0].trim();
            String vendeurNom = infos[1].trim();
            String cibleNom = infos[2].trim();
            double pourcentage;

            try {
                pourcentage = Double.parseDouble(infos[3].trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.err.println("Pourcentage invalide dans l'événement : " + evenement.getContenu());
                return;
            }

            // Recherche des entités concernées
            Entite acheteur = participationService.getEntiteParNom(acheteurNom);
            Entite vendeur = participationService.getEntiteParNom(vendeurNom);
            Entite cible = participationService.getEntiteParNom(cibleNom);

            if (acheteur == null || vendeur == null || cible == null) {
                System.err.println("Entité inconnue dans l’événement : " + evenement.getContenu());
                return;
            }

            // Mise à jour des participations
            boolean reussi = participationService.transfererParts(vendeur, acheteur, cible, pourcentage);
            if (!reussi) {
                System.err.println("Erreur lors du transfert des parts.");
            } else {
                System.out.println("Rachat traité avec succès : " + evenement.getContenu());
            }

        } catch (Exception e) {
            System.err.println("Erreur inattendue lors du traitement du rachat : " + e.getMessage());
        }
    }

    /**
     * Retourne les abonnements actuels.
     *
     * @return une map des types d'événements et des modules abonnés
     */
    public Map<String, List<ModuleSpecialise>> getAbonnements() {
        return abonnements;
    }
}
