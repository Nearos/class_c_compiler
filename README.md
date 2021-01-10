# Description of the course work is likely to be upatded! #

Please note that the description of the course work may be updated from time to time to add clarifications or fix mistakes.
Your are highly encouraged to "watch" this repository for any changes.

# Deadlines #

1. [Part 1 (parser)](desc/part1/), Friday 5 February 2021 at 5pm, weight = 20%
2. Part 2 (ast builder + semantic analyser), Friday 26 February 2021 at 5pm, weight = 20%
3. Part 3 (code generator), Friday 19 March 2021, at 5pm, weight = 25%
4. Part 4 (register allocator), Wednesday 14 April 2021, at 5pm, weight = 25%

Specific instructions for each part can be found above by clicking on the part name.

# Scoreboard #

We automatically run a series of test programs using your compiler about twice a day.
You can keep track of your progress and see how many tests pass/fail using the scoreboard by following this link **once the automarking will start**: TBC.
The scoreboard **is provided as a best effort service**, do not rely on it as it may come down unexpectedly:no guarantees are offered.

# Marking #

The marking will be done using an automated test suite on a Linux machine using Java 11.
Please note that you are not allowed to modify the `Main.java` file which is the main entry point to the compiler.
The `Main.java` will be replaced by our own when we run the automarker.
Also make sure that the build script provided remains unchanged so that your project can be built on our machine.
Furthermore, **the use of any external libraries is forbidden**.
In case of doubt, ask us on the online forum.

For all parts of the coursework, the marking will be a function of the number of successful tests as shown in the scoreboard and a series of hidden tests.


## Parts 1-4
2/3 (66.6%) of the mark will be determined by the scoreboard tests and 1/3 (33.3%) will be determined by the hidden tests.
You will get 1 point for each passing test and -1 for each failing test (only for the lexer, parser and semantic analysis components).
Then, the mark is calculated by dividing the number of points achieved by the number of tests.
The hidden tests are marked independently from the visible ones.



# Tests #

Although we do not mark you on the tests you will create for testing your own compiler, we do ask you to add all the tests you used into the `tests` folder of your repository.
If we find students that do not have any tests (or very few), and they managed to pass most of our tests, this will raise suspicion that this might be a case of academic misconduct.
Also make sure that you do not share your tests as they should be written by yourself alone (we will run plagiarism detection software on all the code, including the tests, that are in your repository).

# Setup #

## Register your student id and name

First, we will need you fill up [this google form](TODO)
in order for us to register you for the automarking.
If you are not registered, we won't be able to mark you.
Also please make sure to keep `comp520-coursework-w2021` as your repository name, otherwise automarking will fail.

## GitLab ##
We will rely on gitlab and it is mandatory to use it for this coursework.
GitLab is an online repository that can be used with the git control revision system.
The CS department runs a GitLab hosting service, and all students should be able to access it with their CS account.

Important: do not share your code and repository with anyone and keep your source code secret.
If we identify that two students have identical portion of code, both will be considered to have cheated.


## Obtaining your own copy of the repository
We are going to be using the Git revision control system during the course.
If you use your own machine then make sure to install Git.

You will need to have your own copy of the project's repository. In order to fork this repository, click the fork button:

![Forking the repository](/figures/gl_fork1.png "Forking this repository.")

![Forking the repository](/figures/gl_fork2.png "Forking this repository.")

Then, make the repository private

![Making repository private](/figures/gl_private1.png "Making repository private.")

![Making repository private](/figures/gl_private2.png "Making repository private.")

![Making repository private](/figures/gl_private3.png "Making repository private.")

Now, grant access to the teaching staff

![Granting the teaching staff read access](/figures/gl_permissions1.png "Granting the teaching staff read access.")

![Granting the teaching staff read access](/figures/gl_permissions2.png "Granting the teaching staff read access.")

You should grant the following users *Reporter* access:
  * TODO
  * Christophe Dubach (username: cdubach)
  * Jacob Mai (username: mpeng5)

Next, you will have to clone the forked repository to your local machine.
In order to clone the repository you should launch a terminal and type:

```
$ git clone git@gitlab.cs.mcgill.ca:XXXXXXXX/comp520-coursework-w2021.git
```

where XXXXXXXX is your CS gitlab account id.


## Development environment (editor) setup
You can choose to use any development environment for your project, such as IntelliJ, Eclipse, Emacs, Vim or your favourite text editor.
Choose whichever you are confident with.
However, we higly recommend using IntelliJ Idea since you will benefit from features such as the debugger, and the project is already setup to be used with IntelliJ. 

IntelliJ is available on the CS lab machines.
 To launch it on the CS machines, open a terminal and simply type:
 ```
 idea
```

If you wish to install IntelliJ on your own machine, you can download the latest copy of the free community edition here:

* Community edition of [IntelliJ](https://www.jetbrains.com/idea/).



To import the project with IntelliJ, after it opens select "Import Project" and select the root directory of your project.
On the following screen, ensure that the "Create project from existing sources" option is selected.
You will then be presented with a series of screens.
Just keep selecting "Next" without modifying any options.
If you are asked whether to overwrite an existing .iml file, select the overwrite option.

To confirm that the project is setup correctly, you can try to run the Main.java file directly from the idea.
To do so, right click the Main file in the src directory.
In the context menu, select the "Run Main.main()" option. 
The program should now have run successfully


## Building the project
In order to build the project you must have Ant installed, which is installed already on the CS lab machines.
Your local copy of the repository contains an Ant build file (`build.xml`).
You can build the project from the commandline by typing:
```
$ ant build
```
This command outputs your compiler in a directory called `bin` within the project structure.
Thereafter, you can run your compiler from the commandline by typing:
```
$ java -cp bin Main
```
The parameter `cp` instructs the Java Runtime to include the local directory `bin` when it looks for class files.
It is important to ensure that you can compile and run your compiler from the command line since this is how the auto-marker will performed its task.

You can find a series of tests in the `tests` folder.
To run the lexer on one of them, you can type:

```
$ java -cp bin Main -lexer tests/fibonacci.c dummy.out
```

Which should produce the following output:

```
Lexing error: unrecognised character (#) at 1:0
INVALID
Lexing error: unrecognised character (i) at 1:1
INVALID
Lexing error: unrecognised character (n) at 1:2
INVALID
Lexing error: unrecognised character (c) at 1:3
INVALID
...
Lexing error: unrecognised character (}) at 34:2
INVALID
Lexing error: unrecognised character (}) at 35:0
INVALID
Lexing: failed (331 errors)
```


You can clean the `bin` directory by typing:
```
$ ant clean
```
This command effectively deletes the `bin` directory.

## Working with git and pushing your changes

Since we are using an automated marking mechnism (based on how many progams can run successfully through your compiler), it is important to understand how git works. If you want to benefit from the nightly automatic marking feedback, please ensure that you push all your changes daily onto your GitLab centralised repository.

We suggest you follow the excelent [tutorial](https://www.atlassian.com/git/tutorials/what-is-version-control) from atlassian on how to use git. In particular you will need to understand the following basic meachnisms:

* [add and commit](https://www.atlassian.com/git/tutorials/saving-changes)
* [push](https://www.atlassian.com/git/tutorials/syncing/git-push)
