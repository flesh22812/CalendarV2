package com.example.calendarv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static io.realm.Realm.getApplicationContext;

public class RealmAdapter extends RecyclerView.Adapter<RealmAdapter.ViewHolder> {
    Context context;
    List<Event> eventList;

    //  private Realm mRealm;
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date_start;
        TextView date_finish;
        TextView description;
        ImageView del;
        Realm realm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.listName);
            date_start = (TextView) itemView.findViewById(R.id.listTimeS);
            date_finish = (TextView) itemView.findViewById(R.id.listTimeF);
            description = (TextView) itemView.findViewById(R.id.listDescrip);
            del = itemView.findViewById(R.id.imageView2);
        }
    }

    public RealmAdapter( @NonNull List<Event> eventList) {
        this.eventList = eventList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        ImageView del = view.findViewById(R.id.imageView2);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Realm realm;
        realm = Realm.getDefaultInstance();
        Event eventData = eventList.get(position);
        Timestamp dateS = new Timestamp(Long.valueOf(eventData.getDateStart()));
        Timestamp dateF = new Timestamp(Long.valueOf(eventData.getDateFinish()));
        Timestamp dateDel = new Timestamp(dateS.getYear(), dateS.getMonth(), dateS.getDate(), 0, 0, 0, 0);
        holder.name.setText(eventData.getName());
        if (dateS.getMinutes() < 10) {
            holder.date_start.setText(String.valueOf(dateS.getHours()) + ":0" + String.valueOf(dateS.getMinutes()));
        } else
            holder.date_start.setText(String.valueOf(dateS.getHours()) + ":" + String.valueOf(dateS.getMinutes()));
        if (dateF.getMinutes() < 10)
            holder.date_finish.setText("-" + String.valueOf(dateF.getHours()) + ":0" + String.valueOf(dateF.getMinutes()));
        else
            holder.date_finish.setText("-" + String.valueOf(dateF.getHours()) + ":" + String.valueOf(dateF.getMinutes()));
        holder.description.setText(eventData.getDescription());
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealmResults<Event> realmResultsStart = realm.where(Event.class).between("dateStart", dateDel.getTime(), (dateDel.getTime() + 86400000)).findAll();
                realm.beginTransaction();
                realmResultsStart.deleteFromRealm(position);
                realm.commitTransaction();
                Toast.makeText(getApplicationContext(), "Удалено", Toast.LENGTH_SHORT).show();
                int newPosition = holder.getAdapterPosition();
                eventList.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition, eventList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}