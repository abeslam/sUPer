package org.duckdns.spacedock.sUPer.controle;

import libupsystem.Arme;
import libupsystem.BasicNPCFighter;
import libupsystem.Perso;

/**
 * Created by iconoctopus on 6/6/16.
 * Cette classe représente un personnage et l'environnement avec lequel il interragit (notamment le ND de sa cible) c'est utile pour n pas avoir à gérer plusieurs listes (les persos et leurs cibles) avec risque de décorrélation. Un autre but est d'encapsuler les évolutions de la libupsystem dans cette classe
 */
class CharacterAssembly
{
    private int m_fighterIndex;
    private int m_targetND;
    private Perso m_perso;
    private int m_rm;//TODO : ne servira à rien quand le ND sera acessible à partir de la libupsystem, a changer DQP

    CharacterAssembly(int p_index, int p_rm)
    {
        this(p_index, p_rm, 25);//ND cible de 25 par défaut
    }

    CharacterAssembly(int p_index, int p_rm, int p_ND)
    {
        m_fighterIndex = p_index;
        m_perso = new BasicNPCFighter(p_rm);
        m_targetND = p_ND;
        m_rm = p_rm;
    }

    String getLibellePerso()
    {
        return m_perso.getLibellePerso();
    }

    void setArme(int p_rolled, int p_kept)
    {
        m_perso.setArme(new Arme(p_rolled, p_kept, 0, 0));
    }

    int getVDRolled()
    {
        return m_perso.getArme().getDesLances();
    }

    void setTargetND(int p_ND)
    {
        m_targetND = p_ND;
    }

    int getTargetND()
    {
        return (m_targetND);
    }

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

    int getVDKept()
    {
        return m_perso.getArme().getDesGardes();
    }
}
