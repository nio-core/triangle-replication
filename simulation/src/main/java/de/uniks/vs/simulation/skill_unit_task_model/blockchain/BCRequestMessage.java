package de.uniks.vs.simulation.skill_unit_task_model.blockchain;

import de.uniks.vs.simulator.model.Message;

public class BCRequestMessage extends Message {

    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
