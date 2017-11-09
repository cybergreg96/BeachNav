package com.project.beachnav.beachnav;

/**
 * @author Austin Leavitt
 * 11/2/2017
 * node class
 */

import java.util.ArrayList;
import java.lang.Math;

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
    public double getDistance(Node other) {
        double x1 = lng;
        double y1 = lat;
        double x2 = other.getY();
        double y2 = other.getX();
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    //Returns the node that is closest to the current one and goes in the direction of the destination
    public Node getShortest(Node destination) {
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
        Node current = new Node();
        current = start;

        while(current.getDistance(destination) != 0) {
            Node prev = current;
            current = current.getShortest(destination);
            path.add(prev);
            System.out.println(prev.getLabel());
        }
        System.out.println(current.getLabel());
        path.add(current);

        return path;
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

