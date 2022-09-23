package de.uniks.vs.simulation.skill_unit_task_model.warehouse_example;

import de.uniks.vs.simulation.skill_unit_task_model.USTNode;
import de.uniks.vs.simulation.skill_unit_task_model.components.SkillUnit;
import de.uniks.vs.simulation.skill_unit_task_model.components.TaskExecutor;

public class UAVNode extends USTNode {

    public UAVNode( int x, int y) {
        super(new SkillUnit(), x, y);
        this.unit.initSkillsFoTask("UAV");
    }

    public UAVNode(SkillUnit unit, int x, int y, TaskExecutor taskExecutor) {
        super(unit, x, y, taskExecutor);
    }

    public UAVNode(SkillUnit unit, int x, int y) {
        super(unit, x, y);
    }
}
