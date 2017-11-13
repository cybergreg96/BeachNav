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
    public void drawPath(ArrayList<Node> shortestPath, GoogleMap gmap)
    {
        Node[] nodeArr = (Node[])shortestPath.toArray();
        ArrayList<Location> drawnLocs = new ArrayList<Location>();
        PolylineOptions lineToDraw = new PolylineOptions();
        lineToDraw.color(Color.parseColor("#CC0000FF"));
        lineToDraw.width(5);
        lineToDraw.visible(true);
        for(int i=0;i<=nodeArr.length;i++){
            Location toAdd = new Location(nodeArr[i].getLabel());
            toAdd.setLatitude(nodeArr[i].getX());
            toAdd.setLongitude(nodeArr[i].getY());
            drawnLocs.add(toAdd);
        }
        for(Location drawn: drawnLocs){
            lineToDraw.add(new LatLng(drawn.getLatitude(), drawn.getLongitude()));
        }
        gmap.addPolyline(lineToDraw);
    }
}
