package de.uniks.vs.simulation.skill_unit_task_model.components;

import de.uniks.vs.simulation.skill_unit_task_model.warehouse_example.PlanTree;

import java.util.*;

public class SkillUnit {

    private Task task;
    private String id;
    private Set<Skill> skills;
    private Set<SkillUnit> units = new HashSet<>();

    public void init() {
        Plan plan = PlanTree.get();
        ArrayList<Plan> leafPlans = getAllLeafs(plan);
        ArrayList<Task> leafTask =getAllLeafPasks(leafPlans);
        int planIndex = new Random().nextInt(leafTask.size());
        Task targetTask = leafTask.get(planIndex);
        this.skills = new HashSet<>();
        Set<Skill> reqSkills = targetTask.getReqSkills();
        this.skills.addAll(reqSkills);
//        this.skills = (Set<Skill>) targetTask.getReqSkills().clone();
    }

    public void initSkillsFoTask(String task) {
        Plan plan = PlanTree.get();
        ArrayList<Plan> leafPlans = getAllLeafs(plan);
        ArrayList<Task> leafTask =getAllLeafPasks(leafPlans);

        for (Task _task: leafTask) {

            if (_task.getId().equals(task)) {
                extractSkillsFromTask(_task);
            }
        }
    }

    private void extractSkillsFromTask(Task task) {
        this.skills = new HashSet<>();
        Set<Skill> reqSkills = task.getReqSkills();
        this.skills.addAll(reqSkills);
    }

    private ArrayList<Task> getAllLeafPasks(ArrayList<Plan> leafPlans) {
        ArrayList<Task> tasks = new ArrayList<>();

        for (Plan plan:leafPlans) {
            tasks.addAll(plan.getAllTasks());
        }
        return tasks;
    }

    private ArrayList<Plan> getAllLeafs(Plan plan) {
        ArrayList<Plan> subPlans = plan.getSubPlans();

        if (subPlans.isEmpty()) {
            return new ArrayList<>(Collections.singletonList(plan));
        }

        ArrayList<Plan> allLeafs = new ArrayList();

        for (Plan subPlan : subPlans) {
            allLeafs.addAll(getAllLeafs(subPlan));
        }
        return allLeafs;
    }

    public boolean hasSkill(Skill skill) {

        for (SkillUnit unit : this.units) {

            for (Skill _skill: unit.getSkills()) {

                if (!this.skills.contains(unit)) {
                       skills.add(_skill);
                }
            }
        }
        return skills.parallelStream().anyMatch(s -> s.equals(skill));
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public void addSkills(Set<Skill> skills) {

        if (this.skills == null)
            this.skills = new HashSet<>();
        this.skills.addAll(skills);
    }
    public void addSkills(Skill... skills) {

        if (this.skills == null)
            this.skills = new HashSet<>();
        Collections.addAll(this.skills, skills);
    }

    public void setUnits(Set<SkillUnit> units) {
        this.units = units;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Set<SkillUnit> getUnits() {
        return units;
    }

    public void addUnit(SkillUnit unit) {
        this.units.add(unit);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
