package de.uniks.vs.simulation.triangle_replication;

import de.uniks.vs.simulation.triangle_replication.model.SimpleTriangleReplicationNode;
import de.uniks.vs.simulation.triangle_replication.model.SimpleSubnet;
import de.uniks.vs.simulator.model.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class SimpleTriangleReplication {
    private SimpleTriangleReplicationNode node;

    public SimpleTriangleReplication(SimpleTriangleReplicationNode node) {
        this.node = node;
    }

    String statistics;
    public void add() {
        statistics = "";
//        System.out.println("\nHA: ========================================================");
//        System.out.println("HA: === 1 === create_new_node");
        long start = System.nanoTime();
        int _id = node.getID();
        this.node.setText(String.valueOf(this.node.getID()));
//        System.out.println("HA:   node id " + _id);
        // %   -if first node then create subnet and return
        if (this.node.getSimulation().getAllNodes().size() == 1) {
            this.node.addSubnet(new SimpleSubnet());
            long end = System.nanoTime();
            float duration = end - start;
            this.printStatistics(_id, duration);
            return;
        }
        // %   -search for at least one but no more than two neighbouring nodes from set of nodes which connectivity less than four
//        System.out.println("HA: === 2 === get_node_less_then_connections");
        LinkedList<Integer> _target_nodes = this.getNodeLessThenConnections(_id, 3);
//        System.out.println("HA:   target node size " + _target_nodes.size());
        // %   -selects the nodes with the lowest connectivity
//        System.out.println("HA: === 3 === get_nodes_with_lowest_connectivity");
        _target_nodes = this.getNodesWithLowestConnectivity(_id);
//        System.out.println("HA:   target nodes " + _target_nodes);
        statistics += _target_nodes.size();
        // %   -If two selected nodes are in the same subnet they are preferred
        HashMap<Integer, LinkedList<Integer>> node_groups = this.groupNodes(_target_nodes);
//        System.out.println("HA:    all nodes:" + this.getAllNodes().keySet());
//        for (Map.Entry entry : node_groups.entrySet()) {
//            System.out.println("HA:      Subnet id:" + entry.getKey() + "  nodes: " + entry.getValue().toString());
//        }
//        if (node_groups.size() > 1) System.out.println("HA:      multiple sub groups " + node_groups.size());
        // %   -If subnet is not completely occupied and at least one is already receiving data from another
        Map.Entry<Integer, LinkedList<Integer>> entry = node_groups.entrySet().iterator().next();
        if (entry.getValue().size() < 3) {
            // %   -Then data of new node is merged with that of the incomplete subnet
            _target_nodes = getTargetNodes(_target_nodes, node_groups, entry);
//            System.out.println("HA: === 4 === connect when subnet has free places " + _target_nodes);
            this.connect(_id, _target_nodes);
        } else {
            // %   -Else selected node(s) must not be in the same subnet
            _target_nodes.clear();
            _target_nodes.add(entry.getValue().getFirst());
//            System.out.println("HA: === 4 === connect when subnet is complete " + _target_nodes);
            this.connect(_id, _target_nodes);
        }
        // %   -result: subnet in triangle topology with maximum connectivity

        // %   -data of joining node  transmitted to  newly connected nodes

        // let _id  = self.nodes.try_lock().unwrap().back().unwrap().id;

        // println!(".... addNewNode {} ...", _id);

        // println!("{}", self.nodes.lock().unwrap().len());
        this.node.updateInfo();
        long end = System.nanoTime();
        if ( (_id < 100 && (_id % 10 == 0) )
                || (_id > 99 && _id < 1000 && _id % 100 == 0)
                || (_id > 999 && _id % 1000 == 0) ) {
            float duration = end - start;
            this.printStatistics(_id, duration);
        }
    }

    private void printStatistics(int id, float duration) {
        duration =duration/1000000;
        HashMap<Integer,Integer>  member_per_subnet = new HashMap<>();
        HashMap<Integer,Integer> subnets_per_member= new HashMap<>();

        HashMap<Integer, SimpleSubnet> subnets = SimpleSubnet.subnets;
        for (SimpleSubnet subnet : subnets.values()) {
            int member_size = subnet.getMembers().size();
            if(member_per_subnet.containsKey(member_size)) {
                int count = member_per_subnet.get(member_size);
                member_per_subnet.put(member_size,(count+1));
            } else {
                member_per_subnet.put(member_size,1);
            }
        }
        for (Node node : this.getAllNodes().values()) {
            if (!(node instanceof SimpleTriangleReplicationNode))
                continue;
            SimpleTriangleReplicationNode t_node = (SimpleTriangleReplicationNode) node;
            int subnet_count = t_node.getSubnets().size();
            if(subnets_per_member.containsKey(subnet_count)) {
                int count = subnets_per_member.get(subnet_count);
                subnets_per_member.put(subnet_count,(count+1));
            } else {
                subnets_per_member.put(subnet_count,1);
            }
        }
        float mps_1 = member_per_subnet.getOrDefault(1, 0);
        float mps_2 = member_per_subnet.getOrDefault(2, 0);
        float mps_3 = member_per_subnet.getOrDefault(3, 0);
        double m = mps_1/3/this.getAllNodes().size() + mps_2/2/this.getAllNodes().size() + mps_3/this.getAllNodes().size();
        m = mps_1/3/subnets.size() + mps_2/2/subnets.size() + mps_3/subnets.size();
        String statistics = id + "; " + mps_1 + "; " +mps_2 + "; " +mps_3 + "; " + this.statistics;
        statistics += "; " + this.getAllNodes().size()
                + "; " + getListEntries(member_per_subnet,3)
                + getListEntries(subnets_per_member,2) + m + "; "+ duration + "\n";

        Path filePath = Path.of("statistics_simple_100000.txt");
        System.out.println(statistics);

        try(FileWriter fileWriter = new FileWriter(filePath.toFile(),true)){
            fileWriter.write(statistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getListEntries(HashMap<Integer, Integer> values, int count) {
        String result = "";
        for (int i = 1; i <= count; i++) {
            result += values.get(i) + "; ";
        }
        return result;
    }

    private LinkedList<Integer> getTargetNodes(LinkedList<Integer> _target_nodes, HashMap<Integer, LinkedList<Integer>> node_groups, Map.Entry<Integer, LinkedList<Integer>> entry) {
        if (entry.getValue().size() == 1) {
//            System.out.println("HA: === 4 === set if size = 1  " + this.getSubnetforNode(entry.getValue().getFirst())); // TODO: select all nodes for one subnet
            _target_nodes = this.getSubnetforNode(entry.getValue().getFirst());
            if (_target_nodes.size() > 2) {
                _target_nodes = entry.getValue();
            }
        } else {
            if (node_groups.size() > 1) {
//                System.out.println("HA: === 4 === set " + entry.getValue());
                Iterator<LinkedList<Integer>> iterator = node_groups.values().iterator();
                _target_nodes.clear();
                _target_nodes.add(iterator.next().get(0));
                if (this.getSubnet(entry.getKey()).getMembers().size()<3) {
                    _target_nodes.add(iterator.next().get(0));
                }
            } else {
//                System.out.println("HA: === 4 === set " + entry.getValue());
                _target_nodes = entry.getValue();
            }
        }
        return _target_nodes;
    }

    private SimpleSubnet getSubnet(Integer subnet_id) {
        return SimpleSubnet.subnets.get(subnet_id);
    }

    private LinkedList<Integer> getSubnetforNode(int node_id) {
        LinkedList<Integer> subnet = new LinkedList<>();
        SimpleTriangleReplicationNode node1 = this.getNode(node_id);
        subnet.add(node_id);

        for (Integer neighbor : node1.getNeighbors()) {
            subnet.add(neighbor);
        }
        return subnet;
    }

    private HashMap<Integer, LinkedList<Integer>> groupNodes(LinkedList<Integer> nodes) {
        LinkedList<Integer> _nodes = new LinkedList<>(nodes);
        HashMap<Integer, LinkedList<Integer>> groups = new HashMap<>();
        HashMap<Integer, Node> all_nodes = this.getAllNodes();

        for (int node_id : nodes) {
            if (!(all_nodes.get(node_id) instanceof SimpleTriangleReplicationNode))
                continue;
            SimpleTriangleReplicationNode node = (SimpleTriangleReplicationNode) all_nodes.get(node_id);
            Set<Integer> _subnets = node.getSubnets().keySet();

            for (int subnet_id : _subnets) {

                if (groups.containsKey(subnet_id)) {
                    groups.get(subnet_id).add(node_id);
                } else {
                    groups.put(subnet_id, new LinkedList<>(List.of(node_id)));
                }
            }
        }
        return groups;
    }

    private void connect(int id, LinkedList<Integer> target_nodes) {

        for (int _id : target_nodes) {
            this.connect_2_nodes(id, _id);
        }
        updateSubnet(id, target_nodes);
    }

    private void updateSubnet(int node_id, LinkedList<Integer> target_nodes) {
        SimpleTriangleReplicationNode node = this.getNode(node_id);

        SimpleSubnet subnet = getSubnetforNode(target_nodes);

        if (this.node.getSimulation().getNodeCount() > 2 && target_nodes.size() == 1) {
            this.getNode(node_id).addSubnet(subnet);
            this.getNode(target_nodes.getFirst()).addSubnet(subnet);
            return;
        }
        this.getNode(node_id).addSubnet(subnet);

        for (int id :subnet.getMembers()) {
            this.getNode(id).updateInfo();
        }
    }

    private SimpleSubnet getSubnetforNode(LinkedList<Integer> target_nodes) {

        for (int target_node_id : target_nodes) {
            if (!(this.getNode(target_node_id) instanceof SimpleTriangleReplicationNode))
                continue;
            SimpleTriangleReplicationNode target_node = this.getNode(target_node_id);

            for (Map.Entry<Integer, SimpleSubnet> entry : target_node.getSubnets().entrySet()) {

                if (entry.getValue().members.size() < 3) {
                    return entry.getValue();
                }
            }
        }
        return new SimpleSubnet();
    }

    private void connect_2_nodes(int node1, int node2) {
        Node _node1 = getNode(node1);
        Node _node2 = getNode(node2);
        this.node.getSimulation().connectNodes(_node1, _node2);
    }

    private SimpleTriangleReplicationNode getNode(int node_id) {
        return (SimpleTriangleReplicationNode) this.getAllNodes().get(node_id);
    }

    private LinkedList<Integer> getNodesWithLowestConnectivity(int id) {
        LinkedList<Integer> _result = new LinkedList();
//        System.out.println("NET: nodes with lowest connection");
        for (int i = 0; i < 3; i++) {
            LinkedList<Integer> _tmp = this.getNodeLessThenConnections(id, i);
            if (!_tmp.isEmpty()) {
                _result.addAll(_tmp);
//                System.out.println("NET:   lowest nodes size " + _result.size());
                break;
            }
        }
//        System.out.println("NET: --");
        return _result;
    }

    private LinkedList<Integer> getNodeLessThenConnections(int id, int max_connectivity) {
        LinkedList<Integer> _result = new LinkedList<>();
        HashMap<Integer, Node> _nodes = this.getAllNodes();
//        System.out.println("NET: nodes with less then " + max_connectivity + " connection");

        for (Map.Entry<Integer, Node> _node : _nodes.entrySet()) {
//            System.out.println("NET:   " + id + " != " + _node.getKey() + " " + _node.getValue().getConnections().size() + " <= " + max_connectivity);

            if (_node.getKey() != id && _node.getValue().getConnections().size() <= max_connectivity) {
                _result.add(_node.getKey());
//                System.out.println("      (" + _node.getKey() + " " + _node.getValue().getConnections().size() + ")");
            }
        }
//        System.out.println("NET:   nodes size " + _result.size());
        return _result;
    }

    private HashMap<Integer, Node> getAllNodes() {
        HashMap<Integer, Node> allNodes = new HashMap<>();
        node.getSimulation().getAllNodes().forEach((k) -> allNodes.put(k.getID(), k));
        return allNodes;
    }
}
