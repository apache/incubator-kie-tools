UberFire Dynamic Plugin
========

This is a dynamic plugin project for UberFire. It demonstrates development of runtime 
plugins using the Errai/UberFire programming model. Currently plugins are packaged as 
standard .jar files containing a /js subdir for all compiled JavaScript files.

Plugin jars can be deployed to UberFire's /plugin directory (e.g. in UberFire Showcase) 
either before an UberFire application starts or at runtime. The plugins are processed and 
the contained JavaScript files are injected into the DOM by Errai's ScriptInjector before 
the main application script is executed. This makes all perspectives, editors, screens, as
well as all other activity beans available to Errai's bean manager without requiring any 
hand-written registration logic. When UberFire applications bootstrap, all activities of 
all deployed plugins are automatically available.

This specific demo plugin provides a dynamic perspective with a custom menu and a dynamic 
screen and editor. Once the .jar is deployed to the plugin dir of UberFire Showcase, the 
plugin is processsed and the browser will prompt the user to activate the plugin. After 
that the plugin's perspective, its screens and editors will be available to the application.

Future steps
========================
1. Define an app bundle format to replace the .jar format and hold both client and server-side plugin code
1. Refactor UberFire to allow for fine-grained module reuse to shrink the compiled plugin size
1. Implement an UberFire marketplace to register / browse / download plugins

Notes
========================
1. The dynamic GWT plugin mechanism works side-by-side with the original plain JavaScript plugin mechanism in UberFire

Giving it a try
========================
1. Run `mvn clean gwt:run` in uberfire-showcase
2. Launch the showcase
3. Run `mvn clean install` in uberfire-dynamic-plugin
4. Copy `uberfire-dynamic-plugin/target/uberfire-dynamic-plugin.jar` to the showcase's plugin directory (`uberfire-showcase/src/main/webapp/plugins`)
5. A popup should appear after a few seconds indicating that a new plugins is available
6. After clicking OK, there should be a new perspective available (dynamic perspective), which contains a dynamic screen and editor (bound to a dynamic resource type)