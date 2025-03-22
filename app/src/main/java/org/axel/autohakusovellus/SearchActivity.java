package org.axel.autohakusovellus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    private EditText CityNameEdit;
    private EditText YearEdit;
    private TextView StatusText;
    private static final String API_URL = "https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/mkan/statfin_mkan_pxt_11ic.px";
    private HashMap<String, String> areaCodeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CityNameEdit = findViewById(R.id.CityNameEdit);
        YearEdit = findViewById(R.id.YearEdit);
        StatusText = findViewById(R.id.StatusText);
        fetchData();
    }
    public void fetchData() {
        new Thread(() -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode metadata = objectMapper.readTree(new URL(API_URL));

                JsonNode areaNames = metadata.get("variables").get(0).get("valueTexts");
                JsonNode areaCodes = metadata.get("variables").get(0).get("values");

                for (int i = 0; i < areaNames.size(); i++) {
                    areaCodeMap.put(areaNames.get(i).asText().toLowerCase(), areaCodes.get(i).asText());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getData(Context context, String city, int year) {
        new Thread(() -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                String areaCode = areaCodeMap.get(city.toLowerCase());
                if (areaCode == null) {
                    runOnUiThread(()->StatusText.setText("Haku epäonnistui, kaupunkia ei olemassa tai se on kirjoitettu väärin."));
                    return;
                }

                JsonNode jsonRequest = objectMapper.readTree(context.getResources().openRawResource(R.raw.query));

                ((ObjectNode) jsonRequest.get("query").get(0).get("selection")).putArray("values").add(areaCode);
                ((ObjectNode) jsonRequest.get("query").get(2).get("selection")).putArray("values").add("0");
                ((ObjectNode) jsonRequest.get("query").get(3).get("selection")).putArray("values").add(String.valueOf(year));

                URL url = new URL(API_URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                byte[] input = objectMapper.writeValueAsBytes(jsonRequest);
                try (OutputStream os = con.getOutputStream()) {
                    os.write(input, 0, input.length);
                }

                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }
                }

                JsonNode responseData = objectMapper.readTree(response.toString());

                JsonNode vehicleTypes = responseData.get("dimension").get("Ajoneuvoluokka").get("category").get("label");
                JsonNode values = responseData.get("value");

                CarDataStorage storage = CarDataStorage.getInstance();
                storage.clearData();
                storage.setCity(city);
                storage.setYear(year);

                for (int i = 0; i < values.size(); i++) {
                    String type = vehicleTypes.get(String.valueOf(i)).asText();
                    int amount = values.get(i).asInt();
                    storage.addCarData(new CarData(type, amount));
                }

                runOnUiThread(()->StatusText.setText("Haku onnistui"));

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(()->StatusText.setText("Haku epäonnistui, kaupunkia ei olemassa tai se on kirjoitettu väärin."));
            }
        }).start();
    }

    public void onSearchPressed(View view) {
        String city = CityNameEdit.getText().toString();
        String yearString = YearEdit.getText().toString();

        if (city.isEmpty()) {
            StatusText.setText("Haku epäonnistui, kaupunkia ei olemassa tai se on kirjoitettu väärin.");
            return;
        }
        try {
            int year = Integer.parseInt(yearString);
            getData(this, city, year);
        } catch (NumberFormatException e) {
            StatusText.setText("Haku epäonnistui, kaupunkia ei olemassa tai se on kirjoitettu väärin.");
        }
    }

    public void onMoveToListPressed(View view) {
        Intent intent = new Intent(this, ListInfoActivity.class);
        startActivity(intent);
    }

    public void onBackToMainPressed(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}