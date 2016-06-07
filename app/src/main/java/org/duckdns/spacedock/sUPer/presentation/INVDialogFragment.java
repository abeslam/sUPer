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
//TODO extraire une superclasse pour les fragments de cette application qui sont très proches les uns des autres

/**
 * Fragment représentant une boite de dialogue permettant d'ajouter de nouveaux combatants
 */
public class INVDialogFragment extends DialogFragment
{
    private FightBoard m_activity;
    private int m_index = -1;

    //Widgets d'interaction
    EditText m_rolledEditText;
    EditText m_keptEditText;

    /**
     * listener du bouton OK de la boite de dialogue, appelle la métode de callback associée dans l'activité principale
     */
    private final DialogInterface.OnClickListener m_INVDialogListener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int id)
        {
            //TODO: la ligne suivante bien que d'une concision admirable fait trop à la fois, la diviser pour plus de clarté
            m_activity.INVChangedCallback(m_index, Integer.parseInt(m_rolledEditText.getText().toString()), Integer.parseInt(m_keptEditText.getText().toString()));//on passe à l'activité les nouvelles caracs d'arme
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
        m_activity = (FightBoard) getActivity();
        m_index = getArguments().getInt("index");

        //crée un builder pour construire la boite de dialogue et lui passe l'activité mère comme contexte avant de lui affecter un titre
        AlertDialog.Builder builder = new AlertDialog.Builder(m_activity);
        builder.setTitle(R.string.INVDialogTitle);

        //inflate la vue de la boite de dialogue à partir du layout xml et l'affecte au builder
        View invSetupView = m_activity.getLayoutInflater().inflate(R.layout.fragment_inv_dialog, null);//TODO: voir si il est possible de passer autre chose que null comme rootelement
        builder.setView(invSetupView);

        //récupère des pointeurs sur les widgets d'interraction présents dans la boite dialogue
        m_rolledEditText = (EditText) invSetupView.findViewById(R.id.vdLanceValue);
        m_keptEditText = (EditText) invSetupView.findViewById(R.id.vdGardeValue);

        //configuration initiale des champs
        m_rolledEditText.setText("" + getArguments().getInt("rolled"));
        m_keptEditText.setText("" + getArguments().getInt("kept"));

        builder.setCancelable(true);//ainsi on pourra faire back pour annuler

        //Passe au bouton positif son listener, le négatif est géré par le fait qu'il n'y a simplement rien à faire en ce cas
        builder.setPositiveButton(R.string.DialogModify, m_INVDialogListener);
        builder.setNegativeButton(R.string.DialogCancelButton, null);

        //la méthode create produit la boite mais ne l'afiche pas, ce sera l'activité qui appellera show() sur ce fragment
        return (builder.create());
    }
}
