package de.uniks.vs.simulation;

import de.uniks.vs.physics.Physics;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.BlockchainNode;
import de.uniks.vs.simulator.Simulator;
import de.uniks.vs.simulator.model.utils.NodeTypes;
import javafx.stage.Stage;

public class BlockchainSimulator extends Simulator {

    @Override
    public void start(Stage stage) {
        NodeTypes.registerNodeType("BCNode", BlockchainNode.class);

        Physics physics = new Physics();
        this.title = "^._.^   Skill-Unit-Task Simulation   v0.1";
        startApplication(stage);
    }
}


