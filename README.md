# JCedit-5.0

The version of the JCedit to support streaming CODA components.

Editor for creating and editing experiment control system
configuration files written in Control Oriented Ontology Language (COOL).

Users Manual is in progress...

Note:
To sign a jar file you have to:

a) create a key in the keystore
keytool -genkey -keystore vgKeys -alias jcedit

b) sign a jar with a key
jarsigner -keystore vgKeys xyz.jar jcedit

d) to run the program
java --add-opens java.base/java.lang=ALL-UNNAMED 
--add-opens java.base/java.lang.reflect=ALL-UNNAMED 
--add-opens java.base/java.lang.invoke=ALL-UNNAMED 
-jar jcedit-5.0-SNAPSHOT.jar
