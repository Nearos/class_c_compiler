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
import java.util.Collections;

public class LivenessGraph{
    public static class Node{
        public Node(Register register){
            this.register = register;
            this.edges = new ArrayList<>();
        }

        public final Register register;
        public ArrayList<Node> edges;
    }

    private final List<Node> nodes;

    public LivenessGraph(List<Node> nodes){
        this.nodes = nodes;
    }

    public static class RegisterMap{
        //virtual registers allocated to real registers
        private final Map<Register, Register> registerAllocation;
        //any other encountered virtual register is spilled

        //hardware registers to spill into
        private final List<Register> spill;

        //labels to map spilled registers to
        private final Map<Register, Label> spilledLabels;

        private final List<Register> toBeSaved;

        

        public RegisterMap( Map<Register, Register> registerAllocation, 
                            List<Register> spill,
                            Map<Register, Label> spilledLabels){
            this.registerAllocation = registerAllocation;
            this.spill = spill;
            this.spilledLabels = spilledLabels;

            this.toBeSaved = new ArrayList<>();

            toBeSaved.addAll(this.spill);
            for(Register reg: this.registerAllocation.values()){
                if(!toBeSaved.contains(reg))
                    toBeSaved.add(reg);
            }


        }

        public AssemblyProgram.Section generateDataSection(){
            AssemblyProgram.Section ret = new AssemblyProgram.Section(AssemblyProgram.Section.Type.DATA);
            for(Label label: spilledLabels.values()){
                ret.emit(label);
                ret.emit(new AssemblyItem.Directive.Space(4));
            }

            return ret;
        }

        public List<Instruction> genPushRegisters(){
            List<Instruction> ret = new LinkedList<>();

            Register tempRegister = spill.get(0);
            for(Label label: spilledLabels.values()){
                // load content of memory at label into register
                ret.add(new LA(tempRegister, label));
                ret.add(new Load("lw", tempRegister, tempRegister, 0));

                // push register onto stack
                ret.add(new IInstruction("addi", Register.Arch.sp, Register.Arch.sp, -4));
                ret.add(new Store("sw", tempRegister, Register.Arch.sp, 0));
            }

            for(Register reg: toBeSaved){
                // push register onto stack
                ret.add(new IInstruction("addi", Register.Arch.sp, Register.Arch.sp, -4));
                ret.add(new Store("sw", reg, Register.Arch.sp, 0));
            }
            return ret;
        }

        public List<Instruction> genPopRegisters(){
            List<Instruction> ret = new LinkedList<>();

            Collections.reverse(toBeSaved);
            for(Register reg: toBeSaved){
                // pop register from stack
                ret.add(new Load("lw", reg, Register.Arch.sp, 0));
                ret.add(new IInstruction("addi", Register.Arch.sp, Register.Arch.sp, 4));

            }
            
            Collections.reverse(toBeSaved);

            List<Label> spilled = new ArrayList<>(spilledLabels.values());
            Collections.reverse(spilled);

            Register temp1 = spill.get(0);
            Register temp2 = spill.get(1);

            for(Label label: spilled){
                // pop from stack into $t0
                ret.add(new Load("lw", temp1, Register.Arch.sp, 0));
                ret.add(new IInstruction("addi", Register.Arch.sp, Register.Arch.sp, 4));

                // store content of $t0 in memory at label
                ret.add(new LA(temp2, label));
                ret.add(new Store("sw", temp1, temp2, 0));
            }
            return ret;
        }

        public List<Instruction> apply(Instruction in){

            if(in.equals(AssemblyItem.Instruction.pushRegisters)){
                return genPushRegisters();
            }

            if(in.equals(AssemblyItem.Instruction.popRegisters)){
                return genPopRegisters();
            }

            List<Instruction> pre = new LinkedList<>();
            List<Instruction> post = new LinkedList<>();

            Map<Register, Register> instructionRegMap = new HashMap<>();

            int spillIndex = 0;

            Register def = in.def();
            if(def!=null && def.isVirtual()){

                Register out = registerAllocation.get(def);
                if(out==null){
                    Register spillRegister = spill.get(spillIndex++);
                    Register addrRegister = spill.get(spillIndex); //can overlap with others; it doesn't matter
                    //spilled: add store instruction
                    Label label = spilledLabels.get(def);
                    post.add(new LA(addrRegister, label));
                    post.add(new Store("sw", spillRegister,  addrRegister, 0));
                    instructionRegMap.put(def, spillRegister);

                }else{
                    instructionRegMap.put(def, out);

                }
            }

            for(Register inReg: in.uses()){
                if(!inReg.isVirtual())continue;

                Register out = registerAllocation.get(inReg);
                if(out==null){
                    Register spillRegister = spill.get(spillIndex++);
                    //spilled: add load instruction
                    Label label = spilledLabels.get(def);
                    post.add(new LA(spillRegister, label));
                    post.add(new Store("sw", spillRegister,  spillRegister, 0));
                    instructionRegMap.put(inReg, spillRegister);

                }else{
                    instructionRegMap.put(inReg, out);

                }
            }

            Instruction applied = in.rebuild(instructionRegMap);

            List<Instruction> ret = pre;
            ret.add(applied);
            ret.addAll(post);

            return ret; 
        }
    }

    public RegisterMap generateRegisterMap(ArrayList<Register> availableRegisters){

        

        //Chaitin

        final int availableRegistersSize = availableRegisters.size() - 3 ; //18 - 3 for spilling

        Stack<Node> removedNodes = new Stack<Node>();
        List<Node> spilled = new ArrayList<Node>();
        Map<Register, Label> spilledLabels = new HashMap<Register, Label>();

        while(removedNodes.size() + spilled.size() < nodes.size()){
            Node leastEdges = null;
            int minEdges = Integer.MAX_VALUE;

            Node mostEdges = null;
            int maxEdges = 0;

            for(Node node: nodes){
                if(removedNodes.search(node) != -1 ||spilled.contains(node))continue;

                int edgeCount = 0;
                for(Node edge: node.edges){
                    if(removedNodes.search(edge)== -1 && !spilled.contains(edge)){
                        edgeCount++;
                    }
                }

                if(edgeCount > maxEdges){
                    mostEdges = node;
                    maxEdges = edgeCount;
                }

                if(edgeCount < minEdges){
                    leastEdges = node;
                    minEdges = edgeCount;
                }
            }

            if(minEdges < availableRegistersSize){
                removedNodes.add(leastEdges);
            }else{
                spilled.add(mostEdges);
                spilledLabels.put(mostEdges.register, new Label(mostEdges.register.toString()));
            }

        }

        //all virtual registers have been removed

        Map<Register, Register> registerAllocation = new HashMap<>();

        while(!removedNodes.empty()){
            //get a node
            Node node = removedNodes.pop();
            //find a register for it
            for(Register reg: availableRegisters){
                boolean good = true;
                for(Node edge: node.edges){
                    if(removedNodes.search(edge) != -1 || spilled.contains(edge)) continue;
                    Register edgeReg = registerAllocation.get(edge.register);
                    if(edgeReg.equals(reg)){
                        good = false;
                        break;
                    }
                }

                if(good){
                    registerAllocation.put(node.register, reg);
                    break;
                }
            }
        }

        return new RegisterMap(registerAllocation, 
            availableRegisters.subList(availableRegistersSize, availableRegistersSize+3),
            spilledLabels);
    }
}