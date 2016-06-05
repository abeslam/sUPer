package org.duckdns.spacedock.sUPer.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.duckdns.spacedock.sUPer.R;
import org.duckdns.spacedock.sUPer.controle.SessionManager;

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
            FighterView parent = (FighterView) ((ViewGroup) view.getParent());
            int parentIndex = parent.getFighterIndex();

            if (id == R.id.delButton)//c'est le bouton "supprimer" qui a été cliqué
            {
                EditText placeholder = (EditText) parent.findViewById(R.id.fighterName);
                placeholder.setText("dont't kill #" + parentIndex);
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
     * @param p_RM
     */
    void newFighterCallback(int p_RM)//TODO blinder contre les valeurs autres que [1;5] par émission d'exception
    {
        int index = manager.addFighter(p_RM);//récupération du premier indice libre

        //création de la nouvelle FighterView
        FighterView view = new FighterView(this, index);
        EditText name = (EditText) view.findViewById(R.id.fighterName);
        name.setText("" + p_RM);

        //passage des listeners aux divers boutons de la FighterView
        Button delButton = (Button) view.findViewById(R.id.delButton);
        delButton.setOnClickListener(fighterViewListener);
        Button hurtButton = (Button) view.findViewById(R.id.hurtButton);
        hurtButton.setOnClickListener(fighterViewListener);

        //ajout de la vue au paneau coulissant
        rootElement.addView(view);
    }
}
