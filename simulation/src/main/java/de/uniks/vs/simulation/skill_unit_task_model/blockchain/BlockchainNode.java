package de.uniks.vs.simulation.skill_unit_task_model.blockchain;

import de.uniks.vs.simulator.model.Message;
import de.uniks.vs.simulator.model.Node;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements.Block;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements.Blockchain;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements.Miner;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements.Transaction;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class BlockchainNode extends Node {

    public static int DIFFICULTY = 1; //required amount of zeros at the beginning of valid hash
    public static int MAX_BLOCKS_AMOUNT = 4; //10000; //amount of blocks in chain
    public static int ROUND_TIME = 100; //100000; //amount of blocks in chain
    static int nextNode = 1;

    protected final Miner miner;
    protected Blockchain blockchain;
    protected int counter;

    private boolean callBlockchain = false;
    private ArrayList<Blockchain> blockchains = new ArrayList<>();

    // 1. new transactions are broadcast to all nodes
    // 2. each node collects new transitions inot a block
    // 3. In each round (= mining a new block) a random node gets to broadcast its block
    // 4. Other nodes accept the block only if all transactions etc. in it are valid
    // 5. Nodes express their acceptance of the block by including its hash in the next block they create and append to the chain

    public BlockchainNode(int x, int y) {
        super(x, y);
        this.blockchain = new Blockchain();
        Blockchain.BLOCK_SIZE = MAX_BLOCKS_AMOUNT;
        this.miner = new Miner(this.blockchain, DIFFICULTY);
        this.counter = 0;
        this.updateInfo();
    }

    @Override
    public synchronized void trigger() {
        // 1. new transactions are broadcast to all nodes
        Transaction transaction = new Transaction(String.valueOf(this.getID()) + System.currentTimeMillis());
        blockchain.addTransaction(transaction);
        TransitionMessage transitionMessage = new TransitionMessage();
        transitionMessage.setColor(Color.GREEN);
        transitionMessage.transition = transaction;
        this.updateInfo();
        super.broadcast(transitionMessage);
        super.trigger();
    }

    @Override
    public synchronized void step() {
        // 3. In each round (= mining a new block) a random node gets to broadcast its block
        if (this.getID() != nextNode)
            return;
        this.counter++;

        if (this.counter == ROUND_TIME || this.blockchain.getLatestBlock().getTransactions().size() == MAX_BLOCKS_AMOUNT) {
            this.counter = 0;
            nextNode = new Random().nextInt(this.getSimulation().getNodeCount()) +1;
            Block block = this.miner.createBlock();
            BlockMessage blockMessage = new BlockMessage();
            blockMessage.setColor(Color.RED);
            blockMessage.block = block;
            this.blockchain.addAndValidateBlock(block);
            this.broadcast(blockMessage);
        }
        this.updateInfo();
        super.step();
    }

    @Override
    public synchronized void onMessageReceived(Message msg) {
        // 2. each node collects new transitions inot a block
        if (msg instanceof TransitionMessage) {
            blockchain.addTransaction(((TransitionMessage) msg).transition);
        }
        // 4. Other nodes accept the block only if all transactions etc. in it are valid
        // 5. Nodes express their acceptance of the block by including its hash in the next block they create and append to the chain
        else if(msg instanceof BlockMessage) {
            super.log(this.getID() + " get new Block from " + msg.getSource().getID());
            boolean valid = blockchain.addAndValidateBlock(((BlockMessage) msg).block);

            if (!valid) {
                callForBlockchain();
            }
        }
        else if (msg instanceof BCRequestMessage) {
            sendBlockchain(((BCRequestMessage)msg).getId());
        }
        else if (msg instanceof SyncMessage) {

            if (callBlockchain) {

                Blockchain blockchain = ((SyncMessage) msg).getBlockchain();
                boolean chainValid = blockchain.isChainValid();
                blockchains.add(blockchain);

                if (blockchains.size() == (this.getSimulation().getNodeCount() - 1)) {

                    for (Blockchain bChain : blockchains) {
                        System.out.println(bChain.getBlockCount());
                    }

                    if (chainValid) {
                        this.blockchain = blockchain;
                        blockchains.clear();
                        callBlockchain = false;
                    }
                }
            }
        }
        this.updateInfo();
        super.onMessageReceived(msg);
    }

    private void sendBlockchain(int id) {
        SyncMessage syncMessage = new SyncMessage();
        syncMessage.setBlockchain(this.getBlockchain().clone());
        syncMessage.setId(this.getID());
        super.sendTo(syncMessage, id);
    }

    private void callForBlockchain() {
        BCRequestMessage requestMessage = new BCRequestMessage();
        requestMessage.setColor(Color.RED);
        requestMessage.setId(this.getID());

        for (Integer id : this.getNeighbors())
            super.sendTo(requestMessage, id);
        callBlockchain = true;
    }

    protected void updateInfo() {
        this.setText("B:"+blockchain.getChain().size()+" T:"+blockchain.getLatestBlock().getTransactions().size()+ " C:" + this.counter);
    }

    public Blockchain getBlockchain() {
        return this.blockchain;
    }

    public int getCounter() {
        return counter;
    }
}
