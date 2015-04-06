
SRC_DIR=./src
LIB_DIR=./lib
LOG4J_JARS=$(LIB_DIR)/log4j/log4j-1.2.17.jar
JUNIT_JARS=$(LIB_DIR)/junit/hamcrest-core-1.3.jar:$(LIB_DIR)/junit/junit-4.12.jar
JAVA_SOURCES = ./src/Cella.java ./src/Diary.java ./src/XLevel.java

COMPILE_JAVA_SOURCES = javac -sourcepath $(SRC_DIR) -classpath $(LIB_DIR):$(LOG4J_JARS):$(JUNIT_JARS) -d $(LIB_DIR) $(JAVA_SOURCES)

all: java_sources

java_sources: $(JAVA_SOURCES)
	$(COMPILE_JAVA_SOURCES)


