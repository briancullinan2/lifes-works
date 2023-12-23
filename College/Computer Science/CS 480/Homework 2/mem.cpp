#include <iostream>
extern int etext, edata, end;
using namespace std;

int main( ){
	cout << "Adr etext: " << hex << int(&etext) << "\t ";
	cout << "Adr edata: " << hex << int(&edata) << "\t ";
	cout << "Adr end: " << hex << int(&end ) << "\n";

	char *s1 = "hello"; 
	static int a=1; 
	static int b; 
	int c = 2;
	int d;

	int *i = new int;

	char s2[] = "world";

	cout <<"s1:"<<hex << int(s1) << endl;
	cout <<"a:"<<hex << int(&a) << endl;
	cout <<"b:"<<hex << int(&b) << endl;
	cout <<"c:"<<hex << int(&c) << endl;
	cout <<"d:"<<hex << int(&d) << endl;

	cout <<"i:"<<hex << int(i) << endl;
	cout <<"s2:"<<hex << int(s2) << endl;

	return 0;
}
