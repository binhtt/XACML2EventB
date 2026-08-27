# Installation

This document describes how to set up and run the XACML2Event-B tool.

## Requirements

The following software is required:

- Java Development Kit (JDK) 17 or later
- Eclipse IDE for Java Developers (optional)
- Rodin Platform for Event-B model verification

The XACML2Event-B transformation tool uses only standard Java libraries and
does not require additional third-party Java dependencies.

## Running in Eclipse

1. Create a new Java project named `XACML2EventB`.
2. Configure the project to use JDK 17 or later.
3. Copy the contents of the repository `src/` directory into the project source
   directory.
4. Ensure that the project contains the following structure:

       src/
       ├── module-info.java
       └── xacml2evtb/
           └── Xacml2EventBTool.java

5. The `module-info.java` file should contain:

       module XACML2EventB {
           requires java.desktop;
           requires java.xml;
       }

6. Open `Xacml2EventBTool.java`.
7. Select **Run As → Java Application**.

The graphical interface of XACML2Event-B should then be displayed.

## Command-Line Compilation

From the project directory, the source code can also be compiled using:

    javac -d out src/module-info.java src/xacml2evtb/Xacml2EventBTool.java

The application can then be executed using:

    java --module-path out -m XACML2EventB/xacml2evtb.Xacml2EventBTool

## Rodin Platform

The generated Event-B models can be inspected and verified using the Rodin
Platform. The Event-B models used in the experiments are provided separately
in the `models/` directory.
