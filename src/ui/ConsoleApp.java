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
        // Importation des données
        dataImport.importerTout(participationService);

        // Vérification des erreurs d'importation
        afficherErreursImportation();

        // Initialisation des modules spécialisés
        initialiserModules();

        // Menu interactif
        boolean continuer = true;
        while (continuer) {
            afficherMenuPrincipal();
            String choix = scanner.nextLine();
            switch (choix) {
                case "1" -> menuAffichages();
                case "2" -> menuSimulations();
                case "3" -> menuHistoriques();
                case "4" -> sAbonner();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }

        System.out.println("Fin de l'application.");
    }

    /**
     * Affiche le menu principal.
     */
    private void afficherMenuPrincipal() {
        System.out.println("""
                \n=== MENU PRINCIPAL ===
                1. Affichages
                2. Simulations d'événements
                3. Historiques
                4. S'abonner à des médias ou personnes
                0. Quitter
                """);
        System.out.print("Votre choix : ");
    }

    /**
     * Menu pour les affichages.
     */
    private void menuAffichages() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("""
                    \n=== AFFICHAGES ===
                    1. Afficher toutes les entités
                    2. Afficher les participations
                    3. Afficher les entités par nombre de médias possédés
                    4. Afficher les entités par nombre d'organisations possédées
                    5. Saisir une entité pour afficher ses propriétaires et propriétés
                    0. Retour au menu principal
                    """);
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine();
            switch (choix) {
                case "1" -> afficherEntites();
                case "2" -> afficherParticipations();
                case "3" -> afficherClassementMedias();
                case "4" -> afficherClassementOrganisations();
                case "5" -> rechercherEntiteParNomExact();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    /**
     * Menu pour les simulations d'événements.
     */
    private void menuSimulations() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("""
                    \n=== SIMULATIONS D'ÉVÉNEMENTS ===
                    1. Simuler une publication
                    2. Simuler un rachat
                    0. Retour au menu principal
                    """);
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine();
            switch (choix) {
                case "1" -> simulerPublication();
                case "2" -> simulerRachat();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    /**
     * Menu pour les historiques.
     */
    private void menuHistoriques() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("""
                    \n=== HISTORIQUES ===
                    1. Afficher l'historique des publications
                    2. Afficher l'historique des alertes
                    0. Retour au menu principal
                    """);
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine();
            switch (choix) {
                case "1" -> afficherHistoriquePublications();
                case "2" -> afficherAlertes();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    /**
     * Permet à l'utilisateur de s'abonner à des médias ou personnes.
     */
    private void sAbonner() {
        System.out.println("\n=== S'ABONNER À DES MÉDIAS OU PERSONNES ===");
        System.out.println("1. S'abonner à des médias");
        System.out.println("2. S'abonner à des personnes");
        System.out.print("Votre choix : ");
        String choix = scanner.nextLine();

        switch (choix) {
            case "1" -> sAbonnerMedias();
            case "2" -> sAbonnerPersonnes();
            default -> System.out.println("Choix invalide.");
        }
    }

    /**
     * Permet de s'abonner à des médias.
     */
    private void sAbonnerMedias() {
        System.out.println("\n=== LISTE DES MÉDIAS DISPONIBLES ===");
        List<Media> medias = participationService.getEntites().values().stream()
                .filter(entite -> entite instanceof Media)
                .map(entite -> (Media) entite)
                .toList();

        if (medias.isEmpty()) {
            System.out.println("Aucun média disponible.");
            return;
        }

        for (int i = 0; i < medias.size(); i++) {
            System.out.println((i + 1) + ". " + medias.get(i).getNom());
        }

        System.out.print("Entrez les numéros des médias à surveiller (séparés par des virgules) : ");
        String saisie = scanner.nextLine();
        String[] indices = saisie.split(",");

        List<Media> mediasSelectionnes = new ArrayList<>();
        for (String index : indices) {
            try {
                int i = Integer.parseInt(index.trim()) - 1;
                if (i >= 0 && i < medias.size()) {
                    mediasSelectionnes.add(medias.get(i));
                } else {
                    System.out.println("Numéro invalide : " + (i + 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide : " + index);
            }
        }

        if (!mediasSelectionnes.isEmpty()) {
            moduleMedia = new ModuleSuiviMedia(mediasSelectionnes, vigie);
            systeme.abonner("rachat", moduleMedia);
            System.out.println("Abonnement aux médias sélectionnés effectué.");
        } else {
            System.out.println("Aucun média sélectionné.");
        }
    }

    /**
     * Permet de s'abonner à des personnes.
     */
    private void sAbonnerPersonnes() {
        System.out.println("\n=== LISTE DES PERSONNES DISPONIBLES ===");
        List<PersonnePhysique> personnes = participationService.getEntites().values().stream()
                .filter(entite -> entite instanceof PersonnePhysique)
                .map(entite -> (PersonnePhysique) entite)
                .toList();

        if (personnes.isEmpty()) {
            System.out.println("Aucune personne disponible.");
            return;
        }

        for (int i = 0; i < personnes.size(); i++) {
            System.out.println((i + 1) + ". " + personnes.get(i).getNom());
        }

        System.out.print("Entrez les numéros des personnes à surveiller (séparés par des virgules) : ");
        String saisie = scanner.nextLine();
        String[] indices = saisie.split(",");

        List<PersonnePhysique> personnesSelectionnees = new ArrayList<>();
        for (String index : indices) {
            try {
                int i = Integer.parseInt(index.trim()) - 1;
                if (i >= 0 && i < personnes.size()) {
                    personnesSelectionnees.add(personnes.get(i));
                } else {
                    System.out.println("Numéro invalide : " + (i + 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide : " + index);
            }
        }

        if (!personnesSelectionnees.isEmpty()) {
            modulePersonne = new ModuleSuiviPersonne(personnesSelectionnees, vigie, participationService);
            systeme.abonner("publication", modulePersonne);
            System.out.println("Abonnement aux personnes sélectionnées effectué.");
        } else {
            System.out.println("Aucune personne sélectionnée.");
        }
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
     * Affiche les entités classées par nombre de médias possédés.
     */
    private void afficherClassementMedias() {
        Map<Entite, Integer> mediasPossedes = participationService.getNombreMediasPossedes();
        if (mediasPossedes.isEmpty()) {
            System.out.println("Aucune entité ne possède de médias.");
            return;
        }

        System.out.println("\n=== Classement des entités par nombre de médias possédés ===");
        mediasPossedes.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Tri décroissant
                .forEach(entry -> System.out.println(entry.getKey().getNom() + " : " + entry.getValue() + " média(s)"));
    }

    /**
     * Affiche les entités classées par nombre d'organisations possédées.
     */
    private void afficherClassementOrganisations() {
        Map<Entite, Integer> organisationsPossedees = participationService.getNombreOrganisationsPossedees();
        if (organisationsPossedees.isEmpty()) {
            System.out.println("Aucune entité ne possède d'organisations.");
            return;
        }

        System.out.println("\n=== Classement des entités par nombre d'organisations possédées ===");
        organisationsPossedees.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Tri décroissant
                .forEach(entry -> System.out.println(entry.getKey().getNom() + " : " + entry.getValue() + " organisation(s)"));
    }

    /**
     * Recherche une entité par nom complet (normalisé).
     * Si une entité est trouvée, affiche ses propriétaires et propriétés.
     */
    private Entite rechercherEntiteParNomExact() {
        System.out.print("Entrez le nom de l'entité : ");
        String saisie = normaliser(scanner.nextLine());

        for (Entite e : participationService.getEntites().values()) {
            if (normaliser(e.getNom()).equals(saisie)) {
                afficherDetailsEntite(e);
                return e;
            }
        }

        System.out.println("Aucune entité ne correspond exactement.");
        afficherEntitesDisponibles();
        return null;
    }

    /**
     * Affiche les détails d'une entité, y compris ses propriétaires et ses propriétés.
     *
     * @param entite L'entité dont les détails doivent être affichés.
     */
    private void afficherDetailsEntite(Entite entite) {
        System.out.println("\n=== Détails de l'entité ===");
        System.out.println("Nom : " + entite.getNom());

        // Affiche les propriétaires de l'entité
        List<Participation> proprietaires = participationService.getProprietaires(entite);
        if (proprietaires.isEmpty()) {
            System.out.println("Propriétaires : Aucun propriétaire enregistré.");
        } else {
            System.out.println("Propriétaires :");
            for (Participation participation : proprietaires) {
                System.out.println("  - " + participation.getProprietaire().getNom() +
                        " (Parts : " + participation.getPourcentage() + "%)");
            }
        }

        // Affiche les propriétés de l'entité
        List<Participation> proprietes = participationService.getProprietes(entite);
        if (proprietes.isEmpty()) {
            System.out.println("Propriétés : Aucune propriété enregistrée.");
        } else {
            System.out.println("Propriétés :");
            for (Participation participation : proprietes) {
                System.out.println("  - " + participation.getCible().getNom() +
                        " (Parts : " + participation.getPourcentage() + "%)");
            }
        }
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
     */
    private String normaliser(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}\\s]", "")
                .toLowerCase();
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
     * Initialise les modules spécialisés pour les entités surveillées.
     */
    private void initialiserModules() {
        Entite personne = participationService.getEntiteParNom("vincent bolloré");
        if (personne instanceof PersonnePhysique) {
            modulePersonne = new ModuleSuiviPersonne(List.of((PersonnePhysique) personne), vigie, participationService);
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
     * Simule une publication mentionnant plusieurs entités.
     */
    private void simulerPublication() {
        System.out.println("\n=== SIMULATION D'UNE PUBLICATION ===");

        System.out.print("Nom du média source : ");
        String nomMedia = scanner.nextLine();
        Media media = (Media) participationService.getEntiteParNom(nomMedia);
        if (media == null) {
            System.out.println("Média introuvable.");
            return;
        }

        System.out.print("Contenu de la publication : ");
        String contenu = scanner.nextLine();

        System.out.print("Type de publication (article, reportage, interview) : ");
        String typePublication = scanner.nextLine();

        System.out.print("Entrez les noms des entités mentionnées (séparés par des virgules) : ");
        String saisieMentions = scanner.nextLine();
        List<Entite> mentions = new ArrayList<>();
        for (String nom : saisieMentions.split(",")) {
            Entite entite = participationService.getEntiteParNom(nom.trim());
            if (entite != null) {
                mentions.add(entite);
            } else {
                System.out.println("Entité introuvable : " + nom.trim());
            }
        }

        Evenement publication = new Evenement(
                LocalDate.now(),
                media,
                contenu,
                typePublication,
                mentions
        );

        systeme.diffuserEvenement(publication);
        System.out.println("Publication simulée avec succès : ");
    }

    /**
     * Simule un événement de rachat.
     */
    private void simulerRachat() {
        System.out.println("\n=== SIMULATION D'UN RACHAT ===");

        System.out.print("Nom de l'acheteur : ");
        String nomAcheteur = scanner.nextLine();
        Entite acheteur = participationService.getEntiteParNom(nomAcheteur);
        if (acheteur == null) {
            System.out.println("Acheteur introuvable.");
            return;
        }

        System.out.print("Nom du vendeur : ");
        String nomVendeur = scanner.nextLine();
        Entite vendeur = participationService.getEntiteParNom(nomVendeur);
        if (vendeur == null) {
            System.out.println("Vendeur introuvable.");
            return;
        }

        System.out.print("Nom de la cible (média ou organisation) : ");
        String nomCible = scanner.nextLine();
        Entite cible = participationService.getEntiteParNom(nomCible);
        if (cible == null) {
            System.out.println("Cible introuvable.");
            return;
        }

        System.out.print("Pourcentage des parts transférées : ");
        double pourcentage;
        try {
            pourcentage = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Pourcentage invalide.");
            return;
        }

        Evenement rachat = new Evenement(
                LocalDate.now(),
                acheteur,
                vendeur,
                cible,
                "Rachat simulé",
                pourcentage
        );
        systeme.traiterRachat(rachat); //traite le rachat
        systeme.diffuserEvenement(rachat);
        System.out.println("Rachat simulé avec succès.");
    }

    /**
     * Affiche l'historique des publications.
     */
    private void afficherHistoriquePublications() {
        System.out.println("\n=== HISTORIQUE DES PUBLICATIONS ===");
        List<Evenement> historique = systeme.getHistorique("publication");
        if (historique.isEmpty()) {
            System.out.println("Aucune publication enregistrée.");
            return;
        }

        for (Evenement evenement : historique) {
            System.out.println("- " + evenement.getDate() + " : " + evenement.getContenu());
        }
    }

    /**
     * Affiche les alertes reçues par la Vigie.
     */
    private void afficherAlertes() {
        System.out.println("\n=== ALERTES REÇUES ===");
        List<String> alertes = vigie.getHistoriqueAlertes();
        if (alertes.isEmpty()) {
            System.out.println("Aucune alerte reçue.");
            return;
        }

        for (String alerte : alertes) {
            System.out.println("- " + alerte);
        }
    }
    }
