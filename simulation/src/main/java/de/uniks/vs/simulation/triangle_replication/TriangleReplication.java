package de.uniks.vs.simulation.triangle_replication;

import de.uniks.vs.simulation.triangle_replication.model.Subnet;
import de.uniks.vs.simulation.triangle_replication.model.TriangleReplicationNode;
import de.uniks.vs.simulator.model.Node;

import java.util.*;

public class TriangleReplication {
    private TriangleReplicationNode node;

    public TriangleReplication(TriangleReplicationNode node) {
        this.node = node;
    }

    public void add(){
        System.out.println("\nHA: ========================================================");
        System.out.println("HA: === 1 === create_new_node");
        int _id = node.getID();
        this.node.setText(String.valueOf(this.node.getID()));
        System.out.println("HA:   node id "+ _id);
        // %   -if first node then create subnet and return
        if (this.node.getSimulation().getAllNodes().size() == 1) {
            this.node.addSubnet(new Subnet());
            return;
        }
        // %   -search for at least one but no more than two neighbouring nodes from set of nodes which connectivity less than four
        System.out.println("HA: === 2 === get_node_less_then_connections");
        LinkedList<Integer> _target_nodes = this.getNodeLessThenConnections(_id, 3);
        System.out.println("HA:   target node size " +_target_nodes.size());
        // %   -selects the nodes with the lowest connectivity
        System.out.println("HA: === 3 === get_nodes_with_lowest_connectivity");
        _target_nodes = this.getNodesWithLowestConnectivity(_id);
        System.out.println("HA:   target nodes " + _target_nodes);
        // %   -If two selected nodes are in the same subnet they are preferred
        HashMap<Integer,LinkedList<Integer>> node_groups = this.groupNodes(_target_nodes);
        System.out.println("HA:    all nodes:" +  this.getAllNodes().keySet());
        for (Map.Entry entry : node_groups.entrySet()) {
            System.out.println("HA:      Subnet id:" + entry.getKey() + "  nodes: " + entry.getValue().toString());
        }
        if (node_groups.size() > 1) System.out.println("HA:      multiple sub groups " + node_groups.size());
        // %   -If subnet is not completely occupied and at least one is already receiving data from another
        Map.Entry<Integer, LinkedList<Integer>> entry = node_groups.entrySet().iterator().next(); // TODO: select best subnet match
        if (entry.getValue().size() < 3) {
            // %   -Then data of new node is merged with that of the incomplete subnet
            _target_nodes = getTargetNodes(_target_nodes, node_groups, entry);
            System.out.println("HA: === 4 === connect when subnet has free places " + _target_nodes);
            this.connect(_id, _target_nodes);
        } else {
            // %   -Else selected node(s) must not be in the same subnet
            _target_nodes.clear();
            _target_nodes.add(entry.getValue().getLast());
            System.out.println("HA: === 4 === connect when subnet is complete " + _target_nodes);
            this.connect(_id, _target_nodes);
        }
        // %   -result: subnet in triangle topology with maximum connectivity

        // %   -data of joining node  transmitted to  newly connected nodes

        // let _id  = self.nodes.try_lock().unwrap().back().unwrap().id;

        // println!(".... addNewNode {} ...", _id);

        // println!("{}", self.nodes.lock().unwrap().len());

    }

    private LinkedList<Integer> getTargetNodes(LinkedList<Integer> _target_nodes, HashMap<Integer, LinkedList<Integer>> node_groups, Map.Entry<Integer, LinkedList<Integer>> entry) {
        if (entry.getValue().size() == 1) {
            System.out.println("HA: === 4 === set if size = 1  " + this.getSubnet(entry.getValue().getFirst())); // TODO: select all nodes for one subnet
            _target_nodes = this.getSubnet(entry.getValue().getFirst());
        }
        else {
            if (node_groups.size() > 1) {
                System.out.println("HA: === 4 === set " + entry.getValue());
                Iterator<LinkedList<Integer>> iterator = node_groups.values().iterator();
                _target_nodes.clear();
                _target_nodes.add(iterator.next().get(0));
                _target_nodes.add(iterator.next().get(0));
            }
            else {
                System.out.println("HA: === 4 === set " + entry.getValue());
                _target_nodes = entry.getValue();
            }
        }
        return _target_nodes;
    }

    private LinkedList<Integer> getSubnet(Integer target) {
        LinkedList<Integer> subnet = new LinkedList<>();
        TriangleReplicationNode node1 = this.getNode(target);
        subnet.add(target);

        for (Integer neighbor : node1.getNeighbors()) {
            subnet.add(neighbor);
        }
        return subnet;
    }

    private HashMap<Integer, LinkedList<Integer>> groupNodes(LinkedList<Integer> nodes) {
        LinkedList<Integer> _nodes = new LinkedList<>(nodes);
        HashMap<Integer, LinkedList<Integer>> groups = new HashMap<>();
        HashMap<Integer, Node> all_nodes = this.getAllNodes();

        for (int node_id: nodes) {
            TriangleReplicationNode node = (TriangleReplicationNode) all_nodes.get(node_id);
            Set<Integer> _subnets = node.getSubnets().keySet();

            for (int subnet_id: _subnets) {

                if(groups.containsKey(subnet_id)) {
                    groups.get(subnet_id).add(node_id);
                } else {
                    groups.put(subnet_id, new LinkedList<>(List.of(node_id)));
                }
            }
        }
        return groups;
    }

    private void connect(int id, LinkedList<Integer> target_nodes) {

        for (int _id : target_nodes ) {
            this.connect_2_nodes(id, _id);
        }
        updateSubnet(id, target_nodes);
    }

    private void updateSubnet(int node_id, LinkedList<Integer> target_nodes) {
        TriangleReplicationNode node = this.getNode(node_id);

        Subnet subnet = getSubnet(target_nodes);

        if (this.node.getSimulation().getNodeCount() > 2 && target_nodes.size() == 1) {
            this.getNode(node_id).addSubnet(subnet);
            this.getNode(target_nodes.getFirst()).addSubnet(subnet);
            return;
        }
        this.getNode(node_id).addSubnet(subnet);
    }

    private Subnet getSubnet(LinkedList<Integer> target_nodes) {

        for (int target_node_id : target_nodes) {
            TriangleReplicationNode target_node = this.getNode(target_node_id);

            for (Map.Entry<Integer, Subnet> entry : target_node.getSubnets().entrySet()) {

                if (entry.getValue().members.size() < 3) {
                    return entry.getValue();
                }
            }
        }
        return new Subnet();
    }

    private void connect_2_nodes(int node1, int node2) {
        Node _node1 = getNode(node1);
        Node _node2 = getNode(node2);
        this.node.getSimulation().connectNodes(_node1,_node2);
    }

    private TriangleReplicationNode getNode(int node) {
        return (TriangleReplicationNode) this.getAllNodes().get(node);
    }

    private LinkedList<Integer> getNodesWithLowestConnectivity(int id) {
        LinkedList<Integer> _result = new LinkedList();
        System.out.println("NET: nodes with lowest connection");
        for( int i = 0; i < 3; i++) {
            LinkedList<Integer> _tmp = this.getNodeLessThenConnections(id, i);
            if (!_tmp.isEmpty()) {
                _result.addAll(_tmp);
                System.out.println("NET:   lowest nodes size " + _result.size());
                break;
            }
        }
        System.out.println("NET: --");
        return _result;
    }

    private LinkedList<Integer> getNodeLessThenConnections(int id, int max_connectivity) {
        LinkedList<Integer> _result = new LinkedList<>();
        HashMap<Integer, Node> _nodes = this.getAllNodes();
        System.out.println("NET: nodes with less then "+max_connectivity+" connection" );

        for(Map.Entry<Integer, Node> _node : _nodes.entrySet()) {
            System.out.println("NET:   "+id+" != "+_node.getKey()+" "+_node.getValue().getConnections().size()+" <= "+max_connectivity);

            if (_node.getKey() != id && _node.getValue().getConnections().size() <= max_connectivity) {
                _result.add(_node.getKey());
                System.out.println("      ("+_node.getKey()+" "+_node.getValue().getConnections().size()+")");
            }
        }
        System.out.println("NET:   nodes size " + _result.size());
        return _result;
    }

    private HashMap<Integer, Node> getAllNodes() {
        HashMap<Integer, Node> allNodes = new HashMap<>();
        node.getSimulation().getAllNodes().forEach((k) -> allNodes.put(k.getID(), k));
        return allNodes;
    }
}
