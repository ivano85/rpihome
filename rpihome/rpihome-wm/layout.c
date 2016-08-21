/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include <stdio.h>
#include <stdlib.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>

#include "layout.h"

#define max(x,y) ((x >= y) ? (x) : (y))

static struct {
    int width;
    int height;
    struct {
        int top;
        int bottom;
        int left;
        int right;
    } padding;
} screen;

static struct layout_status {
    int top_filled;
    int left_filled;
    int right_filled;
    int bottom_filled;
    Display * display;
};

static void apply_window_position(window* window, void* status);
static void place_layedout_window(window* window, void* status);
static void recalculate_boundaries(window* window, void* status);

void init_layout_manager(Display * dpy) {
    Screen * s = DefaultScreenOfDisplay(dpy);
    screen.width = WidthOfScreen(s);
    screen.height = HeightOfScreen(s);
    redo_layout(dpy);
}

void redo_layout(Display *dpy) {
    
    struct layout_status status;
    
    screen.padding.top = 0;
    screen.padding.bottom = 0;
    screen.padding.left = 0;
    screen.padding.right = 0;
    foreach_window(&recalculate_boundaries, NULL);
    
    status.top_filled = 0;
    status.left_filled = 0;
    status.right_filled = 0;
    status.bottom_filled = 0;
    status.display = dpy;
    foreach_window(&place_layedout_window, &status);
    
    foreach_window(&apply_window_position, &status);
    
}

static void apply_window_position(window* window, void* status) {
    
    struct layout_status *s = status;
    
    if (!window->visible) {
        return;
    }
    
    XMoveResizeWindow(s->display, window->window,
            window->wm_position.x, window->wm_position.y,
            window->wm_position.width, window->wm_position.height);
    
}

static void place_layedout_window(window* window, void* status) {
    
    struct layout_status *s = status;
    
    if (!window->visible) {
        return;
    }
    
    if (window->type == DOCK) {
        
    }
    else {
        window->wm_position.x = screen.padding.left;
        window->wm_position.y = screen.padding.top;
        window->wm_position.width = screen.width - screen.padding.left - screen.padding.right;
        window->wm_position.height = screen.height - screen.padding.top - screen.padding.bottom;
    }
    
}

static void recalculate_boundaries(window* window, void* status) {
    
    if (!window->visible) {
        return;
    }
    
    if (window->type == DOCK) {
        
        screen.padding.top = max(screen.padding.top, window->reserve.top);
        screen.padding.bottom = max(screen.padding.bottom, window->reserve.bottom);
        screen.padding.left = max(screen.padding.left, window->reserve.left);
        screen.padding.right = max(screen.padding.right, window->reserve.right);
        
    }
}

void size_normal_window(window *window, XWindowChanges *changes, unsigned long *value_mask) {
    
    changes->x = screen.padding.left;
    changes->y = screen.padding.top;
    changes->width = screen.width - screen.padding.left - screen.padding.right;
    changes->height = screen.height - screen.padding.top - screen.padding.bottom;
    
    *value_mask = CWX | CWY | CWWidth | CWHeight;
    
}

void size_dock_window(window *window, XWindowChanges *changes, unsigned long *value_mask) {
    
}