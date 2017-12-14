package com.project.beachnav.beachnav.activity;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.beachnav.beachnav.R;
import com.project.beachnav.beachnav.other.LocationSet;
import com.project.beachnav.beachnav.other.Node;
import com.project.beachnav.beachnav.other.PathHandler;
import com.project.beachnav.beachnav.other.UserLocation;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created 10/25/2017.
 */

public class MapFragActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private final LatLngBounds CSULB_Bounds =
            new LatLngBounds(new LatLng(33.775, -118.124241), new LatLng(33.789, -118.108));
    private final LatLng CSULB = new LatLng(33.781932, -118.11535);
    private GroundOverlay csulbOverlay;

    private Map<String, Node> mapPlaces = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private EditText location_tf;
    private Marker
            searched_location = null,
            user_location = null;

    protected Node currentLoc = null;
    private Node searchedLoc = null;

    private ArrayList<Node> path;
    private PathHandler pathHandler;
    private Location myLocation;
    private LatLng route = new LatLng(0,0);
    private UserLocation userLocation;
    double userLat, userLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.BN_map);
        mapFragment.getMapAsync(this);

//        if (savedInstanceState != null) {
//            String mId = savedInstanceState.getString("searched_location");
//        }

        initializePathOverlay(); //currentLoc is instantiated here

        userLocation = new UserLocation(getApplicationContext()); //user location checker is here

//      ui based instantiation
        location_tf = findViewById(R.id.editText);
        location_tf.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if ( (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    onSearch(v); hideSoftKeyboard(); return true; //drops pin on searched location
                } return false;
            }
        }); //drops soft keyboard after search is done
        location_tf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {            }
        }); // so it doesn't crash
        location_tf.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                location_tf.setText(""); return true;
            }
        }); // clears search entry when long pressing search bar

        Button routeButton = findViewById(R.id.route);
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onSearch(v);
                onRoute();
            }
        });
        //drops pin on searched location, then routes there
        routeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                location_tf.setError("Route from your location to the location you searched"); return true;
            }
        }); // shows information for route button

        Button currentLocButton = findViewById(R.id.location);
        currentLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCurrentLocation();
            }
        });
        currentLocButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                location_tf.setError("Press to lock on to your location"); return true;
            }
        }); // shows information for location button

        Button setCurrentLocation = findViewById(R.id.setCurrentLoc);
        setCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCurrentLocation();
            }
        });
        setCurrentLocation.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                location_tf.setError("Press to set search entry as the 'user' location. If there is no search entry when this is pressed, and there is a searched location marker, already, then this location will become the 'user' location"); return true;
            }
        });

//        DEV MODE ONLY, COMMENT OUT FOR RELEASE
        CheckBox disabledModeCheckbox = findViewById(R.id.handicapMode);
        disabledModeCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });

        CheckBox overlayToggleCheckbox = findViewById(R.id.overlayToggle);
        overlayToggleCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        overlayToggleCheckbox.setChecked(true); //set toggle to on, as overlay is there at start

        Log.i("MapFragActivity", "all buttons and checkboxes added");
    }

//  responds to check boxes and changes things on the view as necessary
    public void onCheckboxClicked(View v) {
        boolean checked = ((CheckBox) v).isChecked();

        switch(v.getId()) {
            case R.id.handicapMode: // handles check box for disabled mode
                if(checked) {// refer routing algorithm to alternate node map
                    Toast.makeText(this, "Disabled mode would be on", Toast.LENGTH_SHORT).show();
                } else {// have routing algorithm refer to normal node map
                    Toast.makeText(this, "Disabled mode would be off", Toast.LENGTH_SHORT).show();
                } break;
            case R.id.overlayToggle: // handles check box for toggle overlay
                if (checked) { //have overlay be on
                    Toast.makeText(this, "One moment...", Toast.LENGTH_SHORT).show();
                    placeOverlay();
                } else { //have overlay be off
                    csulbOverlay.remove();
                }

        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.appbar_menu, menu);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.search_location).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()){
//            case R.id.current_location: Toast.makeText(getApplicationContext(), "Location", Toast.LENGTH_SHORT).show(); return true;
//            case R.id.settings: Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show(); return true;
//            case R.id.help: Toast.makeText(getApplicationContext(), "Help", Toast.LENGTH_SHORT).show(); return true;
//            default: return super.onOptionsItemSelected(item);
//        }
//    }

//  drops down soft keyboard when pressed
    private void hideSoftKeyboard() {
        InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(location_tf.getWindowToken(),0);
    }

    /**
     * Will find a location that matches the search item as best as possible.
     * (Mapped to search dialog the same way findLocation was to that button)
     * ..we need to be able to handle anything that the search dialog can give
     *  -> auto-suggestions from a database?
     */
//  searches and modifies the mapFragment such that it shows the location of the string in question on the map.
    public void onSearch(View v) {

        if (path != null) {
            path = null; // nulls and un-draws the path (with clearPath())
            pathHandler.clearPath();
        }

        String location = location_tf.getText().toString().trim(); // takes string in textbox
                                                                //trim() allows use of autocomplete
        if (location.equals("")) { //handles empty string
            location_tf.setError("Can't search nothing. Try searching a location.");
        } else {
            try { //mapPlaces finds key:location and returns a Node containing location info
                searchedLoc = mapPlaces.get(location);
                LatLng latLng = new LatLng(searchedLoc.getX(),searchedLoc.getY());
                if (searched_location != null) { searched_location.setPosition(latLng);
                    searched_location.setTitle(searchedLoc.getLabel());
                } else // 'else' is called on first press in activity run
                    searched_location = mMap.addMarker(new MarkerOptions()
                            .position(latLng).title(searchedLoc.getLabel()));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            } catch (Exception e) { //what happens when location is not in the hashmap? this does
                location_tf.setError("Location not found. Try another location.");
            }   }
    }

    //event handler for route button, finds shortest path from current location to searched location
    public void onRoute() {
//        findCurrentLocation();
        if(route.latitude<=33.788763 && route.longitude>=-118.122509 // checks if
                && route.latitude>=33.775236 && route.longitude<=-118.108002) {

            try { currentLoc.setCoordinates(myLocation.getLatitude(), myLocation.getLongitude());
            }catch (Exception e) {
                Toast.makeText(this, "Please Enable Location Services", Toast.LENGTH_LONG).show();
                return;
            } Log.i("MapFragActivity", "myLocation - userLat: " + userLat + ", userLong: " + userLong);

            if (path != null) {
                path = null; //and then un-draw the path (with removePath()), and leave the marker
                pathHandler.clearPath();
            }

            try {
                path = Node.getPath(currentLoc, searchedLoc);
                pathHandler = new PathHandler(path, mMap);
                pathHandler.genVisualPath();
                pathHandler.show();
            } catch (Exception e) {
                location_tf.setError("We need your location and the location you want to go to.");
                e.printStackTrace();
                return;
            }
        } else{
            Toast.makeText(this, "Get on campus to route", Toast.LENGTH_LONG).show();
        }
    } // only navigates while user's currentLocation is within CSULB_bounds (endgame)

    // event handler for location button, finds current location using UserLocation
    public void findCurrentLocation() {
        //user location
//        userLocation = new UserLocation(getApplicationContext());

        myLocation = userLocation.getLocation(); //get location coordinates
        try {
            userLat = myLocation.getLatitude();
            userLong = myLocation.getLongitude();
            Log.i("MapFragActivity", "myLocation - userLat: " + userLat + ", userLong: " + userLong);

            currentLoc.setCoordinates(userLat, userLong); //updates currentLoc coordinates

            LatLng latLng = new LatLng(userLat, userLong); //updates currentLoc marker (user_location)
//            route = new LatLng(userLat, userLong);
            route = latLng;
            if (user_location != null) {user_location.setPosition(latLng); user_location.setTitle("You are here");}
            else {
                user_location = mMap.addMarker(new MarkerOptions()
                        .position(latLng).title("You are Here").anchor(0.5f,0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_location)));
            } Toast.makeText(this, "Loading User Location", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Please Enable Location Services", Toast.LENGTH_LONG).show();}
    }

    //  event handler that sets the search entry as the user's location
    public void changeCurrentLocation() {
        if (path != null) {
            path = null; //and then un-draw the path (with removePath()), and leave the marker
            pathHandler.clearPath();
        }
        String current = location_tf.getText().toString().trim();
        if (!current.equals("")) { //handles empty string
            try { //mapPlaces finds key:location and returns a Node containing location info
                currentLoc = mapPlaces.get(current);
                LatLng latLng = new LatLng(currentLoc.getX(),currentLoc.getY());

                route = latLng;
                if (user_location != null) { user_location.setPosition(latLng);
                    user_location.setTitle(currentLoc.getLabel());
                } else
                    user_location = mMap.addMarker(new MarkerOptions()
                            .position(latLng).title(currentLoc.getLabel()).anchor(0.5f,0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_location)));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            } catch (Exception e) {
                location_tf.setError("Location not found. Try another location.");
            }   }
        else {
            if (searchedLoc != null) { //replace currentLoc with searchedLoc
                Node temp = searchedLoc; searchedLoc = null; currentLoc = temp;
                        LatLng latLng = new LatLng(currentLoc.getX(), currentLoc.getY());
                route = latLng;
                // replace red marker with headhunter marker
                user_location.setPosition(latLng);
                user_location.setTitle(currentLoc.getLabel());
                user_location.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.target_location));
//                } else
//                    user_location = mMap.addMarker(new MarkerOptions()
//                            .position(latLng).title(currentLoc.getLabel()).anchor(0.5f,0.5f)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_location)));
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                searched_location.remove();
                searched_location = null; //removes searched_location marker from mMap
            }
        }
    }

    public void nodeSwap (Node x, Node y) {
        Node temp = x;
        x = y;
        y = temp;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker within CSULB.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings sett = mMap.getUiSettings();
        sett.setMyLocationButtonEnabled(true);
        sett.setZoomControlsEnabled(false);
        sett.setScrollGesturesEnabled(true);
        mMap.setMinZoomPreference(15.0f);
        mMap.setLatLngBoundsForCameraTarget(CSULB_Bounds);

        placeOverlay();
        Log.i("MapFragActivity", "overlay added");

        mMap.animateCamera(CameraUpdateFactory.newLatLng(CSULB));

        Toast.makeText(this, "Press and hold buttons for more information", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Press and hold search box to clear entry", Toast.LENGTH_SHORT).show();

        findCurrentLocation(); //after instantiation with initializePathOverlay(), this updates it


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////      TODO: Consider calling
//            return;
//        }
//        mMap.setMyLocationEnabled(true);   // something to look at
    }

    public void placeOverlay() {
        csulbOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.csulb_map2016_edited_c))
                .position(CSULB, 1570f, 1582f));
    }

    @Override
    protected void onPause() {
//      pause services, location updating, etc
        super.onPause();
    }
    @Override
    protected void onResume() {
//      resume services, location updating, etc
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//      save items and/or parcelables from the activity into the bundle for later use
        if (mMap != null) {
            outState.putParcelable("camera_position", mMap.getCameraPosition());
            outState.putString("searched_location", location_tf.getText().toString());
            super.onSaveInstanceState(outState);
        }
    }
    @Override // restores items and/or parcelables sent into the bundle back into the activity
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
//    things to save/ pull back out:
//    currentlocation, cameraposition, searchedlocation, map(done automatically), path(?)

    /*
    * 10/24/2017 - Carl Costa / Austin Leavitt - 11/13/2017
    * The plan is to change this to Map<String, Node>
    * Instantiates the map
    * this method hardcodes all the keys for all the places in CSULB
    * and then creates a web of nodes that represent every walkable path on campus
    *
    * future plan: transfer all information in this method into a sqlitebuilder database...
       *  would have 800 lines less
       *  back-end would have an easier time traversing for routing algorithm
    */
    protected void initializePathOverlay() {
        //Array of All Nodes
        LocationSet set = new LocationSet();

        //Campus Location Nodes
        Node ecs = new Node("ECS",33.783529, -118.110287, new ArrayList<Node>(),set);
        Node en2 = new Node("EN2",33.783215, -118.110925, new ArrayList<Node>(),set);
        Node en3 = new Node("EN3", 33.783694, -118.111157, new ArrayList<Node>(),set);
        Node en4 = new Node("EN4",33.783681, -118.110674, new ArrayList<Node>(),set);
        Node vec = new Node("VEC",33.782818, -118.110636, new ArrayList<Node>(),set);
        Node outpost = new Node("Outpost",33.782340, -118.110410, new ArrayList<Node>(),set);
        Node sspa = new Node ("SSPA", 33.782017, -118.110383, new ArrayList<Node>(),set);
        Node HC = new Node("Horn Center",33.783444, -118.113983, new ArrayList<Node>(),set);
        Node UAM = new Node("Museum", 33.783425, -118.114629, new ArrayList<Node>(),set);
        Node kin = new Node("Kinesiology", 33.782898, -118.112586, new ArrayList<Node>(),set);
        Node srwc = new Node("srwc",33.785038, -118.109484,new ArrayList<Node>(),set);
        Node hhs1 = new Node("hhs1",33.782388, -118.112801,new ArrayList<Node>(),set);
        Node hhs2 = new Node("hhs2", 33.782384, -118.112125, new ArrayList<Node>(),set);
        Node hhsGnrl = new Node("hhs", 33.782326, -118.112490, new ArrayList<Node>(),set);
        Node bh = new Node("Brotman Hall",33.782659, -118.115339, new ArrayList<Node>(),set);
        Node usu = new Node("Student Union",33.781281, -118.113450, new ArrayList<Node>(),set);
        Node cp = new Node("Central Plant",33.781316, -118.112386, new ArrayList<Node>(),set);
        Node cafe = new Node("Cafe",33.780574, -118.114071, new ArrayList<Node>(),set);
        Node bks = new Node("Bookstore",33.779974, -118.114158,new ArrayList<Node>(),set);
        Node mlsc = new Node("Molecular Science",33.780301, -118.112488,new ArrayList<Node>(),set);
        Node hsci = new Node("Hall of Science",33.779830, -118.112639,new ArrayList<Node>(),set);
        Node mic = new Node("Microbiology",33.779429, -118.111720, new ArrayList<Node>(),set);
        Node ph1 = new Node("Peterson Hall 1",33.778898, -118.112505, new ArrayList<Node>(),set);
        Node ph2 = new Node("Peterson Hall 2",33.779272, -118.112482, new ArrayList<Node>(),set);
        Node fa1 = new Node("FA1",33.777211, -118.112557, new ArrayList<Node>(),set);
        Node fa2 = new Node("FA2",33.777474, -118.112361, new ArrayList<Node>(),set);
        Node fa3 = new Node("FA3",33.777935, -118.112311, new ArrayList<Node>(),set);
        Node fa4 = new Node("FA4",33.778336, -118.112741, new ArrayList<Node>(),set);
        Node ut = new Node("University Theatre",33.776733, -118.112113,new ArrayList<Node>(),set);
        Node utc = new Node("UTC", 33.776735, -118.111652,new ArrayList<Node>(),set);
        Node ta = new Node("TA",33.776510, -118.112639,new ArrayList<Node>(),set);
        Node mhb = new Node("Macintosh Building",33.776882, -118.113202,new ArrayList<Node>(),set);
        Node as = new Node("as", 33.777009, -118.114096, new ArrayList<Node>(),set);
        Node lib = new Node("Library", 33.777207, -118.114842, new ArrayList<Node>(),set);
        Node la1 = new Node("Liberal Arts 1", 33.777664, -118.114713, new ArrayList<Node>(),set);
        Node la2 = new Node("Liberal Arts 2",33.777987, -118.114547,new ArrayList<Node>(),set);
        Node la3 = new Node("Liberal Arts 3",33.778292, -118.114440,new ArrayList<Node>(),set);
        Node la4 = new Node("Liberal Arts 4",33.778566, -118.114338,new ArrayList<Node>(),set);
        Node la5 = new Node("Liberal Arts 5",33.778898, -118.114241,new ArrayList<Node>(),set);
        Node lh = new Node("Lecture Hall",33.778187, -118.113976,new ArrayList<Node>(),set);
        Node cla = new Node("CLA",33.777815, -118.114132,new ArrayList<Node>(),set);
        Node psy = new Node("Psychology",33.779318, -118.114439,new ArrayList<Node>(),set);
        Node ed2 = new Node("Education 2",33.775727, -118.114354,new ArrayList<Node>(),set);
        Node eed = new Node("EED",33.776224, -118.114156,new ArrayList<Node>(),set);
        Node mmc = new Node("Multimedia Center",33.776768, -118.114561,new ArrayList<Node>(),set);
        Node annex = new Node("ANNEX",33.777081, -118.111909,new ArrayList<Node>(),set);
        Node lab = new Node("Language Arts Building",33.776887, -118.112687, new ArrayList<Node>(),set);
        Node fo2 = new Node("Faculty Office 2",33.778497, -118.113910,new ArrayList<Node>(),set);
        Node fo3 = new Node("Faculty Office 3",33.779128, -118.113688,new ArrayList<Node>(),set);
        Node fo4 = new Node("Faculty Office 4",33.778202, -118.111990,new ArrayList<Node>(),set);
        Node fo5 = new Node("Faculty Office 5",33.779103, -118.112462,new ArrayList<Node>(),set);
        Node pp  = new Node("Prospector Pete", 33.778759, -118.113802, new ArrayList<Node>(),set);
        Node cba = new Node("cba", 33.784164, -118.115942, new ArrayList<Node>(),set);
        Node et = new Node("et", 33.783197, -118.109718, new ArrayList<Node>(),set);
        Node hsd = new Node("hsd", 33.782707, -118.109834, new ArrayList<Node>(),set);
        Node desn = new Node("desn", 33.782315, -118.109727, new ArrayList<Node>(),set);
        Node fnd = new Node("fnd", 33.781343, -118.110574, new ArrayList<Node>(),set);
        Node ten = new Node("Tennis Court", 33.784364, -118.111129, new ArrayList<Node>(),set);
        Node rug = new Node("Rugby Field", 33.784874, -118.112103, new ArrayList<Node>(),set);
        Node base = new Node("Baseball Field", 33.786143, -118.112494, new ArrayList<Node>(),set);
        Node umc = new Node("UMC", 33.787452, -118.112444, new ArrayList<Node>(),set);
        Node pyramid = new Node("Pyramid", 33.787498, -118.114410, new ArrayList<Node>(),set);
        Node bac = new Node("bac", 33.786426, -118.114875, new ArrayList<Node>(),set);
        Node pk1 = new Node("Parking Structure 1", 33.786230, -118.115756, new ArrayList<Node>(),set);
        Node track = new Node("Track and Field", 33.785235, -118.114807, new ArrayList<Node>(),set);
        Node shs = new Node("SHS", 33.782366, -118.117901, new ArrayList<Node>(),set);
        Node ms = new Node("MS", 33.783378, -118.109841, new ArrayList<Node>(),set);
        Node corp = new Node("corp", 33.783655, -118.109197, new ArrayList<Node>(),set);
        Node rec = new Node("rec", 33.785023, -118.109181, new ArrayList<Node>(),set);
        Node dc = new Node("DC", 33.788187, -118.113302, new ArrayList<Node>(),set);
        Node cpac = new Node("CPAC", 33.788116, -118.112138, new ArrayList<Node>(),set);
        Node fcs = new Node("fcs", 33.781730, -118.115503, new ArrayList<Node>(),set);
        Node sor = new Node("sor", 33.781767, -118.116915, new ArrayList<Node>(),set);
        Node kkjz = new Node("kkjz", 33.777561, -118.114135, new ArrayList<Node>(),set);
        Node acs = new Node("acs", 33.776767, -118.113819, new ArrayList<Node>(),set);
        Node nur = new Node("nur", 33.781781, -118.117846, new ArrayList<Node>(),set);
        Node park = new Node("parkside", 33.786900, -118.119132, new ArrayList<Node>(),set);
        Node jg = new Node("Japanese Garden", 33.785021, -118.119393, new ArrayList<Node>(),set);
        Node hill = new Node("Hillside", 33.782335, -118.118658, new ArrayList<Node>(),set);

        //Intermediate Nodes
        Node a1 = new Node("sw of LA1", 33.777617, -118.115114, new ArrayList<Node>(),set);
        Node a2 = new Node("se of LA1", 33.777447, -118.114372, new ArrayList<Node>(),set);
        Node a3 = new Node("ne of LA1", 33.777765, -118.114274, new ArrayList<Node>(),set);
        Node a4 = new Node("nw of LA1", 33.777950, -118.115012, new ArrayList<Node>(),set);
        Node a5 = new Node("nw of LA2", 33.778180, -118.114933, new ArrayList<Node>(),set);
        Node a6 = new Node("ne of LA2", 33.778013, -118.114193, new ArrayList<Node>(),set);
        Node a7 = new Node("w of LA3",  33.778378, -118.114874, new ArrayList<Node>(),set);
        Node a8 = new Node("nw of LA4", 33.778837, -118.114724, new ArrayList<Node>(),set);
        Node a9 = new Node("se of LA4", 33.778340, -118.114083, new ArrayList<Node>(),set);
        Node b1 = new Node("ne of LA4", 33.778670, -118.113984, new ArrayList<Node>(),set);
        Node b2 = new Node("w of LA2",  33.777943, -118.115013, new ArrayList<Node>(),set);
        Node b3 = new Node("e of LA5",  33.778906, -118.113902, new ArrayList<Node>(),set);
        Node b4 = new Node("e of PSY",  33.779410, -118.113759, new ArrayList<Node>(),set);
        Node b5 = new Node("Psy central", 33.779431, -118.114160, new ArrayList<Node>(),set);
        Node b6 = new Node("se of bookstore", 33.779757, -118.113748, new ArrayList<Node>(),set);
        Node b7 = new Node("south of Amazon", 33.779775, -118.114651, new ArrayList<Node>(),set);
        Node b8 = new Node("ne of bookstore", 33.780159, -118.113773, new ArrayList<Node>(),set);
        Node b9 = new Node("w quad", 33.778242, -118.113685, new ArrayList<Node>(),set);
        Node c1 = new Node("e quad", 33.778104, -118.113037, new ArrayList<Node>(),set);
        Node c2 = new Node("nw of school of art", 33.778559, -118.112845, new ArrayList<Node>(),set);
        Node c3 = new Node("ph right entrance", 33.778777, -118.112808, new ArrayList<Node>(),set);
        Node c4 = new Node("sw of ph", 33.778955, -118.113447, new ArrayList<Node>(),set);
        Node c5 = new Node("right of pp", 33.778706, -118.113535, new ArrayList<Node>(),set);
        Node c6 = new Node("top x", 33.778467, -118.113249, new ArrayList<Node>(),set);
        Node c7 = new Node("bottom x", 33.777673, -118.113507, new ArrayList<Node>(),set);
        Node c8 = new Node("bottom of grass", 33.777245, -118.113614, new ArrayList<Node>(),set);
        Node c9 = new Node("bottom right of grass", 33.777194, -118.113303, new ArrayList<Node>(),set);
        Node d1 = new Node("left of Macintosh building", 33.776885, -118.113424, new ArrayList<Node>(),set);
        Node d2 = new Node("bottom left of grass", 33.777341, -118.113985, new ArrayList<Node>(),set);
        Node d3 = new Node("top left of grass", 33.778967, -118.113454, new ArrayList<Node>(),set);
        Node d4 = new Node("north of academic service building", 33.776936, -118.113623, new ArrayList<Node>(),set);
        Node d5 = new Node("nw of FO4", 33.778397, -118.112206, new ArrayList<Node>(),set);
        Node d6 = new Node("sw of FO4", 33.778118, -118.112274, new ArrayList<Node>(),set);
        Node d7 = new Node("right inner corner of fa4", 33.778170, -118.112587, new ArrayList<Node>(),set);
        Node d8 = new Node("sw of FA3", 33.777556, -118.112774, new ArrayList<Node>(),set);
        Node d9 = new Node("sw of FA2", 33.777452, -118.112814, new ArrayList<Node>(),set);
        Node e1 = new Node("s of FA1", 33.777093, -118.112925, new ArrayList<Node>(),set);
        Node e2 = new Node("west of theater", 33.776661, -118.112385, new ArrayList<Node>(),set);
        Node e3 = new Node("e of FA1", 33.777196, -118.112004, new ArrayList<Node>(),set);
        Node e4 = new Node("nw of ph2", 33.779627, -118.113245, new ArrayList<Node>(),set);
        Node e5 = new Node("w of fo5", 33.779248, -118.113347, new ArrayList<Node>(),set);
        Node e6 = new Node("nw of hsci", 33.780154, -118.113082, new ArrayList<Node>(),set);
        Node e7 = new Node("nw of mlsc", 33.780568, -118.112938, new ArrayList<Node>(),set);
        Node e8 = new Node("USU e entrance", 33.781293, -118.112982, new ArrayList<Node>(),set);
        Node e9 = new Node("School Central", 33.782037, -118.112519, new ArrayList<Node>(),set);
        Node f1 = new Node("North of USU", 33.782032, -118.113422, new ArrayList<Node>(),set);
        Node f2 = new Node("SW of Kin Building", 33.782492, -118.113426, new ArrayList<Node>(),set);
        Node f3 = new Node("W of Kin Building", 33.782946, -118.113560, new ArrayList<Node>(),set);
        Node f4 = new Node("se of CBA", 33.783606, -118.115491, new ArrayList<Node>(),set);
        Node f5 = new Node("ne of USU", 33.781626, -118.112603, new ArrayList<Node>(),set);
        Node f6 = new Node("nw of usu", 33.782005, -118.114623, new ArrayList<Node>(),set);
        Node f7 = new Node("north of lib", 33.777431, -118.114376, new ArrayList<Node>(),set);

        Node g1 = new Node("west of SS/PA", 33.782016, -118.111542, new ArrayList<Node>(),set);
        Node g2 = new Node("west of outpost", 33.782377, -118.111273, new ArrayList<Node>(),set);
        Node g3 = new Node("w of vec", 33.782827, -118.111252, new ArrayList<Node>(),set);
        Node g4 = new Node("x in engineering quad", 33.783028, -118.111278, new ArrayList<Node>(),set);
        Node g5 = new Node("se of en2", 33.783073, -118.110527, new ArrayList<Node>(),set);
        Node g6 = new Node("square quad", 33.783193, -118.110419, new ArrayList<Node>(),set);
        Node g7 = new Node("nw of square quad", 33.783309, -118.110555, new ArrayList<Node>(),set);
        Node g8 = new Node("nw of en2", 33.783318, -118.111109, new ArrayList<Node>(),set);
        Node g9 = new Node("top left engineering area", 33.783359, -118.111432, new ArrayList<Node>(),set);

        Node h1 = new Node("se of ecs", 33.783132, -118.110085, new ArrayList<Node>(),set);
        Node h2 = new Node("e of vec", 33.782818, -118.109990, new ArrayList<Node>(),set);
        Node h3 = new Node("se of vec", 33.782471, -118.110124, new ArrayList<Node>(),set);
        Node h4 = new Node("sw of hsd", 33.782372, -118.109893, new ArrayList<Node>(),set);
        Node h5 = new Node("w of desn", 33.782029, -118.109984, new ArrayList<Node>(),set);
        Node h6 = new Node("se of sspa", 33.781802, -118.110118, new ArrayList<Node>(),set);
        Node h7 = new Node("sw of sspa", 33.781856, -118.111041, new ArrayList<Node>(),set);
        Node h8 = new Node("n of fnd", 33.781343, -118.110574, new ArrayList<Node>(),set);

        Node i1 = new Node("nw of en3", 33.783993, -118.111470, new ArrayList<Node>(),set);
        Node i2 = new Node("w of ten", 33.784503, -118.111526, new ArrayList<Node>(),set);
        Node i3 = new Node("se of baseball", 33.785581, -118.111520, new ArrayList<Node>(),set);
        Node i4 = new Node("ne of baseball", 33.786957, -118.111520, new ArrayList<Node>(),set);
        Node i5 = new Node("e of UMC", 33.787426, -118.111545, new ArrayList<Node>(),set);
        Node i6 = new Node("e of pyramid", 33.787462, -118.113362, new ArrayList<Node>(),set);
        Node i7 = new Node("se of pyramid", 33.786911, -118.113455, new ArrayList<Node>(),set);
        Node i8 = new Node("s of umc", 33.786967, -118.112469, new ArrayList<Node>(),set);
        Node i9 = new Node("s of pyramid", 33.786684, -118.114249, new ArrayList<Node>(),set);

        Node j1 = new Node("e of park1", 33.785818, -118.115161, new ArrayList<Node>(),set);
        Node j2 = new Node("se of park1", 33.784591, -118.115185, new ArrayList<Node>(),set);
        Node j3 = new Node("csulb pickup zone", 33.781724, -118.114598, new ArrayList<Node>(),set);
        Node j4 = new Node("e of nugget", 33.780342, -118.113786, new ArrayList<Node>(),set);
        Node j5 = new Node("sw of bh", 33.782108, -118.115892, new ArrayList<Node>(),set);
        Node j6 = new Node("nw of ms", 33.783490, -118.109959, new ArrayList<Node>(),set);
        Node j7 = new Node("west of awkward pool", 33.781263, -118.114599, new ArrayList<Node>(),set);
        Node j8 = new Node("ne of library", 33.777391, -118.114189, new ArrayList<Node>(),set);
        Node j9 = new Node("ne of la3", 33.778269, -118.114108, new ArrayList<Node>(),set);

        Node k1 = new Node("n of fcs", 33.782213, -118.116312, new ArrayList<Node>(),set);
        Node k2 = new Node("between ecs and vec", 33.783170, -118.110244, new ArrayList<Node>(),set);

        //Adjacencies

        acs.setAdjacent(d4);

        bac.setAdjacent(i9);        bac.setAdjacent(pk1);
        bac.setAdjacent(j1);        bac.setAdjacent(i7);

        base.setAdjacent(i3);        base.setAdjacent(i4);
        base.setAdjacent(rug);        base.setAdjacent(i7);
        base.setAdjacent(i9);        base.setAdjacent(track);

        bh.setAdjacent(f6);        bh.setAdjacent(HC);
        bh.setAdjacent(f5);        bh.setAdjacent(f4);
        bh.setAdjacent(j5);        bh.setAdjacent(fcs);

        bks.setAdjacent(e4);        bks.setAdjacent(b8);
        bks.setAdjacent(b6);

        cafe.setAdjacent(b8);        cafe.setAdjacent(usu);
        cafe.setAdjacent(j4);        cafe.setAdjacent(j7);

        cba.setAdjacent(f4);        cba.setAdjacent(j2);
        cba.setAdjacent(j5);        cba.setAdjacent(shs);

        corp.setAdjacent(ms);        corp.setAdjacent(j6);
        corp.setAdjacent(rec);

        cpac.setAdjacent(dc);        cpac.setAdjacent(umc);

        dc.setAdjacent(i6);        dc.setAdjacent(cpac);
        dc.setAdjacent(umc);

        desn.setAdjacent(h4);

        ecs.setAdjacent(g6);        ecs.setAdjacent(h1);
        ecs.setAdjacent(h2);        ecs.setAdjacent(ten);
        ecs.setAdjacent(en4);        ecs.setAdjacent(rec);
        ecs.setAdjacent(k2);

        en2.setAdjacent(g6);        en2.setAdjacent(g4);
        en2.setAdjacent(g8);

        en3.setAdjacent(g9);        en3.setAdjacent(g8);
        en3.setAdjacent(i1);        en3.setAdjacent(ten);

        en4.setAdjacent(g4);        en4.setAdjacent(ten);
        en4.setAdjacent(ecs);        en4.setAdjacent(g8);

        et.setAdjacent(h1);        et.setAdjacent(h2);
        et.setAdjacent(hsd);        et.setAdjacent(ms);


        fa1.setAdjacent(d9);        fa1.setAdjacent(c1);
        fa1.setAdjacent(fa2);        fa1.setAdjacent(c9);
        fa1.setAdjacent(ut);

        fa2.setAdjacent(d8);        fa2.setAdjacent(d9);
        fa2.setAdjacent(fa1);        fa2.setAdjacent(fa3);

        fa3.setAdjacent(d8);        fa3.setAdjacent(d7);
        fa3.setAdjacent(fa4);        fa3.setAdjacent(fo4);

        fa4.setAdjacent(fa3);        fa4.setAdjacent(d7);
        fa4.setAdjacent(d8);

        fcs.setAdjacent(j3);        fcs.setAdjacent(sor);
        fcs.setAdjacent(j5);        fcs.setAdjacent(bh);

        fnd.setAdjacent(h8);

        fo2.setAdjacent(lh);        fo2.setAdjacent(la5);
        fo2.setAdjacent(ph1);        fo2.setAdjacent(fo3);
        fo2.setAdjacent(b3);        fo2.setAdjacent(c6);
        fo2.setAdjacent(b1);

        fo3.setAdjacent(la5);        fo3.setAdjacent(fo2);
        fo3.setAdjacent(c6);        fo3.setAdjacent(b1);
        fo3.setAdjacent(b4);        fo3.setAdjacent(e4);
        fo3.setAdjacent(e5);

        fo4.setAdjacent(fa3);        fo4.setAdjacent(d7);
        fo4.setAdjacent(ph1);

        fo5.setAdjacent(e5);        fo5.setAdjacent(ph1);

        fnd.setAdjacent(mlsc);

        HC.setAdjacent(bh);        HC.setAdjacent(f4);
        HC.setAdjacent(kin);

        hill.setAdjacent(jg);        hill.setAdjacent(shs);
        hill.setAdjacent(nur);

        hsd.setAdjacent(h2);        hsd.setAdjacent(et);
        hsd.setAdjacent(desn);

        hsci.setAdjacent(e6);        hsci.setAdjacent(e4);
        hsci.setAdjacent(mlsc);        hsci.setAdjacent(ph2);

        hsd.setAdjacent(h4);        hsd.setAdjacent(h2);

        jg.setAdjacent(park);        jg.setAdjacent(hill);

        kin.setAdjacent(g2);        kin.setAdjacent(g3);
        kin.setAdjacent(g4);        kin.setAdjacent(g9);
        kin.setAdjacent(HC);

        kkjz.setAdjacent(a3);        kkjz.setAdjacent(a2);
        kkjz.setAdjacent(d2);

        la1.setAdjacent(a1);        la1.setAdjacent(a2);
        la1.setAdjacent(a3);        la1.setAdjacent(a4);

        la2.setAdjacent(a3);        la2.setAdjacent(a4);
        la2.setAdjacent(a5);        la2.setAdjacent(a6);

        la3.setAdjacent(a5);        la3.setAdjacent(a6);
        la3.setAdjacent(a7);        la3.setAdjacent(a9);

        la4.setAdjacent(a7);        la4.setAdjacent(a8);
        la4.setAdjacent(a9);        la4.setAdjacent(b1);
        la4.setAdjacent(la5);

        la5.setAdjacent(a8);        la5.setAdjacent(b1);
        la5.setAdjacent(fo3);        la5.setAdjacent(fo2);
        la5.setAdjacent(b3);        la5.setAdjacent(la4);

        lab.setAdjacent(mhb);        lab.setAdjacent(ut);
        lab.setAdjacent(c9);

        lh.setAdjacent(a9);        lh.setAdjacent(fo2);
        lh.setAdjacent(b9);        lh.setAdjacent(a6);

        lib.setAdjacent(f7);        mlsc.setAdjacent(e6);
        mlsc.setAdjacent(e7);        mlsc.setAdjacent(hsci);

        mhb.setAdjacent(d1);        mhb.setAdjacent(lab);
        mhb.setAdjacent(c9);

        ms.setAdjacent(h1);        ms.setAdjacent(j6);
        ms.setAdjacent(corp);        ms.setAdjacent(et);

        nur.setAdjacent(sor);        nur.setAdjacent(shs);
        nur.setAdjacent(hill);

        outpost.setAdjacent(g2);        outpost.setAdjacent(vec);
        outpost.setAdjacent(sspa);        outpost.setAdjacent(h3);

        park.setAdjacent(pyramid);        park.setAdjacent(jg);

        pk1.setAdjacent(bac);        pk1.setAdjacent(j1);

        ph1.setAdjacent(e5);        ph1.setAdjacent(d3);
        ph1.setAdjacent(c3);        ph1.setAdjacent(c4);
        ph1.setAdjacent(fo5);        ph1.setAdjacent(fo4);

        ph2.setAdjacent(fo5);

        psy.setAdjacent(b5);

        pk1.setAdjacent(j2);        pk1.setAdjacent(j1);
        pk1.setAdjacent(bac);

        pyramid.setAdjacent(i6);        pyramid.setAdjacent(i9);
        pyramid.setAdjacent(park);        pyramid.setAdjacent(i7);

        rec.setAdjacent(corp);        rec.setAdjacent(ten);
        rec.setAdjacent(ecs);

        rug.setAdjacent(i2);        rug.setAdjacent(base);
        rug.setAdjacent(i8);        rug.setAdjacent(track);

        shs.setAdjacent(j5);        shs.setAdjacent(cba);
        shs.setAdjacent(sor);        shs.setAdjacent(nur);
        shs.setAdjacent(hill);

        sor.setAdjacent(fcs);        sor.setAdjacent(nur);
        sor.setAdjacent(shs);

        sspa.setAdjacent(g1);        sspa.setAdjacent(outpost);
        sspa.setAdjacent(h7);        sspa.setAdjacent(h4);
        sspa.setAdjacent(h3);        sspa.setAdjacent(h5);
        sspa.setAdjacent(desn);

        sor.setAdjacent(fcs);        sor.setAdjacent(j5);

        ta.setAdjacent(ut);

        ten.setAdjacent(i1);        ten.setAdjacent(i2);
        ten.setAdjacent(rec);        ten.setAdjacent(ecs);
        ten.setAdjacent(en4);        ten.setAdjacent(en3);

        track.setAdjacent(j1);        track.setAdjacent(rug);
        track.setAdjacent(base);        track.setAdjacent(j2);

        umc.setAdjacent(i5);        umc.setAdjacent(i6);
        umc.setAdjacent(i8);        umc.setAdjacent(dc);
        umc.setAdjacent(cpac);

        usu.setAdjacent(e6);        usu.setAdjacent(b8);
        usu.setAdjacent(e8);        usu.setAdjacent(e9);
        usu.setAdjacent(cafe);        usu.setAdjacent(f5);
        usu.setAdjacent(f6);        usu.setAdjacent(j3);
        usu.setAdjacent(j4);        usu.setAdjacent(h7);

        cp.setAdjacent(e6);        cp.setAdjacent(b8);
        cp.setAdjacent(e8);        cp.setAdjacent(e9);
        cp.setAdjacent(cafe);        cp.setAdjacent(f5);
        cp.setAdjacent(f6);        cp.setAdjacent(j3);
        cp.setAdjacent(j4);        cp.setAdjacent(h7);

        e6.setAdjacent(cp);        b8.setAdjacent(cp);
        e8.setAdjacent(cp);        e9.setAdjacent(cp);
        cafe.setAdjacent(cp);        f5.setAdjacent(cp);
        f6.setAdjacent(cp);        j3.setAdjacent(cp);
        j4.setAdjacent(cp);        h7.setAdjacent(cp);

        ut.setAdjacent(lab);        ut.setAdjacent(utc);
        ut.setAdjacent(fa1);        ut.setAdjacent(ta);

        utc.setAdjacent(ut);

        vec.setAdjacent(g6);        vec.setAdjacent(g3);
        vec.setAdjacent(g5);        vec.setAdjacent(h2);
        vec.setAdjacent(h3);        vec.setAdjacent(outpost);
        vec.setAdjacent(k2);


        a1.setAdjacent(a2);        a1.setAdjacent(a4);
        a1.setAdjacent(lib);

        a2.setAdjacent(f7);        a2.setAdjacent(la1);
        a2.setAdjacent(a1);        a2.setAdjacent(a3);
        a2.setAdjacent(d2);        a2.setAdjacent(kkjz);

        a3.setAdjacent(a6);        a3.setAdjacent(a4);
        a3.setAdjacent(a2);        a3.setAdjacent(la1);
        a3.setAdjacent(la2);        a3.setAdjacent(kkjz);

        a4.setAdjacent(la1);        a4.setAdjacent(la2);
        a4.setAdjacent(a1);        a4.setAdjacent(a5);
        a4.setAdjacent(a3);

        a5.setAdjacent(a4);        a5.setAdjacent(la3);
        a5.setAdjacent(la2);        a5.setAdjacent(a7);
        a5.setAdjacent(a6);

        a6.setAdjacent(a3);        a6.setAdjacent(a5);
        a6.setAdjacent(a9);        a6.setAdjacent(lh);
        a6.setAdjacent(la2);        a6.setAdjacent(la3);
        a6.setAdjacent(j9);

        a7.setAdjacent(la3);        a7.setAdjacent(la4);
        a7.setAdjacent(a8);        a7.setAdjacent(a5);

        a8.setAdjacent(a7);        a8.setAdjacent(la5);
        a8.setAdjacent(la4);        a8.setAdjacent(b1);

        a9.setAdjacent(b9);        a9.setAdjacent(a6);
        a9.setAdjacent(la4);        a9.setAdjacent(la3);
        a9.setAdjacent(lh);        a9.setAdjacent(fo2);
        a9.setAdjacent(b1);        a9.setAdjacent(a7);
        a9.setAdjacent(j9);

        b1.setAdjacent(a9);        b1.setAdjacent(b3);
        b1.setAdjacent(la5);        b1.setAdjacent(a8);
        b1.setAdjacent(fo3);        b1.setAdjacent(fo2);
        b1.setAdjacent(la4);

//      b2

        b3.setAdjacent(b1);        b3.setAdjacent(b4);
        b3.setAdjacent(la5);        b3.setAdjacent(fo3);

        b4.setAdjacent(b3);        b4.setAdjacent(b5);
        b4.setAdjacent(fo3);        b4.setAdjacent(b6);

        b6.setAdjacent(b7);        b6.setAdjacent(b4);
        b6.setAdjacent(b8);        b6.setAdjacent(hsci);
        b6.setAdjacent(mlsc);        b6.setAdjacent(ph1);
        b6.setAdjacent(fo5);        b6.setAdjacent(bks);

        b7.setAdjacent(b6);

        b8.setAdjacent(b6);        b8.setAdjacent(hsci);
        b8.setAdjacent(mlsc);        b8.setAdjacent(ph1);
        b8.setAdjacent(fo5);        b8.setAdjacent(usu);
        b8.setAdjacent(cafe);        b8.setAdjacent(bks);
        b8.setAdjacent(j4);

        b9.setAdjacent(a9);        b9.setAdjacent(c1);
        b9.setAdjacent(c6);        b9.setAdjacent(c7);
        b9.setAdjacent(fo2);        b9.setAdjacent(lh);
        b9.setAdjacent(d2);        b9.setAdjacent(d3);

        c1.setAdjacent(c9);        c1.setAdjacent(b9);
        c1.setAdjacent(c7);        c1.setAdjacent(c6);
        c1.setAdjacent(c3);        c1.setAdjacent(d8);
        c1.setAdjacent(d9);        c1.setAdjacent(e1);
        c1.setAdjacent(fa1);        c1.setAdjacent(fa2);
        c1.setAdjacent(fa3);

//      c2

        c3.setAdjacent(c6);        c3.setAdjacent(ph1);
        c3.setAdjacent(pp);        c3.setAdjacent(d3);
        c3.setAdjacent(d5);

//      c4
//      c5

        c6.setAdjacent(c1);        c6.setAdjacent(b9);
        c6.setAdjacent(d3);        c6.setAdjacent(c3);
        c6.setAdjacent(fo2);        c6.setAdjacent(fo3);
        c6.setAdjacent(pp);

        c7.setAdjacent(d2);        c7.setAdjacent(c8);
        c7.setAdjacent(c9);        c7.setAdjacent(c1);
        c7.setAdjacent(b9);        c7.setAdjacent(lh);
        c7.setAdjacent(d8);        c7.setAdjacent(d9);

        c8.setAdjacent(c7);        c8.setAdjacent(c9);
        c8.setAdjacent(d1);        c8.setAdjacent(d2);
        c8.setAdjacent(d4);

        c9.setAdjacent(c8);        c9.setAdjacent(c7);
        c9.setAdjacent(d1);        c9.setAdjacent(d4);
        c9.setAdjacent(c1);        c9.setAdjacent(fa1);
        c9.setAdjacent(mhb);        c9.setAdjacent(lab);

        d1.setAdjacent(mhb);

        d2.setAdjacent(b9);        d2.setAdjacent(a2);
        d2.setAdjacent(c7);        d2.setAdjacent(c8);
        d2.setAdjacent(lh);        d2.setAdjacent(fa3);
        d2.setAdjacent(kkjz);        d2.setAdjacent(j8);

        d3.setAdjacent(ph1);        d3.setAdjacent(b9);
        d3.setAdjacent(c3);        d3.setAdjacent(c6);
        d3.setAdjacent(e5);

        d4.setAdjacent(d1);        d4.setAdjacent(acs);


        d5.setAdjacent(c3);        d5.setAdjacent(fa4);
        d5.setAdjacent(fo4);        d5.setAdjacent(d6);

        d6.setAdjacent(d5);        d6.setAdjacent(fo4);
        d6.setAdjacent(fa4);        d6.setAdjacent(d7);

        d7.setAdjacent(fa4);        d7.setAdjacent(d6);
        d7.setAdjacent(d8);        d7.setAdjacent(fa3);

        d8.setAdjacent(d7);        d8.setAdjacent(fa3);
        d8.setAdjacent(fa2);        d8.setAdjacent(c7);
        d8.setAdjacent(c1);        d8.setAdjacent(d9);
        d8.setAdjacent(fa3);

        d9.setAdjacent(d8);        d9.setAdjacent(fa2);
        d9.setAdjacent(fa1);        d9.setAdjacent(c7);
        d9.setAdjacent(e1);        d9.setAdjacent(e3);

        e1.setAdjacent(d9);        e1.setAdjacent(fa1);
        e1.setAdjacent(c9);        e1.setAdjacent(d1);
        e1.setAdjacent(mhb);        e1.setAdjacent(e2);

        e2.setAdjacent(e1);        e2.setAdjacent(utc);
        e2.setAdjacent(ut);        e2.setAdjacent(e3);

        e3.setAdjacent(e2);        e3.setAdjacent(utc);
        e3.setAdjacent(ut);        e3.setAdjacent(fa1);
        e3.setAdjacent(fa2);        e3.setAdjacent(d9);

        e4.setAdjacent(e5);        e4.setAdjacent(fo3);
        e4.setAdjacent(hsci);        e4.setAdjacent(ph2);
        e4.setAdjacent(b4);        e4.setAdjacent(b6);
        e4.setAdjacent(b8);        e4.setAdjacent(bks);
        e4.setAdjacent(e6);

        e5.setAdjacent(e4);        e5.setAdjacent(fo5);
        e5.setAdjacent(ph1);        e5.setAdjacent(d3);

        e6.setAdjacent(hsci);        e6.setAdjacent(mlsc);
        e6.setAdjacent(e4);        e6.setAdjacent(usu);
        e6.setAdjacent(b8);        e6.setAdjacent(e7);
        e6.setAdjacent(e9);

        e7.setAdjacent(e6);        e7.setAdjacent(mlsc);
        e7.setAdjacent(e9);        e7.setAdjacent(e8);

        e8.setAdjacent(e7);        e8.setAdjacent(usu);
        e8.setAdjacent(e9);        e8.setAdjacent(f5);
        e8.setAdjacent(h7);

        e9.setAdjacent(e7);        e9.setAdjacent(e8);
        e9.setAdjacent(f1);        e9.setAdjacent(f5);
        e9.setAdjacent(kin);        e9.setAdjacent(g1);
        e9.setAdjacent(h7);

        f1.setAdjacent(e9);        f1.setAdjacent(usu);
        f1.setAdjacent(f2);        f1.setAdjacent(kin);
        f1.setAdjacent(f5);

        f2.setAdjacent(f1);        f2.setAdjacent(kin);
        f2.setAdjacent(f3);

        f3.setAdjacent(f2);        f3.setAdjacent(kin);
        f3.setAdjacent(HC);        f3.setAdjacent(f4);

        f4.setAdjacent(f3);        f4.setAdjacent(cba);
        f4.setAdjacent(HC);        f4.setAdjacent(bh);

        f5.setAdjacent(e9);        f5.setAdjacent(f1);
        f5.setAdjacent(e8);        f5.setAdjacent(f6);
        f5.setAdjacent(bh);

        f6.setAdjacent(bh);        f6.setAdjacent(f5);
        f6.setAdjacent(j3);

        f7.setAdjacent(lib);        f7.setAdjacent(a2);
        f7.setAdjacent(j8);

        g1.setAdjacent(sspa);        g1.setAdjacent(e9);
        g1.setAdjacent(g2);        g2.setAdjacent(g1);
        g2.setAdjacent(outpost);        g2.setAdjacent(g3);

        g3.setAdjacent(g2);        g3.setAdjacent(vec);
        g3.setAdjacent(kin);        g3.setAdjacent(g4);

        g4.setAdjacent(g3);        g4.setAdjacent(en2);
        g4.setAdjacent(kin);        g4.setAdjacent(g5);
        g4.setAdjacent(g7);        g4.setAdjacent(g9);

        g5.setAdjacent(g6);        g5.setAdjacent(vec);

        g6.setAdjacent(g5);        g6.setAdjacent(vec);
        g6.setAdjacent(en2);        g6.setAdjacent(ecs);
        g6.setAdjacent(g7);        g6.setAdjacent(h1);

        g7.setAdjacent(g6);        g7.setAdjacent(en4);
        g7.setAdjacent(g8);        g7.setAdjacent(g4);

        g8.setAdjacent(g7);        g8.setAdjacent(en3);
        g8.setAdjacent(en2);        g8.setAdjacent(g9);
        g8.setAdjacent(en4);

        g9.setAdjacent(g8);        g9.setAdjacent(g4);
        g9.setAdjacent(en3);        g9.setAdjacent(i1);
        g9.setAdjacent(kin);

        h1.setAdjacent(g6);        h1.setAdjacent(et);
        h1.setAdjacent(h2);        h1.setAdjacent(ecs);
        h1.setAdjacent(ms);

        h2.setAdjacent(h1);        h2.setAdjacent(hsd);
        h2.setAdjacent(h3);        h2.setAdjacent(ecs);

        h3.setAdjacent(h2);        h3.setAdjacent(h2);
        h3.setAdjacent(outpost);        h3.setAdjacent(h4);

        h4.setAdjacent(h3);        h4.setAdjacent(desn);
        h4.setAdjacent(outpost);        h4.setAdjacent(hsd);
        h4.setAdjacent(h5);

        h5.setAdjacent(h4);        h5.setAdjacent(sspa);
        h5.setAdjacent(h6);        h5.setAdjacent(outpost);

        h6.setAdjacent(h5);        h6.setAdjacent(sspa);
        h6.setAdjacent(desn);        h6.setAdjacent(h7);

        h7.setAdjacent(h6);        h7.setAdjacent(sspa);
        h7.setAdjacent(g1);        h7.setAdjacent(e8);
        h7.setAdjacent(e9);


        i1.setAdjacent(g9);        i1.setAdjacent(ten);
        i1.setAdjacent(i2);

        i2.setAdjacent(i1);        i2.setAdjacent(rug);
        i2.setAdjacent(ten);        i2.setAdjacent(i3);

        i3.setAdjacent(i2);        i3.setAdjacent(base);
        i3.setAdjacent(i4);

        i4.setAdjacent(i3);        i4.setAdjacent(base);
        i4.setAdjacent(i5);        i4.setAdjacent(i8);

        i5.setAdjacent(i4);        i5.setAdjacent(umc);

        i6.setAdjacent(umc);        i6.setAdjacent(pyramid);
        i6.setAdjacent(i7);        i6.setAdjacent(dc);

        i7.setAdjacent(i6);        i7.setAdjacent(base);
        i7.setAdjacent(i8);        i7.setAdjacent(pyramid);
        i7.setAdjacent(bac);

        i8.setAdjacent(i7);        i8.setAdjacent(umc);
        i8.setAdjacent(rug);        i8.setAdjacent(i4);
        i8.setAdjacent(i9);

        i9.setAdjacent(i8);        i9.setAdjacent(pyramid);
        i9.setAdjacent(base);        i9.setAdjacent(bac);

        j1.setAdjacent(bac);        j1.setAdjacent(track);
        j1.setAdjacent(j2);

        j2.setAdjacent(j1);        j2.setAdjacent(track);
        j2.setAdjacent(cba);

        j3.setAdjacent(usu);        j3.setAdjacent(f6);
        j3.setAdjacent(fcs);        j3.setAdjacent(j7);

        j4.setAdjacent(cafe);        j4.setAdjacent(usu);
        j4.setAdjacent(b8);

        j5.setAdjacent(bh);        j5.setAdjacent(f6);
        j5.setAdjacent(cba);        j5.setAdjacent(shs);
        j5.setAdjacent(fcs);        j5.setAdjacent(sor);

        j6.setAdjacent(ms);

        j7.setAdjacent(cafe);        j7.setAdjacent(fcs);
        j7.setAdjacent(j3);

        j8.setAdjacent(f7);        j8.setAdjacent(kkjz);
        j8.setAdjacent(d2);

        j9.setAdjacent(a9);        j9.setAdjacent(la3);
        j9.setAdjacent(a6);

        k2.setAdjacent(vec);        k2.setAdjacent(ecs);

        //UPPER CAMPUS
        //USU
        mapPlaces.put("USU", usu);
        mapPlaces.put("University Student Union", usu);
        mapPlaces.put("Student Union", usu);
        //CP Central Plant (Lego)
        mapPlaces.put("CP", cp);
        mapPlaces.put("Central Plant", cp);
        mapPlaces.put("Huge Stairs", cp);
        //CAFE, etc
        mapPlaces.put("CAFE", cafe);
        mapPlaces.put("Nugget", cafe);
        mapPlaces.put("beachwalk", cafe);
        //BKS Book Store
        mapPlaces.put("BKS", bks);
        mapPlaces.put("Book Store", bks);
        mapPlaces.put("University Bookstore", bks);
        mapPlaces.put("Bookstore", bks);
        //MLSC Molecular Science
        mapPlaces.put("MLSC", mlsc);
        mapPlaces.put("Molecular Science", mlsc);
        mapPlaces.put("Molecular Science Building", mlsc);
        //HSCI Hall of Science
        mapPlaces.put("HSCI", hsci);
        mapPlaces.put("Hall of Science", hsci);
        //MIC Microbiology
        mapPlaces.put("MIC", mic);
        mapPlaces.put("Microbiology", mic);
        mapPlaces.put("Micro Biology", mic);
        //PH1
        mapPlaces.put("PH1", ph1);
        mapPlaces.put("Peterson Hall 1", ph1);
        mapPlaces.put("Peterson Hall one", ph1);
        //PH2 Peterson Hall 2
        mapPlaces.put("PH2", ph2);
        mapPlaces.put("Peterson Hall 2", ph2);
        mapPlaces.put("Peterson Hall two", ph2);
        //FA 1
        mapPlaces.put("FA1",fa1);
        mapPlaces.put("Fine Arts 1", fa1);
        //FA 2
        mapPlaces.put("FA2",fa2);
        mapPlaces.put("Fine Arts 2", fa2);
        //FA 3
        mapPlaces.put("FA3", fa3);
        mapPlaces.put("Fine Arts 3", fa3);
        //FA4 fine arts 4
        mapPlaces.put("FA4",fa4);
        mapPlaces.put("Fine Arts 4", fa4);
        //UT University Theatre
        mapPlaces.put("UT", ut);
        //UTC University Telecommunications
        mapPlaces.put("UTC", utc);
        mapPlaces.put("University Telecommunications", utc);
        //TA Theatre Arts Building
        mapPlaces.put("TA", ta);
        //MHB Macintosh Building, The Toaster?
        mapPlaces.put("MHB", mhb);
        mapPlaces.put("Macintosh Building", mhb);
        mapPlaces.put("Macintosh", mhb);
        mapPlaces.put("Toaster", mhb);
        mapPlaces.put("The Toaster", mhb);
        //AS - Academic Services
        mapPlaces.put("Academic Services", as);
        //LIB Library
        mapPlaces.put("LIB", lib);
        mapPlaces.put("Library", lib);
        //LA1
        mapPlaces.put("LA1", la1);
        mapPlaces.put("Liberal Arts 1", la1);
        //LA2
        mapPlaces.put("LA2",la2);
        mapPlaces.put("Liberal Arts 2", la2);
        //LA3
        mapPlaces.put("LA3",la3);
        mapPlaces.put("Liberal Arts 3", la3);
        //LA4
        mapPlaces.put("LA4",la4);
        mapPlaces.put("Liberal Arts 4", la4);
        //LA5
        mapPlaces.put("LA5",la5);
        mapPlaces.put("Liberal Arts 5", la5);
        //LH Lecture Hall
        mapPlaces.put("LH", lh);
        mapPlaces.put("Lecture Hall", lh);
        //CLA
        mapPlaces.put("CLA", cla);
        mapPlaces.put("College of Liberal Arts", cla);
        //PSY Psychology
        mapPlaces.put("PSY", psy);
        mapPlaces.put("Psychology", psy);
        //ED2 the outer edge of the campus
        mapPlaces.put("ED2", ed2);
        mapPlaces.put("Education 2", ed2);
        //EED
        mapPlaces.put("ED1", eed);
        mapPlaces.put("Education 1", eed);
        //MMC Multimedia Center
        mapPlaces.put("MMC", mmc);
        mapPlaces.put("Multimedia Center", mmc);
        //ANNEX (???)
        mapPlaces.put("Art Annex", annex);
        //LAB Language Arts Building
        mapPlaces.put("LAB",lab);
        mapPlaces.put("Language Arts Building",lab);
        //FO2
        mapPlaces.put("FO2", fo2);
        mapPlaces.put("Faculty Office 2", fo2);
        //FO3
        mapPlaces.put("FO3", fo3);
        mapPlaces.put("Faculty Office 3", fo3);
        //FO4 Faculty Office 4
        mapPlaces.put("FO4", fo4);
        mapPlaces.put("Faculty Office 4", fo4);
        //FO5 Faculty Office 5
        mapPlaces.put("FO5", fo5);
        mapPlaces.put("Faculty Office 5", fo5);
        //LOWER CAMPUS
        //ECS
        mapPlaces.put("ECS", ecs);
        mapPlaces.put("Computer Science", ecs);
        mapPlaces.put("CECS", ecs);
        mapPlaces.put("Computer Engineering", ecs);
        mapPlaces.put("Computer Engineering Computer Science", ecs);
        //EN2
        mapPlaces.put("EN2", en2);
        mapPlaces.put("Engineering 2", en2);
        //EN3
        mapPlaces.put("EN3", en3);
        mapPlaces.put("Engineering 3", en3);
        //EN4
        mapPlaces.put("EN4", en4);
        mapPlaces.put("Engineering 4", en4);
        //VEC
        mapPlaces.put("VEC", vec);
        mapPlaces.put("Vivian Engineering Center", vec);
        //Outpost
        mapPlaces.put("Outpost", outpost);
        mapPlaces.put("The Outpost", outpost);
        mapPlaces.put("OP", outpost);
        //SSPA
        mapPlaces.put("SPA", sspa);
        mapPlaces.put("SSPA", sspa);
        mapPlaces.put("SS/PA", sspa);
        mapPlaces.put("School of Social Work", sspa);
        mapPlaces.put("School of Social Work/Public Affairs", sspa);
        //Horn Center
        mapPlaces.put("HC", HC);
        mapPlaces.put("Horn Center", HC);
        mapPlaces.put("Horn", HC);
        mapPlaces.put("Computer Lab", HC);
        mapPlaces.put("Open Access Computer Lab", HC);
        //UAM University Art Museum
        mapPlaces.put("UAM",UAM);
        mapPlaces.put("University Art Museum",UAM);
        mapPlaces.put("Museum",UAM);
        //KIN
        mapPlaces.put("KIN", kin);
        mapPlaces.put("Kinesiology", kin);
        //SRWC
        mapPlaces.put("SRWC", srwc);
        mapPlaces.put("Student Recreation and Wellness Center", srwc);
        mapPlaces.put("Recreation and Wellness Center", srwc);
        //HHS1 (Health and Human Services)
        mapPlaces.put("HHS1", hhs1);
        mapPlaces.put("Health and Human Services 1", hhs1);
        mapPlaces.put("Health Human Services 1", hhs1);
        mapPlaces.put("Human Services 1", hhs1);
        //HHS2
        mapPlaces.put("HHS2", hhs2);
        mapPlaces.put("Health and Human Services 2", hhs2);
        mapPlaces.put("Health Human Services 2", hhs2);
        mapPlaces.put("Human Services 2", hhs2);
        //Health Human Services (in general)
        mapPlaces.put("Health and Human Services", hhsGnrl);
        mapPlaces.put("HHS",hhsGnrl);
        mapPlaces.put("Human Services",hhsGnrl);
        mapPlaces.put("Health Human Services", hhsGnrl);
        //BH
        mapPlaces.put("Brotman Hall", bh);
        mapPlaces.put("BH", bh);

//      default at ECS
        currentLoc = new Node("User",33.783529, -118.110287,new ArrayList<Node>(),new LocationSet());

        ArrayList<Node> z = set.getList();
        for(Node x : z) {
            currentLoc.setAdjacent(x);    // user's location is adjacent to everything
//            x.setAdjacent(currentLoc);  // but not everything is adjacent to the user's location
        }

    }
}
