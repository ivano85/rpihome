# rpihome
A full open source software stack for a Raspberry PI based home automation system

---

This project aims to build all the needed software and other documents to turn a Raspberry PI in a full home automation system. The planned software are:

- X Window Manager
- Top panel for navigation
- A dashboard application to be used as home screen
- A platform for managing components

The main documents to be produced should be:

- A list of all needed hardware and other components, references, etc.
- One or more drawings for building a case to host all components in wall
- Any other document will be useful to the project

## User Interface

The user interface should be very simple, similiar to the Android's one. I mean one top bar with the following items:

- Back button (should close the current window)
- Icon (current window's icon)
- Title (current window's title)
- Current date and time
- Windows button (which opens the list of open windows)
- Home button

All the normal windows will have maximized state. Dialogs and splashes will have respected sizes, while docks must reserve space at the edges of the screen and will place in that space.

## X Window Manager

The very first version of the window manager is already implemented. It maximizes all normal windows and tries to manage docked windows, adapting layout of all windows.

I tried to implement it in Java, but libraries to interact with X11 appear to be buggy and incomplete, so I finally implemented it in C, directly using Xlib.

## Dashboard application

I am developing it using JavaFX. I would like to implement it the way Google Now is: cards allowing the user to manage everything, ordering them by relevance.

## What should it manage?

This system should be able to manage everything:

- Heating
- Indoor and outdoor lights
- WI-FI access (with a captive portal? Useful in hotels and similar)
- Door locks
- Sliding gates
- Video entryphones
- Watering the garden
- Motorized blinds
- Cameras and antitheft system
- Any other need you may have

All this should be manageable from a cloud platform or not, depending on the personal needs.

## Contribute

The project is not as big as it looks like, but any contribution is appreciated. What do I need?

- 3D/CAD designers
- C linux developers
- JavaFX developers

If you think this project could help your home to become a wonderful home, please contribute.

## Costs to build the whole system?

I didn't build it yet, but I can expect to spend **less than 400 euros** or 500 euros at most for everything listed excluding cameras and antitheft management. I didn't look for cameras yet.
