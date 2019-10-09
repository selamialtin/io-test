
export JAVA_HOME=$SNAP/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/jre/bin:$PATH
export PATH=$SNAP/bin/jre:$PATH

java -jar -cp $SNAP/bin/jre-ext/* $SNAP/bin/IOTester.jar
