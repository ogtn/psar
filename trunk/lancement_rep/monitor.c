#include <GL/gl.h>
#include <SDL/SDL.h>
#include "SDL_image.h"
#include <stdio.h>


void resize(int x, int y)
{
    SDL_SetVideoMode(x, y, 32, SDL_OPENGL | SDL_RESIZABLE);

    glEnable(GL_TEXTURE_2D);
    glViewport(0, 0, x, y);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrtho(0, 1, 0, 1, -1000, 1000);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
}


void draw(void)
{
    glBegin(GL_QUADS);

    glTexCoord2d(0, 0);		glVertex2d(0, 0);
    glTexCoord2d(1, 0);     glVertex2d(1, 0);
    glTexCoord2d(1, -1);    glVertex2d(1, 1);
    glTexCoord2d(0, -1);    glVertex2d(0, 1);

    glEnd();
}


void reload(void)
{
    static GLuint texture;
    SDL_Surface *img = IMG_Load("ring.png");

    if(img)
    {
        if(glIsTexture(texture))
            glDeleteTextures(1, &texture);

        glGenTextures(1, &texture);
        glBindTexture(GL_TEXTURE_2D, texture);

        if(img->format->BytesPerPixel == 4)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, img->w, img->h, 0, GL_RGBA, GL_UNSIGNED_BYTE, img->pixels);
        else
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, img->w, img->h, 0, GL_RGB, GL_UNSIGNED_BYTE, img->pixels);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR);	

        SDL_FreeSurface(img);
    }
}


int main(int argc, char **argv)
{
    SDL_Event event;

    int cpt = 0;

    SDL_Init(SDL_INIT_VIDEO);
    resize(512, 512);

    while(1)
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        /* events */
        if(SDL_PollEvent(&event))
        {
            if(event.type == SDL_VIDEORESIZE)
                resize(event.resize.w, event.resize.h);
            else if(event.type == SDL_QUIT || (event.type == SDL_KEYDOWN && event.key.keysym.sym == SDLK_ESCAPE))
                break;
        }

        /* image reloading */
        if(cpt > 250)
        {
            reload();
            cpt = 0;
        }

        /* render() */
        draw();

        /* buffer swapping */
        SDL_GL_SwapBuffers();
        SDL_Delay(16);
        cpt += 16;
    }

    SDL_Quit();

    return EXIT_SUCCESS;
}
