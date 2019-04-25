# Project Title

JSPWiki ImageGen plugin for codeBeamer

## Getting Started

This repository contains the source code for our customized version of [ImageGen plugin](https://www.ecyrd.com/JSPWiki/wiki/ImageGen) used in codeBeamer ALM.

### Building

Use gradle to build the project:

```
gradlew jar
```

The command generates the JAR file in the **build\libs** directory. Default build creates a version of this plugin, which does not include the following JSPWiki plugins: 
- Horizontal Graph
- Vertical Graph
- Directed Acyclic Graph
- Explorer Graph
- Mindmap 

Use the following command to build a legacy version, which INCLUDES the aforementioned plugins:

```
gradlew legacyJar
```

## Installing legacy version

1. (Optional) Build the legacy version of ImageGenPlugin-intland.jar
2. Stop codeBamer ALM instance.
3. Copy the following files to CODEBEAMER_HOME/tomcat/webapps/cb/WEB-INF/lib
  * ImageGenPlugin-intland.jar (Source: [/dist/legacy/ImageGenPlugin-intland.jar](./dist/legacy/ImageGenPlugin-intland.jar))
  * graph-snipsnap.jar (Source: [/lib/graph-snipsnap.jar](./lib/graph-snipsnap.jar))
  * sequence.jar (Source: [/lib/sequence.jar](./lib/sequence.jar))
4. Restart codeBeamer ALM.

## Roll back to default version

1. (Optional) Build the ImageGenPlugin-intland.jar
2. Stop codeBamer ALM instance.
3. Copy the following files to CODEBEAMER_HOME/tomcat/webapps/cb/WEB-INF/lib
  * ImageGenPlugin-intland.jar (Source: [/dist/default/ImageGenPlugin-intland.jar](./dist/default/ImageGenPlugin-intland.jar))
4. Remove the following files from CODEBEAMER_HOME/tomcat/webapps/cb/WEB-INF/lib
  * graph-snipsnap.jar
  * sequence.jar
5. Restart codeBeamer ALM.


## License

Apache License	v2.0

