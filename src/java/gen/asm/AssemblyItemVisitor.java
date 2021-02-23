package gen.asm;


public interface AssemblyItemVisitor {
    public void visitLabel(AssemblyItem.Label label);
    public void visitDirective(AssemblyItem.Directive directive);
    public void visitInstruction(AssemblyItem.Instruction instruction);
    public void visitComment(AssemblyItem.Comment comment);
}
