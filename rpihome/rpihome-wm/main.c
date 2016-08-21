/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: ivano
 *
 * Created on 20 agosto 2016, 16.25
 */

#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>

#include "windows.h"
#include "layout.h"

void manage_top_windows();
void manage_configure_request(XConfigureRequestEvent ev);
void manage_map_request(XMapRequestEvent ev);
void init_window_object(window* window);
void update_window_visibility (XEvent ev);

Display * dpy;
Window root;

/*
 * 
 */
int main(int argc, char** argv) {
    
    XEvent ev;

#ifdef DEBUG
    if(!(dpy = XOpenDisplay(":1"))) return 1;
#else
    if(!(dpy = XOpenDisplay(0x0))) return 1;
#endif
    
    init_windows_store();
    init_layout_manager(dpy);

    root = DefaultRootWindow(dpy);

    XSelectInput(dpy, root, SubstructureRedirectMask | SubstructureNotifyMask);
    XSetWindowBackground(dpy, root, 0xFFFFFF);
    XMapWindow(dpy, root);
    XSync(dpy, False);
    
    manage_top_windows();

    for(;;)
    {
        XNextEvent(dpy, &ev);
        
        switch (ev.type) {
            case ConfigureRequest:
                manage_configure_request(ev.xconfigurerequest);
                break;
            case MapRequest:
                manage_map_request(ev.xmaprequest);
                break;
            case CreateNotify:
                break;
            case DestroyNotify:
                destroy_window(ev.xdestroywindow.window);
                break;
            case ClientMessage:
                break;
            case MapNotify:
                update_window_visibility(ev);
                break;
            case UnmapNotify:
                update_window_visibility(ev);
                break;
        }
        
    }
    
}

void manage_configure_request(XConfigureRequestEvent ev) {
    
    window* win = find_window_by_id(ev.window);
    if (win == NULL) {
        win = add_window(ev.window);
    }
    init_window_object(win);
    
    XWindowChanges changes;
    unsigned long value_mask = ev.value_mask;
    
    if (value_mask & CWX) {
        changes.x = ev.x;
    }
    if (value_mask & CWY) {
        changes.y = ev.y;
    }
    if (value_mask & CWWidth) {
        changes.width = ev.width;
    }
    if (value_mask & CWHeight) {
        changes.height = ev.height;
    }
    
    switch (win->type) {
        case NORMAL:
            value_mask = 0;
            size_normal_window(win, &changes, &value_mask);
            changes.border_width = 0;
            changes.stack_mode = Above;
            value_mask = value_mask | CWBorderWidth | CWStackMode;
            break;
        case DOCK:
            size_dock_window(win, &changes, &value_mask);
            changes.border_width = 0;
            changes.stack_mode = Above;
            value_mask = value_mask | CWBorderWidth | CWStackMode;
            break;
        case DIALOG:
            changes.stack_mode = Above;
            value_mask = value_mask | CWStackMode;
            break;
        case SPLASH:
            changes.stack_mode = Above;
            value_mask = value_mask | CWStackMode;
            break;
        case DESKTOP:
            break;
    }
    
    XConfigureWindow(
            dpy,
            win->window,
            value_mask,
            &changes
        );
    
}

void manage_map_request(XMapRequestEvent ev) {
    XMapWindow(dpy, ev.window);
}

void init_window_object(window* window) {
    
    Atom prop_dock, prop_splash, prop_dialog, prop_desktop, prop_type, prop, da;
    int status, di;
    unsigned long dl, num;
    unsigned char *prop_ret = NULL;
    XSizeHints size_hints;
    long supplied_return;
    XWindowAttributes attr;
    
    prop_type = XInternAtom(dpy, "_NET_WM_WINDOW_TYPE", True);
    prop_desktop = XInternAtom(dpy, "_NET_WM_WINDOW_TYPE_DESKTOP", True);
    prop_dock = XInternAtom(dpy, "_NET_WM_WINDOW_TYPE_DOCK", True);
    prop_splash = XInternAtom(dpy, "_NET_WM_WINDOW_TYPE_SPLASH", True);
    prop_dialog = XInternAtom(dpy, "_NET_WM_WINDOW_TYPE_DIALOG", True);
    
    status = XGetWindowProperty(dpy, window->window, prop_type, 0L, sizeof (Atom), False,
                                AnyPropertyType, &da, &di, &dl, &dl, &prop_ret);
    
    if (status == Success && prop_ret)
    {
        prop = ((Atom *)prop_ret)[0];
        
        /* Compare internal atom numbers */
        if (prop == prop_desktop) {
            window->type = DESKTOP;
        }
        else if (prop == prop_dock) {
            window->type = DOCK;
        }
        else if (prop == prop_splash) {
            window->type = SPLASH;
        }
        else if (prop == prop_dialog) {
            window->type = DIALOG;
        }
        else {
            window->type = NORMAL;
        }
        
    }
    else {
        window->type = NORMAL;
    }
    /*
    status = XGetWMNormalHints(dpy, window->window, &size_hints, &supplied_return);
    
    if (status == Success && size_hints.flags) {
        
        if ((size_hints.flags & PSize) == PSize) {
            window->preferred_size.width = size_hints.width;
            window->preferred_size.height = size_hints.height;
        }
        
    }
    */
    status = XGetWindowAttributes(dpy, window->window, &attr);
    if (status) {
        window->wm_position.x = attr.x;
        window->wm_position.y = attr.y;
        window->wm_position.width = attr.width;
        window->wm_position.height = attr.height;
    }
    
    prop_type = XInternAtom(dpy, "_NET_WM_STRUT_PARTIAL", False);
    status = XGetWindowProperty(dpy, window->window, prop_type, 0L, LONG_MAX, False,
            AnyPropertyType, &da, &di, &num, &dl, &prop_ret);
    if(status == Success)
    {
        if(num > 0) {
            window->reserve.left = prop_ret[0];
            window->reserve.right = prop_ret[1];
            window->reserve.top = prop_ret[2];
            window->reserve.bottom = prop_ret[3];
        }
        else {
            status = -1;
        }

    }
    
    if (status != Success) {
        prop_type = XInternAtom(dpy, "_NET_WM_STRUT", False);
        status = XGetWindowProperty(dpy, window->window, prop_type, 0L, LONG_MAX, False,
                AnyPropertyType, &da, &di, &num, &dl, &prop_ret);
        if(status == Success)
        {
            if(num > 0) {
                window->reserve.left = prop_ret[0];
                window->reserve.right = prop_ret[1];
                window->reserve.top = prop_ret[2];
                window->reserve.bottom = prop_ret[3];
            }

        }
    }
    
}

void update_window_visibility (XEvent ev) {
    
    window* win = NULL;
    
    switch (ev.type) {
        case MapNotify:
            
            win = find_window_by_id(ev.xmap.window);
            if (win == NULL) {
                win = add_window(ev.xmap.window);
            }
            init_window_object(win);
            
            win->visible = True;
            
            break;
        case UnmapNotify:
            
            win = find_window_by_id(ev.xunmap.window);
            if (win == NULL) {
                win = add_window(ev.xunmap.window);
            }
            // Crash...
            //init_window_object(win);
            
            win->visible = False;
            
            break;
    }
    
    if (win != NULL && win->type == DOCK) {
        redo_layout(dpy);
    }
    
}

void manage_top_windows() {
    
    window* win;
    int status;
    Window w;
    Window *children;
    int nNumChildren;
    
    status = XQueryTree(dpy, root, &w, &w, &children, &nNumChildren);
    if (status == 0)
    {
        // Could not query window tree further, aborting
        return;
    }
    
    for (int i = 0; i < nNumChildren; i++) {
        w = children[i];
        win = add_window(w);
        init_window_object(win);
    }
    
}