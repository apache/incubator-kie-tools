Lienzo Testing framework
=========================

This library provides an integration with the [GwtMockito](https://github.com/romartin/gwtmockito) testing library in order 
to test Lienzo based views.               

It provides testing doubles for different Lienzo's overlay types, concrete behaviors and native interfaces in order to
perform decoupled unit testing from a real browser or js engine interaction.                

This library is intended for unit test purposes, it provides the ability to use agnostic engine 
implementations from the browser/engine, but not all the lienzo native tools' functionality is completely covered. 
 It's a good idea to provide as well tests focused on real integrations, such using `GWTTestCase`, which 
 runs a minimal browser/engine scenario.             
 
Pre-requisites
==============

Before setup, please confirm these two pre-requisites:                      

* Your project dependency version resolved for *GWTMockito* artifact must be the same one that is used in this project.
 You can check it [here](./pom.xml). If you use a different gwtmockito version, the lienzo mockito is not guaranteed to be loaded. 
 Ensure your dependency tree resolves:                         
 
        com.google.gwt.gwtmockito:gwtmockito:1.1.6

Build
=====

Run the command:

            mvn clean install -DskipTests

Setup
=====

In order to use lienzo mockito follow the next steps:              

1.- Maven dependency              

Add this dependency into your `pom.xml`:                

            <dependency>
              <groupId>com.ahome-it</groupId>
              <artifactId>lienzo-tests</artifactId>
              <version>2.0.294-RELEASE</version>
              <scope>test</scope>
            </dependency>
            
Or for gradle:

	    compile(group: 'com.ahome-it', name: 'lienzo-tests', version: '2.0.294-RELEASE')
	

2.- Use the JUnit runner `LienzoMockitoTestRunner` in your test case:          

        @RunWith( LienzoMockitoTestRunner.class )
        public class MyLienzoTest {
            ...
            @Test
            public void test1() { }
            ...
        }
        
You can use `@Mock` and `@GwtMock` and the rest of the GwtMockito features as well.         

Examples
========

Examples [here](./src/test/java/com/ait/lienzo/test)

Why this library?
=================

There is a single goal - being able to perform unit testing for Lienzo's based views.                    

Key points to understand:                  

* Lienzo is a framework that is strongly tied to the GWT overlay types and native interfaces. This library provides a 
 JUnit runner built on top of GwtMockito. This decision has been done as GwtMockito solves for us
 many of the common problems when working with GWT, such as native interfaces, overlay types, the gwt bride... 
 you can lean more [here](https://github.com/romartin/gwtmockito).              

* GwtMockito does not solve all the concrete situations and behaviors for the Lienzo's concrete classes. In addition, most of the Lienzo's objects
 are not created through the GWT.create so we can either use features such as @GwtMock.
 
* GwtMockito provides no-op stubbed methods for the native interfaces, which solves most of our transitive dependencies from GWT. 
But on the other hand, this library provides a way to use your custom classes that act as stub for some Lienzo's ones, and defaults with some
 built-in stubs for most common objects used so we can avoid mocking same behaviors in several test classes.                
 
* This library provides no-op stub methods for Lienzo's native interfaces, if no stubbed classes provided for those, and removes the `final` modifiers from overlay types, shapes and nodes, 
so the developer can either provide custom stub classes or just mock the behaviors on the test cases.              

**Note for the developer**           
      
* Use custom class stubbing to perform unit testing based on the object's state.                       
* Use regular mocking to perform unit testing based on the interface behavior.                    

**Note about static method mocking**                
 
This library uses Mockito, which is not able to mock static methods.               

Previously to think about mocking static methods consider that unit-testing assumes that you can instantiate a piece 
of an application in isolation, on the other hand, static methods are considered procedural, they do not depend 
on any object's state so there are no real dependencies/test friends to be mocked. Consider if you have to refactor the static
 method before mocking its behavior.              

If for whatever reason you must mock an static method here are two options:              
* Provide a PR for a PowerMock/EasyMock integration :)                     
* Stub the class that contains the static method and provide your concrete implementation. See next *customization* section.          

Limitations
===========

Currently this library mainly allows to create Lienzo objects and perform mocking on final/native methods that could not be done without it. 
In addition provides some stubbed behaviors that are frequently used to assert our shapes/nodes states, such as object and arrays overlay types
translated to Java. but here are some of the current identified limitations or missing behaviors:            

* Limited stubbed classes              

Some default stubbed classes are provided to make the framework developer's life easier, 
but there are still lots of operations stubbed as no-op, if you need concrete behaviors for these situations you can mock and assert the method behavior 
or provide a custom stub class ( see `Customization` section ), and help us providing a PR for adding it as built-in :)                      

Customization
=============

This module provide some default stubbed methods with in-memory implementations (can be found usually [here](./src/main/java/com/ait/lienzo/test/stub))
 the rest of classes that contains native/final methods are stubbed using no-op implementations, so this module does NOT covers all the Lienzo's features. So at this point you can:                
* Mock the method behaviors and verify them. Take a look at the mocking examples [here](./src/test/java/com/ait/lienzo/test)                     
* Provide custom stubbed classes at compile time.                 

This sections describe how you can provide stubs for concrete classes if you need so.                    

The LienzoGwtMockito JUnit runner defaults with a given settings. Default settings [here](./src/main/java/com/ait/lienzo/test/annotation/Settings.java).                       
Read the following sections if you need to override or add setting entries.                       

**Enable Lienzo JUnit Runner logging**                  

        @RunWith( LienzoMockitoTestRunner.class )
        @Settings( logEnabled = true )
        public class MyLienzoTest {
            ...
        }

**Adding additional classes to stub**               

You can provide custom classes to use as "stub" for some other ones. 
A stub class can be created as:                          

        /**
         * In-memory Map stub for <code>com.ait.tooling.nativetools.client.NObjectJSO</code>
         */
        @StubClass( "com.ait.tooling.nativetools.client.NObjectJSO" )
        public class NObjectJSO  {
            ...
        }

* Must implement same methods and return types as the original class, but using your concrete impl.                               
* Modifiers `final` can be present or removed in your stub. Note that not removing the final modifier, the method cannot be further mocked.                 
* Modifiers `native` must be not present in your stub
* The class must be annotated with the `StubClass` annotation, which specified the fully qualified class name of the Lienzo's class to be stubbed. It's a String parameter as the class could be private / inner.                     

To use additional stubbed classes in your test case/s, add the annotation `Stub` on your test case specifying the stub/s class/es.
Note: Ensure the target class is present on the test classpath.                   

Example:               

A stubbed class declaration example:                  

        @StubClass( "com.ait.tooling.nativetools.client.AGivenClass" )
        public class MyStub  {
            ....
        }
        
If `MyStub` is present on the test classpath, you can use it in your test case as:                    

        @RunWith( LienzoMockitoTestRunner.class )
        @Stubs({ MyStub.class }})
        public class MyLienzoTest {
            ...
        }

You can provide several stubs as:                 

        @RunWith( LienzoMockitoTestRunner.class )
        @Stubs({ MyStub.class, AnotherStub.class })
        public class MyLienzoTest {
            ...
        }


**Adding additional overlay types (JSO) to stub/mock**                       

To avoid providing custom stubs for all the given JSO types and wrappers you can use JSO stubs.                               
The JSO stubs are considered Lienzo's overlay types that contain some concrete native methods that are using a 
common nomenclature along the framework. 
These concrete methods are stubbed with the concrete return objects, and the any other native/final method found in the class will be stubbed as no-operation.                


        @RunWith( LienzoMockitoTestRunner.class )
        @JSOStubs({
            "com.ait.lienzo.client.core.types.Point2D$Point2DJSO",
            "com.ait.lienzo.client.core.types.AnyOtherType"
            })
        public class MyLienzoTest {
            ...
        }

On the other hand, if the JSO and its class members can be mocked:                    

        @RunWith( LienzoMockitoTestRunner.class )
        @JSOMocks({
            "com.ait.lienzo.client.core.types.Point2D$Point2DJSO",
            "com.ait.lienzo.client.core.types.AnyOtherType"
            })
        public class MyLienzoTest {
            ...
        }

Note: Use <code>$</code> as the inner class separator character on the fqcn ( eg: `com.ait.lienzo.client.core.types.Point2D$Point2DJSO` ).                 

**Adding custom class translators**                                    

You can add custom class translators (javassist) implementing the `LienzoMockitoClassTranslator.TranslatorInterceptor` interface:                     

        public class MyCustomClassTranslator implements LienzoMockitoClassTranslator.TranslatorInterceptor {
            ...
        }
        
And use it for concrete tests as:               
        
        @RunWith( LienzoMockitoTestRunner.class )
        @Translators({
                MyCustomClassTranslator.class
        })
        public class MyLienzoTest {
            ...
        }

Note: Ensure the translator class is present on the test classpath.                   

**Overriding default settings**                            

The sections above describe how to add setting entries on top of the default ones.
 If you need to override the default settings:                         
  
        @RunWith( LienzoMockitoTestRunner.class )
        @Settings( 
                mocks = {
                        ---
                    },
                stubs = { 
                        ...
                    }, 
                jsoStubs = {
                        "..."
                }, 
                jsoMocks = {
                        "..."
                },
                translators = {
                        ...
                })
        public class MyLienzoTest {
            ...
        }

Appendix - Versions table
=========================

Use the testing artifact version supported for a concrete Lienzo-Core release:              

        Lienzo-Core            Lienzzo-Tests
        ************************************
        2.0.275-RELEASE          1.0.0-RC2
        2.0.292-RELEASE (+)      Using same versions as `lienzo-core`

