build:
	javac MultiTimer.java
run:
	java MultiTimer
debug:
	javac Multitimer.java && jdb Multitimer
jar:
	javac Multitimer.java && jar cfe MultiTimer.jar MultiTimer *.class
