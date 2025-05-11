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

    /**
     * Charge toutes les entités et participations à partir des fichiers .tsv.
     *
     * @param participationService service pour enregistrer les participations
     */
    public void importerTout(ParticipationService participationService) {
        chargerPersonnes("data/personnes.tsv");
        chargerOrganisations("data/organisations.tsv");
        chargerMedias("data/medias.tsv");
        chargerParticipation("data/personne-media.tsv", participationService);
        chargerParticipation("data/organisation-media.tsv", participationService);
        chargerParticipation("data/personne-organisation.tsv", participationService);
        chargerParticipation("data/organisation-organisation.tsv", participationService);

        participationService.setEntites(entitesParNom);
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
            System.err.println("Erreur lecture fichier personnes : " + chemin);
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
            System.err.println("Erreur lecture fichier organisations : " + chemin);
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
            System.err.println("Erreur lecture fichier médias : " + chemin);
        }
    }

    /**
     * Charge les participations entre entités à partir d’un fichier TSV.
     * Seules les lignes contenant un lien "égal à" sont traitées.
     * Gère les erreurs de format et les entités manquantes.
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

                if (parties.length >= 3 && parties[2].toLowerCase().contains("égal")) {
                    String source = parties[0].trim();
                    String cible = parties[1].trim();
                    String pourcentageStr = parties[2]
                            .replaceAll("[^\\d,.]", "")
                            .replace(",", ".")
                            .trim();

                    try {
                        double pourcentage = Double.parseDouble(pourcentageStr);

                        Entite e1 = ps.getEntiteParNom(source.toLowerCase().trim());
                        Entite e2 = ps.getEntiteParNom(cible.toLowerCase().trim());

                        if (e1 != null && e2 != null) {
                            ps.ajouterParticipation(e1, e2, pourcentage);
                        } else {
                            System.err.println("Entité inconnue : " + source + " ou " + cible + " (ligne ignorée)");
                        }

                    } catch (NumberFormatException ex) {
                        System.err.println("Erreur de format numérique : " + pourcentageStr + " (ligne ignorée)");
                    }

                } else {
                    System.err.println("Ligne non conforme ignorée : " + ligne);
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + chemin);
        }
    }
}
