package de.uniks.vs.simulation.skill_unit_task_model.warehouse_example;

import de.uniks.vs.simulation.skill_unit_task_model.components.Plan;
import de.uniks.vs.simulation.skill_unit_task_model.components.Skill;
import de.uniks.vs.simulation.skill_unit_task_model.components.Task;
import javafx.scene.paint.Color;

public class PlanTree {

    static Plan plan;
    static boolean called = false;

    public static boolean isInitalised() {
        return plan == null? false: true;
    }

    public static boolean isCalled() {

        if (called)
            return true;
        called = true;
        return false;
    }

    public static Plan get() {
        if (plan == null)
            plan = create();
        return plan;
    }

    public static Plan create() {

        Plan servicePlan = new Plan("Services")
                .addTask(new Task("EdgeService", Color.BLUE)
                        .addReqSkill(new Skill("hasService"))
                        .addReqSkill(new Skill("workAsNavigator"))
                )
                .addTask(new Task("KnowledgeService", Color.BLUE)
                        .addReqSkill(new Skill("hasASP"))
                        .addReqSkill(new Skill("workAsKnowledgeBase"))
                );

        Plan robotPlan = new Plan("Robots")
                .addTask(new Task("TransportRobot", Color.GREEN)
                        .addReqSkill(new Skill("hasWheels"))
                        .addReqSkill(new Skill("canTransport"))
                )
                .addTask(new Task("UAV", Color.GREEN)
                        .addReqSkill(new Skill("hasPropellor"))
                        .addReqSkill(new Skill("canFly"))
                );

        Plan warehousePlan = new Plan("Warehouse")
                .addTask(new Task("MainTask", Color.RED)
                        .addReqSkill(new Skill("hasService"))
                        .addReqSkill(new Skill("workAsNavigator"))
                        .addReqSkill(new Skill("hasASP"))
                        .addReqSkill(new Skill("workAsKnowledgeBase"))
                        .addReqSkill(new Skill("hasWheels"))
                        .addReqSkill(new Skill("canTransport"))
                        .addReqSkill(new Skill("hasPropellor"))
                        .addReqSkill(new Skill("canFly"))
                )
                .addSubPlan(servicePlan)
                .addSubPlan(robotPlan);

        return warehousePlan;
    }
}
