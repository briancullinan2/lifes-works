#include <stdio.h> /* printf, stderr, fprintf */
#include <unistd.h> /* _exit */
#include <stdlib.h> /* exit */
#include <errno.h> /* so we can handle the errors */

int main(void)
{
	// get input from keyboard and loop
	printf( "Welcome to Brian's shell program.\n" );

	char input[256];
	while( strcmp(input,"exit") != 0 )
	{
		printf( "sh480>" );
		gets( input );

		// now get arguments from string
		// start at i from before
		int j;
		int arg_count = 1;
		char *arguments[256];
		arguments[0] = input;
		for( j = 0; j < input[j] != '\0'; j++ )
		{
			if( input[j] == ' ' )
			{
				input[j] = (char) 0;

				arguments[arg_count] = &input[j+1];

				arg_count++;
			}
		}
		arguments[arg_count] = (char *) 0;

		if( strcmp(input,"exit") == 0 )
			break;

		//sprintf(arguments, "%d", strlen(input)-i);
		//printf( "command: " );
		//printf( command );
		//printf( "\narguments: " );
		//printf( arguments );
		//printf( "\n" );

		pid_t pid = fork();

		if (pid == 0)
		{
			/* Child process:
			* When fork() returns 0, we are in
			* the child process.
			* Here we count up to ten, one each second.
			*/
			int j;
			/*if( arg_count > 1 )
			{
				if( strcmp(arguments[arg_count-1], "&") != 0 )
				{
					arguments[arg_count-1] = (char *) 0;
				}
			}*/
			execvp ( arguments[0], arguments );
			_exit(0); /* Note that we do not use exit() */
		}
		else if (pid > 0)
		{ 
			/* Parent process:
			* When fork() returns a positive number, we are in the parent process
			* (whose process id is the one returned by the fork).
			* Again we count up to ten.
			*/
			//int i;
			//for (i = 0; i < 100; i++)
			//{
				//printf("parent: %d\n", i);
			//	sleep(1);
			//}
			//exit(0);
			if( arg_count > 1 )
			{
				if( strcmp(arguments[arg_count-1], "&") != 0 )
				{
					wait(NULL);
				}
			}
			else
			{
				wait(NULL);
			}
		}
		else
		{   
			/* Error:
			When fork() returns a negative number, an error happened
			(for example, number of processes reached the limit).
			*/
			fprintf(stderr, "can't fork, error %d\n", errno);
			exit(1);
		}

	}
}
