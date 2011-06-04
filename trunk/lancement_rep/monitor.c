#include <SDL/SDL.h>
#include <stdio.h>

int main(int argc, char **argv)
{
    SDL_Rect pos = {0, 0};
    SDL_Event event;
    SDL_Surface *screen = NULL;
    SDL_Surface *img = NULL;
    SDL_Surface *img2 = NULL;
    int i = 0;
    
    SDL_Init(SDL_INIT_VIDEO);
    screen = SDL_SetVideoMode(1024, 1024, 32, SDL_HWSURFACE | SDL_DOUBLEBUF | SDL_RESIZABLE);
    
    do
    {
        if(SDL_PollEvent(&event))
            if(event.type == SDL_QUIT || event.type == SDL_KEYDOWN && event.key.keysym.sym == SDLK_ESCAPE)
                break;
        
        if((img2 = SDL_LoadBMP("prout.bmp")) != NULL)
        {
			if(img != NULL)
				SDL_FreeSurface(img);
				
			img = img2;
		}
    
        if(img && (SDL_BlitSurface(img, NULL, screen, &pos) == -1))
            puts(SDL_GetError());
            
        SDL_Flip(screen);
        SDL_Delay(16);
    } while(1);

    SDL_Quit();
    
    return EXIT_SUCCESS;
}
