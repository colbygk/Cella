
SRC_DIR=.
LIB_DIR=./lib
LOG4J_JARS=$(LIB_DIR)/log4j
JUNIT_JARS=$(LIB_DIR)/junit
JAVA_SOURCES = ./src/Cella.java

COMPILE_JAVA_SOURCES = javac -sourcepath $(SRC_DIR) -classpath $(LIB_DIR):$(LOG4J_JARS):$(JUNIT_JARS) -d $(LIB_DIR) $(JAVA_SOURCES)

all: java_sources

java_sources: $(JAVA_SOURCES)
	$(COMPILE_JAVA_SOURCES)


