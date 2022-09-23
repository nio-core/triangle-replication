package de.uniks.vs.simulation;

import de.uniks.vs.physics.Physics;
import de.uniks.vs.simulation.skill_unit_task_model.USTNode;
import de.uniks.vs.simulator.Simulator;
import de.uniks.vs.simulator.model.utils.NodeTypes;
import de.uniks.vs.simulation.skill_unit_task_model.warehouse_example.EdgeServiceNode;
import de.uniks.vs.simulation.skill_unit_task_model.warehouse_example.KnowledgeServiceNode;
import de.uniks.vs.simulation.skill_unit_task_model.warehouse_example.TransportRobotNode;
import de.uniks.vs.simulation.skill_unit_task_model.warehouse_example.UAVNode;
import javafx.stage.Stage;

public class SUTSimulator extends Simulator {

    @Override
    public void start(Stage stage) {

        NodeTypes.registerNodeType("TransportRobot", TransportRobotNode.class);
        NodeTypes.registerNodeType("UAV", UAVNode.class);
        NodeTypes.registerNodeType("EdgeService", EdgeServiceNode.class);
        NodeTypes.registerNodeType("KnowledgeService", KnowledgeServiceNode.class);
        NodeTypes.registerNodeType("RandomUnit", USTNode.class);
//        NodeTypes.registerNodeType("BCNode", BlockchainNode.class);

        Physics physics = new Physics();
        this.title = "^._.^   Skill-Unit-Task Simulation   v0.1";
        startApplication(stage);
    }
}


