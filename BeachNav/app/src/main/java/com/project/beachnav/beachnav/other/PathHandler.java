package com.project.beachnav.beachnav.other;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Justin on 11/12/2017.
 */

public class PathHandler {
    /* Void method drawPath iterates through the shortestPath to generate a Location ArrayList using
    * Node 'x' and 'y' values for latitude and longitude coordinates. Method then creates a Polyline
    * object to draw lines between the locations, later adding it to the Google Map Object*/
    private PolylineOptions lineToDraw = new PolylineOptions(); //Line options object compatible with locations
    private GoogleMap gmap;
    private Polyline p; /*= gmap.addPolyline(lineToDraw);*/
    private ArrayList<Location> drawnLocs = new ArrayList<Location>();
    private ArrayList<Node> spath = new ArrayList<Node>();

    public PathHandler(ArrayList<Node> path, GoogleMap mMap){
        spath = path;
        gmap = mMap;
    }
    public void show() {
        lineToDraw.color(Color.parseColor("#CC0000FF"));
        lineToDraw.width(5);
        lineToDraw.visible(true);

    }
    public void genVisualPath(){
        Node[] nodeArr = new Node[spath.size()];
        int w = 0;
        for (Node n : spath) {
            nodeArr[w] = n;
            w++;
        }
        for (int i = 0; i < nodeArr.length; i++) { //Iterates through nodeArr to create Location ArrayList
            Location toAdd = new Location(""); //Provider is unnecessary for this method
            toAdd.setLatitude(nodeArr[i].getX()); //uses Node x value for latitude
            toAdd.setLongitude(nodeArr[i].getY()); //uses Node y value for longitude
            drawnLocs.add(toAdd);
        }
        for (Location drawn : drawnLocs) { //Draws through LatLng objects created from location arrlist
            lineToDraw.add(new LatLng(drawn.getLatitude(), drawn.getLongitude()));
        }
        p = gmap.addPolyline(lineToDraw);
    }
    public void clearPath() {
        lineToDraw.visible(false);
        p.remove();
        drawnLocs.removeAll(Collections.singleton(null));
    }
}
