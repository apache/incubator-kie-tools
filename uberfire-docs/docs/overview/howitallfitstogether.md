# How it all fits together
Built on the strengths of [GWT](http://www.gwtproject.org/) and [ERRAI](http://erraiframework.org/), Uberfire allows you to write and maintain your application code in the Java programming language with all of the Java tooling at your disposal, then deploy it to the browser as a native JavaScript + HTML application.

![code flow shart](shared-code-flowchart.png)

## Killer Features

Here we will highlight some key features of the framework.

### Extensible Plugins Architecture

One key aspect for UberFire is the compile time composition of plugins. Everything is a plugin, so it's very extensible. Uberfire also defines a set of interfaces and life cycle events, making it simple to build extensions of the framework.

Each plugin is a maven module, so when building a distribution, you simply need to add those maven modules as dependencies and they are avaliable to use in your web app.

### Flexible Layout

Drag-and-drop layouts give your users control over their work environment.

![dnd](drag-views.gif)

### Powerful VFS

UberFire has the power of GIT built in. A simple and clean NIO.2 based Virtual File System, using JGIT, ensures consistent APIs for both client and server. Supports change tracking and includes a metadata engine, full-text search, and security integration.

Client-side code that creates a file in the server-side VFS:

@Inject private FileSystem fs;
@Inject private Caller<VFSService> vfsServices;

```
public void onSaveButtonClicked() {
  Path path = PathFactory.newPath(
      fs, "readme.txt", "default://readme.txt");
  vfsServices.call().write(path, "Hello World!");
}```

###Fine-Grained Security

Fully pluggable authentication and authorization system. Includes file, database, PicketLink and JAAS out-of-the-box.


###Native Plugin System
Develop plugins in Java against our declarative, typesafe APIs, or choose your favorite JavaScript framework and develop using that.

Plugins can contribute new perspectives, views, editors, menu items, and more to an UberFire project.

![gwt](gwt-logo-100.png) ![jquery](jquery-ui-logo-100.png)![angular](angularjs-logo-100.png) ![ember](emberjs-logo-100.png)


###Modular Design
Use the parts you want, and leave the rest behind.

###Ready For Clustering
UberFire works flawlessly in clustered and highly-available deployments. GIT allows for a decentralised cluster, with efficient binary replication of content between nodes. Enjoy HA out-of-the-box: load balancing and failover just work!

###Open Source

Developed in the open by the people who use it. Join us!

```
$ git clone https://github.com/uberfire/uberfire.git
$ cd uberfire
$ mvn clean install```

UberFire is licensed under the [Apache Software License, Version 2.0.](http://www.apache.org/licenses/LICENSE-2.0.html)
