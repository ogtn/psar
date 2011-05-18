#include <SDL/SDL.h>
#include <SDL/SDL_image.h>
#include <stdio.h>

int main(int argc, char **argv)
{
    SDL_Rect pos = {0, 0};
    SDL_Event event;
    SDL_Surface *screen = NULL;
    SDL_Surface *img = NULL;
    int i = 0;
    
    SDL_Init(SDL_INIT_VIDEO);
    screen = SDL_SetVideoMode(1024, 1024, 32, SDL_HWSURFACE | SDL_DOUBLEBUF | SDL_RESIZABLE);
    
    while("true")
    {
        if(SDL_PollEvent(&event))
            if(event.type == SDL_QUIT || event.type == SDL_KEYDOWN && event.key.keysym.sym == SDLK_ESCAPE)
                break;
                        
        system("circo graph.txt -Tjpg -o graph.jpg");
            
        if(img != NULL)
            SDL_FreeSurface(img);
            
        if((img = IMG_Load("graph.jpg")) == NULL)
            puts(SDL_GetError());
    
        if(SDL_BlitSurface(img, NULL, screen, &pos) == -1)
            puts(SDL_GetError());
            
        SDL_Flip(screen);
        puts("blit");
        SDL_Delay(16);
    }

    SDL_Quit();
    
    return EXIT_SUCCESS;
}
