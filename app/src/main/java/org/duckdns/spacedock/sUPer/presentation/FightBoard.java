package org.duckdns.spacedock.sUPer.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.duckdns.spacedock.sUPer.R;
import org.duckdns.spacedock.sUPer.controle.SessionManager;

import java.util.ArrayList;

/**
 * Activité principale : gère l'écran depuis lequel l'application débute
 */
public class FightBoard extends AppCompatActivity
{
    /**
     * listener utilisé pour recueillir les clicks sur les bouton principaux de l'interface de l'activité principale (pour l'instant phase et ajouter) on utilise un seul listener avec un if car ainsi un seul objet est créé et pas deux
     */
    private final View.OnClickListener fightBoardListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() == R.id.addButton)//c'est le bouton "ajouter" qui a été cliqué
            {
                new NewFighterDialogFragment().show(getSupportFragmentManager(), "NewFighterDialog");//ouvre la boite de dialogue de configuration
            } else
            {
                if (view.getId() == R.id.nextPhaseButton)//c'est le bouton "phase" qui a été cliqué
                {
                    //à implémenter
                }
            }
        }
    };

    /**
     * listener utilisé pour recueillir les clicks sur les boutons des FighterView : on le définit et l'instancie ici plutôt que dans FighterView car ainsi il n'y aura qu'un seul objet pour toute l'appli et pas une ribambelle
     */
    private final View.OnClickListener fighterViewListener = new View.OnClickListener()
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
                    //test provisoire
                    Button placeholder = (Button) view;
                    placeholder.setText("uncommonandverylong");
                }
            }
        }
    };

    //principales vues de l'application
    private Button addButton;
    private Button nextPhaseButton;
    private ViewGroup rootElement;

    private ArrayList<FighterView> fighterList = new ArrayList<FighterView>();

    /**
     * contrôleur unique (1 par lancement) de l'application
     */
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_board);
        manager = SessionManager.getInstance();

        //affectation des principales vues
        rootElement = (ViewGroup) findViewById(R.id.mainBoard);
        addButton = (Button) findViewById(R.id.addButton);
        nextPhaseButton = (Button) findViewById(R.id.nextPhaseButton);

        //affectation des listeners
        addButton.setOnClickListener(fightBoardListener);
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
                int index = manager.addFighter(rm);//récupération du premier indice libre

                //création de la nouvelle FighterView
                FighterView view = new FighterView(this, index);
                EditText name = (EditText) view.findViewById(R.id.fighterName);
                name.setText("index:" + index);

                //passage des listeners aux divers boutons de la FighterView
                Button delButton = (Button) view.findViewById(R.id.delButton);
                delButton.setOnClickListener(fighterViewListener);
                Button hurtButton = (Button) view.findViewById(R.id.hurtButton);
                hurtButton.setOnClickListener(fighterViewListener);

                //ajout de la vue au paneau coulissant puis à la liste maintenue en interne par l'activité
                rootElement.addView(view);
                if (index == fighterList.size())
                {
                    fighterList.add(view);//en ce cas c'est un ajout en queue
                } else
                {
                    fighterList.set(index, view);//en ce cas on remplace une des références nulles par l'objet
                }
            }
        } else
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
     * @param index l'indice du combattant à supprimer
     */
    private void delFighter(int index)
    {
        View view = fighterList.get(index);
        rootElement.removeView(view);//on supprime la fighterview côté graphique
        fighterList.set(index, null);//on supprime l'élément de la liste maintenue par l'application (pas par la méthode remove(), qui retrie ensuite pour supprimer l'espace vide et perd donc les indices tels que maintenus dans l'appli)
        manager.delFighter(index);//on supprime maintenant le personnage côté contrôle
    }
}
