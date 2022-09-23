package de.uniks.vs.simulation.skill_unit_task_model.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Plan {

    private String id;
    private Plan superPlan;
    private ArrayList<Plan> subPlans = new ArrayList<>();
    private ArrayList<Task> tasks = new ArrayList<>();
    private Set<Skill> actSkills = new HashSet<>();
    private int numberOfInstances;


    public Plan(String id) {
        this.id = id;
    }

    public Plan addTask(Task task) {
        this.tasks.add(task);
        task.setPlan(this);
        task.setFreeInstances(task.getNumberOfInstances() * this.getNumberOfInstances());
        return this;
    }

    public void addTasks(Task... tasks) {

        for (Task t:tasks) {
            this.addTask(t);
        }
    }

    public ArrayList<Plan> getSubPlans() {
        return subPlans;
    }

    public Plan addSubPlan(Plan plan) {
        this.subPlans.add(plan);
        plan.setSuperPlan(this);
        return this;
    }

    private void setSuperPlan(Plan plan) {
        this.superPlan = plan;
    }

    public Plan getSuperPlan() {
        return superPlan;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = (ArrayList<Task>) this.tasks.clone();

        for (Plan plan: subPlans) {
            tasks.addAll(plan.getAllTasks());
        }
        return tasks;
    }

    // ----------------------------
    public Set<Skill> getActSkills() {
        return actSkills;
    }

    public Plan addActSkill(Skill skill) {
        this.actSkills.add(skill);
        return this;
    }
    public void addActSkills(Skill... skills) {

        for (Skill s: skills) {
            addActSkill(s);
        }
    }


    public boolean isComplete() {
        return  tasks.parallelStream().allMatch(t -> t.getUnit() != null);
    }

    public String getId() {
        return id;
    }

    public void setNumberOfInstances(int value) {
        this.numberOfInstances = value;
    }

    public int getNumberOfInstances() {
        return numberOfInstances;
    }
}
