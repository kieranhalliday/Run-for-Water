package yukon.runforwater;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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

import yukon.runforwater.fragments.BuyFragment;
import yukon.runforwater.fragments.InfoFragment;
import yukon.runforwater.fragments.MessageFragment;
import yukon.runforwater.fragments.ProfileFragment;
import yukon.runforwater.fragments.WebViewFragment;

/**
 * Created by Kieran Halliday on 2017-10-31
 */

// TODO
/*
MAJOR
    Game functionality

MINOR
    Let users upload a profile picture/get chat images

    Change pop up colours if no email or password entered
    Create a theme colour scheme for the app
    Change navigation bar icons (Donate, View information, messaging)
 */
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
    private String bio = "", email = "", kenyan = "", partnerUser = "", username = "";

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
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Listeners
        mapFragment.getMapAsync(this);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            changeFragment(getIntent().getExtras().getInt("fragToStart"));
        } catch (NullPointerException e) {
            changeFragment(0);
        }

        myRef = database.getReference().child("Users").child(user.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    switch (child.getKey()) {
                        case "bio":
                            bio = String.valueOf(child.getValue());
                            break;
                        case "email":
                            email = String.valueOf(child.getValue());
                            break;
                        case "kenyan":
                            kenyan = String.valueOf(child.getValue());
                            break;
                        case "partnerUser":
                            partnerUser = String.valueOf(child.getValue());
                            break;
                        case "username":
                            username = String.valueOf(child.getValue());
                            break;
                    }
                }
                currentUser = new User.UserBuilder(email, kenyan.contains("true"), username).bio(bio).partnerUser(partnerUser).build();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("Database Error", "Failed to read value.", databaseError.toException());
            }
        });

        myRef = database.getReference().child("Wells");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Double lat = 0.0, lon = 0.0;
                    String id = "", owner = "";

                    for (DataSnapshot infant : child.getChildren()) {
                        if (infant.getKey().equals("wellLatitude"))
                            lat = (Double) infant.getValue();
                        else if (infant.getKey().equals("wellLongitude"))
                            lon = (Double) infant.getValue();
                        else if (infant.getKey().equals("wellId"))
                            id = String.valueOf(infant.getValue());
                        else if (infant.getKey().equals("contactEmail"))
                            owner = String.valueOf(infant.getValue());
                    }
                    try {
                        MarkerOptions options = new MarkerOptions()
                                .position(new LatLng(lat, lon));
                        Marker m = mMap.addMarker(options);
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
            public void onCancelled(DatabaseError databaseError) {
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
//            case R.id.build_profile_from_app_barr:
//                changeFragment(3);
//                return true;

            case android.R.id.home:
                // This manages the back button on the toolbar
                // Maybe call super?
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
                changeFragment(2);
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
        //Change color of selected
        //Change color of three closest and mark them as three closest
        //Add extra button in popup to view nearby well data
        //Set all other wells to red

        myRef = database.getReference().child("Wells");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int start = 0;

                ArrayList<Float> distances = new ArrayList<>();
                HashMap<Float, InformationWell> wellHashMap = new HashMap<>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    InformationWell informationWell = child.getValue(InformationWell.class);
                    float results[] = new float[1];
                    Location.distanceBetween(informationWell.getWellLatitude(), informationWell.getWellLongitude(), marker.getPosition().latitude, marker.getPosition().longitude, results);
                    distances.add(results[0]);
                    wellHashMap.put(results[0], informationWell);
                }

                Collections.sort(distances);
                closestWells.clear();
                for (int i = start; i <= start + 3; i++) {
                    LatLng latLng = new LatLng(wellHashMap.get(distances.get(i)).getWellLatitude(), wellHashMap.get(distances.get(i)).getWellLongitude());
                    Marker m = locationToMarkers.get(latLng);
                    m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    closestWells.add(wellHashMap.get(distances.get(i)));

                }

                for (int i = start + 4; i < distances.size(); i++) {
                    LatLng latLng = new LatLng(wellHashMap.get(distances.get(i)).getWellLatitude(), wellHashMap.get(distances.get(i)).getWellLongitude());
                    Marker m = locationToMarkers.get(latLng);
                    m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        startActivity(intent);
    }

    public void showChangeWellDataDialog(final Marker marker) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.enter_data_pop_up, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.well_name);
        final EditText edt1 = dialogView.findViewById(R.id.street_address);
        final EditText edt2 = dialogView.findViewById(R.id.town);
        final Button button = dialogView.findViewById(R.id.more_data);
        final Button remove = dialogView.findViewById(R.id.remove_marker);
        final Button showNearbyData = dialogView.findViewById(R.id.show_nearby_wells_data);

        button.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), EnterDetailedWellData.class);

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

            InformationWell informationWell = new InformationWell.InfoWellBuilder(
                    currentUser.getEmail(),
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
            //pass
        });
        enterDataPopUp = dialogBuilder.create();
        enterDataPopUp.show();
    }

    private void showAlternativeDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.show_nearest_wells, null);
        dialogBuilder.setView(dialogView);

        final TextView textView1 = dialogView.findViewById(R.id.tv1);
        final TextView textView2 = dialogView.findViewById(R.id.tv2);
        final TextView textView3 = dialogView.findViewById(R.id.tv3);
        final Button nextData = dialogView.findViewById(R.id.next_data);
        
        current = 1;

        dialogBuilder.setTitle("Nearby Information");
        dialogBuilder.setMessage("Nearby data");
        dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> {

        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
            //pass
        });


        nextData.setOnClickListener(view -> {
            switch (current) {
                case 1:
                    textView1.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.GONE);
                    textView3.setVisibility(View.GONE);

                    String text1 = "Drilled:" + closestWells.get(0).getDepth()
                            + " Dist to h20: " + closestWells.get(0).getDepthToWater()
                            + " Dist to bedrock: " + closestWells.get(0).getDepthToBedrock();
                    textView1.setText(text1);
                    current = 1;
                    break;

                case 2:
                    textView1.setVisibility(View.GONE);
                    textView2.setVisibility(View.VISIBLE);
                    textView3.setVisibility(View.GONE);

                    String text2 = "Drilled:" + closestWells.get(1).getDepth()
                            + " Dist to h20: " + closestWells.get(1).getDepthToWater()
                            + " Dist to bedrock: " + closestWells.get(1).getDepthToBedrock();
                    textView2.setText(text2);
                    current++;
                    break;

                case 3:
                    textView1.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);
                    textView3.setVisibility(View.VISIBLE);

                    String text3 = "Drilled:" + closestWells.get(2).getDepth()
                            + " Dist to h20: " + closestWells.get(2).getDepthToWater()
                            + " Dist to bedrock: " + closestWells.get(2).getDepthToBedrock();
                    textView3.setText(text3);
                    current++;
                    break;
            }
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


    // Might be useful later


//    void sendEmailVerification() {
//        final FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            user.sendEmailVerification()
//                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(LoggedInMain.this,
//                                        "Verification email sent to " + user.getEmail(),
//                                        Toast.LENGTH_SHORT).show();
//                            } else {
//                                Log.e(TAG, "sendEmailVerification", task.getException());
//                                Toast.makeText(LoggedInMain.this,
//                                        "Failed to send verification email.",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
//    }
}





