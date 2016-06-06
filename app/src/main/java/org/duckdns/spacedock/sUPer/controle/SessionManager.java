package org.duckdns.spacedock.sUPer.controle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

//TODO : IMPORTANT blinder tous les accès à un objet charcaterassembly en vérifiant que son indice concorde avec celui de la liste!

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
    private LinkedList<Integer> listFreeIndex = new LinkedList<>();
    private ListIterator<Integer> indexIterator = listFreeIndex.listIterator();

    /**
     * liste des personnages
     */
    private ArrayList<CharacterAssembly> listFighters = new ArrayList<>();

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
    public CreationResult addFighter(int rm)
    {
        int newIndex;
        CharacterAssembly newFighter;
        if (indexIterator.hasNext())//il y a eu des libérations on renvoie donc la première case libre
        {
            newIndex = (indexIterator.next()).intValue();
            indexIterator.previous();//on a récupéré une valeur, il faut donc la supprimer de la liste car cet indice va désormais être occupé par un combattant
            indexIterator.remove();
            newFighter = new CharacterAssembly(newIndex, rm);
            listFighters.set(newIndex, newFighter);//on ajoute le nouveau combattant à la liste à la place de celui qu'il remplace
        } else//pas de case libre, la première case libre est donc l'indice, on l'incrémente après l'avoir récupéré
        {
            newIndex = currentIndex++;
            newFighter = new CharacterAssembly(newIndex, rm);
            listFighters.add(newFighter);//le nouveau combattant est ajouté en queue
        }

        boolean isActive = newFighter.isActive(currentPhase);

        return (new CreationResult(newIndex, isActive));
    }

    /**
     * Supprime un combattant côté contrôleur
     *
     * @param index
     */
    public void delFighter(int index)
    {
        indexIterator.add(index);
        indexIterator.previous();//replace le curseur au cran d'avant afin que hasNext() puisse répondre true lors de sa prochaine interrogation

        listFighters.set(index, null);//le combattant est supprimé de la liste (avec set() et pas remove() afin que sa case reste libre pour ne pas bordéliser les indices des autres
    }

    public ArrayList<Integer> nextPhase()
    {
        ++currentPhase;
        ArrayList<Integer> activeIndexes = new ArrayList<>();
        for (int index = 0; index < listFighters.size(); ++index)
        {
            CharacterAssembly currentfighter = listFighters.get(index);
            if (currentfighter != null && currentfighter.isActive(currentPhase))//currentFighter peut très bien être null car on remplace juste les combattants retirés par des null pour conserver les indices
            {
                activeIndexes.add(index);
            }
        }

        return activeIndexes;
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
        listFighters.get(p_index).setArme(p_rolled, p_kept);
    }

    public int getVDRolled(int p_index)
    {
        return listFighters.get(p_index).getVDRolled();
    }

    public int getVDKept(int p_index)
    {
        return listFighters.get(p_index).getVDKept();
    }

    public void setTargetND(int p_index, int p_ND)
    {
        listFighters.get(p_index).setTargetND(p_ND);
    }

    public int getTargetND(int p_index)
    {
        return listFighters.get(p_index).getTargetND();
    }

    public int getFighterND(int p_index)
    {
        return listFighters.get(p_index).getFighterND();
    }

    public int getCurrentPhase()
    {
        return currentPhase;
    }

    public class CreationResult
    {
        int m_fighterIndex;
        boolean m_active;

        public CreationResult(int p_index, boolean p_active)
        {
            m_fighterIndex = p_index;
            m_active = p_active;
        }

        public int getIndex()
        {
            return m_fighterIndex;
        }

        public boolean isActive()
        {
            return (m_active);
        }
    }
}
