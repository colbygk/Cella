
GIT_VERSION := $(shell git describe --abbrev=4 --dirty --always --tags)
SRC_DIR=./src
LIB_DIR=./lib
PACKAGE=cs523/project2
JAR_CELLA_NAME=cs523-project2-cella.jar
JAR_GELLA_NAME=cs523-project2-gella.jar
JAR_CLASSES_NAME=cs523.jar
LOG4J_JARS=$(LIB_DIR)/log4j/log4j-1.2.17.jar
JUNIT_JAR=$(LIB_DIR)/junit/junit-4.12.jar
HAM_JAR=$(LIB_DIR)/junit/hamcrest-core-1.3.jar
COMMONS_CLI_JAR=$(LIB_DIR)/commons/commons-cli-1.2.jar
JAVA_REFLECTION_SOURCES = Diary.java XLevel.java Loggable.java
JAVA_FQ_REFLECTION_SOURCES = $(JAVA_REFLECTION_SOURCES:%.java=$(SRC_DIR)/%.java)
JAVA_TEST_SOURCES = GATest.java CATest.java NeighborhoodTest.java
JAVA_SOURCES = Neighborhood.java GA.java Gella.java CA.java Cella.java $(JAVA_TEST_SOURCES)
JAVA_FQ_SOURCES = $(JAVA_SOURCES:%.java=$(SRC_DIR)/%.java)
JAVA_NOLIB_CLASSES := $(patsubst %.java,$(PACKAGE)/%.class,$(JAVA_SOURCES) $(JAVA_REFLECTION_SOURCES)) $(PACKAGE)/GA$$1.class $(PACKAGE)/GA$$Worker.class
JAVA_REFLECTION_CLASSES = $(patsubst %.java,$(LIB_DIR)/$(PACKAGE)/%.class,$(JAVA_REFLECTION_SOURCES))
JAVA_CLASSES = $(patsubst %.java,$(LIB_DIR)/$(PACKAGE)/%.class,$(JAVA_SOURCES)) $(PACKAGE)/GA$$1.class $(PACKAGE)/GA$$Worker.class
TEMPFILE1 := $(shell mktemp /tmp/manifest.XXXXX)
TEMPFILE2 := $(shell mktemp /tmp/manifest.XXXXX)


COMPILE_JAVA_SOURCES = javac -sourcepath $(SRC_DIR) -classpath $(LIB_DIR):$(LOG4J_JARS):$(JUNIT_JAR):$(HAM_JAR):$(COMMONS_CLI_JAR) -d $(LIB_DIR) $(JAVA_FQ_SOURCES)
COMPILE_JAVA_REFLECTION_SOURCES = javac -Xlint:none -XDignore.symbol.file=true -sourcepath $(SRC_DIR) -classpath $(LIB_DIR):$(LOG4J_JARS):$(JUNIT_JAR):$(HAM_JAR):$(COMMONS_CLI_JAR) -d $(LIB_DIR) $(JAVA_FQ_REFLECTION_SOURCES)
RUN_JAVA_TESTS = java -Dlog4j.configuration=conf/default-stdout.xml -classpath $(LIB_DIR):$(LOG4J_JARS):$(JUNIT_JAR):$(HAM_JAR):$(COMMONS_CLI_JAR) org.junit.runner.JUnitCore $(patsubst %.java,$(subst /,.,$(PACKAGE)).%,$(JAVA_TEST_SOURCES))

all: build_jars

java_sources: $(JAVA_FQ_SOURCES)
	$(COMPILE_JAVA_SOURCES)

java_reflection_sources: $(JAVA_FQ_REFLECTION_SOURCES)
	$(COMPILE_JAVA_REFLECTION_SOURCES)

# Note, the manifest is also included in the class files jar
# so that the manifest can be quieried for the current version
# information
build_jars: java_reflection_sources java_sources
	sed s/__GIT_VERSION_INFO__/$(GIT_VERSION)/ conf/cella-manifest.mf > $(TEMPFILE1)
	sed s/__GIT_VERSION_INFO__/$(GIT_VERSION)/ conf/gella-manifest.mf > $(TEMPFILE2)
#cd lib && jar cf $(JAR_CLASSES_NAME) $(JAVA_NOLIB_CLASSES) $(PACKAGE)/GA$$1.class $(PACKAGE)/GA$$Worker.class
	cd lib && jar cfm $(JAR_CLASSES_NAME) $(TEMPFILE1) $(PACKAGE)/*.class
	jar cfm $(LIB_DIR)/$(JAR_CELLA_NAME) $(TEMPFILE1) $(LIB_DIR)/$(JAR_CLASSES_NAME) $(LOG4J_JARS) $(JUNIT_JAR) $(HAM_JAR) $(COMMONS_CLI_JAR)
	cd lib && jar cfm $(JAR_CLASSES_NAME) $(TEMPFILE2) $(PACKAGE)/*.class
	jar cfm $(LIB_DIR)/$(JAR_GELLA_NAME) $(TEMPFILE2) $(LIB_DIR)/$(JAR_CLASSES_NAME) $(LOG4J_JARS) $(JUNIT_JAR) $(HAM_JAR) $(COMMONS_CLI_JAR)
	rm -f $(TEMPFILE1)
	rm -f $(TEMPFILE2)

test: java_reflection_sources java_sources
	$(RUN_JAVA_TESTS)
	
clean:
	rm -rf $(JAVA_CLASSES) $(JAVA_REFLECTION_CLASSES) $(LIB_DIR)/$(JAR_CELLA_NAME) $(LIB_DIR)/$(JAR_GELLA_NAME) $(LIB_DIR)/$(JAR_CLASSES_NAME)
