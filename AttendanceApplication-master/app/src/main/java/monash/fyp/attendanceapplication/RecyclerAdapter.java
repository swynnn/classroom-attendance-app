package monash.fyp.attendanceapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Base class for an Adapter.
 * Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<RecyclerItem> data = new ArrayList<>();

    /**
     * Method to set the array list for data
     * @param data collection of items
     */
    public void setData(ArrayList<RecyclerItem> data) {
        this.data = data;
    }

    /**
     * Initialize some private fields to be used by RecyclerView.
     *
     * @param parent parent view
     * @param viewType view type
     * @return view holder item that holds the view of rows
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false); //CardView inflated as RecyclerView list item
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder view holder item
     * @param position position specified for data to be displayed
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        holder.unit.setText(data.get(position).getUnit());
        holder.name.setText(data.get(position).getUnitName());
        holder.date.setText(data.get(position).getDate());

    }

    /**
     * A method to get the number of items in items list.
     * @return number of items
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * View Holder class that holds the item's views
     * It is a object that represents each item in the collection.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, unit, date;

        /**
         * Constructor.
         * @param itemView widget using this class
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unit = itemView.findViewById(R.id.item_unit);
            name = itemView.findViewById(R.id.item_name);
            date = itemView.findViewById(R.id.item_date);
        }
    }
}
