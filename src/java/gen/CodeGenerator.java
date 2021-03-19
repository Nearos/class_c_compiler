package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import regalloc.NaiveRegAlloc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CodeGenerator {


    public void emitProgram(Program astProgram, File outputFile) throws FileNotFoundException {

        //modify ast before generation
        astProgram = (Program)astProgram
            .accept(new pass.BaseASTPass()) //For testing; does nothing but duplicate tree
            .accept(new pass.BlockStatements())
            .accept(new pass.ChangeReturnValuesToPointerArguments())
            .accept(new pass.ChangeStructAssignmentsToFieldAssignments())
            .accept(new pass.MoveLocalVariablesToFunctionBlock())
            ;

        System.out.println("AST after modification:\n");

        PrintWriter astWriter;
        StringWriter sw = new StringWriter();
        try {
            astWriter = new PrintWriter(sw);
            astProgram.accept(new ASTPrinter(astWriter));
            astWriter.flush();
            System.out.print(sw.toString());
            astWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // generate an assembly program with the code generator
        AssemblyProgram asmProgWithVirtualRegs = new AssemblyProgram();
        ProgramGen progGen = new ProgramGen(asmProgWithVirtualRegs);
        progGen.visitProgram(astProgram);

        // run the register naive allocator which remove the virtual registers
        AssemblyProgram asmProgNoVirtualRegs = NaiveRegAlloc.run(asmProgWithVirtualRegs);

        // print the assembly program
        PrintWriter writer = new PrintWriter(outputFile);
        asmProgNoVirtualRegs.print(writer);
        writer.close();
    }


}
