package ui;

import core.*;
import model.*;
import modules.ModuleSuiviMedia;
import modules.ModuleSuiviPersonne;

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
        dataImport.importerTout(participationService);

        Entite personne = participationService.getEntiteParNom("vincent bolloré");
        Entite media = participationService.getEntiteParNom("le monde");

        if (personne instanceof PersonnePhysique) {
            modulePersonne = new ModuleSuiviPersonne(List.of((PersonnePhysique) personne), vigie);
            systeme.abonner("publication", modulePersonne);
        }

        if (media instanceof Media) {
            moduleMedia = new ModuleSuiviMedia(List.of((Media) media), vigie);
            systeme.abonner("rachat", moduleMedia);
        }

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
                case "9" -> testImportation();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }

        System.out.println("Fin de l'application.");
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
                9. Vérifier les entités importées (test)
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
        } catch (NumberFormatException e) {
            System.out.println("Format invalide.");
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
        if (modulePersonne != null) modulePersonne.afficherHistorique();
        if (moduleMedia != null) moduleMedia.afficherHistorique();
    }

    /**
     * Affiche les entités importées avec leur type et le nombre de possessions.
     */
    private void testImportation() {
        Map<String, Entite> entites = participationService.getEntites();
        List<Participation> participations = participationService.getParticipations();

        for (String nom : entites.keySet()) {
            Entite e = entites.get(nom);
            String type = (e instanceof Media) ? "Media"
                    : (e instanceof PersonnePhysique) ? "PersonnePhysique"
                    : (e instanceof PersonneMorale) ? "PersonneMorale"
                    : "Inconnu";

            long nbPossessions = participations.stream()
                    .filter(p -> p.getProprietaire().equals(e))
                    .count();

            System.out.printf("- %-30s | Type : %-15s | Possède : %d\n", e.getNom(), type, nbPossessions);
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
        return null;
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
