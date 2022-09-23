package de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements;

import de.uniks.vs.simulation.skill_unit_task_model.blockchain.BlockchainUtils;

public class Transaction {

    private String hash = null;
    private Object value = null;

    public Transaction(Object value) {
        this.setValue(value);
    }

    public String getHash() { return hash; }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.hash = BlockchainUtils.sha256(value.toString());
        this.value = value;
    }

    @Override
    public String toString() {
        return hash + ":" + value;
    }
}
