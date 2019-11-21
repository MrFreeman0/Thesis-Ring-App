package fi.delektre.ringa.ring_thesis;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import androidx.appcompat.widget.LinearLayoutCompat;
import fi.delektre.ringa.ring_thesis.util.SelectorOption;


@EViewGroup(R.layout.recycler_graph_selector_button)
public class RecyclerSelectorView extends LinearLayoutCompat {

    @ViewById
    ImageView recycler_option_image_view;


    @ViewById
    TextView recycler_option_label;

    public RecyclerSelectorView(Context context){
        super(context);
    }

    public void bind(SelectorOption selectorOption){
        recycler_option_image_view.setImageResource(selectorOption.imageDrawableResource);
        recycler_option_label.setText(selectorOption.optionName);


    }

}