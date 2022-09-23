package de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements;

import de.uniks.vs.simulation.skill_unit_task_model.blockchain.BlockchainUtils;

import java.util.*;
import java.util.function.Consumer;

public class Block {

    final static String genesisPrevHash = "4e17811011ec217ac84a9e037a82758a7de318342886a88a37ebabf90f52af73";

    private long timestamp;
    private int index;
    private long nonce = 0;
    private String hash = null;
    private String previousHash = null;

    private MerkleTree merkleTree = new MerkleTree();
    private Map transactions = Collections.synchronizedMap(new LinkedHashMap<String, Transaction>());

    public Block addTransition(Transaction transaction) {
        this.transactions.put(transaction.getHash(), transaction);
        this.merkleTree.calculateRoot(transactions.values());
//        this.computeHash();
        return this;
    }

    public String computeHash(long nonce) {
//        String message = this.getTimestamp() + this.getIndex() + this.getMerkleTree().getRoot() + transactions.values() + this.getHash();
        String message = this.getIndex() + this.getMerkleTree().getRoot() + transactions.values() + this.getHash();
        String hash = BlockchainUtils.sha256((message + nonce));
        return hash;
    }

    public String computeHash() {

        if (merkleTree.getRoot() == null) {
            setHash(genesisPrevHash);
        }
        else {
//            String string =  index +" "+ merkleTree.getRoot() +" "+ transactions.values() +" "+ nonce +" "+ previousHash;
//            setHash(BlockchainUtils.sha256(string));
            String message = this.getIndex() + this.getMerkleTree().getRoot() + transactions.values() + this.getPreviousHash();
            setHash(BlockchainUtils.sha256(message+nonce));
        }
        return this.hash;
    }


    public Block clone() {
        Block clone = new Block();
        clone.setIndex(this.getIndex());
        clone.setNonce(this.getNonce());
        clone.setHash(this.getHash());
        clone.setPreviousHash(this.getPreviousHash());
        clone.getMerkleTree().setRoot(merkleTree.getRoot());
        clone.setTimestamp(this.getTimestamp());
        List<Transaction> transactionList = new ArrayList();
        Consumer consumer = (t) -> transactionList.add((Transaction) t);
        this.getTransactions().forEach(consumer);
        clone.setTransactions(transactionList);
        return clone;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean transitionsValid()  {
        List<String> tree = merkleTree.createAndGetTree(this.transactions.values());
        String root = tree.get(tree.size() -1);
        return root.equals(merkleTree.getRoot());
    }

    public String getHash() {
        hash = hash == null ? computeHash() : hash;
        return hash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPreviousHash() {
        previousHash = previousHash == null ? genesisPrevHash : previousHash;
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions.values());
    }

    public void setTransactions(List<Transaction> transactions) {

        for (Transaction transaction: transactions) {
            this.addTransition(transaction);
        }
    }

    public MerkleTree getMerkleTree() {
        return merkleTree;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

}
