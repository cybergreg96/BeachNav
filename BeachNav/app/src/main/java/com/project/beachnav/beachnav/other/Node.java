package com.project.beachnav.beachnav.other;

/**
 * @author Austin Leavitt
 * 11/2/2017
 * node class
 */

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class Node {
    String label;
    double lat;
    double lng;
    ArrayList<Node> adjacent;


    public Node() {
        label = "";
        lat = 0.0;
        lng = 0.0;
        adjacent = new ArrayList<Node>();
    }

    //Austin Tao 11/2/2017
    //no need for new ArrayList<Node>() every time
    public Node(String name, double x, double y) {
        label = name;
        lat = x;
        lng = y;
        adjacent = new ArrayList<Node>();
    }

    public Node(String name, double x, double y, ArrayList<Node> nextList) {
        label = name;
        lat = x;
        lng = y;
        adjacent = nextList;
    }

    //Implements the distance formula to find the distance between two points (using a straight line)
    private double getDistance(Node other) {
        double x1 = lng;
        double y1 = lat;
        double x2 = other.getY();
        double y2 = other.getX();
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    //Returns the node that is closest to the current one and goes in the direction of the destination
    private Node getShortest(Node destination) {
        Node shortest = new Node("Dummy", Double.MAX_VALUE,Double.MIN_VALUE, new ArrayList<Node>());
        for(Node x: adjacent) {
            if(this.getDistance(x) < this.getDistance(shortest)
                    && this.getDistance(destination) > x.getDistance(destination)) {
                shortest = x;
            }
        }
        return shortest;
    }

    public static ArrayList<Node> getPath(Node start, Node destination) {
        ArrayList<Node> path = new ArrayList<Node>();

        //Get straight line distance between start and destination
        double rawDistance = start.getDistance(destination);

        //Adding nodes to path
        Node current = start;

        while(current.getDistance(destination) != 0) {
            Node prev = current;
            current = current.getShortest(destination);
            path.add(prev);
//            System.out.println(prev.getLabel());
        }
//        System.out.println(current.getLabel());
        path.add(current);

        return path;
    }

    /* Void method drawPath iterates through the shortestPath to generate a Location ArrayList using
    * Node 'x' and 'y' values for latitude and longitude coordinates. Method then creates a Polyline
    * object to draw lines between the locations, later adding it to the Google Map Object*/
    public static void drawPath(ArrayList<Node> shortestPath, GoogleMap mMap)
    {
//        Node[] nodeArr = (Node[])shortestPath.toArray();
        Node[] nodeArr = new Node[shortestPath.size()];
        int w = 0;
        for (Node n: shortestPath) {
            nodeArr[w] = n;
            w++;
        }

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

    //Setters and getters
    public void setAdjacent(Node other) {
        adjacent.add(other);
    }
    public ArrayList<Node> getAdjacent() {
        return adjacent;
    }

    public void setX(double x) {
        lng = x;
    }
    public void setY(double y) {
        lat = y;
    }
    public double getY() {
        return lng;
    }
    public double getX() {
        return lat;
    }

    public void setLabel(String name) {
        label = name;
    }
    public String getLabel() {
        return label;
    }
}

