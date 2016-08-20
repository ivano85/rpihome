/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   windows.h
 * Author: ivano
 *
 * Created on 20 agosto 2016, 16.41
 */

#ifndef WINDOWS_H
#define WINDOWS_H

#include <X11/Xlib.h>

#ifdef __cplusplus
extern "C" {
#endif
    
    typedef enum {
        NORMAL,
        DOCK,
        DIALOG,
        SPLASH,
        DESKTOP
    } window_type;
    
    typedef struct {
        Window window;
        window_type type;
        struct {
            int width;
            int height;
        } preferred_size;
        struct {
            int x;
            int y;
            int width;
            int height;
        } wm_position;
        int gravity;
        Bool visible;
    } window;

    void init_windows_store();
    
    window* find_window_by_id (Window id);
    window* add_window(Window id);
    window* destroy_window(Window id);
    
    void foreach_window(void (*exec)(window*, void*), void* status);

#ifdef __cplusplus
}
#endif

#endif /* WINDOWS_H */

