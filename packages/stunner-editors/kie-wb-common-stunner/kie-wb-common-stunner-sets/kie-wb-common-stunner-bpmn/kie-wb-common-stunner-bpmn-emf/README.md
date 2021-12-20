# Stunner - GWT support for BPMN2/EMF/XMI

## Introduction

This module encapsulates the EMF/XMI stuff and the jBPM customized models into a single artifact that can be exposed to the GWT compiler.

It's goal is to be the base stuff that allows to perform the Stunner's BPMN domain marshalling on pure client side, by re-using as much as possible Stunner's BPMN marshalling code which was already implemented on top Eclise EMF.

This module provides some basic GWT (client side) support for:

- Eclise EMF
- Eclise XMI
- BPMN2 Eclipse models (customized - jBPM)

_NOTE_: This module is NOT a generic GWT implementation for Eclipse EMF/XMI stuff. It's just focusing on the specific BPMN2 domain and actual implementations (based on Eclipse EMF) and how to make all those available on client side.

_NOTE_: This module is decoupled from Stunner - decoupled from both API and the Stunner's BPMN2 domain model as well). So please DO NOT add any dependency that results on unexpected coupling on Errai/UF/Stunner.

_TIP_: No need to run this module using the whole Stunner (as it's decoupled), so for faster development and bug fixing better using some simple GWT webapp (like [this example one](https://github.com/romartin/gwt-bpmn-marshallers).

## Implementation details

**A) Eclipse EMF - GWT support**
This module relies on the Eclipse EMF core GWT support plugins:

- [Ecore GWT plugin](https://github.com/eclipse/emf/tree/master/plugins/org.eclipse.emf.gwt.ecore) - `org.eclipselabs:org.eclipse.emf.gwt.ecore:2.9.0`
- [Ecore Common GWT plugin](https://github.com/eclipse/emf/tree/master/plugins/org.eclipse.emf.gwt.common) - `org.eclipselabs:org.eclipse.emf.gwt.common:2.9.0`

**B) Eclipse XMI - GWT support**
The existing Eclise XMI library cannot be exposed to client side, so this module includes the necessary classes to achieve our marshalling goals, based on moving/refactoring the existing code from the original library for our custom needs.

So the XMI - GWT support:

- Is being provided by all classes in the `org.eclipse.emf.ecore.xmi` package root in this module
- Code has been moved and then refactored from the library: `org.eclipse.emf:org.eclipse.emf.ecore.xmi:2.5.0.v20100521-1846`

**C) Custom BPMN2 Eclipse models**
This module also contains the custom jBPM Eclipse models for BPMN2 (EMF based):

- Model descriptors
  ** It contains the model and generator descriptor files inside the [model](./model) folder
  ** These files have been obtained from the [Tiho's BPMN2 repository fork](https://github.com/tsurdilo/bpmn2) and refactored in order to expose them to the GWT compiler

- Generated model code
  ** This module contains the generated code from the models above
  ** In case any model descriptor is updated, the generated classes must be also updated, see next section

## HowTo update (re-generate) model classes

**1.- Remove actual model classes** (previously generated)

    mvn clean -Pclean-generated-models

It removes the existing code previously generated from models - `org.eclipse.bpmn2.*` (`BPMN20.genmodel`) - `org.eclipse.dd.*` (`BPMN20.genmodel`) - `org.jboss.drools.*` (`bpmn2emfextmodel.genmodel`) - `bpsim.*` (`bpsim.genmodel`) - `org.omg.spec.bpmn.non.normative.color.*` (`bpmn2color.genmodel`)

NOTE: Manage properly the GIT changes
NOTE: Do NOT refactor/style generated code

**2.- (Re)Generate models**
2.1 - Configure Eclipse workspace - Install EMF support plugins in Eclipse - File -> Import -> Projects from Folder or Archive - Choose directory -> use _kie-wb-common-stunner-bpmn-emf_ as the root folder
2.2 - Apply the excepted model updates as well, if any (in ecore and genmodel files)
2.3 - Generate models - Right click -> Generate model code - `BPMN20.genmodel` - `bpmn2emfextmodel.genmodel` - `bpsim.genmodel` - `bpmn2color.genmodel`
2.4 - Remove the generated `gwt.xml` module files (a new GWT module is being created for each above `genmodel` file), no need for those.
2.5 - Reformat code according our KIE styles
2.6 - Update licenses/copy-rights

NOTE: Manage properly the GIT changes

**3.- Build & run & test app**

**4.- Commit updates**
