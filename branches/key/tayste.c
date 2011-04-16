#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/wait.h>
#include <string.h>


typedef struct data
{
    int nbProc;
    int nbHosts;
    char **hosts;
    char *cmd;
} data;


char *stringClean(char *str)
{
    char *cpy = str;
    
    while(*str)
    {
        if(*str == '\n')
            *str = '\0';
        str++;
    }
    
    return cpy;
}


int nbLines(FILE *file)
{
    int c;
    int nb = 0;
    
    while((c = fgetc(file)) != EOF)
        if(c == '\n')
            nb++;
            
    fseek(file, 0, SEEK_SET);

    return nb;
}


void getArgs(int argc, char **argv, data *d)
{
    int cpt;
    FILE *hostFile;
    char buffer[64];
    
    if(argc < 4)
    {
        puts("Tu peux pas test");
        puts("$ launcher np hostfile \"cmd\"");
        exit(EXIT_FAILURE);
    }
    
    d->nbProc = atoi(argv[1]);
    hostFile = fopen(argv[2], "r");
    d->cmd = argv[3];
    
    if(!hostFile)
    {
        puts("Le hostfile est naze, recommence");
        exit(EXIT_FAILURE);
    }

    cpt = d->nbHosts = nbLines(hostFile);
    d->hosts = malloc(sizeof(char *) * d->nbHosts);

    while(fgets(buffer, 64, hostFile))
    {
        d->hosts[--cpt] = malloc(64);
        stringClean(buffer);
        strcpy(d->hosts[cpt], buffer);
    }
    
    fclose(hostFile);
}


void clean(data *d)
{
    int cpt = d->nbHosts;
    
    while(cpt--)
        free(d->hosts[cpt]);
        
    free(d->hosts);
}


void son(int id, char *cmd, char *ip)
{
    char cmdLine[64];

    sprintf(cmdLine, "ssh %s \"%s\"", ip, cmd);
    /* printf("== Processus %3d sur %s\n", id, ip); */
    system(cmdLine);
    /*puts(cmdLine);*/
}


void father(data *d)
{
    int cpt = d->nbProc;
    
    while(cpt--)
        wait(NULL);
}


void run(data *d)
{
    int id = d->nbProc;
    
    puts("======================================================");
    printf("== Processus: %3d\n", d->nbProc);
    puts("======================================================");
    
    while(id-- && fork());

    if(id == -1)
        father(d);
    else
        son(id, d->cmd, d->hosts[id % d->nbHosts]);
}


int main(int argc, char **argv)
{
    data d;

    getArgs(argc, argv, &d);
    run(&d);
    clean(&d);
    
    return EXIT_SUCCESS;
}
