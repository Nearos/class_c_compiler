package regalloc;

import gen.asm.*;
import java.util.*;

public class LessNaiveRegAlloc{

    public static final Register[] availableArchRegisters = {
            Register.Arch.t0, Register.Arch.t1, Register.Arch.t2, Register.Arch.t3, 
            Register.Arch.t4, Register.Arch.t5, Register.Arch.t6, Register.Arch.t7,
            Register.Arch.t8, Register.Arch.t9,
            Register.Arch.s0, Register.Arch.s1, Register.Arch.s2, Register.Arch.s3,
            Register.Arch.s4, Register.Arch.s5, Register.Arch.s6, Register.Arch.s7,
        };

    public static AssemblyProgram run(AssemblyProgram dirty){
        AssemblyProgram out = new AssemblyProgram();
        for(AssemblyProgram.Section section:dirty.sections){
            if(section.type == AssemblyProgram.Section.Type.DATA){
                out.emitSection(section);
            }else{
                ControlFlowGraph cfg = ControlFlowGraph.generateControlFlowGraph(section);
                LivenessGraph lg = cfg.generateLivenessGraph();
                ArrayList<Register> regList = new ArrayList();
                Collections.addAll(regList, availableArchRegisters);
                LivenessGraph.RegisterMap rm = 
                    lg.generateRegisterMap(regList);

                out.emitSection(rm.generateDataSection());
                AssemblyProgram.Section newText = out.newSection(AssemblyProgram.Section.Type.TEXT);

                section.items.forEach(item ->
                    item.accept(new AssemblyItemVisitor() {
                        public void visitComment(AssemblyItem.Comment comment) {
                            newText.emit(comment);
                        }
                        public void visitLabel(AssemblyItem.Label label) {
                            newText.emit(label);
                        }

                        public void visitDirective(AssemblyItem.Directive directive) {
                            newText.emit(directive);
                        }

                        public void visitInstruction(AssemblyItem.Instruction instruction) {
                            newText.emit("Original Instruction: "+instruction.toString());
                            List<AssemblyItem.Instruction> newInstructions = rm.apply(instruction);
                            for(AssemblyItem.Instruction i: newInstructions){
                                newText.emit(i);
                            }
                        }
                    }));
            }
        }
        return out;
    }
}