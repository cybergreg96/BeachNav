package com.project.beachnav.beachnav;

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Austin on 10/25/2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLngBounds CSULB_Bounds = new LatLngBounds(
            new LatLng(33.765, -118.124241), new LatLng(33.785, -118.108));

    private LocationManager locationManager;
    private GoogleApiClient googleApiClient;

    /*
    * 10/24/2017 - Carl Costa
    * The plan is to change this to Map<String, Node>
    *     where the node
    */
    private Map<String, Node> mapPlaces = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps_searchbox);

//        setContentView(R.layout.activity_maps_searchbar);
//        Toolbar menu_toolbar = (Toolbar) findViewById(R.id.menu_toolbar);
//        setSupportActionBar(menu_toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.BN_map);
        mapFragment.getMapAsync(this);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.options_menu, menu);
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

   /**
    * Will find a location that matches the search item as best as possible.
    * (Mapped to search dialog the same way findLocation was to that button)
    * ..we need to be able to handle anything that the search dialog can give
    *  -> auto-suggestions from a database?
    */
    public void onSearch(View v) {
//  searches and modifies the mapFragment such that it shows the location of the string in question on the map.
        EditText location_tf = (EditText) findViewById(R.id.editText);
        String location = location_tf.getText().toString();

        List<Address> addressList = null;

        if (TextUtils.isEmpty(location)) { //handles empty string in textbox
            location_tf.setError("Can't search nothing. Try searching a location.");
            return;
        }
//        else if (the string does not match any location in the database)
//        return error: "This location doesn't seem to be on campus. Let's try something else."
        else{
            Node address = mapPlaces.get(location);
            LatLng latLng = new LatLng(address.getY(),address.getX());
            System.out.println("Latitude: "+address.getX()+" Longitude: "+address.getY());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
//        else { //for anything else
//            Geocoder geocoder = new Geocoder(this);
//            try {
//                addressList = geocoder.getFromLocationName(location, 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Address address = addressList.get(0);
//            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        }
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
        LatLng CSULB = new LatLng(33.782, -118.116);
        GroundOverlayOptions csulbMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.csulb_map2016))
                .position(CSULB, 1570f, 1520f);
        mMap.addGroundOverlay(csulbMap);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CSULB));
        initializePathOverlay();

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////      TODO: Consider calling
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    /**
     * Instantiates the map
     * this method hardcodes all the keys for all the places in CSULB
     */
    protected void initializePathOverlay() {
        Node ecs = new Node("ECS",33.783529, -118.110287, new ArrayList<Node>());
        Node en2 = new Node("EN2",33.783215, -118.110925, new ArrayList<Node>());
        //EN3
        Node en3 = new Node("EN3", 33.783694, -118.111157, new ArrayList<Node>());
        //new LatLng(33.783681, -118.110674
        Node en4 = new Node("EN4",33.783681, -118.110674, new ArrayList<Node>());
        // VEC
        Node vec = new Node("VEC",33.782818, -118.110636, new ArrayList<Node>());
        //Outpost
        Node outpost = new Node("Outpost",33.782340, -118.110410, new ArrayList<Node>());
        //SSPA
        Node sspa = new Node ("SSPA", 33.782017, -118.110383, new ArrayList<Node>());
        //HC
        Node HC = new Node("Horn Center",33.783444, -118.113983, new ArrayList<Node>());
        //UAM
        Node UAM = new Node("Museum", 33.783425, -118.114629, new ArrayList<Node>());
        //KIN
        Node kin = new Node("Kinesiology", 33.782898, -118.112586, new ArrayList<Node>());
        //srwc
        Node srwc = new Node("srwc",33.785038, -118.109484,new ArrayList<Node>());
        //hhs1
        Node hhs1 = new Node("hhs1",33.782388, -118.112801,new ArrayList<Node>());
        //hhs2
        Node hhs2 = new Node("hhs2", 33.782384, -118.112125, new ArrayList<Node>());
        //Health and Human Services (GENERAL LOCATION)
        Node hhsGnrl = new Node("hhs", 33.782326, -118.112490, new ArrayList<Node>());
        //Brotman Hall
        Node bh = new Node("Brotman Hall",33.782659, -118.115339, new ArrayList<Node>());
        //University Student Union
        Node usu = new Node("Student Union",33.781281, -118.113450, new ArrayList<Node>());
        //Lego/Central Plant
        Node cp = new Node("Central Plant",33.781316, -118.112386, new ArrayList<Node>());
        //cafe
        Node cafe = new Node("Cafe",33.780574, -118.114071, new ArrayList<Node>());
        //Bookstore
        Node bks = new Node("Bookstore",33.779974, -118.114158,new ArrayList<Node>());
        //Molecular Science
        Node mlsc = new Node("Molecular Science",33.780301, -118.112488,new ArrayList<Node>());
        //Hall of Science
        Node hsci = new Node("Hall of Science",33.779830, -118.112639,new ArrayList<Node>());
        //Microbiology
        Node mic = new Node("Microbiology",33.779429, -118.111720, new ArrayList<Node>());
        //Peterson Hall 1
        Node ph1 = new Node("Peterson Hall 1",33.778898, -118.112505, new ArrayList<Node>());
        //Peterson Hall 2
        Node ph2 = new Node("Peterson Hall 2",33.779272, -118.112482, new ArrayList<Node>());
        //FA1
        Node fa1 = new Node("FA1",33.777211, -118.112557, new ArrayList<Node>());
        //FA2
        Node fa2 = new Node("FA2",33.777474, -118.112361, new ArrayList<Node>());
        //FA3
        Node fa3 = new Node("FA3",33.777935, -118.112311, new ArrayList<Node>());
        //FA4
        Node fa4 = new Node("FA4",33.778336, -118.112741, new ArrayList<Node>());
        //UT
        Node ut = new Node("University Theatre",33.776733, -118.112113,new ArrayList<Node>());
        Node utc = new Node("UTC", 33.776735, -118.111652,new ArrayList<Node>());
        Node ta = new Node("TA",33.776510, -118.112639,new ArrayList<Node>());
        Node mhb = new Node("Macintosh Building",33.776882, -118.113202,new ArrayList<Node>());
        Node as = new Node("as", 33.777009, -118.114096, new ArrayList<Node>());
        Node lib = new Node("Library", 33.777207, -118.114842, new ArrayList<Node>());
        Node la1 = new Node("Liberal Arts 1", 33.777664, -118.114713, new ArrayList<Node>());
        Node la2 = new Node("Liberal Arts 2",33.777987, -118.114547,new ArrayList<Node>());
        Node la3 = new Node("Liberal Arts 3",33.778292, -118.114440,new ArrayList<Node>());
        Node la4 = new Node("Liberal Arts 4",33.778566, -118.114338,new ArrayList<Node>());
        Node la5 = new Node("Liberal Arts 5",33.778898, -118.114241,new ArrayList<Node>());
        Node lh = new Node("Lecture Hall",33.778187, -118.113976,new ArrayList<Node>());
        Node cla = new Node("CLA",33.777815, -118.114132,new ArrayList<Node>());
        Node psy = new Node("Psychology",33.779318, -118.114439,new ArrayList<Node>());
        Node ed2 = new Node("Education 2",33.775727, -118.114354,new ArrayList<Node>());
        Node eed = new Node("EED",33.776224, -118.114156,new ArrayList<Node>());
        Node mmc = new Node("Multimedia Center",33.776768, -118.114561,new ArrayList<Node>());
        Node annex = new Node("ANNEX",33.777081, -118.111909,new ArrayList<Node>());
        Node lab = new Node("Language Arts Building",33.776887, -118.112687, new ArrayList<Node>());
        Node fo2 = new Node("Faculty Office 2",33.778497, -118.113910,new ArrayList<Node>());
        Node fo3 = new Node("Faculty Office 3",33.779128, -118.113688,new ArrayList<Node>());
        Node fo4 = new Node("Faculty Office 3",33.778202, -118.111990,new ArrayList<Node>());
        Node fo5 = new Node("Faculty Office 3",33.779103, -118.112462,new ArrayList<Node>());
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
    }

}
