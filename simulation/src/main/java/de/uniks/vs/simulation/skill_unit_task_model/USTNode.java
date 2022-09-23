package de.uniks.vs.simulation.skill_unit_task_model;

import de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements.Blockchain;
import de.uniks.vs.simulation.skill_unit_task_model.components.*;
import de.uniks.vs.simulator.model.Message;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.BlockchainNode;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.TransitionMessage;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements.Transaction;
import de.uniks.vs.simulation.skill_unit_task_model.warehouse_example.PlanTree;
import javafx.scene.paint.Color;

import java.util.concurrent.ConcurrentLinkedQueue;

public class USTNode extends BlockchainNode {

    public static int MAX_BLOCKS_AMOUNT = 10000;
    public static int ROUND_TIME = 100000;

    protected final TaskExecutor taskExecutor;
    protected final SkillUnit unit;

    private static int idCount;

    private ConcurrentLinkedQueue<TransitionMessage> transitionQueue = new ConcurrentLinkedQueue();

    public USTNode(int x, int y) {
        this( new SkillUnit(), x, y);
    }

    public USTNode(SkillUnit unit, int x, int y) {
        super(x, y);
        BlockchainNode.ROUND_TIME = ROUND_TIME;
        BlockchainNode.MAX_BLOCKS_AMOUNT = MAX_BLOCKS_AMOUNT;
        Blockchain.BLOCK_SIZE = MAX_BLOCKS_AMOUNT;
        PlanTree.create();
        this.unit = unit;
        this.unit.init();
        this.taskExecutor = new SimTaskExecutor();
        this.taskExecutor.setUnit(this.unit);
        this.taskExecutor.setBCNode(this);
        this.updateInfo();
    }

    public  USTNode(SkillUnit unit, int x, int y, TaskExecutor taskExecutor) {
        super(x, y);
        BlockchainNode.ROUND_TIME = ROUND_TIME;
        BlockchainNode.MAX_BLOCKS_AMOUNT = MAX_BLOCKS_AMOUNT;
        Blockchain.BLOCK_SIZE = MAX_BLOCKS_AMOUNT;
        this.setId(idCount++);
        this.unit = unit;
        this.taskExecutor = taskExecutor;
        this.taskExecutor.setUnit(this.unit);
        this.taskExecutor.setBCNode(this);
        this.updateInfo();
    }

    @Override
    public void init() {
        super.init();
    }

    // ------------  Blockchain Node ------------
    @Override
    public synchronized void trigger() {

        if (PlanTree.isCalled())
            return;
        Plan plan = PlanTree.get();
        Transaction transaction = new Transaction(plan);
        this.blockchain.addTransaction(transaction);
        TransitionMessage transitionMessage = new TransitionMessage();
        transitionMessage.setColor(Color.GREEN);
        transitionMessage.transition = transaction;
        this.updateInfo();
        super.broadcast(transitionMessage);
        Token token = new Token();
        // not-executed tasks,
        // skill requirements
        // allocated units
        token.setTasks(plan.getAllTasks());
        // list of not yet visited units
        token.addNodes(this.getSimulation().getAllNodes());
        transaction = new Transaction(token );
        this.blockchain.addTransaction(transaction);
        transitionMessage = new TransitionMessage();
        transitionMessage.setColor(Color.GREEN);
        transitionMessage.transition = transaction;
        this.updateInfo();
        this.transitionQueue.add(transitionMessage);
    }

    int i = 0;

    @Override
    public synchronized void step() {
//        System.out.println("SUTNode " + getID());

        if (!transitionQueue.isEmpty()) {
            TransitionMessage transitionMessage = transitionQueue.poll();
            super.broadcast(transitionMessage);
            updateInfo();
        }
//        this.setText(getID() + "  " +i++);
//        System.out.println("SUTNode " + this.getText());
        this.taskExecutor.step();
        super.step();
    }

    @Override
    public synchronized void onMessageReceived(Message msg) {
        super.onMessageReceived(msg);
    }

    // ------------  SUT Node Info  ------------
    @Override
    protected void updateInfo() {
        String currentTask = "";

        if (this.unit != null && this.unit.getTask() != null) {
//            currentTask = this.unit.getTask().getId() + "\n";
            this.setColor(this.unit.getTask().getColor());
//            this.setText(currentTask);
        }
        else if (this.unit != null) {
            this.setText("Unit " +this.getID() + "\n" + getUnit().getSkills().toString());
//            this.setText("BC  Blocks:"+ getBlockchain().getChain().size()
//                + " Transactions:" + getBlockchain().getLatestBlock().getTransactions().size()
//                + " Countdown:" + getCounter());
        }
    }

    public void addToTransactionQueue(TransitionMessage transition) {
        transitionQueue.add(transition);
    }

    public SkillUnit getUnit() {
        return unit;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }
}
