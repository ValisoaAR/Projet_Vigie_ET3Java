package core;

import model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Classe utilitaire permettant de charger les entités et participations
 * à partir des fichiers .tsv fournis.
 */
public class DataImport {

    private final Map<String, Entite> entitesParNom = new HashMap<>();
    private final Map<String, Media> mediasParNom = new HashMap<>();
    private final List<String> erreurs = new ArrayList<>(); // Liste des erreurs d'importation

    // Centralisation des chemins des fichiers
    private static final String CHEMIN_PERSONNES = "data/personnes.tsv";
    private static final String CHEMIN_ORGANISATIONS = "data/organisations.tsv";
    private static final String CHEMIN_MEDIAS = "data/medias.tsv";
    private static final String CHEMIN_PERSONNE_MEDIA = "data/personne-media.tsv";
    private static final String CHEMIN_ORGANISATION_MEDIA = "data/organisation-media.tsv";
    private static final String CHEMIN_PERSONNE_ORGANISATION = "data/personne-organisation.tsv";
    private static final String CHEMIN_ORGANISATION_ORGANISATION = "data/organisation-organisation.tsv";

    /**
     * Charge toutes les entités et participations à partir des fichiers .tsv.
     *
     * @param participationService service pour enregistrer les participations
     */
    public void importerTout(ParticipationService participationService) {
        try {
            chargerPersonnes(CHEMIN_PERSONNES);
            chargerOrganisations(CHEMIN_ORGANISATIONS);
            chargerMedias(CHEMIN_MEDIAS);
            chargerParticipation(CHEMIN_PERSONNE_MEDIA, participationService);
            chargerParticipation(CHEMIN_ORGANISATION_MEDIA, participationService);
            chargerParticipation(CHEMIN_PERSONNE_ORGANISATION, participationService);
            chargerParticipation(CHEMIN_ORGANISATION_ORGANISATION, participationService);

            participationService.setEntites(entitesParNom);
        } catch (Exception e) {
            erreurs.add("Erreur générale lors de l'importation : " + e.getMessage());
        }
    }

    /**
     * Retourne la liste des erreurs rencontrées lors de l'importation.
     *
     * @return liste des erreurs
     */
    public List<String> getErreurs() {
        return erreurs;
    }

    /**
     * Charge les personnes physiques à partir d'un fichier TSV.
     * Chaque ligne correspond à une personne physique.
     *
     * @param chemin chemin relatif du fichier TSV à lire
     */
    private void chargerPersonnes(String chemin) {
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            String ligne;
            br.readLine(); // ignorer l'en-tête
            while ((ligne = br.readLine()) != null) {
                String[] parties = ligne.split("\t");
                if (parties.length > 0) {
                    String nom = parties[0].trim();
                    if (nom != null && !nom.isBlank() && !entitesParNom.containsKey(nom.toLowerCase())) {
                        entitesParNom.put(nom.toLowerCase(), new PersonnePhysique(nom));
                    }
                }
            }
        } catch (IOException e) {
            erreurs.add("Erreur lors de la lecture du fichier personnes : " + chemin + " - " + e.getMessage());
        }
    }

    /**
     * Charge les organisations (personnes morales) à partir d'un fichier TSV.
     * Chaque ligne correspond à une organisation.
     *
     * @param chemin chemin relatif du fichier TSV à lire
     */
    private void chargerOrganisations(String chemin) {
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            String ligne;
            br.readLine(); // ignorer l'en-tête
            while ((ligne = br.readLine()) != null) {
                String[] parties = ligne.split("\t");
                if (parties.length > 0) {
                    String nom = parties[0].trim();
                    if (nom != null && !nom.isBlank() && !entitesParNom.containsKey(nom.toLowerCase())) {
                        entitesParNom.put(nom.toLowerCase(), new PersonneMorale(nom));
                    }
                }
            }
        } catch (IOException e) {
            erreurs.add("Erreur lors de la lecture du fichier organisations : " + chemin + " - " + e.getMessage());
        }
    }

    /**
     * Charge les médias à partir d'un fichier TSV.
     * Chaque ligne correspond à un média, avec un nom et un type.
     *
     * @param chemin chemin relatif du fichier TSV à lire
     */
    private void chargerMedias(String chemin) {
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            String ligne;
            br.readLine(); // ignorer l'en-tête
            while ((ligne = br.readLine()) != null) {
                String[] parties = ligne.split("\t");
                if (parties.length > 0) {
                    String nom = parties[0].trim();
                    String type = (parties.length > 1) ? parties[1].trim() : "inconnu";
                    if (nom != null && !nom.isBlank() && !mediasParNom.containsKey(nom.toLowerCase())) {
                        Media m = new Media(nom, type);
                        mediasParNom.put(nom.toLowerCase(), m);
                        entitesParNom.put(nom.toLowerCase(), m);
                    }
                }
            }
        } catch (IOException e) {
            erreurs.add("Erreur lors de la lecture du fichier médias : " + chemin + " - " + e.getMessage());
        }
    }

    /**
     * Charge les participations entre entités à partir d’un fichier TSV.
     * Seules les lignes contenant un lien "égal à" ou similaire sont traitées.
     * Gère les erreurs de format, les entités manquantes et les pourcentages invalides.
     *
     * @param chemin chemin relatif du fichier TSV à lire
     * @param ps     service de gestion des participations
     */
    private void chargerParticipation(String chemin, ParticipationService ps) {
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            String ligne;
            br.readLine(); // ignorer l'en-tête
            while ((ligne = br.readLine()) != null) {
                String[] parties = ligne.split("\t");

                // Vérification du format attendu
                if (parties.length >= 5 && parties[2].toLowerCase().contains("égal")) {
                    String sourceNom = parties[1].trim();
                    String cibleNom = parties[4].trim();
                    String pourcentageStr = parties[3]
                            .replaceAll("[^\\d,.]", "")
                            .replace(",", ".")
                            .trim();

                    try {
                        double pourcentage = Double.parseDouble(pourcentageStr);

                        // Vérification que le pourcentage est valide
                        if (pourcentage < 0 || pourcentage > 100) {
                            erreurs.add("Pourcentage invalide (" + pourcentage + ") pour la ligne : " + ligne);
                            continue;
                        }

                        // Récupérer les entités source et cible
                        Entite source = entitesParNom.get(sourceNom.toLowerCase());
                        Entite cible = entitesParNom.get(cibleNom.toLowerCase());

                        if (source == null) {
                            erreurs.add("Source inconnue : " + sourceNom + " (ligne ignorée : " + ligne + ")");
                            continue;
                        }

                        if (cible == null) {
                            erreurs.add("Cible inconnue : " + cibleNom + " (ligne ignorée : " + ligne + ")");
                            continue;
                        }

                        // Ajouter la participation via ParticipationService
                        ps.ajouterParticipation(source, cible, pourcentage);

                    } catch (NumberFormatException ex) {
                        erreurs.add("Erreur de format numérique : " + pourcentageStr + " (ligne ignorée : " + ligne + ")");
                    }
                } else {
                    erreurs.add("Ligne non conforme ignorée : " + ligne);
                }
            }

        } catch (IOException e) {
            erreurs.add("Erreur de lecture du fichier : " + chemin + " - " + e.getMessage());
        }
    }
}
