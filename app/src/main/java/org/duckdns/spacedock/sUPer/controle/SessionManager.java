package org.duckdns.spacedock.sUPer.controle;

/**
 * Contrôleur gérant la session à la fois comme singleton de configuration mais aussi avec des méthodes de contrôle
 *
 * @author iconoctopus
 */
public class SessionManager
{
    //première indice non libre de la liste de combattants
    private int currentIndex = 0;

    /**
     * phase actuelle de la session de jeu
     */
    private int currentPhase;

    /**
     * instance du SessionMnager unique
     */
    private static SessionManager instance = null;

    /**
     * pseudo-constructeur public garantissant un et un seul SessionManager par lancement de l'application
     *
     * @return le SessionManager actuel si il a été créé, un nouveau sinon
     */
    public static SessionManager getInstance()
    {
        if (instance == null)
        {
            instance = new SessionManager();
        }
        return (instance);
    }

    /**
     * véritable constructeur, appelé seulement si le SessionManager n'a pas encore été créé
     */
    private SessionManager()
    {
        currentPhase = 1;
    }

    /**
     * Crée un nouveau combattant côté controleur
     *
     * @param p_RM le RM du combattant à créer
     * @return l'indice qui a été affecté au nouveau combattant
     */
    public int addFighter(int p_RM)
    {//TODO : renvoyer plutôt la première case libre, modifier la suppresion des combattants pour qu'elle mette à jour ici une collection d'indices libres
        //renvoie l'indice courant (correspondant au nombre de combattants) puis incrémente l'indice
        return (currentIndex++);
    }

}
