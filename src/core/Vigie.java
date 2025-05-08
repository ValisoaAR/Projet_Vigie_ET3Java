package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant la Vigie des Médias.
 * Elle centralise les alertes envoyées par les modules spécialisés,
 * les historise, et les affiche en console.
 */
public class Vigie {
    private List<String> historiqueAlertes;

    /**
     * Initialise la vigie avec un historique vide.
     */
    public Vigie() {
        this.historiqueAlertes = new ArrayList<>();
    }

    /**
     * Reçoit une alerte depuis un module spécialisé.
     * Elle est enregistrée et affichée en console.
     *
     * @param alerte message d’alerte reçu
     */
    public void recevoirAlerte(String alerte) {
        historiqueAlertes.add(alerte);
        System.out.println("[VIGIE] " + alerte);
    }

    /**
     * @return l’historique complet des alertes reçues
     */
    public List<String> getHistoriqueAlertes() {
        return historiqueAlertes;
    }

    /**
     * Affiche tout l’historique des alertes en console.
     */
    public void afficherHistorique() {
        System.out.println("=== Historique des alertes ===");
        for (String alerte : historiqueAlertes) {
            System.out.println("- " + alerte);
        }
    }
}
