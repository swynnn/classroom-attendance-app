package monash.fyp.attendanceapplication;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class representing the view history activity
 */
public class ViewHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter adapter;
    private ArrayList<RecyclerItem> dataArray;

    /**
     * Initialize the layout and display data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        recyclerView = findViewById(R.id.rv);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize recycler adapter
        adapter = new RecyclerAdapter();

        // get data to be displayed on recycler view
        dataArray = (ArrayList<RecyclerItem>) getIntent().getSerializableExtra(Homepage.VIEW_HISTORY_KEY);
        Collections.sort(dataArray);

        adapter.setData(dataArray);
        recyclerView.setAdapter(adapter);

    }

}
