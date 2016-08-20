/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include <stdio.h>
#include <stdlib.h>
#include <sys/queue.h>
#include "windows.h"

struct entry {
    window window;
    SLIST_ENTRY(entry) next;     /* Singly-linked List. */
};

SLIST_HEAD(windows_list, entry) windows_list;

void init_windows_store() {
    
    SLIST_INIT(&windows_list);
    
}

window* find_window_by_id (Window id) {
    
    struct entry *clientp;
    
    SLIST_FOREACH(clientp, &windows_list, next) {
        if (clientp->window.window == id) {
            return &clientp->window;
        }
    }
    
    return NULL;
    
}

window* add_window(Window id) {
    
    struct entry *clientp;
    
    clientp = malloc(sizeof(struct entry));
    if (clientp == NULL) {
        return NULL;
    }
    
    clientp->window.window = id;
    
    SLIST_INSERT_HEAD(&windows_list, clientp, next);
    
    return &clientp->window;
    
}

window* destroy_window(Window id) {
    
    window* result = NULL;
    struct entry *clientp;
    
    SLIST_FOREACH(clientp, &windows_list, next) {
        if (clientp->window.window == id) {
            result = &clientp->window;
            SLIST_REMOVE(&windows_list, clientp, entry, next);
            free(clientp);
            return result;
        }
    }
    
    return result;
    
}

void foreach_window(void (*exec)(window*, void*), void* status) {
    
    struct entry *clientp;
    
    SLIST_FOREACH(clientp, &windows_list, next) {
        exec(&clientp->window, status);
    }
    
}