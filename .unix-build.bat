javac -cp .:json-java.jar *.java

java -cp .:json-java.jar Runner fcfs fast-first.json -s
@REM java -cp .:json-java.jar Runner fcfs slow-first.json -s
@REM java -cp .:json-java.jar Runner fcfs low-priority-first.json -s
@REM java -cp .:json-java.jar Runner fcfs high-priority-first.json -s
@REM java -cp .:json-java.jar Runner fcfs random-1.json -s
@REM java -cp .:json-java.jar Runner fcfs random-2.json -s

@REM java -cp .:json-java.jar Runner sjf fast-first.json -s
@REM java -cp .:json-java.jar Runner sjf slow-first.json -s
@REM java -cp .:json-java.jar Runner sjf low-priority-first.json -s
@REM java -cp .:json-java.jar Runner sjf high-priority-first.json -s
@REM java -cp .:json-java.jar Runner sjf random-1.json -s
@REM java -cp .:json-java.jar Runner sjf random-2.json -s

@REM java -cp .:json-java.jar Runner ps fast-first.json -s
@REM java -cp .:json-java.jar Runner ps slow-first.json -s
@REM java -cp .:json-java.jar Runner ps low-priority-first.json -s
@REM java -cp .:json-java.jar Runner ps high-priority-first.json -s
@REM java -cp .:json-java.jar Runner ps random-1.json -s
@REM java -cp .:json-java.jar Runner ps random-2.json -s