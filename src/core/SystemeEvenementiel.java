package core;

import model.Evenement;
import model.Entite;
import model.PersonnePhysique;
import model.Participation;

import java.util.*;

/**
 * Gère la propagation des événements aux modules abonnés.
 * Maintient un historique des événements diffusés.
 */
public class SystemeEvenementiel {
    private final Map<String, List<ModuleSpecialise>> abonnements;
    private final ParticipationService participationService;
    private final List<Evenement> historiqueEvenements; // Historique des événements diffusés

    /**
     * Constructeur de SystemeEvenementiel.
     *
     * @param participationService service métier pour gérer les participations
     */
    public SystemeEvenementiel(ParticipationService participationService) {
        this.abonnements = new HashMap<>();
        this.participationService = participationService;
        this.historiqueEvenements = new ArrayList<>();
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
        // Ajoute l'événement à l'historique
        historiqueEvenements.add(evenement);

        // Notifie tous les modules abonnés
        for (List<ModuleSpecialise> modules : abonnements.values()) {
            for (ModuleSpecialise module : modules) {
                module.traiter(evenement);
            }
        }
    }

    /**
     * Traite un événement de type "rachat" et met à jour les participations.
     *
     * @param evenement l'événement de rachat
     */
    public void traiterRachat(Evenement evenement) {
        try {
            // Vérifie que l'événement contient les informations nécessaires
            Entite acheteur = evenement.getAcheteur();
            Entite vendeur = evenement.getVendeur();
            Entite cible = (Entite) evenement.getCible();
            double pourcentage = evenement.getPourcentage();

            if (acheteur == null || vendeur == null || cible == null) {
                System.err.println("Entité manquante dans l’événement de rachat.");
                return;
            }

            // Mise à jour des participations
            boolean reussi = participationService.transfererParts(vendeur, acheteur, cible, pourcentage);
            if (!reussi) {
                System.err.println("Erreur lors du transfert des parts.");
            } else {
                
                System.out.println("Rachat traité avec succès : " +
                        acheteur.getNom() + " a acquis " + pourcentage + "% de " +
                        cible.getNom() + " auprès de " + vendeur.getNom());
            }

        } catch (Exception e) {
            System.err.println("Erreur inattendue lors du traitement du rachat : " + e.getMessage());
        }
    }

    /**
     * Retourne l'historique des événements diffusés.
     *
     * @return liste des événements diffusés
     */
    public List<Evenement> getHistoriqueEvenements() {
        return historiqueEvenements;
    }

    /**
     * Retourne l'historique des événements de type "publication".
     *
     * @return liste des événements de type "publication"
     */
    public List<Evenement> getHistorique(String type) {
        String typeLower = type.toLowerCase();
        List<Evenement> historiquePublications = new ArrayList<>();
        for (Evenement evenement : historiqueEvenements) {
            if (evenement.getType().equalsIgnoreCase(typeLower)) {
                historiquePublications.add(evenement);
            }
        }
        return historiquePublications;
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
