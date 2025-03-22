package org.axel.autohakusovellus;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListInfoActivity extends AppCompatActivity {

    private TextView CityText;
    private TextView YearText;
    private TextView CarInfoText;
    private RecyclerView recyclerView;
    private InfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CityText = findViewById(R.id.CityText);
        YearText = findViewById(R.id.YearText);
        recyclerView = findViewById(R.id.recyclerView);

        CarDataStorage storage = CarDataStorage.getInstance();
        CityText.setText(storage.getCity());
        YearText.setText(String.valueOf(storage.getYear()));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InfoAdapter(storage.getCarData());
        recyclerView.setAdapter(adapter);
    }

}
