package org.duckdns.spacedock.sUPer.presentation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import org.duckdns.spacedock.sUPer.R;

//TODO implémenter meilleure méthode de communication avec l'activité via une interface que l'activité implémentera et qui contiendra une méthode de callback. Actuellement dépendant de la classe spécifique de l'activité

public class NewFighterDialogFragment extends DialogFragment
{
    private FightBoard activity;

    private EditText testRM;

    /**
     * listener du bouton OK de la boite de dialogue, appelle la métode de callback associée dans l'activité principale
     */
    private final DialogInterface.OnClickListener newFighterDialogListener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int id)
        {
            activity.newFighterCallback(Integer.parseInt(testRM.getText().toString()));//on passe à l'activité le RM désiré
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        activity = (FightBoard) getActivity();

        //crée un builder pour construire la boite de dialogue et lui passe l'activité mère comme contexte avant de lui affecter un titre
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.NewFighterDialogTitle);

        //inflate la vue de la boite de dialogue à partir du layout xml et l'affecte au builder
        View fighterSetupView = activity.getLayoutInflater().inflate(R.layout.fragment_new_fighter_dialog, null);
        builder.setView(fighterSetupView);

        //récupère des pointeurs sur les widgets d'interraction présents dans la boite dialogue
        testRM = (EditText) fighterSetupView.findViewById(R.id.testedit);
        //éventuelle initialisation des seekbar mais mieux dans layout


        builder.setCancelable(true);//ainsi on pourra faire back pour annuler
        //Passe au bouton positif son listener, le négatif est géré par le fait qu'il n'y a simplement rien à faire en ce cas
        builder.setPositiveButton(R.string.NewFighterDialogTitleOkButton, newFighterDialogListener);

        //la méthode create produit la boite mais ne l'afiche pas, ce sera l'activité qui appellera show() sur ce fragment
        return (builder.create());
    }
}
