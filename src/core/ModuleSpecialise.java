package core;

import model.Evenement;

/**
 * Interface pour les modules spécialisés qui observent certains types d'événements.
 * Chaque module décide s'il est concerné par un événement et peut déclencher une alerte à la Vigie.
 *
 * @see Evenement
 */
public interface ModuleSpecialise {

    /**
     * Méthode appelée lorsqu'un événement est diffusé dans le système.
     * Le module vérifie s'il est concerné, et peut envoyer une alerte à la Vigie.
     *
     * @param evenement l'événement à traiter
     */
    void traiter(Evenement evenement);
}
