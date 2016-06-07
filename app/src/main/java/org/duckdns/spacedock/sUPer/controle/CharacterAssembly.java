package org.duckdns.spacedock.sUPer.controle;

import libupsystem.Arme;
import libupsystem.BasicNPCFighter;
import libupsystem.Perso;
import libupsystem.RollResult;

/**
 * Created by iconoctopus on 6/6/16.
 * Cette classe représente un personnage et l'environnement avec lequel il interragit (notamment le ND de sa cible) c'est utile pour ne pas avoir à gérer plusieurs listes (les persos et leurs cibles) avec risque de décorrélation. Un autre but est d'encapsuler les évolutions de la libupsystem dans cette classe
 */
class CharacterAssembly
{
    /**
     * le perso issu de libupsystem
     */
    private final Perso m_perso;
    /**
     * ND de la cible courante
     */
    private int m_targetND;
    /**
     * le RM du personnage, devra à terme être supprimé car le ND du perso sera accessible à partir de la libupsystem
     */
    private final int m_rm;//TODO : ne servira à rien quand le ND sera acessible à partir de la libupsystem, a changer DQP

    /**
     * constructeur avec ND cible de 25 par défaut
     *
     * @param p_rm rm du personnage
     */
    CharacterAssembly(int p_rm)
    {
        this(p_rm, 25);//ND cible de 25 par défaut
    }

    /**
     * constructeur sans ND par défaut
     *
     * @param p_rm rm du personnage
     * @param p_ND ND de la cible
     */
    CharacterAssembly(int p_rm, int p_ND)
    {
        m_perso = new BasicNPCFighter(p_rm);
        m_targetND = p_ND;
        m_rm = p_rm;
    }

    /**
     * @return le nom du perso tel que connu par le Perso UP!System
     */
    String getLibellePerso()
    {
        return m_perso.getLibellePerso();
    }

    /**
     * Permet de spécifier une arme
     *
     * @param p_rolled dés lancés
     * @param p_kept   dés gardés
     */
    void setArme(int p_rolled, int p_kept)
    {
        m_perso.setArme(new Arme(p_rolled, p_kept, 0, 0));
    }

    /**
     * @return dés lancés de la VD de l'arme
     */
    int getVDRolled()
    {
        return m_perso.getArme().getDesLances();
    }

    /**
     * @return les dés gardés de la VD de l'arme
     */
    int getVDKept()
    {
        return m_perso.getArme().getDesGardes();
    }

    /**
     * configure le ND de la cible
     *
     * @param p_ND
     */
    void setTargetND(int p_ND)
    {
        m_targetND = p_ND;
    }

    /**
     * @return le ND de la cible actuelle
     */
    int getTargetND()
    {
        return (m_targetND);
    }

    /**
     *
     * @return le ND propre du combattant
     */
    int getFighterND()
    {
        //TODO horrible calcul effectué ici car la libupsystem n'exporte pas le ND..... à modifier en amont puis reporter ici
        int resultat = 0;
        switch (m_rm)
        {
            case 1:
                resultat = 10;
                break;
            case 2:
                resultat = 15;
                break;
            case 3:
                resultat = 25;
                break;
            case 4:
                resultat = 30;
                break;
            case 5:
                resultat = 35;
                break;
        }
        return (resultat);

    }

    /**
     *
     * @param p_phase la phase courante, ce n'est pas une méthode d'interrogation générale : la méthode a un comportement indéfini si on demande pour une phase ultérieure
     * @return vrai si le perso a au moins une action dans la phase en cours
     */
    boolean isActive(int p_phase)
    {
        return m_perso.isActif(p_phase);
    }

    /**
     *
     * @return si le personnage doit être éliminé
     */
    boolean isOut()
    {
        return m_perso.isInconscient();//dans cette version seule l'inconscience vaut élimination
    }

    /**
     *
     * @return si le personnage est sonné
     */
    boolean isStunned()
    {
        return m_perso.isSonne();
    }

    /**
     *
     * @return nombre de blessures légères reçues
     */
    int getNbFleshWounds()
    {
        return m_perso.getBlessuresLegeres();
    }

    /**
     *
     * @return nombre de blessures graves reçues
     */
    int getNbDramaWounds()
    {
        return m_perso.getBlessuresGraves();
    }

    /**
     * fait attaquer le personnage
     * @param p_currentPhase phase actuelle car la libupsystem vérifie si l'action est possible
     * @return les dégâts infligés
     */
    int attack(int p_currentPhase)
    {
        int finalResult;
        RollResult technicalResult = ((BasicNPCFighter) m_perso).attaquer(p_currentPhase, m_targetND);

        if (technicalResult.isJetReussi())//extraction des dégâts
        {
            finalResult = m_perso.genererDegats(technicalResult.getNbIncrements());
        } else//aucun dégâts infligé
        {
            finalResult = 0;
        }
        return (finalResult);
    }

    /**
     * inflige des dégâts à ce personnage
     * @param p_damage les dommages a infliger
     */
    void hurt(int p_damage)
    {
        m_perso.etreBlesse(p_damage, 0);//TODO pour l'instant on ne gère que les armes anciennes, améliorer cela
    }

    /**
     * force un personnage à retirer son initiative
     */
    void regenInit()
    {
        m_perso.genInit();
    }
}
