import core.*;
import model.*;
import modules.ModuleSuiviMedia;
import modules.ModuleSuiviPersonne;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;

/**
 * Classe de test fonctionnel permettant de simuler l’ensemble du système :
 * importation, configuration des modules, simulation d’événements, affichage des résultats.
 */
public class TestFonctionnel {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Point d’entrée du test fonctionnel.
     *
     * @param args arguments inutilisés
     */
    public static void main(String[] args) {
        System.out.println("===== Test Fonctionnel =====");

        // Initialisation des services
        ParticipationService ps = new ParticipationService();
        Vigie vigie = new Vigie();
        SystemeEvenementiel systeme = new SystemeEvenementiel(ps);
        DataImport dataImport = new DataImport();

        // Importation des données
        System.out.println("\n--- Importation des données ---");
        dataImport.importerTout(ps);

        // Vérification des erreurs d'importation
        afficherErreursImportation(dataImport);

        // Configuration des modules de test
        System.out.println("\n--- Configuration des modules ---");
        System.out.println("Nom exact (complet) de la personne à surveiller :");
        PersonnePhysique personne = (PersonnePhysique) rechercherEntiteParNomExact(ps, PersonnePhysique.class);

        System.out.println("Nom exact du média à surveiller :");
        Media media = (Media) rechercherEntiteParNomExact(ps, Media.class);

        ModuleSuiviPersonne mp = new ModuleSuiviPersonne(List.of(personne), vigie);
        ModuleSuiviMedia mm = new ModuleSuiviMedia(List.of(media), vigie);
        systeme.abonner("publication", mp);
        systeme.abonner("rachat", mm);

        // Vérification des abonnements
        verifierAbonnements(systeme);

        // Simulation d’un événement de publication
        System.out.println("\n--- Simulation d’un événement de publication ---");
        Evenement pub = new Evenement("publication", LocalDate.now(), personne,
                media.getNom() + " publie un article sur " + personne.getNom());
        systeme.diffuserEvenement(pub);

        // Simulation d’un rachat
        System.out.println("\n--- Simulation d’un événement de rachat ---");
        System.out.println("Nom exact de l’acheteur :");
        Entite acheteur = rechercherEntiteParNomExact(ps, Entite.class);

        System.out.println("Nom exact du vendeur :");
        Entite vendeur = rechercherEntiteParNomExact(ps, Entite.class);

        System.out.print("Pourcentage : ");
        double pourcentage = Double.parseDouble(scanner.nextLine().replace(",", "."));

        Evenement rachat = new Evenement("rachat", LocalDate.now(), null,
                acheteur.getNom() + ", " + vendeur.getNom() + ", " + media.getNom() + ", " + pourcentage);
        systeme.diffuserEvenement(rachat);

        // Résultats
        System.out.println("\n--- Résultats ---");
        System.out.println("\n--- Participations ---");
        ps.afficherParticipations();

        System.out.println("\n--- Alertes ---");
        vigie.afficherHistorique();

        System.out.println("\n--- Historique Module Personne ---");
        mp.afficherHistorique();

        System.out.println("\n--- Historique Module Media ---");
        mm.afficherHistorique();

        System.out.println("\n===== Fin du test =====");
    }

    /**
     * Affiche les erreurs rencontrées lors de l'importation des fichiers.
     *
     * @param dataImport instance de DataImport
     */
    private static void afficherErreursImportation(DataImport dataImport) {
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
     *
     * @param systeme instance de SystemeEvenementiel
     */
    private static void verifierAbonnements(SystemeEvenementiel systeme) {
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
     * Recherche une entité par son nom exact (après normalisation) dans un type donné.
     * Ignore les majuscules, accents et espaces.
     *
     * @param ps   le service de participations
     * @param type le type d’entité recherché
     * @return l’entité trouvée, ou null si aucune correspondance exacte
     */
    private static Entite rechercherEntiteParNomExact(ParticipationService ps, Class<?> type) {
        System.out.print("→ ");
        String saisie = normaliser(scanner.nextLine());

        for (Entite e : ps.getEntites().values()) {
            if (type.isInstance(e) && normaliser(e.getNom()).equals(saisie)) {
                return e;
            }
        }

        System.out.println("Aucune entité ne correspond exactement à cette saisie.");
        afficherEntitesDisponibles(ps);
        return null;
    }

    /**
     * Affiche les entités disponibles.
     *
     * @param ps le service de participations
     */
    private static void afficherEntitesDisponibles(ParticipationService ps) {
        System.out.println("Entités disponibles :");
        ps.getEntites().values().stream()
                .map(Entite::getNom)
                .sorted()
                .forEach(nom -> System.out.println("- " + nom));
    }

    /**
     * Normalise une chaîne pour faciliter les comparaisons :
     * suppression des accents, espaces, et conversion en minuscules.
     *
     * @param s chaîne à normaliser
     * @return chaîne nettoyée
     */
    private static String normaliser(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}\\s]", "")
                .toLowerCase();
    }
}
