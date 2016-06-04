package org.duckdns.spacedock.sUPer.presentation;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.duckdns.spacedock.sUPer.R;

/**
 * View customisée représentant un élément du panneau coulissant principal
 */
public class FighterView extends RelativeLayout
{
    //vues membres
    private EditText fighterName;
    private Button attackButton;
    private Button hurtButton;
    private Button delButton;
    private TextView ndText;
    private TextView initText;
    private Button invButton;
    private Button statButton;

    //indice du combattant
    private final int fighterIndex;

    /**
     * constructeur sans attributs particuliers
     *
     * @param context l'application mère, a priori FightBoard
     * @param index   l'indice du combattant
     */
    public FighterView(Context context, int index)
    {
        super(context);
        fighterIndex = index;
        init(context);//véritable méthode de construction
    }

    /**
     * constructeur avec attributs particuliers
     *
     * @param context l'application mère, a priori FightBoard
     * @param attrs   attributs de la vue
     * @param index   l'indice du combattant
     */
    public FighterView(Context context, AttributeSet attrs, int index)
    {
        super(context, attrs);
        fighterIndex = index;
        init(context);//véritable méthode de construction
    }

    /**
     * véritable méthode de construction
     *
     * @param context l'application mère, a priori FightBoard
     */
    private void init(Context context)
    {
        //inflate est un raccourci vers l'appel du layoutinflater de l'application
        inflate(getContext(), R.layout.view_fighter, this);

        //affectation des vues
        fighterName = (EditText) findViewById(R.id.fighterName);
        attackButton = (Button) findViewById(R.id.attackButton);
        hurtButton = (Button) findViewById(R.id.hurtButton);
        delButton = (Button) findViewById(R.id.delButton);
        ndText = (TextView) findViewById(R.id.ndText);
        initText = (TextView) findViewById(R.id.initText);
        invButton = (Button) findViewById(R.id.invButton);
        statButton = (Button) findViewById(R.id.statButton);
    }

    /**
     * renvoie l'indice du combattant
     *
     * @return
     */
    public int getFighterIndex()
    {
        return fighterIndex;
    }
}
