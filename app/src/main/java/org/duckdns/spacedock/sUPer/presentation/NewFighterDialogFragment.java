package org.duckdns.spacedock.sUPer.presentation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.duckdns.spacedock.sUPer.R;

//TODO implémenter meilleure méthode de communication avec l'activité via une interface que l'activité implémentera et qui contiendra une méthode de callback. Actuellement dépendant de la classe spécifique de l'activité

public class NewFighterDialogFragment extends DialogFragment
{
    private FightBoard activity;

    //widgets d'interraction de la boite de dialogue
    private SeekBar rmSeekbar;
    private SeekBar nbSeekbar;

    //elements de configuration
    int rm;
    int nb;

    /**
     * listener du bouton OK de la boite de dialogue, appelle la métode de callback associée dans l'activité principale
     */
    private final DialogInterface.OnClickListener newFighterDialogListener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int id)
        {
            activity.newFighterCallback(rm, nb);//on passe à l'activité le RM désiré
        }
    };

    /**
     * listener des SeekBars de la boite de dialogue, là aussi on fait le choix d'un listener avec des if afin d'éviter de créer trop d'objets en RAM
     */
    private final SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            int realValue = ++progress;//les SeekBar sont indicées à partir de 0
            int id = seekBar.getId();
            ViewGroup parent = (ViewGroup) seekBar.getParent();

            if (id == R.id.rmSeekBar)//c'est la barre de RM qui a changé
            {
                TextView rmValue = (TextView) parent.findViewById(R.id.rmValue);
                rmValue.setText("" + realValue);
                rm = realValue;
            } else
            {
                if (id == R.id.nbSeekBar)//c'est la barre de nb qui a changé
                {
                    TextView nbValue = (TextView) parent.findViewById(R.id.nbValue);
                    nbValue.setText("" + realValue);
                    nb = realValue;
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {

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
        rmSeekbar = (SeekBar) fighterSetupView.findViewById(R.id.rmSeekBar);
        nbSeekbar = (SeekBar) fighterSetupView.findViewById(R.id.nbSeekBar);

        //passe les listener aux widgets d'interraction
        rmSeekbar.setOnSeekBarChangeListener(seekBarListener);
        nbSeekbar.setOnSeekBarChangeListener(seekBarListener);

        //initialise les valeurs de configuration
        TextView rmValue = (TextView) fighterSetupView.findViewById(R.id.rmValue);
        rm = rmSeekbar.getProgress() + 1;
        rmValue.setText("" + rm);

        TextView nbValue = (TextView) fighterSetupView.findViewById(R.id.nbValue);
        nb = nbSeekbar.getProgress() + 1;
        nbValue.setText("" + nb);


        builder.setCancelable(true);//ainsi on pourra faire back pour annuler
        //Passe au bouton positif son listener, le négatif est géré par le fait qu'il n'y a simplement rien à faire en ce cas
        builder.setPositiveButton(R.string.NewFighterDialogOkButton, newFighterDialogListener);
        builder.setNegativeButton(R.string.DialogCancelButton, null);

        //la méthode create produit la boite mais ne l'afiche pas, ce sera l'activité qui appellera show() sur ce fragment
        return (builder.create());
    }
}
