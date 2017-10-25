/**
 * If running via command line, the properties are already set in the pom.xml file.
 * If running in an IDE, set these properties in your debug configuration VM arguments.
 * On Eclipse, Run / Debug Configurations... / Arguments / VM arguments:
 * -Dsun.net.spi.nameservice.provider.1=dns,OpenShiftNameService
 * -Dsun.net.spi.nameservice.provider.2=default
 * -Dorg.guvnor.ala.openshift.access.OpenShiftClientListener.postCreate=org.guvnor.ala.openshift.dns.OpenShiftNameServiceClientListener
 * -Dorg.guvnor.ala.openshift.dns.OpenShiftNameServiceClientListener.routerHost=10.34.75.115
 */
package org.guvnor.ala.openshift.dns;