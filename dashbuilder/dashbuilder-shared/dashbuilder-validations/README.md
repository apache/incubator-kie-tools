Dashbuilder validations module
==============================

Introduction
--------------
Dashbuilder integrates with GWT & JSR303 Bean Validation framework in order to perform both client and server side bean validations.                

This module provides both client and server side validation factories, message resolvers and other related stuff.               

Notes
-----
* As by transitive resolution mechanism the dependency for <code>org.hibernate:hibernate-validator:jar</code> is set to version <code>4.3.X</code>, and this version is incompatible with GWT validation framework, this module is using [Dashbuilder Hibernate Validator](../dashbuilder-hibernate-validator/README.md).                
* As GWT validation framework does not support JSR303 custom Validation Providers (see <code>com.google.gwt.validation.client.impl.Validation#byProvider(Class<U> providerType)</code>, 
    this module is built on top of all dashbuilder shared modules and provides the default provider validation factory class and the default validation messages resolver.                      
