package de.uniks.vs.simulation.triangle_replication.model;

import de.uniks.vs.simulation.triangle_replication.SimpleTriangleReplication;
import de.uniks.vs.simulator.algorithms.ColorUtils;
import de.uniks.vs.simulator.model.Message;
import de.uniks.vs.simulator.model.Node;

import java.util.HashMap;

public class SimpleTriangleReplicationNode extends Node {

    private static final int MAX_COLOR = 5;

    protected SimpleTriangleReplication triangleReplication;
    protected HashMap<Integer, SimpleSubnet> subnets;

    public SimpleTriangleReplicationNode(int x, int y) {
        super(x, y);
        this.setColor(ColorUtils.getRandomColor(MAX_COLOR));
        this.triangleReplication = new SimpleTriangleReplication(this);
        this.subnets = new HashMap<>();
    }

    @Override
    public void init() {
        super.init();
        this.triangleReplication.add();
    }

    @Override
    public synchronized void trigger() {
        Message m = new Message();
        m.setColor(this.getColor());
        this.broadcast(m);

        super.trigger();
    }

    @Override
    public synchronized void onMessageReceived(Message msg) {
        Message m = new Message();
        m.setColor(msg.getColor());
        this.broadcastExcept(msg, msg.getSource());

        super.onMessageReceived(msg);
    }

    public HashMap<Integer, SimpleSubnet> getSubnets() {
        return this.subnets;
    }

    public void addSubnet(SimpleSubnet subnet) {
        this.subnets.put(subnet.id, subnet);
    }
}
