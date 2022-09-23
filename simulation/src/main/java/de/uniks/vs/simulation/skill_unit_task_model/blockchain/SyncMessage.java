package de.uniks.vs.simulation.skill_unit_task_model.blockchain;

import de.uniks.vs.simulator.model.Message;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements.Blockchain;

public class SyncMessage extends Message {
    private Blockchain blockchain;
    private int id;

    public void setBlockchain(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }
}
