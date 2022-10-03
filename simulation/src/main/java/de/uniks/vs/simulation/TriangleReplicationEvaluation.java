package de.uniks.vs.simulation;

import de.uniks.vs.simulation.triangle_replication.model.SimpleTriangleReplicationNode;
import de.uniks.vs.simulation.triangle_replication.model.TriangleReplicationNode;
import de.uniks.vs.simulator.model.Node;
import de.uniks.vs.simulator.simulation.Simulation;
import de.uniks.vs.simulator.view.SimulationWindow;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.util.NoSuchElementException;

public class TriangleReplicationEvaluation extends TriangleReplicationSimulator {

    @Override
    public void start(Stage stage) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        runEvaluation();
                    }
                };
                int i = 0;
                while (i < 1000000) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) { }
                    Platform.runLater(updater);
                    i++;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        super.start(stage);
    }

    public void runEvaluation() {
        Simulation simulation = Simulation.getInstance();
        Node simpleNode = new SimpleTriangleReplicationNode(50,50);
//        Node advancedNode = new TriangleReplicationNode(10,10);
        simulation.getSimulation().addNode(simpleNode);
//        simulation.getSimulation().addNode(advancedNode);
    }
}
