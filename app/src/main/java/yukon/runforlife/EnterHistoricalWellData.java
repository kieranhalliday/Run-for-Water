package yukon.runforlife;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnterHistoricalWellData extends Activity implements View.OnClickListener {
    HashMap<LatLng, String> wellIds = new HashMap<>();
    private EditText town, county, localTerrain, comments, depth, depthToWater,
            wellWaterColumn, wellName, drySeasonWaterTableDepth, wetSeasonWaterTableDepth,
            drySeasonFlowRate, wetSeasonFlowRate;
    private User currentUser;
    private double latitude, longitude;
    private String idToUse;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historical_data_page);

        database = FirebaseDatabase.getInstance();


        String target = Objects.requireNonNull(getIntent().getExtras()).getString("Current User");
        Gson gS = new Gson();
        currentUser = gS.fromJson(target, User.class);

        latitude = getIntent().getExtras().getDouble("Latitude");
        longitude = getIntent().getExtras().getDouble("Longitude");
        idToUse = getIntent().getExtras().getString("idToUse");
        wellIds = (HashMap<LatLng, String>) getIntent().getExtras().get("List of Current Wells");

        setListeners();

        Button sendData = findViewById(R.id.sendData);
        Button cancel = findViewById(R.id.cancel_button);

        sendData.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.sendData:
                saveWell();
                finish();
                break;
            case R.id.cancel_button:
                finish();
                break;
        }
    }

    private void saveWell() {
        myRef = database.getReference().child("Wells").child(String.valueOf(idToUse));

        InformationWell informationWell = new InformationWell.InfoWellBuilder(
                currentUser.getEmail(),
                latitude,
                longitude,
                idToUse)
                .build();

        if (!wellIds.containsKey(new LatLng(latitude, longitude))) {
            Map<String, Object> well = new HashMap<>();
            well.put(informationWell.getWellId(), informationWell);
            database.getReference().child("Wells").updateChildren(well);
        }

        getExtraFields();
    }

    private void getExtraFields() {
        if (wellName.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("name");
            newRef.setValue(wellName.getText().toString());
        }

        if (town.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("town");
            newRef.setValue(town.getText().toString());
        }

        if (county.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("county");
            newRef.setValue(county.getText().toString());
        }

        if (localTerrain.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("localTerrain");
            newRef.setValue(localTerrain.getText().toString());
        }

        if (comments.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("comments");
            newRef.setValue(comments.getText().toString());
        }


        if (depth.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("depth");
            newRef.setValue(depth.getText().toString());
        }

        if (depthToWater.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("depthToWater");
            newRef.setValue(depthToWater.getText().toString());
        }

        if (wellWaterColumn.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("wellWaterColumn");
            newRef.setValue(wellWaterColumn.getText().toString());
        }

        if (drySeasonFlowRate.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("drySeasonFlowRate");
            newRef.setValue(drySeasonFlowRate.getText().toString());
        }

        if (drySeasonWaterTableDepth.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("drySeasonWaterTableDepth");
            newRef.setValue(drySeasonWaterTableDepth.getText().toString());
        }

        if (wetSeasonWaterTableDepth.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("wetSeasonWaterTableDepth");
            newRef.setValue(wetSeasonWaterTableDepth.getText().toString());
        }

        if (wetSeasonFlowRate.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("wetSeasonFlowRate");
            newRef.setValue(wetSeasonFlowRate.getText().toString());
        }

        DatabaseReference histRef = myRef.child("historical");
        histRef.setValue(true);

    }

    public void setListeners() {
        wellName = findViewById(R.id.well_name);
        town = findViewById(R.id.town);
        county = findViewById(R.id.county);
        depth = findViewById(R.id.depth);
        depthToWater = findViewById(R.id.depthToWater);
        localTerrain = findViewById(R.id.localTerrain);
        wellWaterColumn = findViewById(R.id.wellWaterColumn);
        drySeasonFlowRate = findViewById(R.id.drySeasonFlowRate);
        drySeasonWaterTableDepth = findViewById(R.id.drySeasonWaterTableDepth);
        wetSeasonFlowRate = findViewById(R.id.wetSeasonFlowRate);
        wetSeasonWaterTableDepth = findViewById(R.id.wetSeasonWaterTableDepth);
        comments = findViewById(R.id.comments);
    }
}
