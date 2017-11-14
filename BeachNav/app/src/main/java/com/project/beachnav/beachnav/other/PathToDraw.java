package com.project.beachnav.beachnav.other;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;

/**
 * Created by Justin on 11/12/2017.
 */

public class PathToDraw {
    /* Void method drawPath iterates through the shortestPath to generate a Location ArrayList using
    * Node 'x' and 'y' values for latitude and longitude coordinates. Method then creates a Polyline
    * object to draw lines between the locations, later adding it to the Google Map Object*/
    public void drawPath(ArrayList<Node> shortestPath, GoogleMap mMap)
    {
        Node[] nodeArr = (Node[])shortestPath.toArray();
        ArrayList<Location> drawnLocs = new ArrayList<Location>();
        PolylineOptions lineToDraw = new PolylineOptions(); //Line object compatible with locations
        lineToDraw.color(Color.parseColor("#CC0000FF"));
        lineToDraw.width(5);
        lineToDraw.visible(true);
        for(int i=0;i<nodeArr.length;i++){ //Iterates through nodeArr to create Location ArrayList
            Location toAdd = new Location(""); //Provider is unnecessary for this method
            toAdd.setLatitude(nodeArr[i].getX()); //uses Node x value for latitude
            toAdd.setLongitude(nodeArr[i].getY()); //uses Node y value for longitude
            drawnLocs.add(toAdd);
        }
        for(Location drawn: drawnLocs){ //Draws through LatLng objects created from location arrlist
            lineToDraw.add(new LatLng(drawn.getLatitude(), drawn.getLongitude()));
        }
        mMap.addPolyline(lineToDraw); //adds lines to visible map
    }
}
