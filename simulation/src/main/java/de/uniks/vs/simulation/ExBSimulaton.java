package de.uniks.vs.simulation;

import de.uniks.vs.simulation.skill_unit_task_model.USTNode;
import de.uniks.vs.simulation.skill_unit_task_model.components.*;
import de.uniks.vs.simulation.skill_unit_task_model.warehouse_example.EdgeServiceNode;
import de.uniks.vs.simulation.skill_unit_task_model.warehouse_example.TransportRobotNode;
import de.uniks.vs.simulation.skill_unit_task_model.warehouse_example.UAVNode;

public class ExBSimulaton {

    private boolean running = false;
    Token token;
    TaskExecutor taskExecutor;

    private void initTaskExecutor() {
        int nextNode = token.getNextNode();
        USTNode startNode = (USTNode) token.getNodeByID(nextNode);
        this.taskExecutor = startNode.getTaskExecutor();
        this.taskExecutor.setToken(this.token);
    }

    private void initToken() {
        this.token = new Token();
    }

    private void initPlans(int wareNr,int rTeamNr, int edgeServiceNr, int uavNr, int transportNr) {
        // ---- Warehouse
        Plan warehouse = new Plan("Warehouse");
        warehouse.setNumberOfInstances(wareNr);

        Task _robotTeam = new Task("RobotTeam", warehouse);
        Skill robotTeamReqSkill1 = new Skill("Action:Wait");
        Skill robotTeamReqSkill2 = new Skill("Action:Drive");
        Skill robotTeamReqSkill3 = new Skill("Action:Fly");
        Skill robotTeamReqSkill4 = new Skill("Action:PickUp");
        Skill robotTeamReqSkill5 = new Skill("Action:PutDown");
        _robotTeam.addReqSkills(robotTeamReqSkill1, robotTeamReqSkill2 ,robotTeamReqSkill3, robotTeamReqSkill4, robotTeamReqSkill5);
        warehouse.addTask(_robotTeam);

        Task _edgeService = new Task("EdgeService", warehouse);
        Skill startReqSkill1 = new Skill("Action:Compute");
        Skill startReqSkill2 = new Skill("Action:Collect");
        _edgeService.addReqSkills(startReqSkill1, startReqSkill2);
        warehouse.addTask(_edgeService);

        token.addTasks(_robotTeam, _edgeService);

        // ---- Robot Team
        Plan robotTeam = new Plan("RobotTeam");
        robotTeam.setNumberOfInstances(rTeamNr);
        Skill transportMemberSkill1 = new Skill("Action:Wait");
        Skill transportMemberSkill2 = new Skill("Action:Drive");
        Skill uavMemberSkill1 = new Skill("Action:Fly");
        Skill uavMemberSkill2 = new Skill("Action:PickUp");
        Skill uavMemberSkill3 = new Skill("Action:PutDown");
        robotTeam.addActSkills(transportMemberSkill1, transportMemberSkill2, uavMemberSkill1, uavMemberSkill2, uavMemberSkill3);

        Task _uavMember = new Task("RunAsUAVMember", robotTeam);
        _uavMember.addReqSkills(uavMemberSkill1, uavMemberSkill2, uavMemberSkill3);

        Task _tranportMember = new Task("RunAsTransportMember", robotTeam);
        _tranportMember.addReqSkills(transportMemberSkill1, transportMemberSkill2);
        robotTeam.addTasks(_uavMember, _tranportMember);

        token.addTasks(_uavMember, _tranportMember);

        // ---- Edge Service
        Plan edgeService = new Plan("Service");
        edgeService.setNumberOfInstances(edgeServiceNr);
        Skill edgeActSkill1 = new Skill("Action:Collect");
        Skill edgeActSkill2 = new Skill("Action:Compute");
        edgeService.addActSkills(edgeActSkill1, edgeActSkill2);

        Task _runAsEdge = new Task("RunAsEdge", edgeService);
        Skill edgeReqSkill1 = new Skill("Action:Store");
        Skill edgeReqSkill2 = new Skill("Action:Load");
        _runAsEdge.addReqSkills(edgeReqSkill1, edgeReqSkill2);
        edgeService.addTask(_runAsEdge);

        token.addTasks(_runAsEdge);

        // ---- UAV
        Plan uav = new Plan("UAV");
        uav.setNumberOfInstances(uavNr);
        Skill uavActSkill1 = new Skill("Action:Fly");
        Skill uavActSkill2 = new Skill("Action:PickUp");
        Skill uavActSkill3 = new Skill("Action:PutDown");
        uav.addActSkills(uavActSkill1, uavActSkill2, uavActSkill3);

        Task _runAsUAV = new Task("RunAsUAV", uav);
        Skill uavReqSkill1 = new Skill("Action:Up");
        Skill uavReqSkill2 = new Skill("Action:Down");
        Skill uavReqSkill3 = new Skill("Action:Stop");
        _runAsUAV.addReqSkills(uavReqSkill1, uavReqSkill2, uavReqSkill3);
        uav.addTask(_runAsUAV);

        token.addTasks(_runAsUAV);

        // ---- Transport
        Plan transport = new Plan("Transport");
        transport.setNumberOfInstances(transportNr);
        Skill transportActSkill1 = new Skill("Action:Wait");
        Skill transportActSkill2 = new Skill("Action:Drive");
        transport.addActSkills(transportActSkill1, transportActSkill2);

        Task _runAsTransport = new Task("RunAsTransport", transport);
        Skill transportReqSkill1 = new Skill("Action:Stop");
        Skill transportReqSkill2 = new Skill("Action:Forward");
        Skill transportReqSkill3 = new Skill("Action:Backward");
        Skill transportReqSkill4 = new Skill("Action:Left");
        Skill transportReqSkill5 = new Skill("Action:Right");
        _runAsTransport.addReqSkills(transportReqSkill1, transportReqSkill2, transportReqSkill3, transportReqSkill4, transportReqSkill5);
        transport.addTask(_runAsTransport);

        token.addTasks(_runAsTransport);
    }

    private void initUnits(int edgeNr, int uavNr, int transportNr) {

        for (int i = 0; i < edgeNr; i++) {
            SkillUnit edgeUnit = new SkillUnit();
            edgeUnit.setId("EdgeUnit");
            Skill edgeSkill1 = new Skill("Action:Store");
            Skill edgeSkill2 = new Skill("Action:Load");
            edgeUnit.addSkills(edgeSkill1, edgeSkill2);
            EdgeServiceNode edgeServiceNode = new EdgeServiceNode(edgeUnit, 0, 0, new TaskExecutor());
            token.addNode(edgeServiceNode);
        }

        for (int i = 0; i < uavNr; i++) {
            SkillUnit uavUnit = new SkillUnit();
            uavUnit.setId("UAVUnit");
            Skill uavSkill1 = new Skill("Action:Up");
            Skill uavSkill2 = new Skill("Action:Down");
            Skill uavSkill3 = new Skill("Action:Stop");
            uavUnit.addSkills(uavSkill1, uavSkill2, uavSkill3);
            UAVNode uavNode = new UAVNode(uavUnit, 0, 0, new TaskExecutor());
            token.addNode(uavNode);
        }

        for (int i = 0; i < transportNr; i++) {
            SkillUnit transportUnit = new SkillUnit();
            transportUnit.setId("TransportUnit");
            Skill transportSkill1 = new Skill("Action:Stop");
            Skill transportSkill2 = new Skill("Action:Forward");
            Skill transportSkill3 = new Skill("Action:Backward");
            Skill transportSkill4 = new Skill("Action:Left");
            Skill transportSkill5 = new Skill("Action:Right");
            transportUnit.addSkills(transportSkill1, transportSkill2, transportSkill3, transportSkill4, transportSkill5);
            TransportRobotNode transportRobotNode = new TransportRobotNode(transportUnit, 0, 0, new TaskExecutor());
            token.addNode(transportRobotNode);
        }
    }

    private void run() {
        this.running = true;
        int step = 0;

        printStatus();

        while(running) {
            if (this.taskExecutor != null) {
                this.taskExecutor.step();
                this.taskExecutor = this.taskExecutor.getNext();
            }
            printStatus();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printStatus() {
        System.out.println("\n============  Status  ===========");
        System.out.print("Task Set (" +this.token.getTasks().size()+ "):");
//        this.token.getTasks().stream().forEach(task -> System.out.print(task.getId()+ ", "));
        this.token.getAvailableTasks().stream().forEach(task -> System.out.print(task.getId()+ ", "));
        System.out.println();
        System.out.print("             ");
        this.token.getTasks().forEach(task -> {if (task.getFreeInstances() > 0 && task.getUnit() == null) System.out.print(task.getPlan().getId()+ "("+ task.getId()  +":"+ task.getFreeInstances() +"), ");});
        System.out.println();
        System.out.println("----------------------------------");
        System.out.print("Unit Pool (" +this.token.getNodes().size()+ "):");
        this.token.getNodes().stream().forEach(node -> System.out.print(((USTNode)node).getUnit().getId() + node.getID() +", "));
        System.out.println();
        System.out.println("==============  END  ============\n");
    }

    public static void main(String[] args) {
        ExBSimulaton simulaton = new ExBSimulaton();
        simulaton.initToken();
        simulaton.initPlans(0,0,1,1,0);
        simulaton.initUnits(1,1,0);
        simulaton.initTaskExecutor();

        simulaton.run();
    }
}
