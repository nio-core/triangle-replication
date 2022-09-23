package de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements;

import de.uniks.vs.simulation.skill_unit_task_model.blockchain.BlockchainUtils;

import java.util.ArrayList;
import java.util.List;

public class Miner {

    Blockchain chain;
    List<Transaction> transactionPool = new ArrayList();
    private int difficulty = 0;

    public Miner(Blockchain chain) {
        this.chain = chain;
    }

    public Miner(Blockchain blockchain, int difficulty) {
        this(blockchain);
        this.difficulty = difficulty;
    }

    public void mine(Transaction transaction) {
        transactionPool.add(transaction);

        if (transactionPool.size() > Blockchain.BLOCK_SIZE) {
            this.createBlockAndApplyToChain();
        }
    }

    public Block createBlock() {
        Block block = chain.createNewBlock();
        block.setPreviousHash(chain.getLatestBlock().getHash());
        block.setHash(proofOfWork(block));
        return block;
    }

    public void createBlockAndApplyToChain() {
        Block block = createBlock();
        chain.addAndValidateBlock(block);
        transactionPool = new ArrayList();
    }

    private String proofOfWork(Block block) {
        StringBuilder nonceKey = new StringBuilder(""+block.getNonce());
        nonceKey.append(String.valueOf(block.getNonce()).repeat(Math.max(0, this.difficulty)));
        long nonce = -1;
        boolean nonceFound = false;
        String nonceHash = null;
//        String message = block.getTimestamp() + block.getIndex() + block.getMerkleTree().getRoot() + transactionPool + block.getPreviousHash();
//        String message = chain.getLatestBlock().getTimestamp() + chain.getLatestBlock().getIndex() + chain.getLatestBlock().getMerkleTree().getRoot() + chain.getLatestBlock().getTransactions() + block.getPreviousHash();
        String message =  chain.getLatestBlock().getIndex() + chain.getLatestBlock().getMerkleTree().getRoot() + chain.getLatestBlock().getTransactions() + block.getPreviousHash();
        long start = System.currentTimeMillis();

        while (!nonceFound) {
            nonce++;
            nonceHash = BlockchainUtils.sha256((message + nonce));
            nonceFound = nonceHash.startsWith(nonceKey.toString());
        }
        System.out.println("M: Mining Time: " + ((System.currentTimeMillis()-start)/1000) + "s");
        block.setNonce(nonce);
        return nonceHash;
    }
}
