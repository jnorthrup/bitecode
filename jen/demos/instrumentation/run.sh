#!/bin/sh
CLASSPATH=lib/asm-2.1.jar:lib/asm-tree-2.1.jar:lib/asm-attrs-2.1.jar:lib/asm-commons-2.1.jar:lib/jen-0.14.jar:lib/junit-3.8.1.jar:target/agent-demo-1.0.jar

# For easy build the agent jar also carries the Main in this demo, and we append the whole lot to the boot
# classpath. We need to do this because we're adding our interface to everything, including _some_ platform 
# classes, so it needs to be available in the same loader (i.e. the bootstrap one) as them.
#
# In reality you would be more careful, perhaps having a jar with your boot classes, and a separate one for
# the agent, and everything else. Here we actually have to place it in both classpaths, which isn't ideal,
# but much easier for the demo ;) 
#
# If you have a non-Sun JRE you may need to adjust the arguments to suit.
java -Xbootclasspath/a:$CLASSPATH -javaagent:target/agent-demo-1.0.jar jen.demo.instrument.Main

