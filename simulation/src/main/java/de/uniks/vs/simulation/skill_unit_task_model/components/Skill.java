package de.uniks.vs.simulation.skill_unit_task_model.components;

public class Skill {
    private String id;

    public Skill(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Skill{" + "id='" + id + "} ";
    }
}
