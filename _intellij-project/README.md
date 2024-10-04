# KIE Tools @ IntelliJ IDEA

To make it easier for developing Java-, Maven-based packages, this folder contains an IntelliJ IDEA project configured for select packages. It currently includes all Maven-based packages of KIE Tools. Two additional IntelliJ IDEA project modules are included too, given their "global" nature on the KIE Tools repository -- `scripts` and `root-env`.

### Importing this project on IntelliJ IDEA

After cloning `kie-tools` locally:
1. Bootstrap the repository with `pnpm bootstrap [pnpm-filter?]` command to wire everything together and configure `.mvn/maven.config` files for all packages. You only need to do it once per clone or when you change the dependency graph on `package.json` files.
2. Build the repository in development mode with `pnpm [pnpm-filter?] build:dev` to make sure your local Maven repository (Usually `~/.m2/repository`) is populated.

It's recommended to that before opening IntelliJ IDEA to make indexing faster once you do.

Once prompted by IntelliJ IDEA, select "Open" and choose this folder.

<img src="docs/intellij-open.png" alt="drawing" style="max-width:600px; display:block" />
<img src="docs/intellij-select-folder.png" alt="drawing" style="max-width:600px; display:block" />

You should now be able to develop all Java- and Maven-based projects normally.

For more information on Maven-based packages on KIE Tools, please refere to the [KIE Tools repo manual](../repo/docs/user_manual.md)



