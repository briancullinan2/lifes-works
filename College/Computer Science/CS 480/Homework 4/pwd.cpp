#include <cstdlib>
#include <iostream>
#include <sys/stat.h>
#include <dirent.h>

using namespace std;

//global variables
void pwd(char *dir);
char* CUR_DIR = ".";
char* PAR_DIR = "..";
struct dirent *entry;

//main method
int main(int argc, char *argv[])
{
    //print path
    pwd(CUR_DIR);
    //print newline
    cout << endl;
    
    //exit
    return EXIT_SUCCESS;
}

//recursive pwd method
void pwd(char *dir) {
    //change to dir
    chdir(dir);
    
    //store directory
    struct stat curbuf;
    stat(CUR_DIR, &curbuf);
    
    //change to parent dir
    chdir(PAR_DIR);
    
    //char buff[256];
    DIR *dp;

    if((dp=opendir(dir))==NULL)
    {
        printf("Error");
        exit(1);
    }

    //for all files in directory
    while ((entry = readdir(dp)) != NULL)
    {
          //get file name
          struct stat parbuf;
          stat(entry->d_name, &parbuf);
          
          //ignores current dir
	  if( strcmp(entry->d_name, CUR_DIR) == 0)
		  continue;

          //if current inode matches stored inode
          if (curbuf.st_ino == parbuf.st_ino)
          {
                //if root node
                stat(PAR_DIR, &parbuf);
		if (curbuf.st_ino == parbuf.st_ino)
			return;

		char *dirname = entry->d_name;

                //recursive call
                pwd(CUR_DIR);
                
                //print and change back
                cout << "/" << dirname;
		//stat(CUR_DIR, &parbuf);

		return;
          }
    }
}

