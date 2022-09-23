package de.uniks.vs.simulation.skill_unit_task_model.components;

import de.uniks.vs.simulator.model.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Token {

    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<Node> nodes = new ArrayList<>();
    private SkillUnit getCurrentUnit;

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public List<Task> getAvailableTasks() {
        List<Task> availableTasks = tasks.stream().filter(task -> {
            if (task.getFreeInstances() > 0) return true;
            return false;
        }).collect(Collectors.toList());
        return availableTasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTasks(Task... tasks) {
        Collections.addAll(this.tasks, tasks);
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public void addNodes(List<Node> nodes) {
        this.nodes.addAll(nodes);
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public int getNextNode() {
        return nodes.size() > 0 ? nodes.get(0).getID(): -1;
    }

    public Node getNodeByID(int id) {
        return nodes.stream().findFirst().filter(node -> node.getID() == id).get();
    }

    public void removeNode(Node node) {
        this.nodes.remove(node);
    }

    public Token clone() {
        Token token = new Token();
        token.setTasks((ArrayList<Task>) this.getTasks().clone());
        token.addNodes((ArrayList<Node>) this.nodes.clone());
        return token;
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
    }

    public void removeUnit(SkillUnit unit) {
        this.nodes.remove(unit);
    }

    public void setCurrentUnit(SkillUnit unit) {
        this.getCurrentUnit = unit;
    }

    public SkillUnit getCurrentUnit() {
        return getCurrentUnit;
    }
}
