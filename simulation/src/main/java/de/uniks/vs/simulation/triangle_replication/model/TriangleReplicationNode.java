package de.uniks.vs.simulation.triangle_replication.model;

import de.uniks.vs.simulation.triangle_replication.TriangleReplication;
import de.uniks.vs.simulator.algorithms.ColorUtils;
import de.uniks.vs.simulator.model.Message;
import de.uniks.vs.simulator.model.Node;

import java.util.HashMap;
import java.util.Map;

public class TriangleReplicationNode extends Node {

    private static final int MAX_COLOR = 5;

    protected TriangleReplication triangleReplication;
    protected HashMap<Integer, Subnet> subnets;

    public TriangleReplicationNode(int x, int y) {
        super(x, y);
        this.setColor(ColorUtils.getRandomColor(MAX_COLOR));
        this.triangleReplication = new TriangleReplication(this);
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

    public HashMap<Integer, Subnet> getSubnets() {
        return this.subnets;
    }

    public void addSubnet(Subnet subnet) {
        this.subnets.put(subnet.getID(), subnet);
        subnet.addMember(this.getID());
    }

    public void updateInfo() {
        this.setColor(this.getColor());
        String text = this.getID() + "  ";
        for (Map.Entry<Integer, Subnet> entry :this.subnets.entrySet()) {
            text += entry.getKey() +":["+entry.getValue().getMembers() + "]";
        }
        this.setText(text);
    }
}
