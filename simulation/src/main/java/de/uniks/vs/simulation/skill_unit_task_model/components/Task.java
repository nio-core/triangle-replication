package de.uniks.vs.simulation.skill_unit_task_model.components;

import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Task {
    private String id;
    private Color color;
    private Plan plan;
    private Set<Skill> reqSkills = new HashSet<>();
    private SkillUnit unit;
    private int numberOfInstances = 1;
    private int freeInstances;


    public Task(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    public Task(String id, Plan plan) {
        this.id = id;
        this.plan = plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Plan getPlan() {
        return plan;
    }

    public String getId() {
        return id;
    }

    // ----------------------------
    public Set<Skill> getReqSkills() {
        return reqSkills;
    }

    public Task addReqSkill(Skill skill) {
        this.reqSkills.add(skill);
        return this;
    }
    public void addReqSkills(Skill... skills) {

        for (Skill s: skills) {
            addReqSkill(s);
        }
    }

    // ----------------------------
    public void allocate(SkillUnit unit) {
        this.unit = unit;
        this.unit.setTask(this);
    }

    public Color getColor() {
        return color;
    }

    public SkillUnit getUnit() {
        return unit;
    }

    @Override
    protected Task clone() {
        Task task = new Task(this.id, this.plan);
        task.reqSkills = this.reqSkills;
        task.color = this.color;
        task.setFreeInstances(freeInstances-1);
        return task;
    }

    public void setNumberOfInstances(int numberOfInstances) {
        this.numberOfInstances = numberOfInstances;
    }

    public int getNumberOfInstances() {
        return numberOfInstances;
    }

    public void setFreeInstances(int freeInstances) {
        this.freeInstances = freeInstances;
    }

    public int getFreeInstances() {
        return freeInstances;
    }
}
