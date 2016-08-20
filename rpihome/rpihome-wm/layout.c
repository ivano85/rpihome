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
    
    // TODO: da rivedere perche' cosi' non va bene
    if (window->type == DOCK) {
        if (window->gravity == NorthGravity ||
                window->gravity == NorthEastGravity ||
                window->gravity == NorthWestGravity) {
            
            window->wm_position.y = s->top_filled;
            s->top_filled += window->wm_position.height;
            
        }
        else if (window->gravity == SouthGravity ||
                window->gravity == SouthEastGravity ||
                window->gravity == SouthWestGravity) {
            
            window->wm_position.y = screen.height - window->wm_position.height - s->bottom_filled;
            s->bottom_filled += window->wm_position.height;
            
        }

        if (window->gravity == EastGravity ||
                window->gravity == NorthEastGravity ||
                window->gravity == SouthEastGravity) {
            
            window->wm_position.x = screen.width - window->wm_position.width - s->right_filled;
            s->right_filled += window->wm_position.width;
            
        }
        else if (window->gravity == WestGravity ||
                window->gravity == NorthWestGravity ||
                window->gravity == SouthWestGravity) {
            
            window->wm_position.x = s->left_filled;
            s->left_filled += window->wm_position.width;
            
        }
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
    
    // TODO: da rivedere perche' cosi' non va bene
    if (window->type == DOCK) {
        if (window->gravity == NorthGravity ||
                window->gravity == NorthEastGravity ||
                window->gravity == NorthWestGravity) {
            
            screen.padding.top += window->wm_position.height;
            
        }
        else if (window->gravity == SouthGravity ||
                window->gravity == SouthEastGravity ||
                window->gravity == SouthWestGravity) {
            
            screen.padding.bottom += window->wm_position.height;
            
        }

        if (window->gravity == EastGravity ||
                window->gravity == NorthEastGravity ||
                window->gravity == SouthEastGravity) {
            
            screen.padding.right += window->wm_position.width;
            
        }
        else if (window->gravity == WestGravity ||
                window->gravity == NorthWestGravity ||
                window->gravity == SouthWestGravity) {
            
            screen.padding.left += window->wm_position.width;
            
        }
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
    
    if (window->gravity == NorthGravity ||
            window->gravity == NorthEastGravity ||
            window->gravity == NorthWestGravity) {
        changes->y = 0;
        *value_mask |= CWY;
    }
    else if (window->gravity == SouthGravity ||
            window->gravity == SouthEastGravity ||
            window->gravity == SouthWestGravity) {
        changes->y = screen.height - window->preferred_size.height;
        *value_mask |= CWY;
    }
    
    if (window->gravity == EastGravity ||
            window->gravity == NorthEastGravity ||
            window->gravity == SouthEastGravity) {
        changes->x = screen.width - window->preferred_size.width;
        *value_mask |= CWX;
    }
    else if (window->gravity == WestGravity ||
            window->gravity == NorthWestGravity ||
            window->gravity == SouthWestGravity) {
        changes->x = 0;
        *value_mask |= CWX;
    }
    
}