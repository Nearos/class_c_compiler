#include "mimic-stdlib.h"


/*struct vector{
    int x;
    int y;
};

struct vector assignVector(int x, int y){
    struct vector ret;
    ret.x=x;
    ret.y=y;
    return ret;
}

void printVector(struct vector ipt){
    print_s((char*)"(");
    print_i(ipt.x);
    print_s((char*)", ");
    print_i(ipt.y);
    print_s((char*)")");
}

struct vector addVectors(struct vector v1, struct vector v2){
    struct vector ret;
    ret.x = v1.x+v2.x;
    ret.y = v1.y+v2.y;
    return ret;
}

void main(){
    struct vector v1;
    struct vector v2;
    printVector(addVectors(assignVector(34, 34), assignVector(1, 5)));
}*/

/*int fibonacci(int n){
    int res1;
    int res2;

    if(n == 0){
        return 0;
    }

    if(n == 1){
        return 1;
    }

    return fibonacci(n-1) + fibonacci(n-2);
}

void main(){
    int n;
    int ret;

    n = read_i();

    print_s((char*)"The ");
    print_i(n);
    print_s((char*)"th fibonacci number is: ");

    ret = fibonacci(n);

    print_i(ret);
}*/

/*void main(){
    int a;
    int b;
    int c;
    int d;
    int e;
    int f;
    int g; 
    int h;
    int i;
    int j; 
    int k;
    int l;
    int m;
    int n;
    int o;
    int p;
    int q;
    int r;
    int s;
    int t;
    int u;
    int v;
    int w;
    int x;
    int y;
    int z;
    int res;
    int acc;

    a = 1;
    b = 2;
    c = 3;
    d = 4;
    e = 5;
    f = 6;
    g = 7;
    h = 8;
    i = 9;
    j = 10;
    k = 11;
    l = 12;
    m = 13;
    n = 14;
    o = 15;
    p = 16;
    q = 17;
    r = 18;
    s = 19;
    t = 20;
    u = 21;
    v = 22;
    w = 23;
    x = 24; 
    y = 25;
    z = 26;

    res = a+b+c+d+e+f+g+h+i+j+k+l+m+n+o+p+q+r+s+t+u+v+w+x+y+z;
    print_i(res);
    print_s((char*)"\n");
    acc = 0;
    i = 0;
    while(i < 27){
        acc = acc + i;
        i = i + 1;
    }
    print_i(acc);
    print_s((char*)"\n");
}//*/

/*int isNumber(char c){
    char* digits;
    int i;

    digits = (char*)"0123456789";
    i = 0;
    while(i != 10){
        if(c == digits[i]){
            return i;
        }
        i = i + 1;
    }
    return -1;
}

int dec2bin(char* num, int acc){
    if(*num == '\0'){
        return acc;
    }
    return dec2bin(&num[1], acc*10 + isNumber(*num));
}

void main(){
    char* str;
    int i;
    int cont;

    print_s((char*)"Enter a number: ");
    str = (char*)mcmalloc(80);
    i=0;
    cont = 1;
    while(i!=80 && cont){
        str[i] = read_c();
        cont = isNumber(str[i]) > 0;
        i = i + 1;
    }
    str[i-1] = '\0';

    print_s(str);
    print_s(" ");
    print_i(dec2bin(str, 0));
}*/

int a() {
  int b[15];
  int c;
  int d;
  int e;
  int f;
  int g;
  int h;
  int i;
  int j;
  int k;
  int l[13];
  int m;
  int n;
  int o;
  int p;
  int q;
  int r;
  b[0] = 4;
  b[1] = 32;
  b[2] = 247;
  b[3] = 212;
  b[4] = 5;
  b[5] = 35;
  b[6] = 6;
  b[7] = 1;
  b[8] = 134;
  b[9] = 87;
  b[10] = 149;
  b[11] = 42;
  b[12] = 27;
  b[13] = 15;
  b[14] = 4;
  c = 4;
  d = 32;
  e = 247;
  f = 212;
  g = 5;
  h = 35;
  i = 6;
  j = 1;
  k = 134;
  m = 0;
  while (m < 13) {
    l[m] = b[m];
    m = m + 1;
  }
  n = 4 + 4;
  o = 32 + 32;
  p = 247 - 247;
  q = 0;
  r = 0;
  while (q < 248) {
    int s;
    if (q < 158) {
      s = -(-(b[q / 15] - 4 - (b[q % 15] + 3)));
    } else {
      int t;
      int u;
      int v;
      if ((q - 158) / 3 < 15) {
        t = b[(q - 158) / 3];
      } else {
        t = b[(q - 158) / 3 - 15];
      }
      if ((q - 158) / 3 < 9) {
        int w;
        if ((q - 158) / 3 == 0)
          w = c;
        else if ((q - 158) / 3 == 1)
          w = d;
        else if ((q - 158) / 3 == 2)
          w = e;
        else if ((q - 158) / 3 == 3)
          w = f;
        else if ((q - 158) / 3 == 4)
          w = g;
        else if ((q - 158) / 3 == 5)
          w = h;
        else if ((q - 158) / 3 == 6)
          w = i;
        else if ((q - 158) / 3 == 7)
          w = j;
        else
          w = k;
        u = w;
      } else {
        u = b[((q - 158) / 3 - 9) % 15];
      }
      if ((q - 158) % 3 == 0)
        v = n;
      else if ((q - 158) % 3 == 1)
        v = o;
      else
        v = p;
      s = t + b[(q - 158) / 3 / 15] * b[(q - 158) / 3 % 15] + u - v;
    }
    r = s + r;
    q = q + 1;
  }
  return r;
}


void main() { print_i(a()); }