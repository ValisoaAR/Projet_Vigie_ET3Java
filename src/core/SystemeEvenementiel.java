package core;

import model.Evenement;
import model.Entite;

import java.util.*;

/**
 * Gère la propagation des événements aux modules abonnés.
 * Applique les effets métiers si nécessaire (rachat),
 * puis notifie uniquement les modules concernés.
 */
public class SystemeEvenementiel {
    private final Map<String, List<ModuleSpecialise>> abonnements;
    private final ParticipationService participationService;

    /**
     * Initialise le système avec un gestionnaire de participations.
     *
     * @param participationService service métier pour les transferts de parts
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
        abonnements.computeIfAbsent(key, unused -> new ArrayList<>());
        abonnements.get(key).add(module);
    }

    /**
     * Reçoit un événement, applique ses effets si nécessaire,
     * puis le transmet aux modules abonnés au type concerné.
     *
     * @param evenement l’événement à diffuser
     */
    public void diffuserEvenement(Evenement evenement) {
        String type = evenement.getType().toLowerCase();

        if (type.equals("rachat")) {
            // Format du contenu : "acheteur, vendeur, cible, pourcentage"
            try {
                String[] infos = evenement.getContenu().split(",");
                if (infos.length != 4) {
                    System.err.println("Format invalide pour un rachat : " + evenement.getContenu());
                    return;
                }

                String acheteurNom = infos[0].trim();
                String vendeurNom = infos[1].trim();
                String cibleNom = infos[2].trim();
                double pourcentage = Double.parseDouble(infos[3].trim().replace(",", "."));

                Entite acheteur = participationService.getEntiteParNom(acheteurNom);
                Entite vendeur = participationService.getEntiteParNom(vendeurNom);
                Entite cible = participationService.getEntiteParNom(cibleNom);

                if (acheteur == null || vendeur == null || cible == null) {
                    System.err.println("Entité inconnue dans l’événement : " + evenement.getContenu());
                    return;
                }

                boolean reussi = participationService.transfererParts(vendeur, acheteur, cible, pourcentage);
                if (!reussi) {
                    System.out.println("Rachat incohérent ignoré.");
                }

                // Pour que les modules soient au courant de l'événement, on remplit la cible
                evenement.setCible(cible);

            } catch (Exception e) {
                System.err.println("Erreur dans le traitement d’un rachat : " + evenement.getContenu());
            }
        }

        List<ModuleSpecialise> modules = abonnements.getOrDefault(type, Collections.emptyList());
        for (ModuleSpecialise module : modules) {
            module.traiter(evenement);
        }
    }
}
