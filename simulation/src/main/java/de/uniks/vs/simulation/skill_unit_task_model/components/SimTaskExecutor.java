package de.uniks.vs.simulation.skill_unit_task_model.components;

import de.uniks.vs.simulation.skill_unit_task_model.USTNode;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.TransitionMessage;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements.Block;
import de.uniks.vs.simulation.skill_unit_task_model.blockchain.elements.Transaction;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SimTaskExecutor extends TaskExecutor {

    protected final ExecutorService executor;

    protected SkillUnit unit;
    protected USTNode bcNode;
    protected Token token;
    protected boolean taskProcessIsRunning;

    public SimTaskExecutor() {
        executor = Executors.newFixedThreadPool(1);
        taskProcessIsRunning = false;
    }

    public void step() {
        // checking token, identify current unit ID in the token
        token = null;
        boolean check = checkToken();

        if (taskProcessIsRunning || !check)
            return;

        Color saveColor = bcNode.getColor();
        bcNode.setColor(Color.LIGHTGRAY);

        System.out.println("TM: " + this.bcNode.getID() + ": task handling ....");
        taskProcessIsRunning = true;

        // start task process
        executor.submit(() -> {
            ArrayList<Task> tasks = this.token.getTasks();

            for (Task task : tasks) {
                String id = task.getId();
                System.out.println("TM: " + this.bcNode.getID() + ": Task(" + id +") " + task.getReqSkills());
                System.out.println("TM: " + this.bcNode.getID() + ": Node(" + this.bcNode.getID() +") " + this.unit.getSkills());

                if (checkSkillMatches(task)) {
                    System.out.println("TM:                    match Task(" + id +") " + task.getReqSkills());
                    task.allocate(this.unit);
                    this.token.removeTask(task);
                    this.token.removeUnit(this.unit);
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // send token to the next un visited unit
            passToken();
            bcNode.setColor(saveColor);
            taskProcessIsRunning = false;
        });
    }

    protected boolean checkSkillMatches(Task task) {

        for ( Skill skill :task.getReqSkills() ) {

            if (!this.unit.hasSkill(skill)) {
                return false;
            }
        }
        return true;
    }

    protected void passToken() {
        System.out.println("TM: passing token 1");
        token = token.clone();
        token.removeNode(this.bcNode);

        if (this.unit.getTask() == null)
            token.addNode(this.bcNode);
        else if (this.unit.getTask().getPlan().isComplete()) {
            System.out.println("TM: plan is complete");
            USTNode sutNode = newNode(30, 30);
            sutNode.setColor(Color.ORANGE);
//            sutNode.setText(this.unit.getTask().getPlan().getId());
            Plan superPlan = this.unit.getTask().getPlan().getSuperPlan();
            boolean complete = superPlan.getSubPlans().parallelStream().allMatch(p -> p.isComplete());

            if (complete) {
                System.out.println("TM: superNode 1");

                USTNode superNode = newNode(60, 60);
                superNode.setColor(Color.MAGENTA);
                ArrayList<Plan> subPlans = superPlan.getSubPlans();

                for(Plan plan : subPlans) {

                    for (Task task :plan.getAllTasks()) {
//                        token.removeNode(task.getUnit());
                        superNode.getUnit().addSkills(task.getReqSkills());
//                        sutNode.setText(superPlan.getId());
                    }
                }
            }
//            for (Plan p : superPlan.getSubPlans()) {
//                if(!p.isComplete())
//                    break;
//            }
        }
        System.out.println("TM: passing token 2");
        checkForNewNodes();
        sendToken();
    }

    protected USTNode newNode(int x, int y) {
        SkillUnit unit = new SkillUnit();
        USTNode node = new USTNode(unit, this.bcNode.getX() + x, this.bcNode.getY() + y);
        this.bcNode.getSimulation().addNode(node);
        this.bcNode.getSimulation().connectAll();
        token.addNode(node);
        return node;
    }

    protected void checkForNewNodes() {
        //TODO
    }

    protected void sendToken() {
        System.out.println("TM: send token");

        Transaction transaction = new Transaction(token.clone());
        this.bcNode.getBlockchain().addTransaction(transaction);
        TransitionMessage transitionMessage = new TransitionMessage();
        transitionMessage.setColor(Color.GREEN);
        transitionMessage.transition = transaction;
        this.bcNode.addToTransactionQueue(transitionMessage);
    }

    protected boolean checkToken() {
        ArrayList<Block> chain = bcNode.getBlockchain().getChain();

        for (int i = chain.size()-1; i >= 0; i--) {
            Block block = chain.get(i);
            List<Transaction> transactions = block.getTransactions();

            for (int j = transactions.size()-1; j >= 0; j--){
                Transaction transaction = transactions.get(j);

                if (transaction.getValue() instanceof Token) {
                    token = (Token) transaction.getValue();
                    break;
                }
            }
            if (token != null)
                break;
        }
        if (token == null || (token.getNextNode() != this.bcNode.getID()))
            return false;
        return true;
    }

    public void setUnit(SkillUnit unit) {

        if (this.unit == unit)
            return;
        this.unit = unit;
    }

    public void setBCNode(USTNode bcNode) {
        this.bcNode = bcNode;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
