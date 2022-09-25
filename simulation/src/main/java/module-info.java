module simulation {
        requires simulator;
        requires retrofit2;
        requires io.reactivex.rxjava3;
        requires org.apache.logging.log4j;
        requires javafx.graphics;
        requires java.desktop;
    requires physics;
        exports de.uniks.vs.simulation;
        exports de.uniks.vs.simulation.skill_unit_task_model;
        exports de.uniks.vs.simulation.skill_unit_task_model.warehouse_example;
        exports de.uniks.vs.simulation.skill_unit_task_model.blockchain;
        exports de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements;
        exports de.uniks.vs.simulation.triangle_replication;
}