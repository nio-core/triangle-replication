package de.uniks.vs.simulation.triangle_replication.model;

import java.util.HashMap;
import java.util.HashSet;

public class Subnet {
    public static int subnet_counter = 0;
    public static HashMap<Integer, Subnet> subnets = new HashMap<>();

    public int id;
    public HashSet<Integer> members;

    public Subnet() {
        this.id = subnet_counter++;
        this.members = new HashSet<>();
        subnets.put(this.id, this);
    }
}
