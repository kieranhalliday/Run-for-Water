package yukon.runforlife;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Kieran Halliday on 2017-11-06
 */

public class EnterDetailedWellData extends AppCompatActivity implements View.OnClickListener {
    private EditText cdnGroup, streetAddress, town, county, typeOfProperty, localTerrain,
            contactName, contactCell, drillingStartDate, drillingFinishDate,
            pumpInstallationDate, comments, numberOfDaysDrilling,
            averageDrillingPerDayInMeters, depth, depthToWater, depthOfPumpIntake, locationRelativeToSlope,
            depthToBedrock, depthDrilledIntoBedRock, numberOfNearbyWells, idOfNearbyWells,
            wellWaterColumn, wellName, drySeasonWaterTableDepth, wetSeasonWaterTableDepth,
            drySeasonFlowRate, wetSeasonFlowRate;
    private RadioButton historical, rockBitUsed;

    private User currentUser;
    private double latitude, longitude;
    private String idToUse;
    HashMap<LatLng, String> wellIds = new HashMap<>();

    private InformationWell informationWell;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extra_data_page);

        mAuth = FirebaseAuth.getInstance();
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
        Button sendData1 = findViewById(R.id.sendData1);
        Button switchScreens = findViewById(R.id.switchViews);
        Button switchScreens1 = findViewById(R.id.switchViews1);

        sendData.setOnClickListener(this);
        sendData1.setOnClickListener(this);
        switchScreens.setOnClickListener(this);
        switchScreens1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.switchViews:
                if (findViewById(R.id.number_screen).getVisibility() == View.GONE) {
                    findViewById(R.id.number_screen).setVisibility(View.VISIBLE);
                    findViewById(R.id.string_screen).setVisibility(View.GONE);
                    break;
                } else {
                    findViewById(R.id.number_screen).setVisibility(View.GONE);
                    findViewById(R.id.string_screen).setVisibility(View.VISIBLE);
                    break;
                }

            case R.id.switchViews1:
                if (findViewById(R.id.number_screen).getVisibility() == View.GONE) {
                    findViewById(R.id.number_screen).setVisibility(View.VISIBLE);
                    findViewById(R.id.string_screen).setVisibility(View.GONE);
                    break;
                } else {
                    findViewById(R.id.number_screen).setVisibility(View.GONE);
                    findViewById(R.id.string_screen).setVisibility(View.VISIBLE);
                    break;
                }

            case R.id.sendData:
                saveWell();
                finish();
                break;

            case R.id.sendData1:
                saveWell();
                finish();
                break;
        }
    }

    private void saveWell() {
        myRef = database.getReference().child("Wells").child(String.valueOf(idToUse));

        informationWell = new InformationWell.InfoWellBuilder(
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

        if (streetAddress.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("streetAddress");
            newRef.setValue(streetAddress.getText().toString());
        }

        if (town.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("town");
            newRef.setValue(town.getText().toString());
        }

        if (cdnGroup.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("cdnGroup");
            newRef.setValue(cdnGroup.getText().toString());
        }

        if (county.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("county");
            newRef.setValue(county.getText().toString());
        }

        if (typeOfProperty.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("typeOfProperty");
            newRef.setValue(typeOfProperty.getText().toString());
        }

        if (localTerrain.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("localTerrain");
            newRef.setValue(localTerrain.getText().toString());
        }

        if (contactName.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("contactName");
            newRef.setValue(contactName.getText().toString());
        }

        if (contactCell.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("contactCell");
            newRef.setValue(contactCell.getText().toString());
        }

        if (drillingStartDate.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("drillingStartDate");
            newRef.setValue(drillingStartDate.getText().toString());
        }

        if (drillingFinishDate.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("drillingFinishDate");
            newRef.setValue(drillingFinishDate.getText().toString());
        }

        if (pumpInstallationDate.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("pumpInstallationDate");
            newRef.setValue(pumpInstallationDate.getText().toString());
        }

        if (comments.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("comments");
            newRef.setValue(comments.getText().toString());
        }

        if (numberOfDaysDrilling.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("numberOfDaysDrilling");
            newRef.setValue(numberOfDaysDrilling.getText().toString());
        }

        if (averageDrillingPerDayInMeters.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("averageDrillingPerDayInMeters");
            newRef.setValue(averageDrillingPerDayInMeters.getText().toString());
        }

        if (depth.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("depth");
            newRef.setValue(depth.getText().toString());
        }

        if (depthToWater.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("depthToWater");
            newRef.setValue(depthToWater.getText().toString());
        }

        if (depthOfPumpIntake.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("depthOfPumpIntake");
            newRef.setValue(depthOfPumpIntake.getText().toString());
        }

        if (locationRelativeToSlope.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("locationRelativeToSlope");
            newRef.setValue(locationRelativeToSlope.getText().toString());
        }

        if (depthToBedrock.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("depthToBedrock");
            newRef.setValue(depthToBedrock.getText().toString());
        }

        if (depthDrilledIntoBedRock.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("depthDrilledIntoBedrock");
            newRef.setValue(depthDrilledIntoBedRock.getText().toString());
        }

        if (numberOfNearbyWells.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("numberOfNearbyWells");
            newRef.setValue(numberOfNearbyWells.getText().toString());
        }

        if (idOfNearbyWells.getText().toString().length() > 0) {
            DatabaseReference newRef = myRef.child("idsOfNearbyWells");
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


        DatabaseReference rockRef = myRef.child("rockBitUsed");
        rockRef.setValue(rockBitUsed.isChecked());


        DatabaseReference histRef = myRef.child("historical");
        histRef.setValue(historical.isChecked());

    }

    public void setListeners() {
        wellName = findViewById(R.id.well_name);
        cdnGroup = findViewById(R.id.cdnGroup);
        streetAddress = findViewById(R.id.street_address);
        town = findViewById(R.id.town);
        county = findViewById(R.id.county);
        typeOfProperty = findViewById(R.id.typeOfProperty);
        localTerrain = findViewById(R.id.localTerrain);
        contactName = findViewById(R.id.contactName);
        contactCell = findViewById(R.id.contactCell);
        drillingStartDate = findViewById(R.id.drilling_start_date);
        drillingFinishDate = findViewById(R.id.drillingFinishDate);
        comments = findViewById(R.id.comments);
        pumpInstallationDate = findViewById(R.id.pumpInstallationDate);
        numberOfDaysDrilling = findViewById(R.id.numberOfDaysDrilling);
        averageDrillingPerDayInMeters = findViewById(R.id.averageDrillingPerDayInMeters);
        depth = findViewById(R.id.depth);
        depthToWater = findViewById(R.id.depthToWater);
        depthOfPumpIntake = findViewById(R.id.depthOfPumpIntake);
        locationRelativeToSlope = findViewById(R.id.locationRelativeToSlope);
        depthToBedrock = findViewById(R.id.depthToBedrock);
        depthDrilledIntoBedRock = findViewById(R.id.depthDrilledIntoBedRock);
        numberOfNearbyWells = findViewById(R.id.numberOfNearbyWells);
        idOfNearbyWells = findViewById(R.id.idOfNearbyWells);
        wellWaterColumn = findViewById(R.id.wellWaterColumn);
        rockBitUsed = findViewById(R.id.rockbit);
        historical = findViewById(R.id.historical);
        drySeasonFlowRate = findViewById(R.id.drySeasonFlowRate);
        drySeasonWaterTableDepth = findViewById(R.id.drySeasonWaterTableDepth);
        wetSeasonFlowRate = findViewById(R.id.wetSeasonFlowRate);
        wetSeasonWaterTableDepth = findViewById(R.id.wetSeasonWaterTableDepth);


    }
}
