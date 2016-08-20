/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   layout.h
 * Author: ivano
 *
 * Created on 20 agosto 2016, 18.35
 */

#ifndef LAYOUT_H
#define LAYOUT_H

#include "windows.h"

#ifdef __cplusplus
extern "C" {
#endif
    
    void init_layout_manager(Display * dpy);
    
    void redo_layout(Display *dpy);
    
    void size_normal_window(window *window, XWindowChanges *changes, unsigned long *value_mask);
    void size_dock_window(window *window, XWindowChanges *changes, unsigned long *value_mask);

#ifdef __cplusplus
}
#endif

#endif /* LAYOUT_H */

