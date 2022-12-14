package regalloc;

import gen.asm.*;
import gen.asm.AssemblyItem.*;
import gen.asm.AssemblyItem.Instruction.*;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;


public class ControlFlowGraph{

    private static class Node{
        public Node(AssemblyItem.Instruction instruction){
            this.instruction = instruction;
            this.incomingEdges = new LinkedList<>();
            this.outgoingEdges = new LinkedList<>();
            this.liveIn = new ArrayList<>();
            this.liveOut = new ArrayList<>();

            this.name = "I"+nameNum++;
        }

        public List<Node> outgoingEdges;
        public List<Node> incomingEdges;

        public final String name;

        public final AssemblyItem.Instruction instruction;

        public ArrayList<Register> liveIn;
        public ArrayList<Register> liveOut;

        private static int nameNum = 1;

        public String dotHeader(){
            return name +" [label=\""+instruction.toString()+"\"];\n";
        }

        public String dotEdges(){
            String ret = "";
            for(Node node: outgoingEdges){
                ret += name + " -> " + node.name + ";\n";
            }
            return ret;
        }
    }

    public String toDot(){
        String ret="digraph CFG{\n";
        for(Node node: nodes){
            ret += node.dotHeader();
        }
        ret +="\n";
        for(Node node: nodes){
            ret += node.dotEdges();
        }

        ret +="}";
        return ret;
    }

    private final List<Node> nodes;

    private ControlFlowGraph(List<Node> nodes){ //only generated by generateControlFlowGraph
        this.nodes = nodes;
    }

    public static ControlFlowGraph generateControlFlowGraph(AssemblyProgram.Section textSection){
        if(textSection.type != AssemblyProgram.Section.Type.TEXT){
            throw new IllegalArgumentException("Can't generate CFG from data section.");
        }

        //Graph to be returned
        List<Node> nodes = new LinkedList<Node>();

        //Structures to help building the graph
        Map<Label, Node> labelMap = new HashMap<Label, Node>();   //So a jump knows where to jump to
        Node lastNode = null;                                   //previous node to link to next
        List<Label> pendingLables = new LinkedList<>();        //labels not yet assigned to an instruction
        Map<Label, Node> pendingJumps = new HashMap<>();       //jumps to labels defined after them. 
                                                                //In this case, node is the jumping node

        for(AssemblyItem i: textSection.items){
            if(i instanceof Label){
                pendingLables.add(
                        ((Label) i)
                    );
            }
            if(i instanceof Instruction){
                //generate node
                Node node = new Node((Instruction)i);

                //link previous node
                if(lastNode != null){
                    lastNode.outgoingEdges.add(node);
                    node.incomingEdges.add(lastNode);
                }

                //setup as next previous node
                lastNode = node;

                for(Label s: pendingLables){
                    //link labels
                    labelMap.put(s, node);

                    //handle pending jumps
                    Node jumpingHereNode = pendingJumps.get(s);
                    if(jumpingHereNode != null){
                        jumpingHereNode.outgoingEdges.add(node);
                        node.incomingEdges.add(jumpingHereNode);
                        pendingJumps.remove(s);
                    }
                }
                

                //clear pending labels
                pendingLables.clear();

                //handle jumps
                Label target = null;

                if(i instanceof Jump){
                    Jump jump = (Jump)i;
                    if(jump.opcode != "jal"){ //jal is function call
                        target = jump.label;
                        lastNode = null; //unconditional jump; next instruction never follows
                    }
                }

                if(i instanceof Jr){
                    //only jr $ra is generated
                    lastNode = null; //unconditional...
                }

                if(i instanceof Branch){
                    //any branch 
                    target = ((Branch)i).label;

                    //conditional; next instruction can follow
                }

                if(target != null){
                    Node targetNode = labelMap.get(target);

                    if(targetNode != null){
                        node.outgoingEdges.add(targetNode);
                        targetNode.incomingEdges.add(node);
                    }else{
                        pendingJumps.put(target, node);
                    }
                }

                //append node
                nodes.add(node);
            }
        }

        ControlFlowGraph cfg =  new ControlFlowGraph(nodes);
        //System.out.println(cfg.toDot());
        return cfg;
    }

    //Do liveness analysis on this CFG
    private void analyseLiveness(){
        //CFG should already have empty liveIn and liveOut sets, as generated by the node constructor

        //initialize the liveIns to the registers the nodes use
        for(Node node: nodes){
            for(Register reg: node.instruction.uses()){
                if(reg.isVirtual())
                    node.liveIn.add(reg);
            }
        }

        boolean done = true;

        do{
            done = true;
            for(Node node: nodes){
                //algoridm

                //liveOut = union of succ liveIn
                for(Node succ: node.outgoingEdges){
                    for(Register reg: succ.liveIn){
                        if(!node.liveOut.contains(reg)){
                            done = false;
                            node.liveOut.add(reg);
                        }
                    }
                }

                //liveIn = use union (out - def)
                //use should already be there
                for(Register reg: node.liveOut){
                    if((
                            node.instruction.def() == null 
                            || !node.instruction.def().equals(reg)) 
                        && !node.liveIn.contains(reg)){
                        done = false;
                        node.liveIn.add(reg);
                    }
                }

            }
        }while(!done);
    }

    private static void addMutualEdge(LivenessGraph.Node n1, LivenessGraph.Node n2){
        if(!n1.edges.contains(n2)){
            n1.edges.add(n2);
        }
        if(!n2.edges.contains(n1)){
            n2.edges.add(n1);
        }
    }

    public LivenessGraph generateLivenessGraph(){
        analyseLiveness();

        Map<Register, LivenessGraph.Node> nodeMap = new HashMap<>();

        for(Node node: nodes){
            for(Register reg: node.liveIn){

                //get liveness graph node for this register
                LivenessGraph.Node regNode = nodeMap.get(reg);
                if(regNode == null){
                    regNode = new LivenessGraph.Node(reg);
                    nodeMap.put(reg, regNode);
                }

                for(Register regPrime: node.liveIn){
                    if(regPrime == reg){
                        break;
                    }

                    //all regPrimes here should already be in the nodemap
                    addMutualEdge(regNode, nodeMap.get(regPrime)); 

                }
            }

            for(Register reg: node.liveOut){

                //get liveness graph node for this register
                LivenessGraph.Node regNode = nodeMap.get(reg);
                if(regNode == null){
                    regNode = new LivenessGraph.Node(reg);
                    nodeMap.put(reg, regNode);
                }

                for(Register regPrime: node.liveOut){
                    if(regPrime == reg){
                        break;
                    }

                    //all regPrimes here should already be in the nodemap
                    addMutualEdge(regNode, nodeMap.get(regPrime)); 

                }
            }
        }

        LivenessGraph lg = new LivenessGraph(new ArrayList<>(nodeMap.values()));
        //System.out.println(lg.toDot());
        return lg;
    }
}