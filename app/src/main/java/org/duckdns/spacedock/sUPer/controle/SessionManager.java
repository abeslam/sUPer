package org.duckdns.spacedock.sUPer.controle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import libupsystem.Arme;
import libupsystem.BasicNPCFighter;
import libupsystem.Perso;

/**
 * Contrôleur gérant la session à la fois comme singleton de configuration mais aussi avec des méthodes de contrôle
 *
 * @author iconoctopus
 */
public class SessionManager
{
    /**
     * premier indice libre à la fin de la liste des combattants (il peut y a voir des trous dans la liste)
     */
    private int currentIndex = 0;
    /**
     * liste des indices libres dans la liste des combattants
     */
    private LinkedList<Integer> listFreeIndex = new LinkedList<Integer>();
    private ListIterator<Integer> indexIterator = listFreeIndex.listIterator();

    /**
     * liste des personnages
     */
    private ArrayList<Perso> listFighters = new ArrayList<Perso>();

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
     * @param rm le RM du combattant à créer
     * @return l'indice qui a été affecté au nouveau combattant
     */
    public int addFighter(int rm)
    {
        int newIndex;
        BasicNPCFighter newFighter = new BasicNPCFighter(rm);

        if (indexIterator.hasNext())//il y a eu des libérations on renvoie donc la première case libre
        {
            newIndex = (indexIterator.next()).intValue();
            indexIterator.previous();//on a récupéré une valeur, il faut donc la supprimer de la liste car cet indice va désormais être occupé par un combattant
            indexIterator.remove();
            listFighters.set(newIndex, newFighter);//on ajoute le nouveau combattant à la liste à la place de celui qu'il remplace
        } else//pas de case libre, la première case libre est donc l'indice, on l'incrémente après l'avoir récupéré
        {
            newIndex = currentIndex++;
            listFighters.add(newFighter);//le nouveau combattant est ajouté en queue
        }
        return (newIndex);
    }

    /**
     * Supprime un combattant côté contrôleur
     *
     * @param index
     */
    public void delFighter(int index)
    {
        indexIterator.add(new Integer(index));
        indexIterator.previous();//replace le curseur au cran d'avant afin que hasNext() puisse répondre true lors de sa prochaine interrogation

        listFighters.set(index, null);//le combattant est supprimé de la liste (avec set() et pas remove() afin que sa case reste libre pour ne pas bordéliser les indices des autres
    }

    /**
     * renvoie le nom d'un combattant
     *
     * @param index son indice dans les listes
     * @return
     */
    public String getName(int index)
    {
        return listFighters.get(index).getLibellePerso();
    }

    /**
     * affecte l'arme passée en paramétre au combattant d'indice passé en paramétre
     *
     * @param p_index
     * @param p_rolled
     * @param p_kept
     */
    public void setArme(int p_index, int p_rolled, int p_kept)
    {
        listFighters.get(p_index).setArme(new Arme(p_rolled, p_kept, 0, 0));
    }

    public int getVDRolled(int p_index)
    {
        return listFighters.get(p_index).getArme().getDesLances();
    }

    public int getVDKept(int p_index)
    {
        return listFighters.get(p_index).getArme().getDesGardes();
    }



}
