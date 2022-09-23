package de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Blockchain {

    public static int BLOCK_SIZE = 10;

    private ArrayList<Block> chain = new ArrayList<>();

    public Blockchain() {
        this.chain.add(createNewBlock());
    }

    public Blockchain(ArrayList<Block> blocks) {
        this();
        chain = blocks;
    }

    public Block createNewBlock() {
        String previousHash = Block.genesisPrevHash;//"root";

        if (chain.size() > 0)
            previousHash = blockChainHash();
        Block block = new Block();
        block.setTimestamp(System.currentTimeMillis());
        block.setIndex(chain.size());
        block.setPreviousHash(previousHash);
        return block;
    }

    public String blockChainHash() {
        return getLatestBlock().getHash();
    }

    public boolean isChainValid() {
        // TODO: validate block chain
//        String previousHash = chain.get(0).getPreviousHash();
//
//        for (Block block : chain) {
//            String currentHash = block.getPreviousHash();
//
//            if (!currentHash.equals(previousHash)) {
//                return false;
//            }
//            previousHash = block.getHash();
//        }
        return true;
    }

    public int getBlockCount() {
        return this.chain.size();
    }

    public Block getLatestBlock() {
        return this.chain.get(this.chain.size() - 1);
    }

    public boolean addAndValidateBlock(Block block) {
        Block tempBlock = block;

        for (int i = chain.size() - 1; i >= 0; i--) {
            Block b = chain.get(i);
            String hash = b.computeHash(tempBlock.getNonce());
            if (b.getHash().equals(tempBlock.getPreviousHash()) && hash.equals(tempBlock.getHash())) {
                tempBlock = b;
            } else {
                System.out.println("Block Invalid "+"current Block:"+ b.getHash() + "("+b.getIndex()+") !=  previous Block:" +  block.getPreviousHash()+ "("+block.getIndex()+")");
                System.out.println("Block Invalid "+b.getHash() + "("+b.getIndex()+")" +" != " + tempBlock.getPreviousHash()+ "("+tempBlock.getIndex()+")"  + "\n            " + hash +" != "+tempBlock.getHash() );
                return false;
            }
        }
        this.chain.add(block);
        return true;
    }

    public Blockchain addTransaction(Transaction transaction) {

        if (chain.size() == 0) {
            this.chain.add(createNewBlock());
        }

        if (getLatestBlock().getTransactions().size() >= BLOCK_SIZE) {
            this.chain.add(createNewBlock());
        }
        getLatestBlock().addTransition(transaction);
        return this;
    }

    public Blockchain clone() {
        List<Block> chainClone = chain.stream().map(b -> b.clone()).collect(Collectors.toList());
        Blockchain blockchainClone = new Blockchain(new ArrayList(chainClone));
        return blockchainClone;
    }

    public ArrayList<Block> getChain() {
        return this.chain;
    }
}
