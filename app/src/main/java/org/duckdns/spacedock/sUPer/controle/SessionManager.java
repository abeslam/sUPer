package org.duckdns.spacedock.sUPer.controle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

//TODO : IMPORTANT blinder tous les accès à un objet charcaterassembly en vérifiant que son indice concorde avec celui de la liste!
//TODO : les méthodes pour avancer de phase, ajouter un combattant, en retirer un ou en faire attaquer un devraient retourner un objet d'une classe adaptée (pas juste un actionresult générique), l'idée est de supprimer les getter qui brisent l'encapsulation pour les remplacer par un retour d'information intelligent de la part du SessionManager

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
    private int m_currentIndex;

    /**
     * phase actuelle de la session de jeu
     */
    private int m_currentPhase;

    /**
     * tour actuel de la session de jeu
     */
    private int m_currentTurn;

    /**
     * liste des personnages
     */
    private ArrayList<CharacterAssembly> m_listFighters = new ArrayList<>();

    /**
     * liste des indices libres dans la liste des combattants
     */
    private LinkedList<Integer> m_listFreeIndex = new LinkedList<>();
    private ListIterator<Integer> m_indexIterator = m_listFreeIndex.listIterator();

    /**
     * liste des personnages actifs dans la phase en cours
     */
    private LinkedList<Integer> m_listActiveFighters = new LinkedList<>();
    private ListIterator<Integer> m_activeFightersIterator = m_listActiveFighters.listIterator();

    /**
     * instance unique du SessionMnager
     */
    private static SessionManager m_instance = null;//doit être statique pour être appelé par getInstance en contexte statique

    /**
     * pseudo-constructeur public garantissant un et un seul SessionManager par lancement de l'application
     *
     * @return le SessionManager actuel si il a été créé, un nouveau sinon
     */
    public static SessionManager getInstance()//doit être statique pour être appelé hors instanciation d'un objet
    {
        if (m_instance == null)
        {
            m_instance = new SessionManager();
        }
        return (m_instance);
    }

    /**
     * véritable constructeur, appelé seulement si le SessionManager n'a pas encore été créé
     */
    private SessionManager()
    {
        m_currentPhase = 1;
        m_currentTurn = 1;
        m_currentIndex = 0;
    }

    /**
     * Crée un nouveau combattant côté controleur
     *
     * @param p_rm le RM du combattant à créer
     * @return l'indice qui a été affecté au nouveau combattant et un booleen valant vrai si le personnage sera actif dans la phase en cours
     */
    public ActionResult addFighter(int p_rm)
    {
        int newIndex;
        boolean isActive;
        if (p_rm > 0 && p_rm < 6)
        {
            CharacterAssembly newFighter;
            if (m_indexIterator.hasNext())//il y a eu des libérations on renvoie donc la première case libre
            {
                newIndex = (m_indexIterator.next()).intValue();
                m_indexIterator.previous();//on a récupéré une valeur, il faut donc la supprimer de la liste car cet indice va désormais être occupé par un combattant
                m_indexIterator.remove();
                newFighter = new CharacterAssembly(p_rm);
                m_listFighters.set(newIndex, newFighter);//on ajoute le nouveau combattant à la liste à la place de celui qu'il remplace
            } else//pas de case libre, la première case libre est donc l'indice, on l'incrémente après l'avoir récupéré
            {
                newIndex = m_currentIndex++;
                newFighter = new CharacterAssembly(p_rm);
                m_listFighters.add(newFighter);//le nouveau combattant est ajouté en queue
            }

            isActive = newFighter.isActive(m_currentPhase);

            if (isActive)//si le combattant est actif dans la phase courante, on ajoute son indice à la liste idoine
            {
                m_activeFightersIterator.add(newIndex);
            }
        } else
        {
            throw new IllegalArgumentException("RM doit être compris entre 1 et 5 inclus");
        }
        return (new ActionResult(newIndex, isActive));
    }

    /**
     * Supprime un combattant côté contrôleur
     *
     * @param p_index indice du combattant à supprimer
     */
    public void delFighter(int p_index)
    {
        if (p_index >= 0)
        {
            m_indexIterator.add(p_index);
            m_indexIterator.previous();//replace le curseur au cran d'avant afin que hasNext() puisse répondre true lors de sa prochaine interrogation
            m_listFighters.set(p_index, null);//le combattant est supprimé de la liste (avec set() et pas remove() afin que sa case reste libre pour ne pas bordéliser les indices des autres
            m_listActiveFighters.remove(Integer.valueOf(p_index));//on enlève le combattant de la liste des combatants actifs
            m_activeFightersIterator = m_listActiveFighters.listIterator();//reset de l'itérateur associé
        } else
        {
            throw new IllegalArgumentException("Index<0");
        }
    }

    /**
     * passe à la phase suivante voire au tour suivant
     *
     * @return la liste des indices des combattants actifs
     */
    public List<Integer> nextPhase()
    {
        ++m_currentPhase;
        m_listActiveFighters.clear();//la list des combattants actifs est vidée pour être reconstruite
        m_activeFightersIterator = m_listActiveFighters.listIterator();//on reset l'itérateur
        boolean newTurn = false;

        if (m_currentPhase > 10)//on doit passer au tour suivant
        {
            newTurn = true;
            ++m_currentTurn;
            m_currentPhase = 1;
        }

        for (int index = 0; index < m_listFighters.size(); ++index)//parcours de la liste de tous les combattants
        {
            CharacterAssembly currentfighter = m_listFighters.get(index);

            if (currentfighter != null)//currentFighter peut très bien être null car on remplace juste les combattants retirés par des null pour conserver les indices
            {
                if (newTurn)
                {
                    currentfighter.regenInit();//nouveau tour, les combattants voient leur initiative régénérée pour le nouveau tour
                }
                if (currentfighter.isActive(m_currentPhase))
                {
                    m_activeFightersIterator.add(index);//chaque personnage actif dans la phase courante est ajouté à la liste idoine
                }
            }
        }

        return m_listActiveFighters;
    }

    /**
     * fait attaquer un personnage contre le ND de sa cible actuelle
     *
     * @param p_index l'indice du personnage
     * @return une structure contenant les dégâts infligés (0 si échec) et un booléen indiquant si le combattant reste actif après attaque
     */
    public ActionResult attack(int p_index)
    {//TODO vérifier si ce combattant est bien actif
//TODO améliorer la valeur de retour par un booléen indiquant la réussite ou non de l'attaque ainsi que le statut actif explicite du perso plutot que le bricolage actuel basé sur une comparaison à 0 dans la classe appelante
        if (p_index >= 0)
        {
            CharacterAssembly attacker = m_listFighters.get(p_index);
            int degats = attacker.attack(m_currentPhase);
            boolean stillActive = true;
            if (!attacker.isActive(m_currentPhase))//si le combattant n'est plus actif on le retire de la liste de combattans actifs
            {
                m_listActiveFighters.remove(Integer.valueOf(p_index));
                m_activeFightersIterator = m_listActiveFighters.listIterator();
                stillActive = false;
            }
            return (new ActionResult(degats, stillActive));//dans ce cas le paramétre booléen indique si le combattant reste actif, pas si le jet est réussi, si le jet est raté les dégâts vaudront simplement 0
        } else
        {
            throw new IllegalArgumentException("index<0");
        }
    }

    /**
     * Inflige des dégâts au personnage indiqué
     *
     * @param p_index  indice du personnage ciblé
     * @param p_damage dégâts à infliger
     * @return une structure contenant le nombres de blessures légères et graves ainsi que le statut sonné/inconscient du personnage
     */
    public HealthReport hurt(int p_index, int p_damage)
    {
        int nbFlesh = 0;
        int nbDrama = 0;
        boolean isStunned = false;
        boolean isOut;

        if (p_index >= 0 && p_damage >= 0)
        {
            CharacterAssembly victim = m_listFighters.get(p_index);
            victim.hurt(p_damage);

            if (victim.isOut())//si la victime est éliminée on ne va pas plus loin dans l'évaluation de son statut
            {
                isOut = true;
            } else
            {
                isOut = false;
                nbFlesh = victim.getNbFleshWounds();
                nbDrama = victim.getNbDramaWounds();
                isStunned = victim.isStunned();
            }
        } else
        {
            throw new IllegalArgumentException("indice et dégâts doivent être supérieurs ou égaux à 0");
        }
        return (new HealthReport(nbFlesh, nbDrama, isStunned, isOut));
    }


    /**
     * renvoie le nom d'un combattant
     *
     * @param p_index son indice dans les listes
     * @return la chaine représentant son nom, issue de la couche libupsystem
     */
    public String getName(int p_index)
    {
        if (p_index >= 0)
        {
            return m_listFighters.get(p_index).getLibellePerso();
        } else
        {
            throw new IllegalArgumentException("indice<0");
        }
    }

    /**
     * affecte l'arme passée en paramétre au combattant d'indice passé en paramétre
     *
     * @param p_index  indice du combattant
     * @param p_rolled dés lancés
     * @param p_kept   dés gardés
     */
    public void setArme(int p_index, int p_rolled, int p_kept)
    {
        if (p_index >= 0 && p_rolled >= 0 && p_kept >= 0)
        {
            m_listFighters.get(p_index).setArme(p_rolled, p_kept);
        } else
        {
            throw new IllegalArgumentException("indice et nombres de dés doivent être >=0");
        }
    }

    /**
     * @param p_index indice du combattant
     * @return dés lancés de l'arme du combattant
     */
    public int getVDRolled(int p_index)
    {
        if (p_index >= 0)
        {
            return m_listFighters.get(p_index).getVDRolled();
        } else
        {
            throw new IllegalArgumentException("indice < 0");
        }
    }

    /**
     * @param p_index indice du combattant
     * @return dés gardés de l'arme du combattant
     */
    public int getVDKept(int p_index)
    {
        if (p_index >= 0)
        {
            return m_listFighters.get(p_index).getVDKept();
        } else
        {
            throw new IllegalArgumentException("indice < 0");
        }
    }

    /**
     * @param p_index indice du combattant
     * @param p_ND    le ND de la cible
     */
    public void setTargetND(int p_index, int p_ND)
    {
        if (p_index >= 0 && p_ND >= 0)
        {
            m_listFighters.get(p_index).setTargetND(p_ND);
        } else
        {
            throw new IllegalArgumentException("indice et ND doivent être >=0");
        }
    }

    /**
     * @param p_index indice du combattant
     * @return le ND de la cible
     */
    public int getTargetND(int p_index)
    {
        if (p_index >= 0)
        {
            return m_listFighters.get(p_index).getTargetND();
        } else
        {
            throw new IllegalArgumentException("indice < 0");
        }
    }

    /**
     * @param p_index indice du combattant
     * @return le ND du combattant lui-même
     */
    public int getFighterND(int p_index)
    {
        if (p_index >= 0)
        {
            return m_listFighters.get(p_index).getFighterND();
        } else
        {
            throw new IllegalArgumentException("indice < 0");
        }
    }

    /**
     * @return la phase courante
     */
    public int getCurrentPhase()
    {
        return m_currentPhase;
    }

    /**
     * @return le tour courant
     */
    public int getCurrentTurn()
    {
        return m_currentTurn;
    }

    /**
     * @return un booléen indiquant si un personnage est actif dans la phase en cours
     */
    public boolean isAnyoneActive()
    {
        boolean notEmpty = !m_listActiveFighters.isEmpty();
        return (notEmpty);
    }

    /**
     * classe représentant le résultat d'une action
     */
    public class ActionResult
    {
        int m_effect;
        boolean m_assessment;

        public ActionResult(int p_effect, boolean p_assessment)
        {
            m_effect = p_effect;
            m_assessment = p_assessment;
        }

        public int getEffect()
        {
            return m_effect;
        }

        public boolean assess()
        {
            return (m_assessment);
        }
    }

    /**
     * classe permettant de représenter l'état de santé d'un personnage
     */
    public class HealthReport
    {
        int m_fleshNumber;
        int m_dramaNumber;
        boolean m_isStunned;
        boolean m_isOut;

        public HealthReport(int p_flesh, int p_drama, boolean p_stunned, boolean p_out)
        {
            m_fleshNumber = p_flesh;
            m_dramaNumber = p_drama;
            m_isStunned = p_stunned;
            m_isOut = p_out;
        }

        public boolean isOut()
        {
            return (m_isOut);
        }

        public boolean isStunned()
        {
            return (m_isStunned);
        }

        public int getFleshWounds()
        {
            return (m_fleshNumber);
        }

        public int getDramaWounds()
        {
            return (m_dramaNumber);
        }
    }

}



