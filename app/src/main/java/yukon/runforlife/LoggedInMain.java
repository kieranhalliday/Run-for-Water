package yukon.runforlife;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import yukon.runforlife.fragments.BuyFragment;
import yukon.runforlife.fragments.InfoFragment;
import yukon.runforlife.fragments.MessageFragment;
import yukon.runforlife.fragments.ProfileFragment;
import yukon.runforlife.fragments.WebViewFragment;

/**
 * Created by Kieran Halliday on 2017-10-31
 */

// This is the main class, handles the map and marker placement
public class LoggedInMain extends LocationProvider implements OnMapReadyCallback,
        BottomNavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener {

    HashMap<LatLng, String> wellIds = new HashMap<>();
    HashMap<LatLng, String> wellOwners = new HashMap<>();
    HashMap<LatLng, Marker> locationToMarkers = new HashMap<>();

    ArrayList<LatLng> toBeDeleted = new ArrayList<>();
    ArrayList<InformationWell> closestWells = new ArrayList<>();

    // User info
    private User currentUser = null;
    private String email = "", partnerUser = "", username = "";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    //Pop-ups
    private AlertDialog enterDataPopUp;
    int current;

    Gson gS;

    // [START LIFECYCLE MANAGEMENT]
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logged_in_main);
        gS = new Gson();

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        // Views
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Listeners
        Objects.requireNonNull(mapFragment).getMapAsync(this);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            changeFragment(Objects.requireNonNull(getIntent().getExtras()).getInt("fragToStart"));
        } catch (NullPointerException e) {
            changeFragment(0);
        }

        // Parse user data into object
        myRef = database.getReference().child("Users").child(user.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    switch (Objects.requireNonNull(child.getKey())) {
                        case "email":
                            email = String.valueOf(child.getValue());
                            break;
                        case "partnerUser":
                            partnerUser = String.valueOf(child.getValue());
                            break;
                        case "username":
                            username = String.valueOf(child.getValue());
                            break;
                        default:
                            break;
                    }
                }
                currentUser = new User.UserBuilder(email, username).partnerUser(partnerUser).build();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("Database Error", "Failed to read value.", databaseError.toException());
            }
        });

        // Parse well data into object
        myRef = database.getReference().child("Wells");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Double lat = 0.0, lon = 0.0;
                    String id = "", owner = "", historical = "FALSE";

                    for (DataSnapshot infant : child.getChildren()) {
                        switch (Objects.requireNonNull(infant.getKey())) {
                            case "wellLatitude":
                                lat = (Double) infant.getValue();
                                break;
                            case "wellLongitude":
                                lon = (Double) infant.getValue();
                                break;
                            case "wellId":
                                id = String.valueOf(infant.getValue());
                                break;
                            case "contactEmail":
                                owner = String.valueOf(infant.getValue());
                                break;
                            case "historical":
                                historical = String.valueOf(infant.getValue());
                        }
                    }
                    try {
                        MarkerOptions options = new MarkerOptions()
                                .position(new LatLng(lat, lon));
                        Marker m = mMap.addMarker(options);
                        if (historical.contains("true")) {
                            // Historical Wells are in blue
                            m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        } else {
                            m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        }

                        wellIds.put(new LatLng(lat, lon), id);
                        wellOwners.put(new LatLng(lat, lon), owner);
                        if (!toBeDeleted.contains(new LatLng(lat, lon))) {
                            locationToMarkers.put(new LatLng(lat, lon), m);
                        } else {
                            child.getRef().removeValue();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("Database Error", "Failed to read value.", databaseError.toException());
            }
        });
    }

    // [START APP BAR]
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                changeFragment(0);
                return true;

            case R.id.get_educational_content:
                changeFragment(5);
                return true;

            case R.id.webView:
                changeFragment(4);
                return true;

            case R.id.sign_out_app_bar:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        findViewById(R.id.mapFragmentContainer).setVisibility(View.GONE);

        switch (item.getItemId()) {
            case R.id.navigation_info:
                changeFragment(0);
                return true;
            case R.id.navigation_donate:
                changeFragment(1);
                return true;
            case R.id.navigation_messages:
                Toast.makeText(getApplicationContext(), "Feature temporarily disabled. In development",
                        Toast.LENGTH_LONG).show();
                changeFragment(0);
                return true;
        }
        return false;
    }


    // Google Maps API Callbacks
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("New Marker")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // TODO
        // This method is a work in progress
        //Change color of selected
        //Change color of three closest and mark them as three closest
        //Add extra button in popup to view nearby well data
        //Set all other wells to red
        // No color change now because historical wells will always be one color

        myRef = database.getReference().child("Wells");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int start = 0;

                ArrayList<Float> distances = new ArrayList<>();
                HashMap<Float, InformationWell> wellHashMap = new HashMap<>();

                closestWells.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    InformationWell informationWell = child.getValue(InformationWell.class);

                    assert informationWell != null;
                    if(informationWell.getWellLongitude() == marker.getPosition().longitude
                            && informationWell.getWellLatitude() == marker.getPosition().latitude) {
                        closestWells.add(informationWell);
                    }

                    // Used to calculate distance between wells, work in progress
//                    float results[] = new float[1];
//                    Location.distanceBetween(informationWell != null ? informationWell.getWellLatitude() : 0, informationWell != null ? informationWell.getWellLongitude() : 0, marker.getPosition().latitude, marker.getPosition().longitude, results);
//                    distances.add(results[0]);
//                    wellHashMap.put(results[0], informationWell);
                }

//                Collections.sort(distances);
                // Also used to calculate distances between well, work in progress
//                for (int i = start; i <= start + 3 && distances.size() > 3; i++) {
//                    LatLng latLng = new LatLng(wellHashMap.get(distances.get(i)).getWellLatitude(), wellHashMap.get(distances.get(i)).getWellLongitude());
//                    Marker m = locationToMarkers.get(latLng);
//                    m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                    closestWells.add(wellHashMap.get(distances.get(i)));
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("Database Error", "Failed to read value.", databaseError.toException());
            }
        });

        if (doIOwnThisWell(marker.getPosition())) {
            showChangeWellDataDialog(marker);
        } else {
            Toast.makeText(getApplicationContext(), "You must have created this well in order to edit its information",
                    Toast.LENGTH_SHORT).show();
            showAlternativeDialog();
        }
        return true;
    }


    // START MY CUSTOM METHODS
    void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        currentUser = null;
        AuthUI.getInstance()
                .signOut(this);
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        startActivity(intent);

    }

    public void showChangeWellDataDialog(final Marker marker) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        // According to the documentation, passing null in this situation is ok
        final View dialogView = inflater.inflate(R.layout.enter_data_pop_up, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.well_name);
        final EditText edt1 = dialogView.findViewById(R.id.street_address);
        final EditText edt2 = dialogView.findViewById(R.id.town);
        final Button addNewWellDataButton = dialogView.findViewById(R.id.more_data);
        final Button addHistoricalWellDataButton = dialogView.findViewById(R.id.enter_historical_data);
        final Button remove = dialogView.findViewById(R.id.remove_marker);
        final Button showNearbyData = dialogView.findViewById(R.id.show_well_data);

        addNewWellDataButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), EnterDetailedWellData.class);

            intent.putExtra("Current User", new GsonBuilder().create().toJson(currentUser, User.class));
            intent.putExtra("Latitude", marker.getPosition().latitude);
            intent.putExtra("Longitude", marker.getPosition().longitude);
            intent.putExtra("idToUse", getWellIdToUse(marker.getPosition()));
            intent.putExtra("List of Current Wells", wellIds);

            enterDataPopUp.dismiss();
            startActivity(intent);
        });

        addHistoricalWellDataButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), EnterHistoricalWellData.class);

            intent.putExtra("Current User", new GsonBuilder().create().toJson(currentUser, User.class));
            intent.putExtra("Latitude", marker.getPosition().latitude);
            intent.putExtra("Longitude", marker.getPosition().longitude);
            intent.putExtra("idToUse", getWellIdToUse(marker.getPosition()));
            intent.putExtra("List of Current Wells", wellIds);

            enterDataPopUp.dismiss();
            startActivity(intent);
        });

        remove.setOnClickListener(view -> {
            //marker.remove();

            //toBeDeleted.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
            enterDataPopUp.dismiss();
        });

        showNearbyData.setOnClickListener(view -> {
            enterDataPopUp.dismiss();
            showAlternativeDialog();
        });


        dialogBuilder.setTitle("Well information");
        dialogBuilder.setMessage("Enter required information below");
        dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> {
            String name = edt.getText().toString();
            String address = edt1.getText().toString();
            String town = edt2.getText().toString();
            String email = currentUser != null ? currentUser.getEmail() : "No email provided";

            InformationWell informationWell = new InformationWell.InfoWellBuilder(
                    email,
                    marker.getPosition().latitude,
                    marker.getPosition().longitude,
                    getWellIdToUse(marker.getPosition()))
                    .build();

            myRef = database.getReference().child("Wells").child(String.valueOf(informationWell.getWellId()));

            if (!wellIds.containsKey(marker.getPosition())) {
                Map<String, Object> well = new HashMap<>();
                well.put(informationWell.getWellId(), informationWell);
                database.getReference().child("Wells").updateChildren(well);
            }

            if (name.length() > 0) {
                DatabaseReference newRef = myRef.child("name");
                newRef.setValue(name);
            }

            if (address.length() > 0) {
                DatabaseReference newRef = myRef.child("streetAddress");
                newRef.setValue(address);
            }

            if (town.length() > 0) {
                DatabaseReference newRef = myRef.child("town");
                newRef.setValue(town);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
            // do nothing
        });
        enterDataPopUp = dialogBuilder.create();
        enterDataPopUp.show();
    }

    private void showAlternativeDialog() {
        if (closestWells.size() < 1) {
            Toast.makeText(LoggedInMain.this, "Must have at least one well nearby in order to see nearby data",
                    Toast.LENGTH_LONG).show();
            return;
        }

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        // According to the documentation, passing null in this situation is ok
        final View dialogView = inflater.inflate(R.layout.show_nearest_wells, null);
        dialogBuilder.setView(dialogView);

        final TextView textView1 = dialogView.findViewById(R.id.tv1);
        final Button nextData = dialogView.findViewById(R.id.next_data);

        current = 1;

        dialogBuilder.setTitle("Well Information");
        dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> {

        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
            // do nothing
        });

        nextData.setOnClickListener(view -> {
            textView1.setVisibility(View.VISIBLE);
            String text1 = "WellId:" + closestWells.get(0).getWellId()
                    + "\nLat" + closestWells.get(0).getWellLatitude()
                    + "\nLong" + closestWells.get(0).getWellLongitude();

            textView1.setText(text1);
        });

        enterDataPopUp = dialogBuilder.create();
        enterDataPopUp.show();

    }

    public String getWellIdToUse(LatLng latLng) {
        if (wellIds.containsKey(latLng)) {
            return wellIds.get(latLng);
        } else {
            return String.valueOf(wellIds.size());
        }
    }

    public boolean doIOwnThisWell(LatLng latLng) {
        return !wellOwners.containsKey(latLng) || wellOwners.get(latLng).equals(currentUser.getEmail());
    }


    // [BOTTOM BAR NAVIGATION]
    private void changeFragment(int position) {

        Fragment newFragment;
        Boolean stayOnSameFragment = false;

        // Pressing home button
        if (position == 0) {
            findViewById(R.id.mapFragmentContainer).setVisibility(View.VISIBLE);
            return;

        } else if (position == 1) {
            newFragment = new BuyFragment();

        } else if (position == 2) {
            if (currentUser.getPartnerUser().equals("")) {
                Toast.makeText(LoggedInMain.this, "Must be paired in order to chat",
                        Toast.LENGTH_SHORT).show();
                stayOnSameFragment = true;
                newFragment = null;
            } else {
                Bundle bundle = new Bundle();
                String target = gS.toJson(currentUser);
                bundle.putString("Current User", target);

                MessageFragment fragobj = new MessageFragment();
                fragobj.setArguments(bundle);
                newFragment = fragobj;
            }

        } else if (position == 3) {
            newFragment = new ProfileFragment();

        } else if (position == 4) {
            newFragment = new WebViewFragment();

        } else {
            newFragment = new InfoFragment();

        }

        if (!stayOnSameFragment) {
            findViewById(R.id.mapFragmentContainer).setVisibility(View.GONE);
            getFragmentManager().beginTransaction().replace(
                    R.id.fragmentContainer, newFragment)
                    .commit();
        }
    }
}





