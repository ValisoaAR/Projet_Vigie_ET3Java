package ui;

import core.*;
import model.*;
import modules.ModuleSuiviMedia;
import modules.ModuleSuiviPersonne;
import core.SystemeEvenementiel;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;

/**
 * Interface console permettant de simuler les interactions avec le système de surveillance :
 * importation, affichages, simulations d’événements, historique, classements.
 */
public class ConsoleApp {
    private final Scanner scanner = new Scanner(System.in);
    private final ParticipationService participationService = new ParticipationService();
    private final Vigie vigie = new Vigie();
    private final SystemeEvenementiel systeme = new SystemeEvenementiel(participationService);
    private final DataImport dataImport = new DataImport();

    private ModuleSuiviPersonne modulePersonne;
    private ModuleSuiviMedia moduleMedia;

    /**
     * Démarre le menu interactif de l'application console.
     */
    public void demarrer() {
        // Importation des données
        dataImport.importerTout(participationService);

        // Vérification des erreurs d'importation
        afficherErreursImportation();

        // Initialisation des modules spécialisés
        initialiserModules();

        // Menu interactif
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine();
            switch (choix) {
                case "1" -> afficherEntites();
                case "2" -> afficherParticipations();
                case "3" -> simulerPublication();
                case "4" -> simulerRachat();
                case "5" -> afficherAlertes();
                case "6" -> afficherClassementPossessionMedias();
                case "7" -> afficherClassementPossessionOrganisations();
                case "8" -> afficherHistoriquesModules();
                case "9" -> saisirMediaEtAfficherProprietaires();
                case "10" -> saisirOrganisationEtAfficherDetails();
                case "11" -> saisirPersonneEtAfficherPossessions();
                case "12" -> verifierAbonnements();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }

        System.out.println("Fin de l'application.");
    }

    /**
     * Initialise les modules spécialisés pour les entités surveillées.
     */
    private void initialiserModules() {
        Entite personne = participationService.getEntiteParNom("vincent bolloré");
        if (personne instanceof PersonnePhysique) {
            modulePersonne = new ModuleSuiviPersonne(List.of((PersonnePhysique) personne), vigie);
            systeme.abonner("publication", modulePersonne);
        } else {
            System.err.println("Erreur : L'entité 'vincent bolloré' n'a pas été trouvée ou n'est pas une PersonnePhysique.");
        }

        Entite media = participationService.getEntiteParNom("le monde");
        if (media instanceof Media) {
            moduleMedia = new ModuleSuiviMedia(List.of((Media) media), vigie);
            systeme.abonner("rachat", moduleMedia);
        } else {
            System.err.println("Erreur : L'entité 'le monde' n'a pas été trouvée ou n'est pas un Media.");
        }
    }

    /**
     * Affiche le menu principal.
     */
    private void afficherMenu() {
        System.out.println("""
                \n=== MENU ===
                1. Afficher toutes les entités
                2. Afficher les participations
                3. Simuler une publication
                4. Simuler un rachat de parts
                5. Afficher les alertes reçues
                6. Afficher les entités par nombre de médias possédés
                7. Afficher les entités par nombre d'organisations possédées
                8. Afficher l’historique des modules spécialisés
                9. Saisir un média et afficher ses propriétaires
                10. Saisir une organisation et afficher ses relations
                11. Saisir une personne et afficher ses possessions
                12. Vérifier les abonnements aux événements
                0. Quitter
                """);
        System.out.print("Votre choix : ");
    }

    /**
     * Affiche toutes les entités connues, triées alphabétiquement.
     */
    private void afficherEntites() {
        List<String> noms = new ArrayList<>(participationService.getEntites().keySet());
        noms.sort(String::compareTo);
        for (String nom : noms) {
            System.out.println("- " + nom);
        }
    }

    /**
     * Affiche la liste des participations enregistrées.
     */
    private void afficherParticipations() {
        participationService.afficherParticipations();
    }

    /**
     * Simule une publication mentionnant une personne.
     * Utilise une recherche stricte mais tolérante (casse, accents, espaces).
     */
    private void simulerPublication() {
        System.out.println("Nom complet de la personne mentionnée :");
        Entite cible = rechercherEntiteParNomExact();
        if (cible == null) return;

        System.out.print("Contenu de la publication : ");
        String contenu = scanner.nextLine();
        Evenement e = new Evenement("publication", LocalDate.now(), cible, contenu);
        systeme.diffuserEvenement(e);
        System.out.println("Événement de publication simulé.");
    }

    /**
     * Simule un événement de rachat avec sélection précise des entités concernées.
     */
    private void simulerRachat() {
        System.out.println("Nom complet de l’acheteur :");
        Entite acheteur = rechercherEntiteParNomExact();
        if (acheteur == null) return;

        System.out.println("Nom complet du vendeur :");
        Entite vendeur = rechercherEntiteParNomExact();
        if (vendeur == null) return;

        System.out.println("Nom complet de la cible :");
        Entite cible = rechercherEntiteParNomExact();
        if (cible == null) return;

        System.out.print("Pourcentage à transférer : ");
        double pourcentage;
        try {
            pourcentage = Double.parseDouble(scanner.nextLine().replace(",", "."));
            if (pourcentage <= 0 || pourcentage > 100) {
                System.out.println("Erreur : Le pourcentage doit être compris entre 0 et 100.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Erreur : Format de pourcentage invalide.");
            return;
        }

        String contenu = acheteur.getNom() + ", " + vendeur.getNom() + ", " + cible.getNom() + ", " + pourcentage;
        Evenement e = new Evenement("rachat", LocalDate.now(), null, contenu);
        systeme.diffuserEvenement(e);
        System.out.println("Événement de rachat simulé.");
    }

    /**
     * Affiche les alertes reçues par la Vigie.
     */
    private void afficherAlertes() {
        vigie.afficherHistorique();
    }

    /**
     * Affiche les entités triées par nombre décroissant de médias possédés.
     */
    private void afficherClassementPossessionMedias() {
        Map<Entite, Integer> compteur = new HashMap<>();

        for (Participation p : participationService.getParticipations()) {
            if (p.getCible() instanceof Media) {
                Entite proprio = p.getProprietaire();
                compteur.put(proprio, compteur.getOrDefault(proprio, 0) + 1);
            }
        }

        compteur.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry ->
                        System.out.println(entry.getKey().getNom() + " possède " + entry.getValue() + " média(s)")
                );
    }

    /**
     * Affiche les entités triées par nombre décroissant d’organisations possédées.
     */
    private void afficherClassementPossessionOrganisations() {
        Map<Entite, Integer> compteur = new HashMap<>();

        for (Participation p : participationService.getParticipations()) {
            if (p.getCible() instanceof PersonneMorale) {
                Entite proprio = p.getProprietaire();
                compteur.put(proprio, compteur.getOrDefault(proprio, 0) + 1);
            }
        }

        compteur.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry ->
                        System.out.println(entry.getKey().getNom() + " possède " + entry.getValue() + " organisation(s)")
                );
    }

    /**
     * Affiche les historiques des modules spécialisés.
     */
    private void afficherHistoriquesModules() {
        if (modulePersonne != null) {
            System.out.println("\n--- Historique Module Personne ---");
            modulePersonne.afficherHistorique();
        } else {
            System.out.println("Aucun module de suivi pour les personnes n'a été initialisé.");
        }

        if (moduleMedia != null) {
            System.out.println("\n--- Historique Module Media ---");
            moduleMedia.afficherHistorique();
        } else {
            System.out.println("Aucun module de suivi pour les médias n'a été initialisé.");
        }
    }

    /**
     * Saisit un média et affiche toutes les entités qui le possèdent avec leurs pourcentages.
     */
    private void saisirMediaEtAfficherProprietaires() {
        System.out.println("Nom du média :");
        Entite media = rechercherEntiteParNomExact();
        if (!(media instanceof Media)) {
            System.out.println("Erreur : L'entité saisie n'est pas un média.");
            return;
        }

        System.out.println("\n--- Propriétaires du média : " + media.getNom() + " ---");
        List<Participation> participations = participationService.getParticipations();
        boolean proprietairesTrouves = false;

        for (Participation p : participations) {
            if (p.getCible().equals(media)) {
                proprietairesTrouves = true;
                String type = (p.getProprietaire() instanceof PersonnePhysique) ? "PersonnePhysique"
                        : (p.getProprietaire() instanceof PersonneMorale) ? "PersonneMorale"
                        : "Inconnu";
                System.out.printf("- %-30s | Type : %-15s | Pourcentage : %.2f%%\n",
                        p.getProprietaire().getNom(), type, p.getPourcentage());
            }
        }

        if (!proprietairesTrouves) {
            System.out.println("Aucun propriétaire trouvé pour ce média.");
        }
    }

    /**
     * Saisit une organisation et affiche ce qu'elle possède et les entités qui possèdent des parts.
     */
    private void saisirOrganisationEtAfficherDetails() {
        System.out.println("Nom de l'organisation :");
        Entite organisation = rechercherEntiteParNomExact();
        if (!(organisation instanceof PersonneMorale)) {
            System.out.println("Erreur : L'entité saisie n'est pas une organisation.");
            return;
        }

        System.out.println("\n--- Détails de l'organisation : " + organisation.getNom() + " ---");

        // Afficher les entités qui possèdent des parts dans cette organisation
        System.out.println("Propriétaires de l'organisation :");
        List<Participation> participations = participationService.getParticipations();
        boolean proprietairesTrouves = false;

        for (Participation p : participations) {
            if (p.getCible().equals(organisation)) {
                proprietairesTrouves = true;
                String type = (p.getProprietaire() instanceof PersonnePhysique) ? "PersonnePhysique"
                        : (p.getProprietaire() instanceof PersonneMorale) ? "PersonneMorale"
                        : "Inconnu";
                System.out.printf("- %-30s | Type : %-15s | Pourcentage : %.2f%%\n",
                        p.getProprietaire().getNom(), type, p.getPourcentage());
            }
        }

        if (!proprietairesTrouves) {
            System.out.println("Aucun propriétaire trouvé pour cette organisation.");
        }

        // Afficher ce que l'organisation possède
        System.out.println("\nPossessions de l'organisation :");
        boolean possessionsTrouvees = false;

        for (Participation p : participations) {
            if (p.getProprietaire().equals(organisation)) {
                possessionsTrouvees = true;
                System.out.printf("- %-30s | Pourcentage : %.2f%%\n", p.getCible().getNom(), p.getPourcentage());
            }
        }

        if (!possessionsTrouvees) {
            System.out.println("Cette organisation ne possède rien.");
        }
    }

    /**
     * Saisit une personne physique et affiche ce qu'elle possède.
     */
    private void saisirPersonneEtAfficherPossessions() {
        System.out.println("Nom de la personne :");
        Entite personne = rechercherEntiteParNomExact();
        if (!(personne instanceof PersonnePhysique)) {
            System.out.println("Erreur : L'entité saisie n'est pas une personne physique.");
            return;
        }

        System.out.println("\n--- Possessions de la personne : " + personne.getNom() + " ---");
        List<Participation> participations = participationService.getParticipations();
        boolean possessionsTrouvees = false;

        for (Participation p : participations) {
            if (p.getProprietaire().equals(personne)) {
                possessionsTrouvees = true;
                System.out.printf("- %-30s | Pourcentage : %.2f%%\n", p.getCible().getNom(), p.getPourcentage());
            }
        }

        if (!possessionsTrouvees) {
            System.out.println("Cette personne ne possède rien.");
        }
    }

    /**
     * Affiche les erreurs rencontrées lors de l'importation des fichiers.
     */
    private void afficherErreursImportation() {
        List<String> erreurs = dataImport.getErreurs();
        if (erreurs.isEmpty()) {
            System.out.println("Aucune erreur d'importation détectée.");
        } else {
            System.out.println("Erreurs d'importation détectées :");
            for (String erreur : erreurs) {
                System.out.println("- " + erreur);
            }
        }
    }

    /**
     * Vérifie les abonnements aux événements dans le système.
     */
    private void verifierAbonnements() {
        Map<String, List<ModuleSpecialise>> abonnements = systeme.getAbonnements();
        if (abonnements.isEmpty()) {
            System.out.println("Aucun module abonné.");
        } else {
            System.out.println("Modules abonnés par type d'événement :");
            abonnements.forEach((type, modules) -> {
                System.out.println("- " + type + " :");
                for (ModuleSpecialise module : modules) {
                    System.out.println("  * " + module.getClass().getSimpleName());
                }
            });
        }
    }

    /**
     * Recherche une entité par nom complet (normalisé).
     * Nécessite une correspondance exacte après suppression de la casse, des espaces et accents.
     *
     * @return l’entité trouvée ou null
     */
    private Entite rechercherEntiteParNomExact() {
        System.out.print("→ ");
        String saisie = normaliser(scanner.nextLine());

        for (Entite e : participationService.getEntites().values()) {
            if (normaliser(e.getNom()).equals(saisie)) {
                return e;
            }
        }

        System.out.println("Aucune entité ne correspond exactement.");
        afficherEntitesDisponibles();
        return null;
    }

    /**
     * Affiche les entités disponibles si une recherche échoue.
     */
    private void afficherEntitesDisponibles() {
        System.out.println("Entités disponibles :");
        participationService.getEntites().values().stream()
                .map(Entite::getNom)
                .sorted()
                .forEach(nom -> System.out.println("- " + nom));
    }

    /**
     * Supprime les accents, les espaces et met la chaîne en minuscules pour comparaison stricte.
     *
     * @param s chaîne à nettoyer
     * @return chaîne normalisée
     */
    private String normaliser(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}\\s]", "")
                .toLowerCase();
    }
}
