package com.project.beachnav.beachnav.other;


import java.util.ArrayList;

public class LocationSet {

    private ArrayList<Node> list;

    public LocationSet() {
        list = new ArrayList<Node>();
    }

    public void add(Node x) {
        list.add(x);
    }

    public ArrayList<Node> getList() {
        return list;
    }
}
