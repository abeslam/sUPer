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


/**
 * Created by iconoctopus on 6/5/16.
 */
public class INVDialogFragment extends DialogFragment
{
    private FightBoard activity;
    private int m_index = -1;

    //Widgets d'interaction
    EditText rolledEditText;
    EditText keptEditText;

    /**
     * listener du bouton OK de la boite de dialogue, appelle la métode de callback associée dans l'activité principale
     */
    private final DialogInterface.OnClickListener INVDialogListener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int id)
        {
            //TODO: la ligne suivante bien que d'une concision admirable fait trop à la fois, la diviser pour plus de clarté
            activity.INVChangedCallback(m_index, Integer.parseInt(rolledEditText.getText().toString()), Integer.parseInt(keptEditText.getText().toString()));//on passe à l'activité les nouvelles caracs d'arme
        }
    };


    /**
     * Véritable "constructeur" fournissant l'instance unique mais SURTOUT recevant des paramétres et permettant donc leur affectation à des variable membres dans OnCreateDialog()
     *
     * @param p_index
     * @return
     */
    static INVDialogFragment getInstance(int p_index, int p_rolled, int p_kept)
    {
        INVDialogFragment frag = new INVDialogFragment();
        Bundle args = new Bundle();
        args.putInt("index", p_index);
        args.putInt("rolled", p_rolled);
        args.putInt("kept", p_kept);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        activity = (FightBoard) getActivity();
        m_index = getArguments().getInt("index");

        //crée un builder pour construire la boite de dialogue et lui passe l'activité mère comme contexte avant de lui affecter un titre
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.INVDialogTitle);

        //inflate la vue de la boite de dialogue à partir du layout xml et l'affecte au builder
        View invSetupView = activity.getLayoutInflater().inflate(R.layout.fragment_inv_dialog, null);//TODO: voir si il est possible de passer autre chose que null comme rootelement
        builder.setView(invSetupView);

        //récupère des pointeurs sur les widgets d'interraction présents dans la boite dialogue
        rolledEditText = (EditText) invSetupView.findViewById(R.id.vdLanceValue);
        keptEditText = (EditText) invSetupView.findViewById(R.id.vdGardeValue);

        //configuration initiale des champs
        rolledEditText.setText("" + getArguments().getInt("rolled"));
        keptEditText.setText("" + getArguments().getInt("kept"));

        builder.setCancelable(true);//ainsi on pourra faire back pour annuler

        //Passe au bouton positif son listener, le négatif est géré par le fait qu'il n'y a simplement rien à faire en ce cas
        builder.setPositiveButton(R.string.DialogModify, INVDialogListener);
        builder.setNegativeButton(R.string.DialogCancelButton, null);

        //la méthode create produit la boite mais ne l'afiche pas, ce sera l'activité qui appellera show() sur ce fragment
        return (builder.create());
    }
}
