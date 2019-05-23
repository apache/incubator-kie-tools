Google Charts Renderer for Dashbuilder
--
This project is a Dashbuilder renderer implementation. It is not provided by default in Dashbuilder, Business Central, jBPM WB or Drools WB distribution. The default implementation is `dashbuilder-renderer-c3`. Users that want to use Google Renderer again must build the target web application from sources after including google renderer JAR. For example, if you want the Google Charts back in Business Central you must:

1) Go to `kie-wb-distributions/business-central-parent/business-central-webapp` and modify `pom.xml` to add google charts back:

~~~
...
<dependencies>
...
    <dependency>
      <groupId>org.dashbuilder</groupId>
      <artifactId>dashbuilder-renderer-google</artifactId>
      <scope>provided</scope>
    </dependency>
...
</dependencies>
...
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>gwt-maven-plugin</artifactId>
        <configuration>
          <compileSourcesArtifacts>
            ...
            <compileSourcesArtifact>org.dashbuilder:dashbuilder-renderer-google</compileSourcesArtifact>
            ...
          <//compileSourcesArtifacts>
        </configuration>
      </plugin>        
...
~~~

2) Also add the GCharts module (`org.dashbuilder.renderer.GChartsRenderer`) to `kie-wb-distributions/business-central-parent/business-central-webapp/src/main/resources/org/kie/bc/KIEWebapp.gwt.xml`:



~~~
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE module PUBLIC "-//Google Inc.//DTD Google Web Toolkit 2.4.0//EN"
    "http://google-web-toolkit.googlecode.com/svn/tags/2.5.0/distro-source/core/src/gwt-module.dtd">
<module>
  ...
  <inherits name="org.dashbuilder.renderer.GChartsRenderer"/>
  ...
</module>
~~~

3) On `business-central-webapp` run `mvn clean install`. It will build the business-central-webapp WAR. After the build finishes you can build a distribution WAR by building some of the projects in `kie-wb-distributions/business-central-parent/business-central-distribution-wars`

4) Finally make sure to select Google Charts using the system property `org.dashbuilder.renderer.default=gwtcharts` when starting the server were Business Central is installed. If you don't do this C3 will still be used.
