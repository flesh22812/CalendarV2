package com.example.calendarv2;

import android.annotation.SuppressLint;
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

import static io.realm.Realm.getApplicationContext;

/**
 * Adapter from database to recyclerview
 */
public class RealmAdapter extends RecyclerView.Adapter<RealmAdapter.ViewHolder> {

    private List<EventEntity> eventList;
    private IClickDeleteListener clickListener = new MainActivity();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView dateStart;
        private TextView dateFinish;
        private TextView description;
        private ImageView del;

        /**
         * Initialize view components
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.listName);
            dateStart = itemView.findViewById(R.id.listTimeS);
            dateFinish = itemView.findViewById(R.id.listTimeF);
            description = itemView.findViewById(R.id.listDescrip);
            del = itemView.findViewById(R.id.imageView2);
            del.setOnClickListener(new View.OnClickListener() {
                /**
                 * Method onClick deletes event from recycle view and database
                 */
                @SuppressLint("SyntheticAccessor")
                @Override
                public void onClick(View view) {
                    clickListener.deleteEvent(view, eventList.get(getAdapterPosition()).getId());
                    deleteEvent();
                }
            });
        }

        /**
         * This method outputs the events in normal style
         */
        @SuppressLint("SetTextI18n")
        public void outputEvents(@NonNull EventEntity eventData) {
            Timestamp dateS = new Timestamp(Long.valueOf(eventData.getDateStart()));
            Timestamp dateF = new Timestamp(Long.valueOf(eventData.getDateFinish()));
            Timestamp dateDel = new Timestamp(dateS.getYear(), dateS.getMonth(), dateS.getDate(), 0, 0, 0, 0);
            name.setText(eventData.getName());
            if (dateS.getMinutes() < 10) {
                dateStart.setText(String.valueOf(dateS.getHours()) + ":0" + String.valueOf(dateS.getMinutes()));
            } else {
                dateStart.setText(String.valueOf(dateS.getHours()) + ":" + String.valueOf(dateS.getMinutes()));
            }

            if (dateF.getMinutes() < 10) {
                dateFinish.setText("-" + String.valueOf(dateF.getHours()) + ":0" + String.valueOf(dateF.getMinutes()));
            } else {
                dateFinish.setText("-" + String.valueOf(dateF.getHours()) + ":" + String.valueOf(dateF.getMinutes()));
            }
            description.setText(eventData.getDescription());
        }

        /**
         * This method needs for the deleting events from recycle view
         */
        @SuppressLint("SyntheticAccessor")
        public void deleteEvent() {
            int newPosition = getAdapterPosition();
            Toast.makeText(getApplicationContext(), "Удалено", Toast.LENGTH_SHORT).show();
            eventList.remove(newPosition);
            notifyItemRemoved(newPosition);
            notifyItemRangeChanged(newPosition, eventList.size());
        }
    }

    public RealmAdapter(@NonNull List<EventEntity> eventList) {
        this.eventList = eventList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.outputEvents(eventList.get(position));
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }


}