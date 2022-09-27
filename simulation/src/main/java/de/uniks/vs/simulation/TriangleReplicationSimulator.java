package de.uniks.vs.simulation;

import de.uniks.vs.physics.Physics;
import de.uniks.vs.simulation.triangle_replication.model.TriangleReplicationNode;
import de.uniks.vs.simulator.Simulator;
import de.uniks.vs.simulator.model.utils.NodeTypes;
import javafx.stage.Stage;

public class TriangleReplicationSimulator extends Simulator {

    @Override
    public void start(Stage stage) {

        NodeTypes.registerNodeType("TRNode", TriangleReplicationNode.class);

        Physics physics = new Physics();
        this.title = "^._.^   Triangle Replication Simulation   v0.1";
        startApplication(stage);
    }
}
