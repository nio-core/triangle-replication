package de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements;

import de.uniks.vs.simulation.skill_unit_task_model.blockchain.BlockchainUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MerkleTree {

    private String merkleRoot = null;
    private List<String> tree = null;

    public void calculateRoot(Collection<Transaction> transactions) {
        this.createTree(transactions);
        this.setRoot(this.tree.get(this.tree.size()-1));
    }

    public List<String> createAndGetTree(Collection<Transaction> transactions) {
        this.createTree(transactions);
        return this.getTree();
    }

    private void createTree(Collection<Transaction> transactions) {
        this.tree = new ArrayList<>();

        for (Transaction transaction : transactions) {
            tree.add(transaction.getHash());
        }
        int levelOffset = 0;

        for (int levelSize = transactions.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {

            for (int left = 0; left < levelSize; left += 2) {
                int right = Math.min(left + 1, levelSize - 1);
                String tleft = tree.get(levelOffset + left);
                String tright = tree.get(levelOffset + right);
                tree.add(BlockchainUtils.sha256((tleft + tright)));
            }
            levelOffset += levelSize;
        }
        return;
    }

    public String getRoot() {
        return merkleRoot;
    }

    public void setRoot(String root) {
        this.merkleRoot = root;
    }

    public List<String> getTree() {
        return tree;
    }
}
