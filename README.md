# MultiTimer
MultiTimer is a tool to quickly and easily time multiple things at once.
Despite originally being written to get more practice with javafx back in 2017, I still use it quite regularly.

Do note that nothing is displayed until a timer is started, and the window can not be selected by mouse until a timer is started.

## Usage

MultiTimer is designed to be minimilistic in it's display, so no information is provided for it's control.

### Add a Timer

If any key with the exception of Backspace, Escape, or Space is pressed, a corrisponding timer will begin couting from the time the key was pressed.

### Reset a Timer

If a key is pressed that corrisponds to a timer that already exists is pressend, that timer is reset and begins counting up again.

### Remove a Timer

If ctrl is held while pressing the key for an existing timer, that timer is removed from the display.

### Remove all Timers

When Backspace is pressed, all existing timers are removed.

### Pause / Unpause

When Space is pressed, all timers are paused.
Pressing Space again causes counting to resume.

### Move Display

To move the display, simply click and drag anywhere with the mouse.

### Quit

When Escape is pressed the program will exit.
