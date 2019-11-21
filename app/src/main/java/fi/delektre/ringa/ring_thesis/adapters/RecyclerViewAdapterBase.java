package fi.delektre.ringa.ring_thesis.adapters;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerViewAdapterBase<T, V extends View> extends RecyclerView.Adapter<ViewWrapper<V>> {
    public List<T> items = new ArrayList<T>();

    @Override
    public int getItemCount(){
        return items.size();
    }

    @Override
    public final ViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<V>(onCreateItemView(parent,viewType));
    }

    protected abstract V onCreateItemView(ViewGroup parent, int ViewType);

}
