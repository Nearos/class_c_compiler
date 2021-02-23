# Part III : Code Generation

**Important**: these instructions are subject to change.
Please make sure to check any updates made to this page (use the "watch" feature on gitlab to be automatically notified).  

The goal of part III is to write the code generator, targeting MIPS32 assembly.
For this part, you will only be using virtual registers (except for special purpose registers such as `$sp, $fp, ...`).
We provide you with a very native register allocator that assign each virtual register to a label and use memory to store the content of the registers as seen in the class.





## 0. Setup and Learning

Your first task consist of setting up the MARS mips simulator.
First download the simulator [here](./Mars4_5.jar) and follow Part 1 of the [tutorial](http://courses.missouristate.edu/KenVollmar/mars/tutorial.htm) to learn how to use the simulator.
We also encourage you to have a look at the documentation provided by MARS which can be found [here](http://courses.missouristate.edu/KenVollmar/mars/Help/MarsHelpIntro.html) as well as the [MIPS 32 Instruction Set Quick Reference](./MD00565-2B-MIPS32-QRC-01.01-1.pdf).
For a more detailed explanation about the MIPS architecture, please have a look at the [MIPS Assembly WikiBooks](http://en.wikibooks.org/wiki/MIPS_Assembly).
Another MIPS summary of all the instructions supported can be found here: [MIPS Green Card](https://booksite.elsevier.com/9780124077263/downloads/COD_5e_Greencard.pdf)

**Important**: the marking system will run the simulator from the command line which may change slightly the behaviour of your program (especially when it comes to handling input).
You should always make sure that all your tests execute correctly with the simulator run from the command line.


## 1. Generating a simple program

Your first real task should consist of producing an empty program (e.g. just an empty main function) and see that you can produce an assembly file.
Next, we suggest that you implement the print_i function using the corresponding system calls (check the lecture notes and the link above to the MARS documentation that explain how to do this).
To test it, you should implement support for integer litterals and have a hard-coded case in the function call node to handle a call to print_i.

Please note that we expect your programs to have one main function which should be the assembly entry point for the simulator. 
To understand how instructions can be generated using the starting code we give you, take a look at the `Test` class in the `gen/` package.

## 2. Binary Operators

Your next task should be to add support for all the binary operators, which is mostly done by implementing the `ExprGen` visitor. 
When you need to request a new virtual register to store the results of an operation, simply instantiate one with `new Register.Virtual()`.

Please note that the `||` and `&&` operators should be implemented with control flow as seen in the lecture.
Note that in the following example

```C
if ((1==0) && foo() == 2)
    ...
```

the function foo is never called at runtime since the semantic `&&` imposes that if the left side is false, the right side expression should not be executed. A similar logic applies for `||`. 



## 3. Variable allocations and uses

Your next task should be to implement allocations of global and local variables.

As seen during the course, the global variables all go in the static storage area (data section of the assembly file).

The local variables (variables inside a function) go onto the stack.
You should allocate them at a fix offset from the frame pointer ($fp) and store this offset either in a symbol table that you carry around or directly in the VarDecl AST node as a field.
Note that the only thing your compiler has to emit with respect to local variable is code to move the stack pointer ($sp) by an offset corresponding to the size of all the local variables declared on the stack.

Global variable allocation should be handled in the `ProgramGen` visitor while local variables should be handled in the `FunGen` visitor.

Next you should implement the logic to read and write local or global variables.
You can use the `lw` and `sw` instruction to read from or write to a variable respectively.
The tricky part will be to identify the location of the variables; either a label if globally allocated, or an offset from the frame pointer if locally allocated.
We encourage you to store this allocation information in the `VarDecl` node when allocating variables.

### sizeof and data alignment

We will follow the following specification for the size of the different types:
`sizeof(char)==1`, `sizeof(int)==4`, `sizeof(int*)==4`

Also arrays should always be represented in a compact form but you may need to pad the end of the array to make sure it is aligned to a 4 byte boundary.
As seen during the lecture, in the case of structures, you should make sure all the field are aligned at a 4 byte boundary.



## 4. struct/array accesses and assignments

Next you should add support for struct and array accesses.
This can be implemented using the `lw` and `sw` instructions for struct and a combination of `add` instruction with the `lw` and `sw` instructions for array accesses.
The idea is to get the address of an array into a register, then add to it the index (keeping in mind that addresses are expressed in byte, so an access to `a[x]` where a is an int array means an offset of x*4 from the base address where the array is stored).

As part of this step, we also suggest that you implement assignments.
As seen in the lecture, in the case of a `struct`, we highly encourage you to rewrite the AST before reaching the code generator to "inline" the assignment of each field.

## 5. Branching (if-then-else, loop, logical operators)

We suggest that you then implement the loop and if-then-else control structures as seen during the course using the branch instructions.


## 6. Function call

You can them move on to implementing function calls, by far the most challenging part.

To keep things simple, we highly recommend that you pass all arguments and return values using the stack, rather by register.
This will simplify greatly your code generation logic.

As seen during the lectures, you have to emit instructions on the caller size before the call occurs (precall) and after the call (postreturn).
On the callee side you have to emit instructions in the epilogue and prologue.

To facilitate the implementation of function call, we provide you with two "fake" instructions: `pushRegisters` and `popRegisters`.
These two instructions are responsible for pushing all the registers used by the function onto the stack and restore them.
These two instructions are "expended" during register allocation since this is only at that stage of the compilation that we know exactly how many registers the function uses.
We suggest that you follow the convention presented in the lecture when it comes to function call, with one difference:
you should place the saved registers on the stack last since we do not know ahead of time how many registers a function will really use until we reach register allocation.

Lastly, you should not forget to deal with function that returns `struct`.
As explained in the lecture, the best strategy is to rewrite the AST before you reach code generation to remove the need to deal with this case.

## 7. stdLib functions

Finally, you should add support for all the standard library functions found in the file `minic-stdlib.h` provided in the tests folder.
These should all be implemented using [system calls](http://courses.missouristate.edu/KenVollmar/mars/Help/SyscallHelp.html).



## New Files

A new package has been added under `gen/`. This package should be used to store your code generator.

 * The `gen.CodeGenerator` is the only class which `Main.java` directly interfaces with.
 * The `gen.asm.*Gen` classes are the main four visitors that you will need to produce code.
 * The class `gen.Test` shows you an example on how to emit instructions.

Another new package has been added under `gen/asm`. This package contains most of the assembly related constructs:
 * The `gen.asm.Register` class represents registers and contain a definition of most MIPS32 registers.
 * The `gen.AssemblyProgram` class represents an assembly programs which consists of several `Section`.
 * The `gen.asm.AssemblItem` class contains the items that appear in an assembly program: `Label`, `Instruction`, `Directive` and `Comment`.
 * the `gen.asm.AssemblyItemVisitor` class offers a visitor interface for `AssemblyItem`.
 
 The new package `regalloc/` contains a very naive register allocator.
 



## Updated Files

* The `Main.java` has been updated to provide a new commandline pass `-gen` which runs your code generator.

