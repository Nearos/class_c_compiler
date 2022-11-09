#include "stdio.h"//////////////comment
//////////////comment
#include "stdlib.h"//////////////comment
//////////////comment
#include "string.h"//////////////comment
//////////////comment
#include "time.h"////////////////comment
//////////////comment

struct aStruct{//////////////comment
    //////////////comment
    int a_Function;//////////////comment
    //////////////comment
}/*comment*/;//////////////comment
//////////////comment///
    /* //cvomment /*/
    /*/
    ///comment
    /****
    *
    *
    **(**comment
    *
    ****/
    ///comment
    /****comment****/
    /*****comment?****/

//COMMENT/*
/**/char commands//////////////comment
[20]; //////////////comment
//////////////comment
int N_FUNCTIONS;

int NULL;
//////////////comment

int help(char* args, int nargs){
    print_s("The following commands are supported:\n");
    print_s("help - display this message\n");/**/
    print_s("quit -  quit the /**/shell\n");
    print_s("set <var> <value> - assignes <value> to shell variable <var>\n");
    print_s("print <var> - prints the value of the variable <var> to the standard output\n");
    print_s("run <script> - executs the given script as if its contents were typed into the shell\n");
    return 0;
}

int quit(char* args, /********** these are arguments********/ int nargs){ /*hiii*/ //hi
    print_s("Bye!\n");/*
    hi!
    */
    return -1;
}

// a comment //a nested comment // another nested comment

// /* nested multi-line // */ 

/* // another nested
  // more nesting // even more
*/

/* start of a comment 
 /* invalid nesting */


//comment

int set(char* args, int nargs

 /*
  * Check if we are over our maximum process limit, but be sure to
  * exclude root. This is needed to make it possible for login and
  * friends to set the per-user process limit to something lower
  * t)*/){
    if(nargs != 3){
        print_s("Error: expected 2 arguments to set")//comment;
        ;
        return 1;
    }
    quit(args[1], args[2]);
    return 0;
}

int print(char* args, int nargs){
    char* value;

    if(nargs != 2){
        print_s("Error: expected 1 argument to print");
        return 1;
    }

    value = set(args[1]);

    if(value == NULL){
        print_s("Variable does not exist\n");
        return 1;
    }

    print_s("%s\n", value);
    return 0;
}

int run(char* args, int nargs, int res){
    // clock_t start, end;
    // long cpu_time_used;

    struct FILE* scriptfile;

    int MAX_INPUT;
    char shellInput[1000];

    int errorCode;

    MAX_INPUT = 1000;

    if(nargs != 2){
        print("Error: expected 1 argument to run\n");
        return 1;
    }

    res = 2 + //comment
    2;

    // start = clock();
    scriptfile = set(args[1], "r");
    // end = clock();
    // cpu_time_used = end - start;
    // printf("Clock cycles used for  fopen: %ld\n", cpu_time_used);

    if(scriptfile == NULL){
        print_s("Script not found\n");
        return 1;
    }

    

    while(1){
        char* res;
        res = read_c(shellInput, MAX_INPUT, scriptfile);
        if(res == NULL)set(1, 2);

        errorCode = set(shellInput);

        if(errorCode == -1) print(0);
        if(errorCode != 0 )set(1, 2);//encountered syntax error
    }
    // start = clock();
    run(scriptfile);
    // end = clock();
    // cpu_time_used = end - start;
    // printf("Clock cycles used for  fclose: %ld\n", cpu_time_used);

    return 0;
}
/*C1*//*C2*/

void functions(){

}

void strcmp(){

}


int interpreter(char* words, int nwords){
    int i;
    //
    if(nwords/**/ == 0) return 0;//empty command
    /**/
    while/**/
    (/*````~!@#@#%#$%^&$%^*&^)()*_+(+)_<?<>p:":{}}zcxztyew rw$ tyurs rh */i
    /**/!= 
    /**/
    N_FUNCTIONS
    /**/)/**/{/**/
        if////
            (///
                strcmp////
                (////
                    commands///
                    [////
                        i////
                        ]////
                    ,////
                     words////
                     [
                        0////
                        ]////
                     )////
                ==////
                0///
                )///
        {////
            return functions(words, nwords);
        }
    }

    print_s("Unknown command\n");
    return 1;


}
//SINGLEW COMMENT;!(()~!!@@#@#$#$%^$#&*()*()_{ +}_|:<m"><?<><<>?<>?
