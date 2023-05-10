package com.example.firsttry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import at.wolframdental.Scanner.R;

public class ArtikelAdapter extends RecyclerView.Adapter<ArtikelAdapter.ArtikelViewHolder> {

    private Context mContext;
    private static List<Artikel> artikelListe;

    private OnItemCountChangeListener onItemCountChangeListener;

    public interface OnItemCountChangeListener {
        void onItemCountChange(int itemCount);
    }

    public ArtikelAdapter(Context context, List<Artikel> artikelListe) {
        this.mContext = context;
        this.artikelListe = artikelListe;
    }

    public void setOnItemCountChangeListener(OnItemCountChangeListener listener) {
        this.onItemCountChangeListener = listener;
    }

    @NonNull
    @Override
    public ArtikelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artikel_list_item, parent, false);
        return new ArtikelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtikelViewHolder holder, int position) {
        Artikel artikel = artikelListe.get(artikelListe.size() - 1 - position);
        holder.artikelnummerTextView.setText(String.valueOf(artikel.getArtikelnummer()));
        holder.beschreibungTextView.setText(artikel.getBeschreibung());

        if (onItemCountChangeListener != null) {
            onItemCountChangeListener.onItemCountChange(artikelListe.size());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Löschen durch Longpress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return artikelListe.size();
    }

    public class ArtikelViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView artikelnummerTextView;
        TextView beschreibungTextView;

        public ArtikelViewHolder(@NonNull View itemView) {
            super(itemView);
            artikelnummerTextView = itemView.findViewById(R.id.artikelnummer_text_view);
            beschreibungTextView = itemView.findViewById(R.id.beschreibung_text_view);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = artikelListe.size() - 1 - getAdapterPosition();
            artikelListe.remove(position);
            notifyDataSetChanged();
            if (onItemCountChangeListener != null) {
                onItemCountChangeListener.onItemCountChange(artikelListe.size());
            }
            return true;
        }
    }
}