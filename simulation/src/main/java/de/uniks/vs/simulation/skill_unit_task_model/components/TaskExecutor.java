package de.uniks.vs.simulation.skill_unit_task_model.components;

import de.uniks.vs.simulation.skill_unit_task_model.USTNode;
import de.uniks.vs.simulation.skill_unit_task_model.warehouse_example.EdgeServiceNode;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskExecutor {

    protected ExecutorService executor;

    protected SkillUnit unit;
    protected USTNode bcNode;
    protected Token token;
    private TaskExecutor next;
    private Task task;
    private Random random;

    public TaskExecutor() {
        this.executor = Executors.newFixedThreadPool(2);
        this.random = new Random();
    }

    public void step() {
        System.out.println("TE: " + this.bcNode.getID() + ": task handling ....");
        List<Task> tasks = this.token.getAvailableTasks();

        for (Task task : tasks) {
            String id = task.getId();
//                System.out.println("TE: " + this.unit.getId() +" "+ this.bcNode.getID() + ": Task(" + id + ") " + task.getReqSkills());
//                System.out.println("TE: " + this.unit.getId() +" "+ this.bcNode.getID() + ": Node(" + this.bcNode.getID() + ") " + this.unit.getSkills());

            if (checkSkillMatches(task)) {
                System.out.println("TE: " + this.unit.getId() + " " + this.bcNode.getID() + "      MATCH FOUND Task(" + id + ") " + task.getReqSkills());
                this.task = task;
                task.allocate(this.unit);
                this.token.removeTask(task);

                if (task.getFreeInstances() > 1)
                    this.token.addTasks(task.clone());
                this.token.removeUnit(this.unit);
                startTask();
                break;
            }
        }
        passToken();
    }

    private void startTask() {

        executor.submit(() -> {
            while (true) {

                if( !checkSkillMatches(this.task)) {
                    System.out.println("ERROR");
                }

                try {
                    Thread.sleep( 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executor.submit(() -> {
            System.out.println("TE: " + this.task.getId() + "::" + this.task.getPlan().getId() + " running ...");

            while (true) {

                try {
                    Thread.sleep(this.random.nextInt(20) * 1000 + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                Skill tmp = this.unit.getSkills().iterator().next();
//                this.unit.getSkills().remove(tmp);
            }
        });
    }

    protected boolean checkSkillMatches(Task task) {
        Set<Skill> skills = this.unit.getSkills();

        for ( Skill skill :task.getReqSkills() ) {
            Optional<Skill> optionalSkill = skills.parallelStream().filter(_skill -> _skill.getId().equals(skill.getId())).findFirst();

            if (optionalSkill.isEmpty())
                return false;
        }
        return true;
    }

    protected void passToken() {
        System.out.println("TE: passing token ...");
        token.removeNode(this.bcNode);

        if(this.task != null) {
            Plan plan = this.task.getPlan();

            if(plan.isComplete()) {
                System.out.println("TM: "+ plan.getId() +" is complete");
                newNode(plan);
                plan.setNumberOfInstances(plan.getNumberOfInstances()-1);
            }
        }
        else {
            token.addNode(this.bcNode);
        }
        int nodeID = this.token.getNextNode();

        if (nodeID != -1) {
            USTNode nextNode = (USTNode) this.token.getNodeByID(nodeID);
            nextNode.getTaskExecutor().setToken(this.token);
            next = nextNode.getTaskExecutor();

            if (next != this)
                this.token = null;
        }
    }

    protected USTNode newNode(Plan plan) {
        SkillUnit unit = new SkillUnit();
        unit.setId(plan.getId());
        Set<Skill> actSkills = plan.getActSkills();
        unit.addSkills(actSkills);
        USTNode ustNode = new USTNode(unit, 0, 0, new TaskExecutor());
        token.addNode(ustNode);
        System.out.println("New Virtual Unit " + ustNode.getID());
        return ustNode;
    }

    protected void sendToken() {
        System.out.println("TM: send token");
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
        this.token.setCurrentUnit(this.unit);
    }

    public TaskExecutor getNext() {
        return next;
    }
}
