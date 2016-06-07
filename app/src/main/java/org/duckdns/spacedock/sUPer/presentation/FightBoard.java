package org.duckdns.spacedock.sUPer.presentation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.duckdns.spacedock.sUPer.R;
import org.duckdns.spacedock.sUPer.controle.SessionManager;

import java.util.ArrayList;
import java.util.ListIterator;

//TODO : trier les éléments de strings.xml et décider d'un mode de nommage cohérent
//TODO mieux blinder les méthodes façon prog par contrat (y compris constructeurs)
//TODO repasser dans les xml et les fichiers de code et virer les chaines de caractères en dur et les concaténations barbares avec "+" au lieu de concat
//TODO à terme il pourrait s'avérer judicieux de supprimer la la liste des vues qui est difficile à synchroniser avec celle des personnages du controleur vues, on pourrait affecter un id aux vues et les supprimer via une recherche sur celui-ci
//TODO il serait bon de vérifier que ce sont les bons claviers qui sont accessibles à chaque champ
//TODO vérifier utilisation correcte des R.string avec getString()
//TODO supprimer tous les warnings
/**
 * Activité principale : gère l'écran depuis lequel l'application débute
 */
public class FightBoard extends AppCompatActivity
{
    /**
     * enum indiquant la couleur à utiliser pour le texte
     */
    private enum TextColor
    {
        ALERT, OK
    }

    /**
     * listener utilisé pour recueillir les clicks sur les bouton principaux de l'interface de l'activité principale (pour l'instant phase et ajouter) on utilise un seul listener avec un if car ainsi un seul objet est créé et pas deux
     */
    private final View.OnClickListener m_fightBoardListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View p_view)
        {
            if (p_view.getId() == R.id.addButton)//c'est le bouton "ajouter" qui a été cliqué
            {
                new NewFighterDialogFragment().show(getSupportFragmentManager(), "NewFighterDialog");//ouvre la boite de dialogue de configuration de nouveaux combattants
            } else
            {
                if (p_view.getId() == R.id.nextPhaseButton)//c'est le bouton "phase" qui a été cliqué
                {
                    ListIterator<Integer> activeIterator = m_manager.nextPhase().listIterator();

                    for (int index = 0; index < m_fighterViewList.size(); ++index)
                    {
                        FighterView currentPane = m_fighterViewList.get(index);

                        if (currentPane != null)//il pourrait très bien être nul vu que les vues sont maintenues à leur indice même lorsque l'on en supprime une, il y a alors des trous
                        {
                            Button attackButton = (Button) currentPane.findViewById(R.id.attackButton);
                            if (activeIterator.hasNext())//ce combattant sera actif,
                            {
                                if (index == activeIterator.next())//le combattant est actif
                                {
                                    //autoriser le bouton; passer le texte en vert
                                    enableButton(attackButton);
                                    attackButton.setText(R.string.attackButton);
                                } else//le combattant est inactif
                                {
                                    activeIterator.previous();//on a bougé le curseur pour rien
                                    disableButton(attackButton);
                                    attackButton.setText(R.string.attackButton);
                                }

                            } else//il n'y a plus de combattant actif, on ne s'occupe plus du curseur
                            {
                                //griser le bouton; passer le texte en rouge
                                disableButton(attackButton);
                                attackButton.setText(R.string.attackButton);
                            }
                        }
                    }
                    m_nextPhaseButton.setText(getString(R.string.Turn) + " " + m_manager.getCurrentTurn() + " " + getString(R.string.Phase) + " " + m_manager.getCurrentPhase());//on affiche le numéro de phase
                    boolean anyoneActive = m_manager.isAnyoneActive();
                    if (anyoneActive)//si un combattant est actif il devient impossible de changer de phase
                    {
                        disableButton(m_nextPhaseButton);
                    }
                }
            }
        }
    };

    /**
     * listener utilisé pour recueillir les clicks sur les boutons des FighterView : on le définit et l'instancie ici plutôt que dans FighterView car ainsi il n'y aura qu'un seul objet pour toute l'appli et pas une ribambelle
     */
    private final View.OnClickListener m_fighterViewListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int id = view.getId();
            FighterView parent = (FighterView) (view.getParent());
            int parentIndex = parent.getFighterIndex();

            if (id == R.id.delButton)//c'est le bouton "supprimer" qui a été cliqué
            {
                delFighter(parentIndex);
            } else
            {
                if (id == R.id.hurtButton)//c'est le bouton pour infliger des dégâts qui a été cliqué
                {
                    HurtDialogFragment dialogFragment = HurtDialogFragment.getInstance(parentIndex);
                    dialogFragment.show(getSupportFragmentManager(), "HurtDialog");
                } else
                {
                    if (id == R.id.invButton)//c'est le bouton d'inventaire qui a été cliqué
                    {
                        INVDialogFragment dialogFragment = INVDialogFragment.getInstance(parentIndex, m_manager.getVDRolled(parentIndex), m_manager.getVDKept(parentIndex));//récupère une instance du fragment en passant l'index et les caracs de l'arme en paramétre à la méthode statique qui le met en bundle
                        dialogFragment.show(getSupportFragmentManager(), "INVDialog"); //ouvre la boite de dialogue de configuration mais seulement après avoir indiqué au fragment ses paramétres
                    } else
                    {
                        if (id == R.id.attackButton)//c'est le bouton "attaquer" qui a été cliqué
                        {
                            SessionManager.ActionResult attackResult = m_manager.attack(parentIndex);
                            //si le combattant est inactif on désactive son bouton d'attaque
                            if (!attackResult.assess())
                            {
                                disableButton((Button) view);
                            }
                            int degats = attackResult.getEffect();
                            if (degats == 0)//l'attaque est un échec
                            {
                                ((Button) view).setText(R.string.attackFailed);
                            } else
                            {
                                ((Button) view).setText("" + degats);//TODO utiliser concat à la place de ce +
                            }
                            if (!m_manager.isAnyoneActive())//si c'était la dernière attaque du tour on active le bouton des phases
                            {
                                enableButton(m_nextPhaseButton);
                            }


                        } else
                        {
                            if (id == R.id.statButton)//c'est le bouton "statut" qui a été cliqué
                            {
                                STATDialogFragment dialogFragment = STATDialogFragment.getInstance(parentIndex, m_manager.getTargetND(parentIndex));
                                dialogFragment.show(getSupportFragmentManager(), "STATDialog"); //ouvre la boite de dialogue de configuration mais seulement après avoir indiqué au fragment ses paramétres
                            }
                        }
                    }
                }
            }
        }
    };

    //principales vues de l'application
    private Button m_addButton;
    private Button m_nextPhaseButton;
    private ViewGroup m_rootElement;

    private ArrayList<FighterView> m_fighterViewList = new ArrayList<FighterView>();

    /**
     * contrôleur unique (1 par lancement) de l'application
     */
    private SessionManager m_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_board);
        m_manager = SessionManager.getInstance();

        //affectation des principales vues
        m_rootElement = (ViewGroup) findViewById(R.id.mainBoard);
        m_addButton = (Button) findViewById(R.id.addButton);
        m_nextPhaseButton = (Button) findViewById(R.id.nextPhaseButton);

        //affectation des listeners
        m_addButton.setOnClickListener(m_fightBoardListener);
        m_nextPhaseButton.setOnClickListener(m_fightBoardListener);

        //initialement le bouton de changement de phase est inactif
        disableButton(m_nextPhaseButton);

        m_nextPhaseButton.setText(getString(R.string.Turn) + " " + m_manager.getCurrentTurn() + " " + getString(R.string.Phase) + " " + m_manager.getCurrentPhase());
    }

    /**
     * callback appelée par la boite de dialogue de création de nouveau combattant
     *
     * @param rm rang de menace
     * @param nb nb de combattants à créer
     */
    void newFighterCallback(int rm, int nb)//TODO blinder contre les valeurs autres que [1;5] par émission d'exception et vérifier aussi que ca ne depasse pas la taille du tableau
    {
        if (rm <= 5 && rm > 0 && nb > 0)
        {
            for (int i = 0; i < nb; ++i)
            {
                SessionManager.ActionResult creationResult = m_manager.addFighter(rm);//récupération du premier indice libre et de si il est actif dans cette phase puis création du combattant côté contrôle
                int index = creationResult.getEffect();
                boolean activeFighter = creationResult.assess();

                //création de la nouvelle FighterView
                FighterView view = new FighterView(this, index);
                EditText name = (EditText) view.findViewById(R.id.fighterName);
                name.setText(m_manager.getName(index));
                TextView fighterND = (TextView) view.findViewById(R.id.ndText);
                fighterND.setText("ND:" + m_manager.getFighterND(index));

                //passage des listeners aux divers boutons de la FighterView
                Button delButton = (Button) view.findViewById(R.id.delButton);
                delButton.setOnClickListener(m_fighterViewListener);
                Button hurtButton = (Button) view.findViewById(R.id.hurtButton);
                hurtButton.setOnClickListener(m_fighterViewListener);
                Button invButton = (Button) view.findViewById(R.id.invButton);
                invButton.setOnClickListener(m_fighterViewListener);
                Button statButton = (Button) view.findViewById(R.id.statButton);
                statButton.setOnClickListener(m_fighterViewListener);
                Button attackButton = (Button) view.findViewById(R.id.attackButton);
                attackButton.setOnClickListener(m_fighterViewListener);

                //gestion du statut actif ou non du nouveau combattant
                if (activeFighter)
                {
                    enableButton(attackButton);
                    disableButton(m_nextPhaseButton);//il y a au moins un combattant actif, on ne passe donc pas à la phase suivante
                } else
                {
                    disableButton(attackButton);
                    if (!m_manager.isAnyoneActive())//personne n'est actif, on peut donc passer à la phase suivante
                    {
                        enableButton(m_nextPhaseButton);
                    }
                }

                //ajout de la vue au paneau coulissant puis à la liste maintenue en interne par l'activité
                m_rootElement.addView(view);
                if (index == m_fighterViewList.size())
                {
                    m_fighterViewList.add(view);//en ce cas c'est un ajout en queue
                } else
                {
                    m_fighterViewList.set(index, view);//en ce cas on remplace une des références nulles par l'objet
                }
            }
            //construction du message de confirmation de creation qui sera présenté par toast
            String creationConfirmation = "";
            creationConfirmation = creationConfirmation.concat(getString(R.string.creation));
            creationConfirmation = creationConfirmation.concat(String.valueOf(nb));
            if (nb > 1)
            {
                creationConfirmation = creationConfirmation.concat(getString(R.string.fighters));
            } else
            {
                creationConfirmation = creationConfirmation.concat(getString(R.string.fighter));
            }
            creationConfirmation = creationConfirmation.concat(String.valueOf(rm));
            Toast.makeText(this, creationConfirmation, Toast.LENGTH_SHORT).show();
        } else//erreur sur paramétres
        {
            String message;
            if (rm > 5)
            {
                message = "rm>5";
            } else
            {
                if (rm <= 0)
                {
                    message = "rm<=0";
                } else
                {
                    message = "nb<=0";
                }
            }
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * supprime un combattant : tant du côté graphique qu'auprès du SessionManager
     *
     * @param p_index l'indice du combattant à supprimer
     */
    private void delFighter(int p_index)
    {
        if (p_index >= 0)
        {
            View view = m_fighterViewList.get(p_index);
            m_rootElement.removeView(view);//on supprime la fighterview côté graphique
            m_fighterViewList.set(p_index, null);//on supprime l'élément de la liste maintenue par l'application (pas par la méthode remove(), qui retrie ensuite pour supprimer l'espace vide et perd donc les indices tels que maintenus dans l'appli)
            m_manager.delFighter(p_index);//on supprime maintenant le personnage côté contrôle
            if (!m_manager.isAnyoneActive())//si personne n'est actif suite à la suppression on active le bouton de changement de phase
            {
                enableButton(m_nextPhaseButton);
            }
        } else
        {
            throw new IllegalArgumentException("index<0");
        }
    }

    /**
     * passe au SessionManager une nouvelle arme configurée dans la boite de dialogue d'inventaire
     *
     * @param p_index  l'indice du combattant concerné
     * @param p_rolled dés lancés
     * @param p_kept dés gardés
     */
    void INVChangedCallback(int p_index, int p_rolled, int p_kept)
    {
        if (p_index >= 0)
        {
            m_manager.setArme(p_index, p_rolled, p_kept);
        } else
        {
            throw new IllegalArgumentException("index<0");
        }
    }

    /**
     * passe au SessionManager le ND de la cible
     *
     * @param p_index indice du combattant
     * @param p_ND    ND qui va être passé
     */
    void STATChangedCallback(int p_index, int p_ND)
    {
        if (p_index >= 0)
        {
            m_manager.setTargetND(p_index, p_ND);
        } else
        {
            throw new IllegalArgumentException("index<0");
        }
    }

    /**
     * récupère l'état de santé du personnage auprès du SessionManager après lui avoir passé les dégâts à infliger puis met à jour le texte du bouton idoine
     *
     * @param p_index  indice du combattant
     * @param p_damage les dégâts à infliger
     */
    void HurtInflictedCallback(int p_index, int p_damage)
    {
        if (p_index >= 0)
        {
            if (p_damage>0)
            {
                SessionManager.HealthReport report = m_manager.hurt(p_index, p_damage);//récupération de l'état de santé post-application des dégâts
                if (!report.isOut())//si le personnage n'est pas éliminé
                {
                    String healthMessage = "";
                    if (report.isStunned())
                    {
                        healthMessage = healthMessage.concat(getString(R.string.stunned));
                        healthMessage = healthMessage.concat("\n");
                    }
                    healthMessage = healthMessage.concat(String.valueOf(report.getFleshWounds()));
                    healthMessage = healthMessage.concat("\n");
                    int nbDramaWounds = report.getDramaWounds();
                    for (int i = 0; i < nbDramaWounds; ++i)
                    {
                        healthMessage = healthMessage.concat(getString(R.string.DramaWound));
                    }
                    //mise à jour du texte du bouton
                    Button hurtButton = (Button) m_fighterViewList.get(p_index).findViewById(R.id.hurtButton);
                    hurtButton.setText(healthMessage);
                } else//le personnage est éliminé, on ne décortique donc pas le résultat de santé, l'on détruit simplement le combattant en affichant un toast
                {
                    delFighter(p_index);
                    Toast.makeText(this, getString(R.string.outFromWounds), Toast.LENGTH_SHORT).show();
                }
            }
        } else
        {
            throw new IllegalArgumentException("index<0");
        }
    }

    /**
     * met le texte de la vue dans la couleur indiquée
     * @param p_view vue concernée
     * @param p_color couleur issue de la l'enum TextColor de cette classe
     */
    private void setTextColor(TextView p_view, TextColor p_color)
    {
        if (p_color == TextColor.ALERT)
        {
            p_view.setTextColor(Color.RED);
        } else
        {
            if (p_color == TextColor.OK)
            {
                p_view.setTextColor(Color.GREEN);
            }
        }
    }

    /**
     * dégrise un bouton et passe son texte en vert
     * @param p_button vue concernée
     */
    private void enableButton(Button p_button)
    {
        p_button.setEnabled(true);
        setTextColor(p_button, TextColor.OK);
    }

    /**
     * grise un bouton et passe son texte en rouge
     * @param p_button
     */
    private void disableButton(Button p_button)
    {
        p_button.setEnabled(false);
        setTextColor(p_button, TextColor.ALERT);
    }

}
