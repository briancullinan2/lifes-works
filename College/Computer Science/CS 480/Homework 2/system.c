#include <stdio.h> /* printf, stderr, fprintf */
#include <unistd.h> /* _exit */
#include <stdlib.h> /* exit */
#include <errno.h> /* so we can handle the errors */
 
 
int main(void)
{
	printf("Files in Directory are:\n");
	system("ls -l");
	return 0;
}
