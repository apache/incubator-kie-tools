#Layout of Uberfire Archetype

Your Uberfire project will follow the standard Maven project layout. Most open source Java projects follow this layout these days, so this should look familiar.

Here’s a rundown of the specific files and directories you’ll find in every Uberfire project generated from Uberfire Archetype. Don’t get hung up on the details yet. We’ll get to each of these in turn.

##Archetype Structure

- **bom**: "bill of materials" of the archetype. It defines the versions of all the artifacts that will be created in the library
- **parent-with-dependencies**: declares all dependencies versions of the archetype.
- **component**: an example of uberfire component project.
- **showcase**: uberfire showcase directory, that contains the web app and distribution-wars.
