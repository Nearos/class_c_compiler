package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import regalloc.NaiveRegAlloc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CodeGenerator {


    public void emitProgram(Program astProgram, File outputFile) throws FileNotFoundException {

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
