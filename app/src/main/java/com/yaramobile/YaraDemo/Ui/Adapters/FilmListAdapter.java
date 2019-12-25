package com.yaramobile.YaraDemo.Ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yaramobile.YaraDemo.Models.Search;
import com.yaramobile.YaraDemo.R;
import com.yaramobile.YaraDemo.Tools.OnItemClickListener;

import java.util.List;

public class FilmListAdapter extends RecyclerView.Adapter<FilmListAdapter.FilmListViewHolder> {

    private Context mContext;
    private List<Search> mSearchList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public FilmListAdapter(Context mContext, List<Search> mSearchList) {
        this.mContext = mContext;
        this.mSearchList = mSearchList;
    }

    @NonNull
    @Override
    public FilmListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_film, parent, false);

        return new FilmListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmListViewHolder holder, int position) {

        Search mSearch = mSearchList.get(holder.getAdapterPosition());


        Glide.with(mContext)
                .load(mSearch.getPoster())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .placeholder(mContext.getResources().getDrawable(R.drawable.sample))
                .into(holder.imagePosterFilm);

        holder.filmName.setText(mSearch.getTitle());
        holder.typeFilm.setText(mSearch.getType());
        holder.yearsFilm.setText(mSearch.getYear());

        int count = mContext.getResources().getInteger(R.integer.num_gridL);

//        holder.contentView.getLayoutParams().height =
                holder.contentView.getLayoutParams().width =

                ((mContext.getApplicationContext().getResources().getDisplayMetrics().widthPixels) - ((count + 1) * 8)) / count;

        holder.contentView.setOnClickListener(view -> {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(view, holder.getAdapterPosition());
        });

    }

    @Override
    public int getItemCount() {
        return mSearchList.size();
    }

    public Search getItem(int position){

        return mSearchList.get(position);
    }


    public class FilmListViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout contentView;
        private ImageView imagePosterFilm;
        private TextView filmName;
        private TextView typeFilm;
        private TextView yearsFilm;

        public FilmListViewHolder(@NonNull View itemView) {
            super(itemView);

            contentView=itemView.findViewById(R.id.content_view);
            imagePosterFilm = itemView.findViewById(R.id.image_poster_film);
            filmName = itemView.findViewById(R.id.film_name);
            typeFilm = itemView.findViewById(R.id.type_film);
            yearsFilm = itemView.findViewById(R.id.years_film);
        }
    }
}
