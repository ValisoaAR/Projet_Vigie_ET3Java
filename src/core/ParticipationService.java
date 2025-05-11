package core;

import model.Entite;
import model.Participation;

import java.util.*;

/**
 * Gère les participations entre entités (médias, organisations, personnes).
 * Permet l’ajout, le transfert, et la consultation des parts détenues.
 */
public class ParticipationService {
    private final List<Participation> participations = new ArrayList<>();
    private Map<String, Entite> entitesParNom = new HashMap<>();

    /**
     * Recherche une participation spécifique par propriétaire et cible.
     *
     * @param proprietaire l'entité propriétaire
     * @param cible        l'entité ou média cible
     * @return la participation correspondante ou null si elle n'existe pas
     */
    private Participation trouverParticipation(Entite proprietaire, Entite cible) {
        for (Participation p : participations) {
            if (p.getProprietaire().equals(proprietaire) && p.getCible().equals(cible)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Calcule le pourcentage total de parts pour une cible donnée.
     *
     * @param cible l'entité ou média cible
     * @return le pourcentage total de parts
     */
    private double calculerPourcentageTotal(Entite cible) {
        return participations.stream()
                .filter(p -> p.getCible().equals(cible))
                .mapToDouble(Participation::getPourcentage)
                .sum();
    }

    /**
     * Ajoute une nouvelle participation au système.
     * Si une participation similaire existe déjà, met à jour le pourcentage.
     *
     * @param proprietaire entité détentrice
     * @param cible        entité ou média détenu
     * @param pourcentage  pourcentage de parts (0–100)
     */
    public void ajouterParticipation(Entite proprietaire, Entite cible, double pourcentage) {
        if (pourcentage < 0 || pourcentage > 100) {
            System.err.println("Pourcentage invalide : " + pourcentage);
            return;
        }

        double totalPourcentage = calculerPourcentageTotal(cible);
        if (totalPourcentage + pourcentage > 100) {
            System.err.println("Pourcentage total dépasse 100 pour la cible : " + cible);
            return;
        }

        Participation participationExistante = trouverParticipation(proprietaire, cible);
        if (participationExistante != null) {
            participationExistante.setPourcentage(participationExistante.getPourcentage() + pourcentage);
        } else {
            participations.add(new Participation(proprietaire, cible, pourcentage));
        }
    }

    /**
     * Transfère des parts d'une entité à une autre pour une cible donnée.
     *
     * @param vendeur    l'entité qui vend les parts
     * @param acheteur   l'entité qui achète les parts
     * @param cible      la cible des parts
     * @param pourcentage le pourcentage de parts à transférer
     * @return true si le transfert a réussi, false sinon
     */
    public boolean transfererParts(Entite vendeur, Entite acheteur, Entite cible, double pourcentage) {
        // Recherche de la participation du vendeur
        Participation participationVendeur = participations.stream()
                .filter(p -> p.getProprietaire().equals(vendeur) && p.getCible().equals(cible))
                .findFirst()
                .orElse(null);

        if (participationVendeur == null) {
            System.err.println("Le vendeur ne possède pas de parts dans la cible.");
            return false;
        }

        // Vérifie que le vendeur possède suffisamment de parts
        if (participationVendeur.getPourcentage() < pourcentage) {
            System.err.println("Le vendeur ne possède pas suffisamment de parts pour ce transfert.");
            return false;
        }

        // Réduit les parts du vendeur
        participationVendeur.setPourcentage(participationVendeur.getPourcentage() - pourcentage);

        // Recherche ou crée la participation de l'acheteur
        Participation participationAcheteur = participations.stream()
                .filter(p -> p.getProprietaire().equals(acheteur) && p.getCible().equals(cible))
                .findFirst()
                .orElse(null);

        if (participationAcheteur == null) {
            participationAcheteur = new Participation(acheteur, cible, pourcentage);
            participations.add(participationAcheteur);
        } else {
            participationAcheteur.setPourcentage(participationAcheteur.getPourcentage() + pourcentage);
        }

        return true;
    }

    /**
     * Enregistre les entités disponibles (clé = nom).
     *
     * @param mapEntites map des entités par nom
     */
    public void setEntites(Map<String, Entite> mapEntites) {
        this.entitesParNom = mapEntites;
    }

    /**
     * Retourne une entité par son nom normalisé (minuscules + sans espaces superflus).
     *
     * @param nom nom de l’entité recherchée
     * @return entité correspondante ou null
     */
    public Entite getEntiteParNom(String nom) {
        return entitesParNom.get(nom.toLowerCase().trim());
    }

    /**
     * Retourne la liste de toutes les participations.
     *
     * @return liste des participations
     */
    public List<Participation> getParticipations() {
        return participations;
    }

    /**
     * Retourne la map des entités connues (par nom en minuscules).
     *
     * @return map des entités
     */
    public Map<String, Entite> getEntites() {
        return entitesParNom;
    }

    /**
     * Retourne la liste des participations d'une entité donnée.
     *
     * @param proprietaire l'entité propriétaire
     * @return liste des participations de l'entité
     */
    public List<Participation> getParticipationsParEntite(Entite proprietaire) {
        List<Participation> result = new ArrayList<>();
        for (Participation p : participations) {
            if (p.getProprietaire().equals(proprietaire)) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Affiche les participations dans la console.
     */
    public void afficherParticipations() {
        for (Participation p : participations) {
            System.out.println(p);
        }
    }
}
