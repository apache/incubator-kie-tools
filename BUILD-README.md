Errai Project Build README
=============================

This document describes how to set up your environment wrt maven prior to your build

Repositories
------------------
All you need to be able to donwload artifacts into your local M2 repository is to add following
repository definition into your ~/.m2/settings.xml file.

        <repository>
          <id>jboss-public-repository-group</id>
          <name>JBoss Public Maven Repository Group</name>
          <url>https://repository.jboss.org/nexus/content/groups/public/</url>
          <layout>default</layout>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </snapshots>
        </repository>

use the same definition for plugin repository

        <pluginRepository>
          <id>jboss-public-repository-group</id>
          <name>JBoss Public Maven Repository Group</name>
          <url>https://repository.jboss.org/nexus/content/groups/public/</url>
          <layout>default</layout>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </snapshots>
        </pluginRepository>


Sample settings.xml file
----------------------------
In this folder is prepared settings.xml file, which you can use to build (in case you do not want to use your default M2 settings)
eg:
	% mvn clean install -s settings.xml


