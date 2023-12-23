//
//---This file contains a simple example of using "fork" with "pipe".
//

#include <errno.h>
#include <stdio.h>
#include <unistd.h>
#include <fstream>

#include <iostream>

using namespace std;

void Writer (int,char[]);
void Munch1 (int,int);
void Munch2 (int,int);
void Reader (int,char[]);

const int MAXBUFF = 64;

int
main (int argc, char *argv[])
{

    int ends[2];

    if (pipe(ends))
    {
		perror ("Opening pipe");
		exit (-1);
    }

    int pid = fork();
    if (pid > 0)
    {
		close(ends[1]);

		int ends2[2];
		if (pipe(ends2))
		{
			perror ("Opening pipe");
			exit (-1);
		}

		int pid2 = fork();
		if (pid2 > 0)
		{
			close(ends2[1]);

			int ends3[2];
			if (pipe(ends3))
			{
				perror ("Opening pipe");
				exit (-1);
			}

			int pid2 = fork();
			if (pid2 > 0)
			{
				//
				//---Parent process is consumer
				//
				close(ends3[1]);
				Writer (ends3[0], argv[2]);
				exit (0);
			} else if (pid2 == 0) {
				//
				// Child process is producer
				//
				close(ends3[0]);
				Munch2 (ends2[0],ends3[1]);
				exit (0);
			} else {
				perror ("Fork3 failed");
				exit (-1);
			}
		} else if (pid2 == 0) {
			//
			// Child process is producer
			//
			close(ends2[0]);
			Munch1 (ends[0],ends2[1]);
			exit (0);
		} else {
			perror ("Fork2 failed");
			exit (-1);
		}
    } else if (pid == 0) {
		//
		// Child process is producer
		//
		close(ends[0]);
		Reader (ends[1],argv[1]);
		exit (0);
    } else {
		perror ("Fork failed");
		exit (-1);
    }
}

void
Writer (int fd, char file[])
{

    char buff[MAXBUFF];
    int count;

	int line_count = 1;

    //
    //---Read from pipe until no more data (EOF)
    //

    while ((count = read(fd, buff, MAXBUFF)) > 0)
    {
		for( int i = 0; i < count; i++ )
		{
			if( buff[i] == (char)10 )
			{
				line_count++;
			}
		}
		//cout << buff << endl;
    }

	ofstream out(file); 
	if(!out) { 
		cout << "Cannot open file.\n"; 
		return; 
	} 
	out << buff << endl; 

	out.close(); 

    if (count < 0) perror("reading pipe");
    else cout << "Line Count:" << line_count << endl;

    return;
}

void
Munch2 (int fd1, int fd2)
{
    char buff[MAXBUFF];
    int count;

    //
    //---Read from pipe until no more data (EOF)
    //

    while ((count = read(fd1, buff, MAXBUFF)) > 0)
    {
		for( int i = 0; i < count; i++ )
		{
			if( islower(buff[i]) )
			{
				buff[i] = toupper(buff[i]);
			}
		}
		//cout << buff << endl;
    }

	if ((count = write(fd2, buff, strlen(buff)+1)) > 0);
	else perror("writing pipe");

	return;
}

void
Munch1 (int fd1, int fd2)
{
    char buff[MAXBUFF];
    int count;

    //
    //---Read from pipe until no more data (EOF)
    //

    while ((count = read(fd1, buff, MAXBUFF)) > 0)
    {
		for( int i = 0; i < count; i++ )
		{
			if( buff[i] == " "[0] )
			{
				buff[i] = "*"[0];
			}
		}
 		//cout << buff << endl;
   }

	if ((count = write(fd2, buff, strlen(buff)+1)) > 0);
	else perror("writing pipe");

	return;
}

void
Reader (int fd, char file[])
{
    char buff[64];
    int count;

    ifstream inFile;
    
    inFile.open(file);
    if (!inFile) {
        cout << "Unable to open file";
        exit(1); // terminate with error
    }
    
    while (inFile.getline(buff, 63)) {
		//cout << buff << endl;

		if ((count = write(fd, buff, strlen(buff)+1)) > 0);
		else perror("writing pipe");
    }
    
    inFile.close();

    exit (0);
}
