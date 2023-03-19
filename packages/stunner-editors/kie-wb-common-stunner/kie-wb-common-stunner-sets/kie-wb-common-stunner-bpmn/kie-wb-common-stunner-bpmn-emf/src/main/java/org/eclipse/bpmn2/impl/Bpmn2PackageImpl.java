/**
 * <copyright>
 * 
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Reiner Hille-Doering (SAP AG) - initial API and implementation and/or initial documentation
 * 
 * </copyright>
 */
package org.eclipse.bpmn2.impl;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.AdHocOrdering;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.Auditing;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallChoreography;
import org.eclipse.bpmn2.CallConversation;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Category;
import org.eclipse.bpmn2.CategoryValue;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.ChoreographyLoopType;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ComplexBehaviorDefinition;
import org.eclipse.bpmn2.ComplexGateway;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.Conversation;
import org.eclipse.bpmn2.ConversationAssociation;
import org.eclipse.bpmn2.ConversationLink;
import org.eclipse.bpmn2.ConversationNode;
import org.eclipse.bpmn2.CorrelationKey;
import org.eclipse.bpmn2.CorrelationProperty;
import org.eclipse.bpmn2.CorrelationPropertyBinding;
import org.eclipse.bpmn2.CorrelationPropertyRetrievalExpression;
import org.eclipse.bpmn2.CorrelationSubscription;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.DataState;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.DataStoreReference;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.EndPoint;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Escalation;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.EventBasedGatewayType;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.EventSubprocess;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.Extension;
import org.eclipse.bpmn2.ExtensionAttributeDefinition;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.ExtensionDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.GatewayDirection;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalChoreographyTask;
import org.eclipse.bpmn2.GlobalConversation;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.HumanPerformer;
import org.eclipse.bpmn2.ImplicitThrowEvent;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.InputOutputBinding;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.InteractionNode;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.ItemKind;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.LoopCharacteristics;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.MessageFlowAssociation;
import org.eclipse.bpmn2.Monitoring;
import org.eclipse.bpmn2.MultiInstanceBehavior;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.ParticipantAssociation;
import org.eclipse.bpmn2.ParticipantMultiplicity;
import org.eclipse.bpmn2.PartnerEntity;
import org.eclipse.bpmn2.PartnerRole;
import org.eclipse.bpmn2.Performer;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.ProcessType;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.RelationshipDirection;
import org.eclipse.bpmn2.Rendering;
import org.eclipse.bpmn2.Resource;
import org.eclipse.bpmn2.ResourceAssignmentExpression;
import org.eclipse.bpmn2.ResourceParameter;
import org.eclipse.bpmn2.ResourceParameterBinding;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StandardLoopCharacteristics;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubChoreography;
import org.eclipse.bpmn2.SubConversation;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.Transaction;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BpmnDiPackage;
import org.eclipse.bpmn2.di.impl.BpmnDiPackageImpl;
import org.eclipse.dd.dc.DcPackage;
import org.eclipse.dd.dc.impl.DcPackageImpl;
import org.eclipse.dd.di.DiPackage;
import org.eclipse.dd.di.impl.DiPackageImpl;
import org.eclipse.emf.common.util.Reflect;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Bpmn2PackageImpl extends EPackageImpl implements Bpmn2Package {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass documentRootEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass activityEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass adHocSubProcessEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass artifactEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass assignmentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass associationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass auditingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass baseElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass boundaryEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass businessRuleTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass callActivityEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass callChoreographyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass callConversationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass callableElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass cancelEventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass catchEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass categoryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass categoryValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass choreographyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass choreographyActivityEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass choreographyTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass collaborationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass compensateEventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass complexBehaviorDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass complexGatewayEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conditionalEventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conversationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conversationAssociationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conversationLinkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass conversationNodeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass correlationKeyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass correlationPropertyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass correlationPropertyBindingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass correlationPropertyRetrievalExpressionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass correlationSubscriptionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataAssociationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataInputEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataInputAssociationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataObjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataObjectReferenceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataOutputEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataOutputAssociationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataStateEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataStoreEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dataStoreReferenceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass definitionsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass documentationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass endEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass endPointEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass errorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass errorEventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass escalationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass escalationEventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eventBasedGatewayEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass exclusiveGatewayEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass expressionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass extensionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass extensionAttributeDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass extensionAttributeValueEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass extensionDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass flowElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass flowElementsContainerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass flowNodeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass formalExpressionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass gatewayEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass globalBusinessRuleTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass globalChoreographyTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass globalConversationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass globalManualTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass globalScriptTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass globalTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass globalUserTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass groupEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass humanPerformerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass implicitThrowEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass importEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass inclusiveGatewayEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass inputOutputBindingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass inputOutputSpecificationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass inputSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass interactionNodeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass interfaceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass intermediateCatchEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass intermediateThrowEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass itemAwareElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass itemDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass laneEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass laneSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass linkEventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass loopCharacteristicsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass manualTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass messageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass messageEventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass messageFlowEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass messageFlowAssociationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass monitoringEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass multiInstanceLoopCharacteristicsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass operationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass outputSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parallelGatewayEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass participantEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass participantAssociationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass participantMultiplicityEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass partnerEntityEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass partnerRoleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass performerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass potentialOwnerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass processEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass propertyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass receiveTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass relationshipEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass renderingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resourceAssignmentExpressionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resourceParameterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resourceParameterBindingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resourceRoleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass rootElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass scriptTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sendTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sequenceFlowEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass serviceTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass signalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass signalEventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass standardLoopCharacteristicsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass startEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass subChoreographyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass subConversationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass subProcessEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass taskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass terminateEventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass textAnnotationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass throwEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass timerEventDefinitionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass transactionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass userTaskEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eventSubprocessEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum adHocOrderingEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum associationDirectionEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum choreographyLoopTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum eventBasedGatewayTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum gatewayDirectionEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum itemKindEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum multiInstanceBehaviorEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum processTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum relationshipDirectionEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.bpmn2.Bpmn2Package#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private Bpmn2PackageImpl() {
		super(eNS_URI, Bpmn2Factory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link Bpmn2Package#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static Bpmn2Package init() {
		if (isInited)
			return (Bpmn2Package) EPackage.Registry.INSTANCE.getEPackage(Bpmn2Package.eNS_URI);

		initializeRegistryHelpers();

		// Obtain or create and register package
		Object registeredBpmn2Package = EPackage.Registry.INSTANCE.get(eNS_URI);
		Bpmn2PackageImpl theBpmn2Package = registeredBpmn2Package instanceof Bpmn2PackageImpl
				? (Bpmn2PackageImpl) registeredBpmn2Package
				: new Bpmn2PackageImpl();

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(BpmnDiPackage.eNS_URI);
		BpmnDiPackageImpl theBpmnDiPackage = (BpmnDiPackageImpl) (registeredPackage instanceof BpmnDiPackageImpl
				? registeredPackage
				: BpmnDiPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(DiPackage.eNS_URI);
		DiPackageImpl theDiPackage = (DiPackageImpl) (registeredPackage instanceof DiPackageImpl ? registeredPackage
				: DiPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(DcPackage.eNS_URI);
		DcPackageImpl theDcPackage = (DcPackageImpl) (registeredPackage instanceof DcPackageImpl ? registeredPackage
				: DcPackage.eINSTANCE);

		// Create package meta-data objects
		theBpmn2Package.createPackageContents();
		theBpmnDiPackage.createPackageContents();
		theDiPackage.createPackageContents();
		theDcPackage.createPackageContents();

		// Initialize created meta-data
		theBpmn2Package.initializePackageContents();
		theBpmnDiPackage.initializePackageContents();
		theDiPackage.initializePackageContents();
		theDcPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theBpmn2Package.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(Bpmn2Package.eNS_URI, theBpmn2Package);
		return theBpmn2Package;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void initializeRegistryHelpers() {
		Reflect.register(DocumentRoot.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DocumentRoot;
			}

			public Object newArrayInstance(int size) {
				return new DocumentRoot[size];
			}
		});
		Reflect.register(Activity.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Activity;
			}

			public Object newArrayInstance(int size) {
				return new Activity[size];
			}
		});
		Reflect.register(AdHocSubProcess.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof AdHocSubProcess;
			}

			public Object newArrayInstance(int size) {
				return new AdHocSubProcess[size];
			}
		});
		Reflect.register(Artifact.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Artifact;
			}

			public Object newArrayInstance(int size) {
				return new Artifact[size];
			}
		});
		Reflect.register(Assignment.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Assignment;
			}

			public Object newArrayInstance(int size) {
				return new Assignment[size];
			}
		});
		Reflect.register(Association.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Association;
			}

			public Object newArrayInstance(int size) {
				return new Association[size];
			}
		});
		Reflect.register(Auditing.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Auditing;
			}

			public Object newArrayInstance(int size) {
				return new Auditing[size];
			}
		});
		Reflect.register(BaseElement.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof BaseElement;
			}

			public Object newArrayInstance(int size) {
				return new BaseElement[size];
			}
		});
		Reflect.register(BoundaryEvent.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof BoundaryEvent;
			}

			public Object newArrayInstance(int size) {
				return new BoundaryEvent[size];
			}
		});
		Reflect.register(BusinessRuleTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof BusinessRuleTask;
			}

			public Object newArrayInstance(int size) {
				return new BusinessRuleTask[size];
			}
		});
		Reflect.register(CallActivity.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CallActivity;
			}

			public Object newArrayInstance(int size) {
				return new CallActivity[size];
			}
		});
		Reflect.register(CallChoreography.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CallChoreography;
			}

			public Object newArrayInstance(int size) {
				return new CallChoreography[size];
			}
		});
		Reflect.register(CallConversation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CallConversation;
			}

			public Object newArrayInstance(int size) {
				return new CallConversation[size];
			}
		});
		Reflect.register(CallableElement.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CallableElement;
			}

			public Object newArrayInstance(int size) {
				return new CallableElement[size];
			}
		});
		Reflect.register(CancelEventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CancelEventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new CancelEventDefinition[size];
			}
		});
		Reflect.register(CatchEvent.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CatchEvent;
			}

			public Object newArrayInstance(int size) {
				return new CatchEvent[size];
			}
		});
		Reflect.register(Category.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Category;
			}

			public Object newArrayInstance(int size) {
				return new Category[size];
			}
		});
		Reflect.register(CategoryValue.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CategoryValue;
			}

			public Object newArrayInstance(int size) {
				return new CategoryValue[size];
			}
		});
		Reflect.register(Choreography.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Choreography;
			}

			public Object newArrayInstance(int size) {
				return new Choreography[size];
			}
		});
		Reflect.register(ChoreographyActivity.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ChoreographyActivity;
			}

			public Object newArrayInstance(int size) {
				return new ChoreographyActivity[size];
			}
		});
		Reflect.register(ChoreographyTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ChoreographyTask;
			}

			public Object newArrayInstance(int size) {
				return new ChoreographyTask[size];
			}
		});
		Reflect.register(Collaboration.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Collaboration;
			}

			public Object newArrayInstance(int size) {
				return new Collaboration[size];
			}
		});
		Reflect.register(CompensateEventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CompensateEventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new CompensateEventDefinition[size];
			}
		});
		Reflect.register(ComplexBehaviorDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ComplexBehaviorDefinition;
			}

			public Object newArrayInstance(int size) {
				return new ComplexBehaviorDefinition[size];
			}
		});
		Reflect.register(ComplexGateway.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ComplexGateway;
			}

			public Object newArrayInstance(int size) {
				return new ComplexGateway[size];
			}
		});
		Reflect.register(ConditionalEventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ConditionalEventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new ConditionalEventDefinition[size];
			}
		});
		Reflect.register(Conversation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Conversation;
			}

			public Object newArrayInstance(int size) {
				return new Conversation[size];
			}
		});
		Reflect.register(ConversationAssociation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ConversationAssociation;
			}

			public Object newArrayInstance(int size) {
				return new ConversationAssociation[size];
			}
		});
		Reflect.register(ConversationLink.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ConversationLink;
			}

			public Object newArrayInstance(int size) {
				return new ConversationLink[size];
			}
		});
		Reflect.register(ConversationNode.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ConversationNode;
			}

			public Object newArrayInstance(int size) {
				return new ConversationNode[size];
			}
		});
		Reflect.register(CorrelationKey.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CorrelationKey;
			}

			public Object newArrayInstance(int size) {
				return new CorrelationKey[size];
			}
		});
		Reflect.register(CorrelationProperty.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CorrelationProperty;
			}

			public Object newArrayInstance(int size) {
				return new CorrelationProperty[size];
			}
		});
		Reflect.register(CorrelationPropertyBinding.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CorrelationPropertyBinding;
			}

			public Object newArrayInstance(int size) {
				return new CorrelationPropertyBinding[size];
			}
		});
		Reflect.register(CorrelationPropertyRetrievalExpression.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CorrelationPropertyRetrievalExpression;
			}

			public Object newArrayInstance(int size) {
				return new CorrelationPropertyRetrievalExpression[size];
			}
		});
		Reflect.register(CorrelationSubscription.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof CorrelationSubscription;
			}

			public Object newArrayInstance(int size) {
				return new CorrelationSubscription[size];
			}
		});
		Reflect.register(DataAssociation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DataAssociation;
			}

			public Object newArrayInstance(int size) {
				return new DataAssociation[size];
			}
		});
		Reflect.register(DataInput.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DataInput;
			}

			public Object newArrayInstance(int size) {
				return new DataInput[size];
			}
		});
		Reflect.register(DataInputAssociation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DataInputAssociation;
			}

			public Object newArrayInstance(int size) {
				return new DataInputAssociation[size];
			}
		});
		Reflect.register(DataObject.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DataObject;
			}

			public Object newArrayInstance(int size) {
				return new DataObject[size];
			}
		});
		Reflect.register(DataObjectReference.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DataObjectReference;
			}

			public Object newArrayInstance(int size) {
				return new DataObjectReference[size];
			}
		});
		Reflect.register(DataOutput.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DataOutput;
			}

			public Object newArrayInstance(int size) {
				return new DataOutput[size];
			}
		});
		Reflect.register(DataOutputAssociation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DataOutputAssociation;
			}

			public Object newArrayInstance(int size) {
				return new DataOutputAssociation[size];
			}
		});
		Reflect.register(DataState.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DataState;
			}

			public Object newArrayInstance(int size) {
				return new DataState[size];
			}
		});
		Reflect.register(DataStore.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DataStore;
			}

			public Object newArrayInstance(int size) {
				return new DataStore[size];
			}
		});
		Reflect.register(DataStoreReference.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof DataStoreReference;
			}

			public Object newArrayInstance(int size) {
				return new DataStoreReference[size];
			}
		});
		Reflect.register(Definitions.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Definitions;
			}

			public Object newArrayInstance(int size) {
				return new Definitions[size];
			}
		});
		Reflect.register(Documentation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Documentation;
			}

			public Object newArrayInstance(int size) {
				return new Documentation[size];
			}
		});
		Reflect.register(EndEvent.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof EndEvent;
			}

			public Object newArrayInstance(int size) {
				return new EndEvent[size];
			}
		});
		Reflect.register(EndPoint.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof EndPoint;
			}

			public Object newArrayInstance(int size) {
				return new EndPoint[size];
			}
		});
		Reflect.register(org.eclipse.bpmn2.Error.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof org.eclipse.bpmn2.Error;
			}

			public Object newArrayInstance(int size) {
				return new org.eclipse.bpmn2.Error[size];
			}
		});
		Reflect.register(ErrorEventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ErrorEventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new ErrorEventDefinition[size];
			}
		});
		Reflect.register(Escalation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Escalation;
			}

			public Object newArrayInstance(int size) {
				return new Escalation[size];
			}
		});
		Reflect.register(EscalationEventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof EscalationEventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new EscalationEventDefinition[size];
			}
		});
		Reflect.register(Event.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Event;
			}

			public Object newArrayInstance(int size) {
				return new Event[size];
			}
		});
		Reflect.register(EventBasedGateway.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof EventBasedGateway;
			}

			public Object newArrayInstance(int size) {
				return new EventBasedGateway[size];
			}
		});
		Reflect.register(EventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof EventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new EventDefinition[size];
			}
		});
		Reflect.register(ExclusiveGateway.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ExclusiveGateway;
			}

			public Object newArrayInstance(int size) {
				return new ExclusiveGateway[size];
			}
		});
		Reflect.register(Expression.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Expression;
			}

			public Object newArrayInstance(int size) {
				return new Expression[size];
			}
		});
		Reflect.register(Extension.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Extension;
			}

			public Object newArrayInstance(int size) {
				return new Extension[size];
			}
		});
		Reflect.register(ExtensionAttributeDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ExtensionAttributeDefinition;
			}

			public Object newArrayInstance(int size) {
				return new ExtensionAttributeDefinition[size];
			}
		});
		Reflect.register(ExtensionAttributeValue.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ExtensionAttributeValue;
			}

			public Object newArrayInstance(int size) {
				return new ExtensionAttributeValue[size];
			}
		});
		Reflect.register(ExtensionDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ExtensionDefinition;
			}

			public Object newArrayInstance(int size) {
				return new ExtensionDefinition[size];
			}
		});
		Reflect.register(FlowElement.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof FlowElement;
			}

			public Object newArrayInstance(int size) {
				return new FlowElement[size];
			}
		});
		Reflect.register(FlowElementsContainer.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof FlowElementsContainer;
			}

			public Object newArrayInstance(int size) {
				return new FlowElementsContainer[size];
			}
		});
		Reflect.register(FlowNode.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof FlowNode;
			}

			public Object newArrayInstance(int size) {
				return new FlowNode[size];
			}
		});
		Reflect.register(FormalExpression.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof FormalExpression;
			}

			public Object newArrayInstance(int size) {
				return new FormalExpression[size];
			}
		});
		Reflect.register(Gateway.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Gateway;
			}

			public Object newArrayInstance(int size) {
				return new Gateway[size];
			}
		});
		Reflect.register(GlobalBusinessRuleTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof GlobalBusinessRuleTask;
			}

			public Object newArrayInstance(int size) {
				return new GlobalBusinessRuleTask[size];
			}
		});
		Reflect.register(GlobalChoreographyTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof GlobalChoreographyTask;
			}

			public Object newArrayInstance(int size) {
				return new GlobalChoreographyTask[size];
			}
		});
		Reflect.register(GlobalConversation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof GlobalConversation;
			}

			public Object newArrayInstance(int size) {
				return new GlobalConversation[size];
			}
		});
		Reflect.register(GlobalManualTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof GlobalManualTask;
			}

			public Object newArrayInstance(int size) {
				return new GlobalManualTask[size];
			}
		});
		Reflect.register(GlobalScriptTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof GlobalScriptTask;
			}

			public Object newArrayInstance(int size) {
				return new GlobalScriptTask[size];
			}
		});
		Reflect.register(GlobalTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof GlobalTask;
			}

			public Object newArrayInstance(int size) {
				return new GlobalTask[size];
			}
		});
		Reflect.register(GlobalUserTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof GlobalUserTask;
			}

			public Object newArrayInstance(int size) {
				return new GlobalUserTask[size];
			}
		});
		Reflect.register(Group.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Group;
			}

			public Object newArrayInstance(int size) {
				return new Group[size];
			}
		});
		Reflect.register(HumanPerformer.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof HumanPerformer;
			}

			public Object newArrayInstance(int size) {
				return new HumanPerformer[size];
			}
		});
		Reflect.register(ImplicitThrowEvent.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ImplicitThrowEvent;
			}

			public Object newArrayInstance(int size) {
				return new ImplicitThrowEvent[size];
			}
		});
		Reflect.register(Import.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Import;
			}

			public Object newArrayInstance(int size) {
				return new Import[size];
			}
		});
		Reflect.register(InclusiveGateway.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof InclusiveGateway;
			}

			public Object newArrayInstance(int size) {
				return new InclusiveGateway[size];
			}
		});
		Reflect.register(InputOutputBinding.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof InputOutputBinding;
			}

			public Object newArrayInstance(int size) {
				return new InputOutputBinding[size];
			}
		});
		Reflect.register(InputOutputSpecification.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof InputOutputSpecification;
			}

			public Object newArrayInstance(int size) {
				return new InputOutputSpecification[size];
			}
		});
		Reflect.register(InputSet.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof InputSet;
			}

			public Object newArrayInstance(int size) {
				return new InputSet[size];
			}
		});
		Reflect.register(InteractionNode.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof InteractionNode;
			}

			public Object newArrayInstance(int size) {
				return new InteractionNode[size];
			}
		});
		Reflect.register(Interface.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Interface;
			}

			public Object newArrayInstance(int size) {
				return new Interface[size];
			}
		});
		Reflect.register(IntermediateCatchEvent.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof IntermediateCatchEvent;
			}

			public Object newArrayInstance(int size) {
				return new IntermediateCatchEvent[size];
			}
		});
		Reflect.register(IntermediateThrowEvent.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof IntermediateThrowEvent;
			}

			public Object newArrayInstance(int size) {
				return new IntermediateThrowEvent[size];
			}
		});
		Reflect.register(ItemAwareElement.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ItemAwareElement;
			}

			public Object newArrayInstance(int size) {
				return new ItemAwareElement[size];
			}
		});
		Reflect.register(ItemDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ItemDefinition;
			}

			public Object newArrayInstance(int size) {
				return new ItemDefinition[size];
			}
		});
		Reflect.register(Lane.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Lane;
			}

			public Object newArrayInstance(int size) {
				return new Lane[size];
			}
		});
		Reflect.register(LaneSet.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof LaneSet;
			}

			public Object newArrayInstance(int size) {
				return new LaneSet[size];
			}
		});
		Reflect.register(LinkEventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof LinkEventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new LinkEventDefinition[size];
			}
		});
		Reflect.register(LoopCharacteristics.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof LoopCharacteristics;
			}

			public Object newArrayInstance(int size) {
				return new LoopCharacteristics[size];
			}
		});
		Reflect.register(ManualTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ManualTask;
			}

			public Object newArrayInstance(int size) {
				return new ManualTask[size];
			}
		});
		Reflect.register(Message.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Message;
			}

			public Object newArrayInstance(int size) {
				return new Message[size];
			}
		});
		Reflect.register(MessageEventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof MessageEventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new MessageEventDefinition[size];
			}
		});
		Reflect.register(MessageFlow.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof MessageFlow;
			}

			public Object newArrayInstance(int size) {
				return new MessageFlow[size];
			}
		});
		Reflect.register(MessageFlowAssociation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof MessageFlowAssociation;
			}

			public Object newArrayInstance(int size) {
				return new MessageFlowAssociation[size];
			}
		});
		Reflect.register(Monitoring.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Monitoring;
			}

			public Object newArrayInstance(int size) {
				return new Monitoring[size];
			}
		});
		Reflect.register(MultiInstanceLoopCharacteristics.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof MultiInstanceLoopCharacteristics;
			}

			public Object newArrayInstance(int size) {
				return new MultiInstanceLoopCharacteristics[size];
			}
		});
		Reflect.register(Operation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Operation;
			}

			public Object newArrayInstance(int size) {
				return new Operation[size];
			}
		});
		Reflect.register(OutputSet.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof OutputSet;
			}

			public Object newArrayInstance(int size) {
				return new OutputSet[size];
			}
		});
		Reflect.register(ParallelGateway.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ParallelGateway;
			}

			public Object newArrayInstance(int size) {
				return new ParallelGateway[size];
			}
		});
		Reflect.register(Participant.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Participant;
			}

			public Object newArrayInstance(int size) {
				return new Participant[size];
			}
		});
		Reflect.register(ParticipantAssociation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ParticipantAssociation;
			}

			public Object newArrayInstance(int size) {
				return new ParticipantAssociation[size];
			}
		});
		Reflect.register(ParticipantMultiplicity.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ParticipantMultiplicity;
			}

			public Object newArrayInstance(int size) {
				return new ParticipantMultiplicity[size];
			}
		});
		Reflect.register(PartnerEntity.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof PartnerEntity;
			}

			public Object newArrayInstance(int size) {
				return new PartnerEntity[size];
			}
		});
		Reflect.register(PartnerRole.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof PartnerRole;
			}

			public Object newArrayInstance(int size) {
				return new PartnerRole[size];
			}
		});
		Reflect.register(Performer.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Performer;
			}

			public Object newArrayInstance(int size) {
				return new Performer[size];
			}
		});
		Reflect.register(PotentialOwner.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof PotentialOwner;
			}

			public Object newArrayInstance(int size) {
				return new PotentialOwner[size];
			}
		});
		Reflect.register(org.eclipse.bpmn2.Process.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof org.eclipse.bpmn2.Process;
			}

			public Object newArrayInstance(int size) {
				return new org.eclipse.bpmn2.Process[size];
			}
		});
		Reflect.register(Property.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Property;
			}

			public Object newArrayInstance(int size) {
				return new Property[size];
			}
		});
		Reflect.register(ReceiveTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ReceiveTask;
			}

			public Object newArrayInstance(int size) {
				return new ReceiveTask[size];
			}
		});
		Reflect.register(Relationship.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Relationship;
			}

			public Object newArrayInstance(int size) {
				return new Relationship[size];
			}
		});
		Reflect.register(Rendering.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Rendering;
			}

			public Object newArrayInstance(int size) {
				return new Rendering[size];
			}
		});
		Reflect.register(Resource.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Resource;
			}

			public Object newArrayInstance(int size) {
				return new Resource[size];
			}
		});
		Reflect.register(ResourceAssignmentExpression.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ResourceAssignmentExpression;
			}

			public Object newArrayInstance(int size) {
				return new ResourceAssignmentExpression[size];
			}
		});
		Reflect.register(ResourceParameter.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ResourceParameter;
			}

			public Object newArrayInstance(int size) {
				return new ResourceParameter[size];
			}
		});
		Reflect.register(ResourceParameterBinding.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ResourceParameterBinding;
			}

			public Object newArrayInstance(int size) {
				return new ResourceParameterBinding[size];
			}
		});
		Reflect.register(ResourceRole.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ResourceRole;
			}

			public Object newArrayInstance(int size) {
				return new ResourceRole[size];
			}
		});
		Reflect.register(RootElement.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof RootElement;
			}

			public Object newArrayInstance(int size) {
				return new RootElement[size];
			}
		});
		Reflect.register(ScriptTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ScriptTask;
			}

			public Object newArrayInstance(int size) {
				return new ScriptTask[size];
			}
		});
		Reflect.register(SendTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof SendTask;
			}

			public Object newArrayInstance(int size) {
				return new SendTask[size];
			}
		});
		Reflect.register(SequenceFlow.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof SequenceFlow;
			}

			public Object newArrayInstance(int size) {
				return new SequenceFlow[size];
			}
		});
		Reflect.register(ServiceTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ServiceTask;
			}

			public Object newArrayInstance(int size) {
				return new ServiceTask[size];
			}
		});
		Reflect.register(Signal.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Signal;
			}

			public Object newArrayInstance(int size) {
				return new Signal[size];
			}
		});
		Reflect.register(SignalEventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof SignalEventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new SignalEventDefinition[size];
			}
		});
		Reflect.register(StandardLoopCharacteristics.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof StandardLoopCharacteristics;
			}

			public Object newArrayInstance(int size) {
				return new StandardLoopCharacteristics[size];
			}
		});
		Reflect.register(StartEvent.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof StartEvent;
			}

			public Object newArrayInstance(int size) {
				return new StartEvent[size];
			}
		});
		Reflect.register(SubChoreography.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof SubChoreography;
			}

			public Object newArrayInstance(int size) {
				return new SubChoreography[size];
			}
		});
		Reflect.register(SubConversation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof SubConversation;
			}

			public Object newArrayInstance(int size) {
				return new SubConversation[size];
			}
		});
		Reflect.register(SubProcess.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof SubProcess;
			}

			public Object newArrayInstance(int size) {
				return new SubProcess[size];
			}
		});
		Reflect.register(Task.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Task;
			}

			public Object newArrayInstance(int size) {
				return new Task[size];
			}
		});
		Reflect.register(TerminateEventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof TerminateEventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new TerminateEventDefinition[size];
			}
		});
		Reflect.register(TextAnnotation.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof TextAnnotation;
			}

			public Object newArrayInstance(int size) {
				return new TextAnnotation[size];
			}
		});
		Reflect.register(ThrowEvent.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ThrowEvent;
			}

			public Object newArrayInstance(int size) {
				return new ThrowEvent[size];
			}
		});
		Reflect.register(TimerEventDefinition.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof TimerEventDefinition;
			}

			public Object newArrayInstance(int size) {
				return new TimerEventDefinition[size];
			}
		});
		Reflect.register(Transaction.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof Transaction;
			}

			public Object newArrayInstance(int size) {
				return new Transaction[size];
			}
		});
		Reflect.register(UserTask.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof UserTask;
			}

			public Object newArrayInstance(int size) {
				return new UserTask[size];
			}
		});
		Reflect.register(EventSubprocess.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof EventSubprocess;
			}

			public Object newArrayInstance(int size) {
				return new EventSubprocess[size];
			}
		});
		Reflect.register(AdHocOrdering.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof AdHocOrdering;
			}

			public Object newArrayInstance(int size) {
				return new AdHocOrdering[size];
			}
		});
		Reflect.register(AssociationDirection.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof AssociationDirection;
			}

			public Object newArrayInstance(int size) {
				return new AssociationDirection[size];
			}
		});
		Reflect.register(ChoreographyLoopType.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ChoreographyLoopType;
			}

			public Object newArrayInstance(int size) {
				return new ChoreographyLoopType[size];
			}
		});
		Reflect.register(EventBasedGatewayType.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof EventBasedGatewayType;
			}

			public Object newArrayInstance(int size) {
				return new EventBasedGatewayType[size];
			}
		});
		Reflect.register(GatewayDirection.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof GatewayDirection;
			}

			public Object newArrayInstance(int size) {
				return new GatewayDirection[size];
			}
		});
		Reflect.register(ItemKind.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ItemKind;
			}

			public Object newArrayInstance(int size) {
				return new ItemKind[size];
			}
		});
		Reflect.register(MultiInstanceBehavior.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof MultiInstanceBehavior;
			}

			public Object newArrayInstance(int size) {
				return new MultiInstanceBehavior[size];
			}
		});
		Reflect.register(ProcessType.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof ProcessType;
			}

			public Object newArrayInstance(int size) {
				return new ProcessType[size];
			}
		});
		Reflect.register(RelationshipDirection.class, new Reflect.Helper() {
			public boolean isInstance(Object instance) {
				return instance instanceof RelationshipDirection;
			}

			public Object newArrayInstance(int size) {
				return new RelationshipDirection[size];
			}
		});
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static class AllowList implements IsSerializable, EBasicWhiteList {
		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DocumentRoot documentRoot;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Activity activity;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected AdHocSubProcess adHocSubProcess;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Artifact artifact;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Assignment assignment;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Association association;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Auditing auditing;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected BaseElement baseElement;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected BoundaryEvent boundaryEvent;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected BusinessRuleTask businessRuleTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CallActivity callActivity;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CallChoreography callChoreography;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CallConversation callConversation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CallableElement callableElement;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CancelEventDefinition cancelEventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CatchEvent catchEvent;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Category category;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CategoryValue categoryValue;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Choreography choreography;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ChoreographyActivity choreographyActivity;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ChoreographyTask choreographyTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Collaboration collaboration;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CompensateEventDefinition compensateEventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ComplexBehaviorDefinition complexBehaviorDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ComplexGateway complexGateway;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ConditionalEventDefinition conditionalEventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Conversation conversation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ConversationAssociation conversationAssociation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ConversationLink conversationLink;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ConversationNode conversationNode;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CorrelationKey correlationKey;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CorrelationProperty correlationProperty;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CorrelationPropertyBinding correlationPropertyBinding;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CorrelationPropertyRetrievalExpression correlationPropertyRetrievalExpression;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected CorrelationSubscription correlationSubscription;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DataAssociation dataAssociation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DataInput dataInput;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DataInputAssociation dataInputAssociation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DataObject dataObject;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DataObjectReference dataObjectReference;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DataOutput dataOutput;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DataOutputAssociation dataOutputAssociation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DataState dataState;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DataStore dataStore;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected DataStoreReference dataStoreReference;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Definitions definitions;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Documentation documentation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected EndEvent endEvent;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected EndPoint endPoint;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected org.eclipse.bpmn2.Error error;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ErrorEventDefinition errorEventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Escalation escalation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected EscalationEventDefinition escalationEventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Event event;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected EventBasedGateway eventBasedGateway;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected EventDefinition eventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ExclusiveGateway exclusiveGateway;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Expression expression;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Extension extension;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ExtensionAttributeDefinition extensionAttributeDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ExtensionAttributeValue extensionAttributeValue;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ExtensionDefinition extensionDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected FlowElement flowElement;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected FlowElementsContainer flowElementsContainer;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected FlowNode flowNode;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected FormalExpression formalExpression;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Gateway gateway;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected GlobalBusinessRuleTask globalBusinessRuleTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected GlobalChoreographyTask globalChoreographyTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected GlobalConversation globalConversation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected GlobalManualTask globalManualTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected GlobalScriptTask globalScriptTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected GlobalTask globalTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected GlobalUserTask globalUserTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Group group;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected HumanPerformer humanPerformer;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ImplicitThrowEvent implicitThrowEvent;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Import import_;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected InclusiveGateway inclusiveGateway;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected InputOutputBinding inputOutputBinding;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected InputOutputSpecification inputOutputSpecification;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected InputSet inputSet;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected InteractionNode interactionNode;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Interface interface_;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected IntermediateCatchEvent intermediateCatchEvent;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected IntermediateThrowEvent intermediateThrowEvent;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ItemAwareElement itemAwareElement;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ItemDefinition itemDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Lane lane;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected LaneSet laneSet;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected LinkEventDefinition linkEventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected LoopCharacteristics loopCharacteristics;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ManualTask manualTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Message message;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected MessageEventDefinition messageEventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected MessageFlow messageFlow;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected MessageFlowAssociation messageFlowAssociation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Monitoring monitoring;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Operation operation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected OutputSet outputSet;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ParallelGateway parallelGateway;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Participant participant;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ParticipantAssociation participantAssociation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ParticipantMultiplicity participantMultiplicity;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected PartnerEntity partnerEntity;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected PartnerRole partnerRole;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Performer performer;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected PotentialOwner potentialOwner;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected org.eclipse.bpmn2.Process process;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Property property;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ReceiveTask receiveTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Relationship relationship;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Rendering rendering;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Resource resource;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ResourceAssignmentExpression resourceAssignmentExpression;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ResourceParameter resourceParameter;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ResourceParameterBinding resourceParameterBinding;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ResourceRole resourceRole;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected RootElement rootElement;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ScriptTask scriptTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected SendTask sendTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected SequenceFlow sequenceFlow;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ServiceTask serviceTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Signal signal;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected SignalEventDefinition signalEventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected StandardLoopCharacteristics standardLoopCharacteristics;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected StartEvent startEvent;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected SubChoreography subChoreography;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected SubConversation subConversation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected SubProcess subProcess;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Task task;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected TerminateEventDefinition terminateEventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected TextAnnotation textAnnotation;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ThrowEvent throwEvent;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected TimerEventDefinition timerEventDefinition;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected Transaction transaction;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected UserTask userTask;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected EventSubprocess eventSubprocess;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected AdHocOrdering adHocOrdering;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected AssociationDirection associationDirection;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ChoreographyLoopType choreographyLoopType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected EventBasedGatewayType eventBasedGatewayType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected GatewayDirection gatewayDirection;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ItemKind itemKind;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected MultiInstanceBehavior multiInstanceBehavior;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected ProcessType processType;

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		protected RelationshipDirection relationshipDirection;

	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDocumentRoot() {
		return documentRootEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentRoot_Mixed() {
		return (EAttribute) documentRootEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_XMLNSPrefixMap() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_XSISchemaLocation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Activity() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_AdHocSubProcess() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_FlowElement() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Artifact() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Assignment() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Association() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Auditing() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_BaseElement() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_BaseElementWithMixedContent() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_BoundaryEvent() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_BusinessRuleTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CallableElement() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CallActivity() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CallChoreography() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CallConversation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ConversationNode() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CancelEventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_EventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_RootElement() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(21);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CatchEvent() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(22);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Category() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(23);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CategoryValue() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(24);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Choreography() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(25);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Collaboration() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(26);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ChoreographyActivity() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(27);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ChoreographyTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(28);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CompensateEventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(29);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ComplexBehaviorDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(30);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ComplexGateway() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(31);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ConditionalEventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(32);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Conversation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(33);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ConversationAssociation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(34);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ConversationLink() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(35);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CorrelationKey() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(36);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CorrelationProperty() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(37);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CorrelationPropertyBinding() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(38);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CorrelationPropertyRetrievalExpression() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(39);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_CorrelationSubscription() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(40);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_DataAssociation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(41);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_DataInput() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(42);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_DataInputAssociation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(43);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_DataObject() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(44);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_DataObjectReference() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(45);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_DataOutput() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(46);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_DataOutputAssociation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(47);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_DataState() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(48);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_DataStore() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(49);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_DataStoreReference() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(50);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Definitions() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(51);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Documentation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(52);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_EndEvent() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(53);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_EndPoint() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(54);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Error() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(55);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ErrorEventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(56);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Escalation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(57);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_EscalationEventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(58);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Event() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(59);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_EventBasedGateway() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(60);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ExclusiveGateway() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(61);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Expression() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(62);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Extension() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(63);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ExtensionElements() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(64);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_FlowNode() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(65);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_FormalExpression() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(66);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Gateway() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(67);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_GlobalBusinessRuleTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(68);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_GlobalChoreographyTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(69);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_GlobalConversation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(70);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_GlobalManualTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(71);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_GlobalScriptTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(72);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_GlobalTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(73);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_GlobalUserTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(74);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Group() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(75);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_HumanPerformer() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(76);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Performer() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(77);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ResourceRole() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(78);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ImplicitThrowEvent() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(79);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Import() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(80);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_InclusiveGateway() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(81);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_InputSet() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(82);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Interface() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(83);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_IntermediateCatchEvent() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(84);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_IntermediateThrowEvent() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(85);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_IoBinding() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(86);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_IoSpecification() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(87);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ItemDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(88);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Lane() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(89);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_LaneSet() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(90);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_LinkEventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(91);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_LoopCharacteristics() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(92);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ManualTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(93);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Message() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(94);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_MessageEventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(95);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_MessageFlow() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(96);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_MessageFlowAssociation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(97);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Monitoring() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(98);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_MultiInstanceLoopCharacteristics() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(99);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Operation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(100);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_OutputSet() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(101);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ParallelGateway() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(102);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Participant() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(103);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ParticipantAssociation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(104);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ParticipantMultiplicity() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(105);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_PartnerEntity() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(106);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_PartnerRole() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(107);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_PotentialOwner() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(108);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Process() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(109);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Property() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(110);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ReceiveTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(111);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Relationship() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(112);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Rendering() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(113);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Resource() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(114);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ResourceAssignmentExpression() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(115);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ResourceParameter() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(116);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ResourceParameterBinding() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(117);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Script() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(118);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ScriptTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(119);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_SendTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(120);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_SequenceFlow() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(121);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ServiceTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(122);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Signal() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(123);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_SignalEventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(124);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_StandardLoopCharacteristics() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(125);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_StartEvent() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(126);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_SubChoreography() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(127);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_SubConversation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(128);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_SubProcess() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(129);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Task() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(130);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_TerminateEventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(131);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Text() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(132);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_TextAnnotation() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(133);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_ThrowEvent() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(134);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_TimerEventDefinition() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(135);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_Transaction() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(136);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_UserTask() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(137);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDocumentRoot_EventSubProcess() {
		return (EReference) documentRootEClass.getEStructuralFeatures().get(138);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getActivity() {
		return activityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_IoSpecification() {
		return (EReference) activityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_BoundaryEventRefs() {
		return (EReference) activityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_Properties() {
		return (EReference) activityEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_DataInputAssociations() {
		return (EReference) activityEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_DataOutputAssociations() {
		return (EReference) activityEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_Resources() {
		return (EReference) activityEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_LoopCharacteristics() {
		return (EReference) activityEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActivity_CompletionQuantity() {
		return (EAttribute) activityEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getActivity_Default() {
		return (EReference) activityEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActivity_IsForCompensation() {
		return (EAttribute) activityEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getActivity_StartQuantity() {
		return (EAttribute) activityEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAdHocSubProcess() {
		return adHocSubProcessEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAdHocSubProcess_CompletionCondition() {
		return (EReference) adHocSubProcessEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAdHocSubProcess_CancelRemainingInstances() {
		return (EAttribute) adHocSubProcessEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAdHocSubProcess_Ordering() {
		return (EAttribute) adHocSubProcessEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getArtifact() {
		return artifactEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAssignment() {
		return assignmentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAssignment_From() {
		return (EReference) assignmentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAssignment_To() {
		return (EReference) assignmentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAssociation() {
		return associationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAssociation_AssociationDirection() {
		return (EAttribute) associationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAssociation_SourceRef() {
		return (EReference) associationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAssociation_TargetRef() {
		return (EReference) associationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAuditing() {
		return auditingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBaseElement() {
		return baseElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getBaseElement_Documentation() {
		return (EReference) baseElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getBaseElement_ExtensionValues() {
		return (EReference) baseElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getBaseElement_ExtensionDefinitions() {
		return (EReference) baseElementEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseElement_Id() {
		return (EAttribute) baseElementEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBaseElement_AnyAttribute() {
		return (EAttribute) baseElementEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBoundaryEvent() {
		return boundaryEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getBoundaryEvent_AttachedToRef() {
		return (EReference) boundaryEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBoundaryEvent_CancelActivity() {
		return (EAttribute) boundaryEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBusinessRuleTask() {
		return businessRuleTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBusinessRuleTask_Implementation() {
		return (EAttribute) businessRuleTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCallActivity() {
		return callActivityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCallActivity_CalledElement() {
		return (EAttribute) callActivityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCallChoreography() {
		return callChoreographyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCallChoreography_ParticipantAssociations() {
		return (EReference) callChoreographyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCallChoreography_CalledChoreographyRef() {
		return (EReference) callChoreographyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCallConversation() {
		return callConversationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCallConversation_ParticipantAssociations() {
		return (EReference) callConversationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCallConversation_CalledCollaborationRef() {
		return (EReference) callConversationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCallableElement() {
		return callableElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCallableElement_SupportedInterfaceRefs() {
		return (EReference) callableElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCallableElement_IoSpecification() {
		return (EReference) callableElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCallableElement_IoBinding() {
		return (EReference) callableElementEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCallableElement_Name() {
		return (EAttribute) callableElementEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCancelEventDefinition() {
		return cancelEventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCatchEvent() {
		return catchEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCatchEvent_DataOutputs() {
		return (EReference) catchEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCatchEvent_DataOutputAssociation() {
		return (EReference) catchEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCatchEvent_OutputSet() {
		return (EReference) catchEventEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCatchEvent_EventDefinitions() {
		return (EReference) catchEventEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCatchEvent_EventDefinitionRefs() {
		return (EReference) catchEventEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCatchEvent_ParallelMultiple() {
		return (EAttribute) catchEventEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCategory() {
		return categoryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCategory_CategoryValue() {
		return (EReference) categoryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCategory_Name() {
		return (EAttribute) categoryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCategoryValue() {
		return categoryValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCategoryValue_Value() {
		return (EAttribute) categoryValueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCategoryValue_CategorizedFlowElements() {
		return (EReference) categoryValueEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getChoreography() {
		return choreographyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getChoreographyActivity() {
		return choreographyActivityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getChoreographyActivity_ParticipantRefs() {
		return (EReference) choreographyActivityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getChoreographyActivity_CorrelationKeys() {
		return (EReference) choreographyActivityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getChoreographyActivity_InitiatingParticipantRef() {
		return (EReference) choreographyActivityEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getChoreographyActivity_LoopType() {
		return (EAttribute) choreographyActivityEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getChoreographyTask() {
		return choreographyTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getChoreographyTask_MessageFlowRef() {
		return (EReference) choreographyTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCollaboration() {
		return collaborationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollaboration_Participants() {
		return (EReference) collaborationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollaboration_MessageFlows() {
		return (EReference) collaborationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollaboration_Artifacts() {
		return (EReference) collaborationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollaboration_Conversations() {
		return (EReference) collaborationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollaboration_ConversationAssociations() {
		return (EReference) collaborationEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollaboration_ParticipantAssociations() {
		return (EReference) collaborationEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollaboration_MessageFlowAssociations() {
		return (EReference) collaborationEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollaboration_CorrelationKeys() {
		return (EReference) collaborationEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollaboration_ChoreographyRef() {
		return (EReference) collaborationEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCollaboration_ConversationLinks() {
		return (EReference) collaborationEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCollaboration_IsClosed() {
		return (EAttribute) collaborationEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCollaboration_Name() {
		return (EAttribute) collaborationEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCompensateEventDefinition() {
		return compensateEventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCompensateEventDefinition_ActivityRef() {
		return (EReference) compensateEventDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCompensateEventDefinition_WaitForCompletion() {
		return (EAttribute) compensateEventDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getComplexBehaviorDefinition() {
		return complexBehaviorDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComplexBehaviorDefinition_Condition() {
		return (EReference) complexBehaviorDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComplexBehaviorDefinition_Event() {
		return (EReference) complexBehaviorDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getComplexGateway() {
		return complexGatewayEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComplexGateway_ActivationCondition() {
		return (EReference) complexGatewayEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComplexGateway_Default() {
		return (EReference) complexGatewayEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getConditionalEventDefinition() {
		return conditionalEventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConditionalEventDefinition_Condition() {
		return (EReference) conditionalEventDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getConversation() {
		return conversationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getConversationAssociation() {
		return conversationAssociationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConversationAssociation_InnerConversationNodeRef() {
		return (EReference) conversationAssociationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConversationAssociation_OuterConversationNodeRef() {
		return (EReference) conversationAssociationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getConversationLink() {
		return conversationLinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getConversationLink_Name() {
		return (EAttribute) conversationLinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConversationLink_SourceRef() {
		return (EReference) conversationLinkEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConversationLink_TargetRef() {
		return (EReference) conversationLinkEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getConversationNode() {
		return conversationNodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConversationNode_ParticipantRefs() {
		return (EReference) conversationNodeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConversationNode_MessageFlowRefs() {
		return (EReference) conversationNodeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getConversationNode_CorrelationKeys() {
		return (EReference) conversationNodeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getConversationNode_Name() {
		return (EAttribute) conversationNodeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCorrelationKey() {
		return correlationKeyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCorrelationKey_CorrelationPropertyRef() {
		return (EReference) correlationKeyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCorrelationKey_Name() {
		return (EAttribute) correlationKeyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCorrelationProperty() {
		return correlationPropertyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCorrelationProperty_CorrelationPropertyRetrievalExpression() {
		return (EReference) correlationPropertyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCorrelationProperty_Name() {
		return (EAttribute) correlationPropertyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCorrelationProperty_Type() {
		return (EReference) correlationPropertyEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCorrelationPropertyBinding() {
		return correlationPropertyBindingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCorrelationPropertyBinding_DataPath() {
		return (EReference) correlationPropertyBindingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCorrelationPropertyBinding_CorrelationPropertyRef() {
		return (EReference) correlationPropertyBindingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCorrelationPropertyRetrievalExpression() {
		return correlationPropertyRetrievalExpressionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCorrelationPropertyRetrievalExpression_MessagePath() {
		return (EReference) correlationPropertyRetrievalExpressionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCorrelationPropertyRetrievalExpression_MessageRef() {
		return (EReference) correlationPropertyRetrievalExpressionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCorrelationSubscription() {
		return correlationSubscriptionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCorrelationSubscription_CorrelationPropertyBinding() {
		return (EReference) correlationSubscriptionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCorrelationSubscription_CorrelationKeyRef() {
		return (EReference) correlationSubscriptionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataAssociation() {
		return dataAssociationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataAssociation_SourceRef() {
		return (EReference) dataAssociationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataAssociation_TargetRef() {
		return (EReference) dataAssociationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataAssociation_Transformation() {
		return (EReference) dataAssociationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataAssociation_Assignment() {
		return (EReference) dataAssociationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataInput() {
		return dataInputEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataInput_InputSetWithOptional() {
		return (EReference) dataInputEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataInput_InputSetWithWhileExecuting() {
		return (EReference) dataInputEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataInput_InputSetRefs() {
		return (EReference) dataInputEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataInput_IsCollection() {
		return (EAttribute) dataInputEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataInput_Name() {
		return (EAttribute) dataInputEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataInputAssociation() {
		return dataInputAssociationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataObject() {
		return dataObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataObject_IsCollection() {
		return (EAttribute) dataObjectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataObjectReference() {
		return dataObjectReferenceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataObjectReference_DataObjectRef() {
		return (EReference) dataObjectReferenceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataOutput() {
		return dataOutputEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataOutput_OutputSetWithOptional() {
		return (EReference) dataOutputEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataOutput_OutputSetWithWhileExecuting() {
		return (EReference) dataOutputEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataOutput_OutputSetRefs() {
		return (EReference) dataOutputEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataOutput_IsCollection() {
		return (EAttribute) dataOutputEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataOutput_Name() {
		return (EAttribute) dataOutputEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataOutputAssociation() {
		return dataOutputAssociationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataState() {
		return dataStateEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataState_Name() {
		return (EAttribute) dataStateEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataStore() {
		return dataStoreEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataStore_Capacity() {
		return (EAttribute) dataStoreEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataStore_IsUnlimited() {
		return (EAttribute) dataStoreEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDataStore_Name() {
		return (EAttribute) dataStoreEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDataStoreReference() {
		return dataStoreReferenceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDataStoreReference_DataStoreRef() {
		return (EReference) dataStoreReferenceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDefinitions() {
		return definitionsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDefinitions_Imports() {
		return (EReference) definitionsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDefinitions_Extensions() {
		return (EReference) definitionsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDefinitions_RootElements() {
		return (EReference) definitionsEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDefinitions_Diagrams() {
		return (EReference) definitionsEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDefinitions_Relationships() {
		return (EReference) definitionsEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDefinitions_Exporter() {
		return (EAttribute) definitionsEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDefinitions_ExporterVersion() {
		return (EAttribute) definitionsEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDefinitions_ExpressionLanguage() {
		return (EAttribute) definitionsEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDefinitions_Name() {
		return (EAttribute) definitionsEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDefinitions_TargetNamespace() {
		return (EAttribute) definitionsEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDefinitions_TypeLanguage() {
		return (EAttribute) definitionsEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDocumentation() {
		return documentationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentation_Mixed() {
		return (EAttribute) documentationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentation_Text() {
		return (EAttribute) documentationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDocumentation_TextFormat() {
		return (EAttribute) documentationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEndEvent() {
		return endEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEndPoint() {
		return endPointEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getError() {
		return errorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getError_ErrorCode() {
		return (EAttribute) errorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getError_Name() {
		return (EAttribute) errorEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getError_StructureRef() {
		return (EReference) errorEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getErrorEventDefinition() {
		return errorEventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getErrorEventDefinition_ErrorRef() {
		return (EReference) errorEventDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEscalation() {
		return escalationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEscalation_EscalationCode() {
		return (EAttribute) escalationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEscalation_Name() {
		return (EAttribute) escalationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEscalation_StructureRef() {
		return (EReference) escalationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEscalationEventDefinition() {
		return escalationEventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEscalationEventDefinition_EscalationRef() {
		return (EReference) escalationEventDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEvent() {
		return eventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEvent_Properties() {
		return (EReference) eventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEventBasedGateway() {
		return eventBasedGatewayEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEventBasedGateway_EventGatewayType() {
		return (EAttribute) eventBasedGatewayEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEventBasedGateway_Instantiate() {
		return (EAttribute) eventBasedGatewayEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEventDefinition() {
		return eventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExclusiveGateway() {
		return exclusiveGatewayEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExclusiveGateway_Default() {
		return (EReference) exclusiveGatewayEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExpression() {
		return expressionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExtension() {
		return extensionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExtension_Definition() {
		return (EReference) extensionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExtension_MustUnderstand() {
		return (EAttribute) extensionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExtension_XsdDefinition() {
		return (EAttribute) extensionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExtensionAttributeDefinition() {
		return extensionAttributeDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExtensionAttributeDefinition_Name() {
		return (EAttribute) extensionAttributeDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExtensionAttributeDefinition_Type() {
		return (EAttribute) extensionAttributeDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExtensionAttributeDefinition_IsReference() {
		return (EAttribute) extensionAttributeDefinitionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExtensionAttributeDefinition_ExtensionDefinition() {
		return (EReference) extensionAttributeDefinitionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExtensionAttributeValue() {
		return extensionAttributeValueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExtensionAttributeValue_ValueRef() {
		return (EReference) extensionAttributeValueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExtensionAttributeValue_Value() {
		return (EAttribute) extensionAttributeValueEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExtensionAttributeValue_ExtensionAttributeDefinition() {
		return (EReference) extensionAttributeValueEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExtensionDefinition() {
		return extensionDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExtensionDefinition_Name() {
		return (EAttribute) extensionDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExtensionDefinition_ExtensionAttributeDefinitions() {
		return (EReference) extensionDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFlowElement() {
		return flowElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFlowElement_Auditing() {
		return (EReference) flowElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFlowElement_Monitoring() {
		return (EReference) flowElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFlowElement_CategoryValueRef() {
		return (EReference) flowElementEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFlowElement_Name() {
		return (EAttribute) flowElementEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFlowElementsContainer() {
		return flowElementsContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFlowElementsContainer_LaneSets() {
		return (EReference) flowElementsContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFlowElementsContainer_FlowElements() {
		return (EReference) flowElementsContainerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFlowNode() {
		return flowNodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFlowNode_Incoming() {
		return (EReference) flowNodeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFlowNode_Lanes() {
		return (EReference) flowNodeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFlowNode_Outgoing() {
		return (EReference) flowNodeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFormalExpression() {
		return formalExpressionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFormalExpression_Mixed() {
		return (EAttribute) formalExpressionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFormalExpression_Body() {
		return (EAttribute) formalExpressionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFormalExpression_EvaluatesToTypeRef() {
		return (EReference) formalExpressionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFormalExpression_Language() {
		return (EAttribute) formalExpressionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGateway() {
		return gatewayEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGateway_GatewayDirection() {
		return (EAttribute) gatewayEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGlobalBusinessRuleTask() {
		return globalBusinessRuleTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGlobalBusinessRuleTask_Implementation() {
		return (EAttribute) globalBusinessRuleTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGlobalChoreographyTask() {
		return globalChoreographyTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getGlobalChoreographyTask_InitiatingParticipantRef() {
		return (EReference) globalChoreographyTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGlobalConversation() {
		return globalConversationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGlobalManualTask() {
		return globalManualTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGlobalScriptTask() {
		return globalScriptTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGlobalScriptTask_Script() {
		return (EAttribute) globalScriptTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGlobalScriptTask_ScriptLanguage() {
		return (EAttribute) globalScriptTaskEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGlobalTask() {
		return globalTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getGlobalTask_Resources() {
		return (EReference) globalTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGlobalUserTask() {
		return globalUserTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getGlobalUserTask_Renderings() {
		return (EReference) globalUserTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGlobalUserTask_Implementation() {
		return (EAttribute) globalUserTaskEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGroup() {
		return groupEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getGroup_CategoryValueRef() {
		return (EReference) groupEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getHumanPerformer() {
		return humanPerformerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getImplicitThrowEvent() {
		return implicitThrowEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getImport() {
		return importEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getImport_ImportType() {
		return (EAttribute) importEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getImport_Location() {
		return (EAttribute) importEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getImport_Namespace() {
		return (EAttribute) importEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInclusiveGateway() {
		return inclusiveGatewayEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInclusiveGateway_Default() {
		return (EReference) inclusiveGatewayEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInputOutputBinding() {
		return inputOutputBindingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputOutputBinding_InputDataRef() {
		return (EReference) inputOutputBindingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputOutputBinding_OperationRef() {
		return (EReference) inputOutputBindingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputOutputBinding_OutputDataRef() {
		return (EReference) inputOutputBindingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInputOutputSpecification() {
		return inputOutputSpecificationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputOutputSpecification_DataInputs() {
		return (EReference) inputOutputSpecificationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputOutputSpecification_DataOutputs() {
		return (EReference) inputOutputSpecificationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputOutputSpecification_InputSets() {
		return (EReference) inputOutputSpecificationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputOutputSpecification_OutputSets() {
		return (EReference) inputOutputSpecificationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInputSet() {
		return inputSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputSet_DataInputRefs() {
		return (EReference) inputSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputSet_OptionalInputRefs() {
		return (EReference) inputSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputSet_WhileExecutingInputRefs() {
		return (EReference) inputSetEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInputSet_OutputSetRefs() {
		return (EReference) inputSetEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInputSet_Name() {
		return (EAttribute) inputSetEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInteractionNode() {
		return interactionNodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInteractionNode_IncomingConversationLinks() {
		return (EReference) interactionNodeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInteractionNode_OutgoingConversationLinks() {
		return (EReference) interactionNodeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInterface() {
		return interfaceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInterface_Operations() {
		return (EReference) interfaceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInterface_Name() {
		return (EAttribute) interfaceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInterface_ImplementationRef() {
		return (EAttribute) interfaceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIntermediateCatchEvent() {
		return intermediateCatchEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIntermediateThrowEvent() {
		return intermediateThrowEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getItemAwareElement() {
		return itemAwareElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getItemAwareElement_DataState() {
		return (EReference) itemAwareElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getItemAwareElement_ItemSubjectRef() {
		return (EReference) itemAwareElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getItemDefinition() {
		return itemDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getItemDefinition_IsCollection() {
		return (EAttribute) itemDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getItemDefinition_Import() {
		return (EReference) itemDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getItemDefinition_ItemKind() {
		return (EAttribute) itemDefinitionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getItemDefinition_StructureRef() {
		return (EAttribute) itemDefinitionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLane() {
		return laneEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLane_PartitionElement() {
		return (EReference) laneEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLane_FlowNodeRefs() {
		return (EReference) laneEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLane_ChildLaneSet() {
		return (EReference) laneEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLane_Name() {
		return (EAttribute) laneEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLane_PartitionElementRef() {
		return (EReference) laneEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLaneSet() {
		return laneSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLaneSet_Lanes() {
		return (EReference) laneSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLaneSet_Name() {
		return (EAttribute) laneSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLinkEventDefinition() {
		return linkEventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLinkEventDefinition_Source() {
		return (EReference) linkEventDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLinkEventDefinition_Target() {
		return (EReference) linkEventDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLinkEventDefinition_Name() {
		return (EAttribute) linkEventDefinitionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLoopCharacteristics() {
		return loopCharacteristicsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getManualTask() {
		return manualTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMessage() {
		return messageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMessage_ItemRef() {
		return (EReference) messageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMessage_Name() {
		return (EAttribute) messageEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMessageEventDefinition() {
		return messageEventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMessageEventDefinition_OperationRef() {
		return (EReference) messageEventDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMessageEventDefinition_MessageRef() {
		return (EReference) messageEventDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMessageFlow() {
		return messageFlowEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMessageFlow_MessageRef() {
		return (EReference) messageFlowEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMessageFlow_Name() {
		return (EAttribute) messageFlowEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMessageFlow_SourceRef() {
		return (EReference) messageFlowEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMessageFlow_TargetRef() {
		return (EReference) messageFlowEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMessageFlowAssociation() {
		return messageFlowAssociationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMessageFlowAssociation_InnerMessageFlowRef() {
		return (EReference) messageFlowAssociationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMessageFlowAssociation_OuterMessageFlowRef() {
		return (EReference) messageFlowAssociationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMonitoring() {
		return monitoringEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMultiInstanceLoopCharacteristics() {
		return multiInstanceLoopCharacteristicsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMultiInstanceLoopCharacteristics_LoopCardinality() {
		return (EReference) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMultiInstanceLoopCharacteristics_LoopDataInputRef() {
		return (EReference) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMultiInstanceLoopCharacteristics_LoopDataOutputRef() {
		return (EReference) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMultiInstanceLoopCharacteristics_InputDataItem() {
		return (EReference) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMultiInstanceLoopCharacteristics_OutputDataItem() {
		return (EReference) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMultiInstanceLoopCharacteristics_ComplexBehaviorDefinition() {
		return (EReference) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMultiInstanceLoopCharacteristics_CompletionCondition() {
		return (EReference) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMultiInstanceLoopCharacteristics_Behavior() {
		return (EAttribute) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMultiInstanceLoopCharacteristics_IsSequential() {
		return (EAttribute) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMultiInstanceLoopCharacteristics_NoneBehaviorEventRef() {
		return (EReference) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMultiInstanceLoopCharacteristics_OneBehaviorEventRef() {
		return (EReference) multiInstanceLoopCharacteristicsEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOperation() {
		return operationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_InMessageRef() {
		return (EReference) operationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_OutMessageRef() {
		return (EReference) operationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_ErrorRefs() {
		return (EReference) operationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperation_Name() {
		return (EAttribute) operationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperation_ImplementationRef() {
		return (EAttribute) operationEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOutputSet() {
		return outputSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOutputSet_DataOutputRefs() {
		return (EReference) outputSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOutputSet_OptionalOutputRefs() {
		return (EReference) outputSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOutputSet_WhileExecutingOutputRefs() {
		return (EReference) outputSetEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOutputSet_InputSetRefs() {
		return (EReference) outputSetEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOutputSet_Name() {
		return (EAttribute) outputSetEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getParallelGateway() {
		return parallelGatewayEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getParticipant() {
		return participantEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParticipant_InterfaceRefs() {
		return (EReference) participantEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParticipant_EndPointRefs() {
		return (EReference) participantEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParticipant_ParticipantMultiplicity() {
		return (EReference) participantEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParticipant_Name() {
		return (EAttribute) participantEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParticipant_ProcessRef() {
		return (EReference) participantEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getParticipantAssociation() {
		return participantAssociationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParticipantAssociation_InnerParticipantRef() {
		return (EReference) participantAssociationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParticipantAssociation_OuterParticipantRef() {
		return (EReference) participantAssociationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getParticipantMultiplicity() {
		return participantMultiplicityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParticipantMultiplicity_Maximum() {
		return (EAttribute) participantMultiplicityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParticipantMultiplicity_Minimum() {
		return (EAttribute) participantMultiplicityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPartnerEntity() {
		return partnerEntityEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPartnerEntity_ParticipantRef() {
		return (EReference) partnerEntityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPartnerEntity_Name() {
		return (EAttribute) partnerEntityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPartnerRole() {
		return partnerRoleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPartnerRole_ParticipantRef() {
		return (EReference) partnerRoleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPartnerRole_Name() {
		return (EAttribute) partnerRoleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPerformer() {
		return performerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPotentialOwner() {
		return potentialOwnerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getProcess() {
		return processEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProcess_Auditing() {
		return (EReference) processEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProcess_Monitoring() {
		return (EReference) processEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProcess_Properties() {
		return (EReference) processEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProcess_Artifacts() {
		return (EReference) processEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProcess_Resources() {
		return (EReference) processEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProcess_CorrelationSubscriptions() {
		return (EReference) processEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProcess_Supports() {
		return (EReference) processEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProcess_DefinitionalCollaborationRef() {
		return (EReference) processEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProcess_IsClosed() {
		return (EAttribute) processEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProcess_IsExecutable() {
		return (EAttribute) processEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProcess_ProcessType() {
		return (EAttribute) processEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getProperty() {
		return propertyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProperty_Name() {
		return (EAttribute) propertyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getReceiveTask() {
		return receiveTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReceiveTask_Implementation() {
		return (EAttribute) receiveTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReceiveTask_Instantiate() {
		return (EAttribute) receiveTaskEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getReceiveTask_MessageRef() {
		return (EReference) receiveTaskEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getReceiveTask_OperationRef() {
		return (EReference) receiveTaskEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getRelationship() {
		return relationshipEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRelationship_Sources() {
		return (EReference) relationshipEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRelationship_Targets() {
		return (EReference) relationshipEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRelationship_Direction() {
		return (EAttribute) relationshipEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRelationship_Type() {
		return (EAttribute) relationshipEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getRendering() {
		return renderingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResource() {
		return resourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResource_ResourceParameters() {
		return (EReference) resourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getResource_Name() {
		return (EAttribute) resourceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResourceAssignmentExpression() {
		return resourceAssignmentExpressionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceAssignmentExpression_Expression() {
		return (EReference) resourceAssignmentExpressionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResourceParameter() {
		return resourceParameterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getResourceParameter_IsRequired() {
		return (EAttribute) resourceParameterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getResourceParameter_Name() {
		return (EAttribute) resourceParameterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceParameter_Type() {
		return (EReference) resourceParameterEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResourceParameterBinding() {
		return resourceParameterBindingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceParameterBinding_Expression() {
		return (EReference) resourceParameterBindingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceParameterBinding_ParameterRef() {
		return (EReference) resourceParameterBindingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResourceRole() {
		return resourceRoleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceRole_ResourceRef() {
		return (EReference) resourceRoleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceRole_ResourceParameterBindings() {
		return (EReference) resourceRoleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceRole_ResourceAssignmentExpression() {
		return (EReference) resourceRoleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getResourceRole_Name() {
		return (EAttribute) resourceRoleEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getRootElement() {
		return rootElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getScriptTask() {
		return scriptTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getScriptTask_Script() {
		return (EAttribute) scriptTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getScriptTask_ScriptFormat() {
		return (EAttribute) scriptTaskEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSendTask() {
		return sendTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSendTask_Implementation() {
		return (EAttribute) sendTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSendTask_MessageRef() {
		return (EReference) sendTaskEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSendTask_OperationRef() {
		return (EReference) sendTaskEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSequenceFlow() {
		return sequenceFlowEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSequenceFlow_ConditionExpression() {
		return (EReference) sequenceFlowEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSequenceFlow_IsImmediate() {
		return (EAttribute) sequenceFlowEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSequenceFlow_SourceRef() {
		return (EReference) sequenceFlowEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSequenceFlow_TargetRef() {
		return (EReference) sequenceFlowEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getServiceTask() {
		return serviceTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getServiceTask_Implementation() {
		return (EAttribute) serviceTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getServiceTask_OperationRef() {
		return (EReference) serviceTaskEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSignal() {
		return signalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSignal_Name() {
		return (EAttribute) signalEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSignal_StructureRef() {
		return (EReference) signalEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSignalEventDefinition() {
		return signalEventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSignalEventDefinition_SignalRef() {
		return (EAttribute) signalEventDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStandardLoopCharacteristics() {
		return standardLoopCharacteristicsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getStandardLoopCharacteristics_LoopCondition() {
		return (EReference) standardLoopCharacteristicsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getStandardLoopCharacteristics_LoopMaximum() {
		return (EReference) standardLoopCharacteristicsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStandardLoopCharacteristics_TestBefore() {
		return (EAttribute) standardLoopCharacteristicsEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStartEvent() {
		return startEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStartEvent_IsInterrupting() {
		return (EAttribute) startEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSubChoreography() {
		return subChoreographyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSubChoreography_Artifacts() {
		return (EReference) subChoreographyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSubConversation() {
		return subConversationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSubConversation_ConversationNodes() {
		return (EReference) subConversationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSubProcess() {
		return subProcessEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSubProcess_Artifacts() {
		return (EReference) subProcessEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSubProcess_TriggeredByEvent() {
		return (EAttribute) subProcessEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTask() {
		return taskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTerminateEventDefinition() {
		return terminateEventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTextAnnotation() {
		return textAnnotationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTextAnnotation_Text() {
		return (EAttribute) textAnnotationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTextAnnotation_TextFormat() {
		return (EAttribute) textAnnotationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getThrowEvent() {
		return throwEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getThrowEvent_DataInputs() {
		return (EReference) throwEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getThrowEvent_DataInputAssociation() {
		return (EReference) throwEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getThrowEvent_InputSet() {
		return (EReference) throwEventEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getThrowEvent_EventDefinitions() {
		return (EReference) throwEventEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getThrowEvent_EventDefinitionRefs() {
		return (EReference) throwEventEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTimerEventDefinition() {
		return timerEventDefinitionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTimerEventDefinition_TimeDate() {
		return (EReference) timerEventDefinitionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTimerEventDefinition_TimeDuration() {
		return (EReference) timerEventDefinitionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTimerEventDefinition_TimeCycle() {
		return (EReference) timerEventDefinitionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTransaction() {
		return transactionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTransaction_Protocol() {
		return (EAttribute) transactionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTransaction_Method() {
		return (EAttribute) transactionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getUserTask() {
		return userTaskEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getUserTask_Renderings() {
		return (EReference) userTaskEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUserTask_Implementation() {
		return (EAttribute) userTaskEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEventSubprocess() {
		return eventSubprocessEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getAdHocOrdering() {
		return adHocOrderingEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getAssociationDirection() {
		return associationDirectionEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getChoreographyLoopType() {
		return choreographyLoopTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getEventBasedGatewayType() {
		return eventBasedGatewayTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getGatewayDirection() {
		return gatewayDirectionEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getItemKind() {
		return itemKindEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getMultiInstanceBehavior() {
		return multiInstanceBehaviorEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getProcessType() {
		return processTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getRelationshipDirection() {
		return relationshipDirectionEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Bpmn2Factory getBpmn2Factory() {
		return (Bpmn2Factory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated)
			return;
		isCreated = true;

		// Create classes and their features
		documentRootEClass = createEClass(DOCUMENT_ROOT);
		createEAttribute(documentRootEClass, DOCUMENT_ROOT__MIXED);
		createEReference(documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
		createEReference(documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ACTIVITY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__AD_HOC_SUB_PROCESS);
		createEReference(documentRootEClass, DOCUMENT_ROOT__FLOW_ELEMENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ARTIFACT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ASSIGNMENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ASSOCIATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__AUDITING);
		createEReference(documentRootEClass, DOCUMENT_ROOT__BASE_ELEMENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__BASE_ELEMENT_WITH_MIXED_CONTENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__BOUNDARY_EVENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__BUSINESS_RULE_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CALLABLE_ELEMENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CALL_ACTIVITY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CALL_CHOREOGRAPHY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CALL_CONVERSATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CONVERSATION_NODE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CANCEL_EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ROOT_ELEMENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CATCH_EVENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CATEGORY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CATEGORY_VALUE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CHOREOGRAPHY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__COLLABORATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CHOREOGRAPHY_ACTIVITY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CHOREOGRAPHY_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__COMPENSATE_EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__COMPLEX_BEHAVIOR_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__COMPLEX_GATEWAY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CONDITIONAL_EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CONVERSATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CONVERSATION_ASSOCIATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CONVERSATION_LINK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CORRELATION_KEY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CORRELATION_PROPERTY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CORRELATION_PROPERTY_BINDING);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CORRELATION_PROPERTY_RETRIEVAL_EXPRESSION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__CORRELATION_SUBSCRIPTION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DATA_ASSOCIATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DATA_INPUT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DATA_INPUT_ASSOCIATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DATA_OBJECT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DATA_OBJECT_REFERENCE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DATA_OUTPUT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DATA_OUTPUT_ASSOCIATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DATA_STATE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DATA_STORE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DATA_STORE_REFERENCE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DEFINITIONS);
		createEReference(documentRootEClass, DOCUMENT_ROOT__DOCUMENTATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__END_EVENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__END_POINT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ERROR);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ERROR_EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ESCALATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ESCALATION_EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__EVENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__EVENT_BASED_GATEWAY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__EXCLUSIVE_GATEWAY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__EXPRESSION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__EXTENSION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__EXTENSION_ELEMENTS);
		createEReference(documentRootEClass, DOCUMENT_ROOT__FLOW_NODE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__FORMAL_EXPRESSION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__GATEWAY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__GLOBAL_BUSINESS_RULE_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__GLOBAL_CHOREOGRAPHY_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__GLOBAL_CONVERSATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__GLOBAL_MANUAL_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__GLOBAL_SCRIPT_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__GLOBAL_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__GLOBAL_USER_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__GROUP);
		createEReference(documentRootEClass, DOCUMENT_ROOT__HUMAN_PERFORMER);
		createEReference(documentRootEClass, DOCUMENT_ROOT__PERFORMER);
		createEReference(documentRootEClass, DOCUMENT_ROOT__RESOURCE_ROLE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__IMPLICIT_THROW_EVENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__IMPORT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__INCLUSIVE_GATEWAY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__INPUT_SET);
		createEReference(documentRootEClass, DOCUMENT_ROOT__INTERFACE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__INTERMEDIATE_CATCH_EVENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__INTERMEDIATE_THROW_EVENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__IO_BINDING);
		createEReference(documentRootEClass, DOCUMENT_ROOT__IO_SPECIFICATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__ITEM_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__LANE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__LANE_SET);
		createEReference(documentRootEClass, DOCUMENT_ROOT__LINK_EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__LOOP_CHARACTERISTICS);
		createEReference(documentRootEClass, DOCUMENT_ROOT__MANUAL_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__MESSAGE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__MESSAGE_EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__MESSAGE_FLOW);
		createEReference(documentRootEClass, DOCUMENT_ROOT__MESSAGE_FLOW_ASSOCIATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__MONITORING);
		createEReference(documentRootEClass, DOCUMENT_ROOT__MULTI_INSTANCE_LOOP_CHARACTERISTICS);
		createEReference(documentRootEClass, DOCUMENT_ROOT__OPERATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__OUTPUT_SET);
		createEReference(documentRootEClass, DOCUMENT_ROOT__PARALLEL_GATEWAY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__PARTICIPANT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__PARTICIPANT_ASSOCIATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__PARTICIPANT_MULTIPLICITY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__PARTNER_ENTITY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__PARTNER_ROLE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__POTENTIAL_OWNER);
		createEReference(documentRootEClass, DOCUMENT_ROOT__PROCESS);
		createEReference(documentRootEClass, DOCUMENT_ROOT__PROPERTY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__RECEIVE_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__RELATIONSHIP);
		createEReference(documentRootEClass, DOCUMENT_ROOT__RENDERING);
		createEReference(documentRootEClass, DOCUMENT_ROOT__RESOURCE);
		createEReference(documentRootEClass, DOCUMENT_ROOT__RESOURCE_ASSIGNMENT_EXPRESSION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__RESOURCE_PARAMETER);
		createEReference(documentRootEClass, DOCUMENT_ROOT__RESOURCE_PARAMETER_BINDING);
		createEReference(documentRootEClass, DOCUMENT_ROOT__SCRIPT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__SCRIPT_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__SEND_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__SEQUENCE_FLOW);
		createEReference(documentRootEClass, DOCUMENT_ROOT__SERVICE_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__SIGNAL);
		createEReference(documentRootEClass, DOCUMENT_ROOT__SIGNAL_EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__STANDARD_LOOP_CHARACTERISTICS);
		createEReference(documentRootEClass, DOCUMENT_ROOT__START_EVENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__SUB_CHOREOGRAPHY);
		createEReference(documentRootEClass, DOCUMENT_ROOT__SUB_CONVERSATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__SUB_PROCESS);
		createEReference(documentRootEClass, DOCUMENT_ROOT__TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__TERMINATE_EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__TEXT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__TEXT_ANNOTATION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__THROW_EVENT);
		createEReference(documentRootEClass, DOCUMENT_ROOT__TIMER_EVENT_DEFINITION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__TRANSACTION);
		createEReference(documentRootEClass, DOCUMENT_ROOT__USER_TASK);
		createEReference(documentRootEClass, DOCUMENT_ROOT__EVENT_SUB_PROCESS);

		activityEClass = createEClass(ACTIVITY);
		createEReference(activityEClass, ACTIVITY__IO_SPECIFICATION);
		createEReference(activityEClass, ACTIVITY__BOUNDARY_EVENT_REFS);
		createEReference(activityEClass, ACTIVITY__PROPERTIES);
		createEReference(activityEClass, ACTIVITY__DATA_INPUT_ASSOCIATIONS);
		createEReference(activityEClass, ACTIVITY__DATA_OUTPUT_ASSOCIATIONS);
		createEReference(activityEClass, ACTIVITY__RESOURCES);
		createEReference(activityEClass, ACTIVITY__LOOP_CHARACTERISTICS);
		createEAttribute(activityEClass, ACTIVITY__COMPLETION_QUANTITY);
		createEReference(activityEClass, ACTIVITY__DEFAULT);
		createEAttribute(activityEClass, ACTIVITY__IS_FOR_COMPENSATION);
		createEAttribute(activityEClass, ACTIVITY__START_QUANTITY);

		adHocSubProcessEClass = createEClass(AD_HOC_SUB_PROCESS);
		createEReference(adHocSubProcessEClass, AD_HOC_SUB_PROCESS__COMPLETION_CONDITION);
		createEAttribute(adHocSubProcessEClass, AD_HOC_SUB_PROCESS__CANCEL_REMAINING_INSTANCES);
		createEAttribute(adHocSubProcessEClass, AD_HOC_SUB_PROCESS__ORDERING);

		artifactEClass = createEClass(ARTIFACT);

		assignmentEClass = createEClass(ASSIGNMENT);
		createEReference(assignmentEClass, ASSIGNMENT__FROM);
		createEReference(assignmentEClass, ASSIGNMENT__TO);

		associationEClass = createEClass(ASSOCIATION);
		createEAttribute(associationEClass, ASSOCIATION__ASSOCIATION_DIRECTION);
		createEReference(associationEClass, ASSOCIATION__SOURCE_REF);
		createEReference(associationEClass, ASSOCIATION__TARGET_REF);

		auditingEClass = createEClass(AUDITING);

		baseElementEClass = createEClass(BASE_ELEMENT);
		createEReference(baseElementEClass, BASE_ELEMENT__DOCUMENTATION);
		createEReference(baseElementEClass, BASE_ELEMENT__EXTENSION_VALUES);
		createEReference(baseElementEClass, BASE_ELEMENT__EXTENSION_DEFINITIONS);
		createEAttribute(baseElementEClass, BASE_ELEMENT__ID);
		createEAttribute(baseElementEClass, BASE_ELEMENT__ANY_ATTRIBUTE);

		boundaryEventEClass = createEClass(BOUNDARY_EVENT);
		createEReference(boundaryEventEClass, BOUNDARY_EVENT__ATTACHED_TO_REF);
		createEAttribute(boundaryEventEClass, BOUNDARY_EVENT__CANCEL_ACTIVITY);

		businessRuleTaskEClass = createEClass(BUSINESS_RULE_TASK);
		createEAttribute(businessRuleTaskEClass, BUSINESS_RULE_TASK__IMPLEMENTATION);

		callActivityEClass = createEClass(CALL_ACTIVITY);
		createEAttribute(callActivityEClass, CALL_ACTIVITY__CALLED_ELEMENT);

		callChoreographyEClass = createEClass(CALL_CHOREOGRAPHY);
		createEReference(callChoreographyEClass, CALL_CHOREOGRAPHY__PARTICIPANT_ASSOCIATIONS);
		createEReference(callChoreographyEClass, CALL_CHOREOGRAPHY__CALLED_CHOREOGRAPHY_REF);

		callConversationEClass = createEClass(CALL_CONVERSATION);
		createEReference(callConversationEClass, CALL_CONVERSATION__PARTICIPANT_ASSOCIATIONS);
		createEReference(callConversationEClass, CALL_CONVERSATION__CALLED_COLLABORATION_REF);

		callableElementEClass = createEClass(CALLABLE_ELEMENT);
		createEReference(callableElementEClass, CALLABLE_ELEMENT__SUPPORTED_INTERFACE_REFS);
		createEReference(callableElementEClass, CALLABLE_ELEMENT__IO_SPECIFICATION);
		createEReference(callableElementEClass, CALLABLE_ELEMENT__IO_BINDING);
		createEAttribute(callableElementEClass, CALLABLE_ELEMENT__NAME);

		cancelEventDefinitionEClass = createEClass(CANCEL_EVENT_DEFINITION);

		catchEventEClass = createEClass(CATCH_EVENT);
		createEReference(catchEventEClass, CATCH_EVENT__DATA_OUTPUTS);
		createEReference(catchEventEClass, CATCH_EVENT__DATA_OUTPUT_ASSOCIATION);
		createEReference(catchEventEClass, CATCH_EVENT__OUTPUT_SET);
		createEReference(catchEventEClass, CATCH_EVENT__EVENT_DEFINITIONS);
		createEReference(catchEventEClass, CATCH_EVENT__EVENT_DEFINITION_REFS);
		createEAttribute(catchEventEClass, CATCH_EVENT__PARALLEL_MULTIPLE);

		categoryEClass = createEClass(CATEGORY);
		createEReference(categoryEClass, CATEGORY__CATEGORY_VALUE);
		createEAttribute(categoryEClass, CATEGORY__NAME);

		categoryValueEClass = createEClass(CATEGORY_VALUE);
		createEAttribute(categoryValueEClass, CATEGORY_VALUE__VALUE);
		createEReference(categoryValueEClass, CATEGORY_VALUE__CATEGORIZED_FLOW_ELEMENTS);

		choreographyEClass = createEClass(CHOREOGRAPHY);

		choreographyActivityEClass = createEClass(CHOREOGRAPHY_ACTIVITY);
		createEReference(choreographyActivityEClass, CHOREOGRAPHY_ACTIVITY__PARTICIPANT_REFS);
		createEReference(choreographyActivityEClass, CHOREOGRAPHY_ACTIVITY__CORRELATION_KEYS);
		createEReference(choreographyActivityEClass, CHOREOGRAPHY_ACTIVITY__INITIATING_PARTICIPANT_REF);
		createEAttribute(choreographyActivityEClass, CHOREOGRAPHY_ACTIVITY__LOOP_TYPE);

		choreographyTaskEClass = createEClass(CHOREOGRAPHY_TASK);
		createEReference(choreographyTaskEClass, CHOREOGRAPHY_TASK__MESSAGE_FLOW_REF);

		collaborationEClass = createEClass(COLLABORATION);
		createEReference(collaborationEClass, COLLABORATION__PARTICIPANTS);
		createEReference(collaborationEClass, COLLABORATION__MESSAGE_FLOWS);
		createEReference(collaborationEClass, COLLABORATION__ARTIFACTS);
		createEReference(collaborationEClass, COLLABORATION__CONVERSATIONS);
		createEReference(collaborationEClass, COLLABORATION__CONVERSATION_ASSOCIATIONS);
		createEReference(collaborationEClass, COLLABORATION__PARTICIPANT_ASSOCIATIONS);
		createEReference(collaborationEClass, COLLABORATION__MESSAGE_FLOW_ASSOCIATIONS);
		createEReference(collaborationEClass, COLLABORATION__CORRELATION_KEYS);
		createEReference(collaborationEClass, COLLABORATION__CHOREOGRAPHY_REF);
		createEReference(collaborationEClass, COLLABORATION__CONVERSATION_LINKS);
		createEAttribute(collaborationEClass, COLLABORATION__IS_CLOSED);
		createEAttribute(collaborationEClass, COLLABORATION__NAME);

		compensateEventDefinitionEClass = createEClass(COMPENSATE_EVENT_DEFINITION);
		createEReference(compensateEventDefinitionEClass, COMPENSATE_EVENT_DEFINITION__ACTIVITY_REF);
		createEAttribute(compensateEventDefinitionEClass, COMPENSATE_EVENT_DEFINITION__WAIT_FOR_COMPLETION);

		complexBehaviorDefinitionEClass = createEClass(COMPLEX_BEHAVIOR_DEFINITION);
		createEReference(complexBehaviorDefinitionEClass, COMPLEX_BEHAVIOR_DEFINITION__CONDITION);
		createEReference(complexBehaviorDefinitionEClass, COMPLEX_BEHAVIOR_DEFINITION__EVENT);

		complexGatewayEClass = createEClass(COMPLEX_GATEWAY);
		createEReference(complexGatewayEClass, COMPLEX_GATEWAY__ACTIVATION_CONDITION);
		createEReference(complexGatewayEClass, COMPLEX_GATEWAY__DEFAULT);

		conditionalEventDefinitionEClass = createEClass(CONDITIONAL_EVENT_DEFINITION);
		createEReference(conditionalEventDefinitionEClass, CONDITIONAL_EVENT_DEFINITION__CONDITION);

		conversationEClass = createEClass(CONVERSATION);

		conversationAssociationEClass = createEClass(CONVERSATION_ASSOCIATION);
		createEReference(conversationAssociationEClass, CONVERSATION_ASSOCIATION__INNER_CONVERSATION_NODE_REF);
		createEReference(conversationAssociationEClass, CONVERSATION_ASSOCIATION__OUTER_CONVERSATION_NODE_REF);

		conversationLinkEClass = createEClass(CONVERSATION_LINK);
		createEAttribute(conversationLinkEClass, CONVERSATION_LINK__NAME);
		createEReference(conversationLinkEClass, CONVERSATION_LINK__SOURCE_REF);
		createEReference(conversationLinkEClass, CONVERSATION_LINK__TARGET_REF);

		conversationNodeEClass = createEClass(CONVERSATION_NODE);
		createEReference(conversationNodeEClass, CONVERSATION_NODE__PARTICIPANT_REFS);
		createEReference(conversationNodeEClass, CONVERSATION_NODE__MESSAGE_FLOW_REFS);
		createEReference(conversationNodeEClass, CONVERSATION_NODE__CORRELATION_KEYS);
		createEAttribute(conversationNodeEClass, CONVERSATION_NODE__NAME);

		correlationKeyEClass = createEClass(CORRELATION_KEY);
		createEReference(correlationKeyEClass, CORRELATION_KEY__CORRELATION_PROPERTY_REF);
		createEAttribute(correlationKeyEClass, CORRELATION_KEY__NAME);

		correlationPropertyEClass = createEClass(CORRELATION_PROPERTY);
		createEReference(correlationPropertyEClass, CORRELATION_PROPERTY__CORRELATION_PROPERTY_RETRIEVAL_EXPRESSION);
		createEAttribute(correlationPropertyEClass, CORRELATION_PROPERTY__NAME);
		createEReference(correlationPropertyEClass, CORRELATION_PROPERTY__TYPE);

		correlationPropertyBindingEClass = createEClass(CORRELATION_PROPERTY_BINDING);
		createEReference(correlationPropertyBindingEClass, CORRELATION_PROPERTY_BINDING__DATA_PATH);
		createEReference(correlationPropertyBindingEClass, CORRELATION_PROPERTY_BINDING__CORRELATION_PROPERTY_REF);

		correlationPropertyRetrievalExpressionEClass = createEClass(CORRELATION_PROPERTY_RETRIEVAL_EXPRESSION);
		createEReference(correlationPropertyRetrievalExpressionEClass,
				CORRELATION_PROPERTY_RETRIEVAL_EXPRESSION__MESSAGE_PATH);
		createEReference(correlationPropertyRetrievalExpressionEClass,
				CORRELATION_PROPERTY_RETRIEVAL_EXPRESSION__MESSAGE_REF);

		correlationSubscriptionEClass = createEClass(CORRELATION_SUBSCRIPTION);
		createEReference(correlationSubscriptionEClass, CORRELATION_SUBSCRIPTION__CORRELATION_PROPERTY_BINDING);
		createEReference(correlationSubscriptionEClass, CORRELATION_SUBSCRIPTION__CORRELATION_KEY_REF);

		dataAssociationEClass = createEClass(DATA_ASSOCIATION);
		createEReference(dataAssociationEClass, DATA_ASSOCIATION__SOURCE_REF);
		createEReference(dataAssociationEClass, DATA_ASSOCIATION__TARGET_REF);
		createEReference(dataAssociationEClass, DATA_ASSOCIATION__TRANSFORMATION);
		createEReference(dataAssociationEClass, DATA_ASSOCIATION__ASSIGNMENT);

		dataInputEClass = createEClass(DATA_INPUT);
		createEReference(dataInputEClass, DATA_INPUT__INPUT_SET_WITH_OPTIONAL);
		createEReference(dataInputEClass, DATA_INPUT__INPUT_SET_WITH_WHILE_EXECUTING);
		createEReference(dataInputEClass, DATA_INPUT__INPUT_SET_REFS);
		createEAttribute(dataInputEClass, DATA_INPUT__IS_COLLECTION);
		createEAttribute(dataInputEClass, DATA_INPUT__NAME);

		dataInputAssociationEClass = createEClass(DATA_INPUT_ASSOCIATION);

		dataObjectEClass = createEClass(DATA_OBJECT);
		createEAttribute(dataObjectEClass, DATA_OBJECT__IS_COLLECTION);

		dataObjectReferenceEClass = createEClass(DATA_OBJECT_REFERENCE);
		createEReference(dataObjectReferenceEClass, DATA_OBJECT_REFERENCE__DATA_OBJECT_REF);

		dataOutputEClass = createEClass(DATA_OUTPUT);
		createEReference(dataOutputEClass, DATA_OUTPUT__OUTPUT_SET_WITH_OPTIONAL);
		createEReference(dataOutputEClass, DATA_OUTPUT__OUTPUT_SET_WITH_WHILE_EXECUTING);
		createEReference(dataOutputEClass, DATA_OUTPUT__OUTPUT_SET_REFS);
		createEAttribute(dataOutputEClass, DATA_OUTPUT__IS_COLLECTION);
		createEAttribute(dataOutputEClass, DATA_OUTPUT__NAME);

		dataOutputAssociationEClass = createEClass(DATA_OUTPUT_ASSOCIATION);

		dataStateEClass = createEClass(DATA_STATE);
		createEAttribute(dataStateEClass, DATA_STATE__NAME);

		dataStoreEClass = createEClass(DATA_STORE);
		createEAttribute(dataStoreEClass, DATA_STORE__CAPACITY);
		createEAttribute(dataStoreEClass, DATA_STORE__IS_UNLIMITED);
		createEAttribute(dataStoreEClass, DATA_STORE__NAME);

		dataStoreReferenceEClass = createEClass(DATA_STORE_REFERENCE);
		createEReference(dataStoreReferenceEClass, DATA_STORE_REFERENCE__DATA_STORE_REF);

		definitionsEClass = createEClass(DEFINITIONS);
		createEReference(definitionsEClass, DEFINITIONS__IMPORTS);
		createEReference(definitionsEClass, DEFINITIONS__EXTENSIONS);
		createEReference(definitionsEClass, DEFINITIONS__ROOT_ELEMENTS);
		createEReference(definitionsEClass, DEFINITIONS__DIAGRAMS);
		createEReference(definitionsEClass, DEFINITIONS__RELATIONSHIPS);
		createEAttribute(definitionsEClass, DEFINITIONS__EXPORTER);
		createEAttribute(definitionsEClass, DEFINITIONS__EXPORTER_VERSION);
		createEAttribute(definitionsEClass, DEFINITIONS__EXPRESSION_LANGUAGE);
		createEAttribute(definitionsEClass, DEFINITIONS__NAME);
		createEAttribute(definitionsEClass, DEFINITIONS__TARGET_NAMESPACE);
		createEAttribute(definitionsEClass, DEFINITIONS__TYPE_LANGUAGE);

		documentationEClass = createEClass(DOCUMENTATION);
		createEAttribute(documentationEClass, DOCUMENTATION__MIXED);
		createEAttribute(documentationEClass, DOCUMENTATION__TEXT);
		createEAttribute(documentationEClass, DOCUMENTATION__TEXT_FORMAT);

		endEventEClass = createEClass(END_EVENT);

		endPointEClass = createEClass(END_POINT);

		errorEClass = createEClass(ERROR);
		createEAttribute(errorEClass, ERROR__ERROR_CODE);
		createEAttribute(errorEClass, ERROR__NAME);
		createEReference(errorEClass, ERROR__STRUCTURE_REF);

		errorEventDefinitionEClass = createEClass(ERROR_EVENT_DEFINITION);
		createEReference(errorEventDefinitionEClass, ERROR_EVENT_DEFINITION__ERROR_REF);

		escalationEClass = createEClass(ESCALATION);
		createEAttribute(escalationEClass, ESCALATION__ESCALATION_CODE);
		createEAttribute(escalationEClass, ESCALATION__NAME);
		createEReference(escalationEClass, ESCALATION__STRUCTURE_REF);

		escalationEventDefinitionEClass = createEClass(ESCALATION_EVENT_DEFINITION);
		createEReference(escalationEventDefinitionEClass, ESCALATION_EVENT_DEFINITION__ESCALATION_REF);

		eventEClass = createEClass(EVENT);
		createEReference(eventEClass, EVENT__PROPERTIES);

		eventBasedGatewayEClass = createEClass(EVENT_BASED_GATEWAY);
		createEAttribute(eventBasedGatewayEClass, EVENT_BASED_GATEWAY__EVENT_GATEWAY_TYPE);
		createEAttribute(eventBasedGatewayEClass, EVENT_BASED_GATEWAY__INSTANTIATE);

		eventDefinitionEClass = createEClass(EVENT_DEFINITION);

		exclusiveGatewayEClass = createEClass(EXCLUSIVE_GATEWAY);
		createEReference(exclusiveGatewayEClass, EXCLUSIVE_GATEWAY__DEFAULT);

		expressionEClass = createEClass(EXPRESSION);

		extensionEClass = createEClass(EXTENSION);
		createEReference(extensionEClass, EXTENSION__DEFINITION);
		createEAttribute(extensionEClass, EXTENSION__MUST_UNDERSTAND);
		createEAttribute(extensionEClass, EXTENSION__XSD_DEFINITION);

		extensionAttributeDefinitionEClass = createEClass(EXTENSION_ATTRIBUTE_DEFINITION);
		createEAttribute(extensionAttributeDefinitionEClass, EXTENSION_ATTRIBUTE_DEFINITION__NAME);
		createEAttribute(extensionAttributeDefinitionEClass, EXTENSION_ATTRIBUTE_DEFINITION__TYPE);
		createEAttribute(extensionAttributeDefinitionEClass, EXTENSION_ATTRIBUTE_DEFINITION__IS_REFERENCE);
		createEReference(extensionAttributeDefinitionEClass, EXTENSION_ATTRIBUTE_DEFINITION__EXTENSION_DEFINITION);

		extensionAttributeValueEClass = createEClass(EXTENSION_ATTRIBUTE_VALUE);
		createEReference(extensionAttributeValueEClass, EXTENSION_ATTRIBUTE_VALUE__VALUE_REF);
		createEAttribute(extensionAttributeValueEClass, EXTENSION_ATTRIBUTE_VALUE__VALUE);
		createEReference(extensionAttributeValueEClass, EXTENSION_ATTRIBUTE_VALUE__EXTENSION_ATTRIBUTE_DEFINITION);

		extensionDefinitionEClass = createEClass(EXTENSION_DEFINITION);
		createEAttribute(extensionDefinitionEClass, EXTENSION_DEFINITION__NAME);
		createEReference(extensionDefinitionEClass, EXTENSION_DEFINITION__EXTENSION_ATTRIBUTE_DEFINITIONS);

		flowElementEClass = createEClass(FLOW_ELEMENT);
		createEReference(flowElementEClass, FLOW_ELEMENT__AUDITING);
		createEReference(flowElementEClass, FLOW_ELEMENT__MONITORING);
		createEReference(flowElementEClass, FLOW_ELEMENT__CATEGORY_VALUE_REF);
		createEAttribute(flowElementEClass, FLOW_ELEMENT__NAME);

		flowElementsContainerEClass = createEClass(FLOW_ELEMENTS_CONTAINER);
		createEReference(flowElementsContainerEClass, FLOW_ELEMENTS_CONTAINER__LANE_SETS);
		createEReference(flowElementsContainerEClass, FLOW_ELEMENTS_CONTAINER__FLOW_ELEMENTS);

		flowNodeEClass = createEClass(FLOW_NODE);
		createEReference(flowNodeEClass, FLOW_NODE__INCOMING);
		createEReference(flowNodeEClass, FLOW_NODE__LANES);
		createEReference(flowNodeEClass, FLOW_NODE__OUTGOING);

		formalExpressionEClass = createEClass(FORMAL_EXPRESSION);
		createEAttribute(formalExpressionEClass, FORMAL_EXPRESSION__MIXED);
		createEAttribute(formalExpressionEClass, FORMAL_EXPRESSION__BODY);
		createEReference(formalExpressionEClass, FORMAL_EXPRESSION__EVALUATES_TO_TYPE_REF);
		createEAttribute(formalExpressionEClass, FORMAL_EXPRESSION__LANGUAGE);

		gatewayEClass = createEClass(GATEWAY);
		createEAttribute(gatewayEClass, GATEWAY__GATEWAY_DIRECTION);

		globalBusinessRuleTaskEClass = createEClass(GLOBAL_BUSINESS_RULE_TASK);
		createEAttribute(globalBusinessRuleTaskEClass, GLOBAL_BUSINESS_RULE_TASK__IMPLEMENTATION);

		globalChoreographyTaskEClass = createEClass(GLOBAL_CHOREOGRAPHY_TASK);
		createEReference(globalChoreographyTaskEClass, GLOBAL_CHOREOGRAPHY_TASK__INITIATING_PARTICIPANT_REF);

		globalConversationEClass = createEClass(GLOBAL_CONVERSATION);

		globalManualTaskEClass = createEClass(GLOBAL_MANUAL_TASK);

		globalScriptTaskEClass = createEClass(GLOBAL_SCRIPT_TASK);
		createEAttribute(globalScriptTaskEClass, GLOBAL_SCRIPT_TASK__SCRIPT);
		createEAttribute(globalScriptTaskEClass, GLOBAL_SCRIPT_TASK__SCRIPT_LANGUAGE);

		globalTaskEClass = createEClass(GLOBAL_TASK);
		createEReference(globalTaskEClass, GLOBAL_TASK__RESOURCES);

		globalUserTaskEClass = createEClass(GLOBAL_USER_TASK);
		createEReference(globalUserTaskEClass, GLOBAL_USER_TASK__RENDERINGS);
		createEAttribute(globalUserTaskEClass, GLOBAL_USER_TASK__IMPLEMENTATION);

		groupEClass = createEClass(GROUP);
		createEReference(groupEClass, GROUP__CATEGORY_VALUE_REF);

		humanPerformerEClass = createEClass(HUMAN_PERFORMER);

		implicitThrowEventEClass = createEClass(IMPLICIT_THROW_EVENT);

		importEClass = createEClass(IMPORT);
		createEAttribute(importEClass, IMPORT__IMPORT_TYPE);
		createEAttribute(importEClass, IMPORT__LOCATION);
		createEAttribute(importEClass, IMPORT__NAMESPACE);

		inclusiveGatewayEClass = createEClass(INCLUSIVE_GATEWAY);
		createEReference(inclusiveGatewayEClass, INCLUSIVE_GATEWAY__DEFAULT);

		inputOutputBindingEClass = createEClass(INPUT_OUTPUT_BINDING);
		createEReference(inputOutputBindingEClass, INPUT_OUTPUT_BINDING__INPUT_DATA_REF);
		createEReference(inputOutputBindingEClass, INPUT_OUTPUT_BINDING__OPERATION_REF);
		createEReference(inputOutputBindingEClass, INPUT_OUTPUT_BINDING__OUTPUT_DATA_REF);

		inputOutputSpecificationEClass = createEClass(INPUT_OUTPUT_SPECIFICATION);
		createEReference(inputOutputSpecificationEClass, INPUT_OUTPUT_SPECIFICATION__DATA_INPUTS);
		createEReference(inputOutputSpecificationEClass, INPUT_OUTPUT_SPECIFICATION__DATA_OUTPUTS);
		createEReference(inputOutputSpecificationEClass, INPUT_OUTPUT_SPECIFICATION__INPUT_SETS);
		createEReference(inputOutputSpecificationEClass, INPUT_OUTPUT_SPECIFICATION__OUTPUT_SETS);

		inputSetEClass = createEClass(INPUT_SET);
		createEReference(inputSetEClass, INPUT_SET__DATA_INPUT_REFS);
		createEReference(inputSetEClass, INPUT_SET__OPTIONAL_INPUT_REFS);
		createEReference(inputSetEClass, INPUT_SET__WHILE_EXECUTING_INPUT_REFS);
		createEReference(inputSetEClass, INPUT_SET__OUTPUT_SET_REFS);
		createEAttribute(inputSetEClass, INPUT_SET__NAME);

		interactionNodeEClass = createEClass(INTERACTION_NODE);
		createEReference(interactionNodeEClass, INTERACTION_NODE__INCOMING_CONVERSATION_LINKS);
		createEReference(interactionNodeEClass, INTERACTION_NODE__OUTGOING_CONVERSATION_LINKS);

		interfaceEClass = createEClass(INTERFACE);
		createEReference(interfaceEClass, INTERFACE__OPERATIONS);
		createEAttribute(interfaceEClass, INTERFACE__NAME);
		createEAttribute(interfaceEClass, INTERFACE__IMPLEMENTATION_REF);

		intermediateCatchEventEClass = createEClass(INTERMEDIATE_CATCH_EVENT);

		intermediateThrowEventEClass = createEClass(INTERMEDIATE_THROW_EVENT);

		itemAwareElementEClass = createEClass(ITEM_AWARE_ELEMENT);
		createEReference(itemAwareElementEClass, ITEM_AWARE_ELEMENT__DATA_STATE);
		createEReference(itemAwareElementEClass, ITEM_AWARE_ELEMENT__ITEM_SUBJECT_REF);

		itemDefinitionEClass = createEClass(ITEM_DEFINITION);
		createEAttribute(itemDefinitionEClass, ITEM_DEFINITION__IS_COLLECTION);
		createEReference(itemDefinitionEClass, ITEM_DEFINITION__IMPORT);
		createEAttribute(itemDefinitionEClass, ITEM_DEFINITION__ITEM_KIND);
		createEAttribute(itemDefinitionEClass, ITEM_DEFINITION__STRUCTURE_REF);

		laneEClass = createEClass(LANE);
		createEReference(laneEClass, LANE__PARTITION_ELEMENT);
		createEReference(laneEClass, LANE__FLOW_NODE_REFS);
		createEReference(laneEClass, LANE__CHILD_LANE_SET);
		createEAttribute(laneEClass, LANE__NAME);
		createEReference(laneEClass, LANE__PARTITION_ELEMENT_REF);

		laneSetEClass = createEClass(LANE_SET);
		createEReference(laneSetEClass, LANE_SET__LANES);
		createEAttribute(laneSetEClass, LANE_SET__NAME);

		linkEventDefinitionEClass = createEClass(LINK_EVENT_DEFINITION);
		createEReference(linkEventDefinitionEClass, LINK_EVENT_DEFINITION__SOURCE);
		createEReference(linkEventDefinitionEClass, LINK_EVENT_DEFINITION__TARGET);
		createEAttribute(linkEventDefinitionEClass, LINK_EVENT_DEFINITION__NAME);

		loopCharacteristicsEClass = createEClass(LOOP_CHARACTERISTICS);

		manualTaskEClass = createEClass(MANUAL_TASK);

		messageEClass = createEClass(MESSAGE);
		createEReference(messageEClass, MESSAGE__ITEM_REF);
		createEAttribute(messageEClass, MESSAGE__NAME);

		messageEventDefinitionEClass = createEClass(MESSAGE_EVENT_DEFINITION);
		createEReference(messageEventDefinitionEClass, MESSAGE_EVENT_DEFINITION__OPERATION_REF);
		createEReference(messageEventDefinitionEClass, MESSAGE_EVENT_DEFINITION__MESSAGE_REF);

		messageFlowEClass = createEClass(MESSAGE_FLOW);
		createEReference(messageFlowEClass, MESSAGE_FLOW__MESSAGE_REF);
		createEAttribute(messageFlowEClass, MESSAGE_FLOW__NAME);
		createEReference(messageFlowEClass, MESSAGE_FLOW__SOURCE_REF);
		createEReference(messageFlowEClass, MESSAGE_FLOW__TARGET_REF);

		messageFlowAssociationEClass = createEClass(MESSAGE_FLOW_ASSOCIATION);
		createEReference(messageFlowAssociationEClass, MESSAGE_FLOW_ASSOCIATION__INNER_MESSAGE_FLOW_REF);
		createEReference(messageFlowAssociationEClass, MESSAGE_FLOW_ASSOCIATION__OUTER_MESSAGE_FLOW_REF);

		monitoringEClass = createEClass(MONITORING);

		multiInstanceLoopCharacteristicsEClass = createEClass(MULTI_INSTANCE_LOOP_CHARACTERISTICS);
		createEReference(multiInstanceLoopCharacteristicsEClass, MULTI_INSTANCE_LOOP_CHARACTERISTICS__LOOP_CARDINALITY);
		createEReference(multiInstanceLoopCharacteristicsEClass,
				MULTI_INSTANCE_LOOP_CHARACTERISTICS__LOOP_DATA_INPUT_REF);
		createEReference(multiInstanceLoopCharacteristicsEClass,
				MULTI_INSTANCE_LOOP_CHARACTERISTICS__LOOP_DATA_OUTPUT_REF);
		createEReference(multiInstanceLoopCharacteristicsEClass, MULTI_INSTANCE_LOOP_CHARACTERISTICS__INPUT_DATA_ITEM);
		createEReference(multiInstanceLoopCharacteristicsEClass, MULTI_INSTANCE_LOOP_CHARACTERISTICS__OUTPUT_DATA_ITEM);
		createEReference(multiInstanceLoopCharacteristicsEClass,
				MULTI_INSTANCE_LOOP_CHARACTERISTICS__COMPLEX_BEHAVIOR_DEFINITION);
		createEReference(multiInstanceLoopCharacteristicsEClass,
				MULTI_INSTANCE_LOOP_CHARACTERISTICS__COMPLETION_CONDITION);
		createEAttribute(multiInstanceLoopCharacteristicsEClass, MULTI_INSTANCE_LOOP_CHARACTERISTICS__BEHAVIOR);
		createEAttribute(multiInstanceLoopCharacteristicsEClass, MULTI_INSTANCE_LOOP_CHARACTERISTICS__IS_SEQUENTIAL);
		createEReference(multiInstanceLoopCharacteristicsEClass,
				MULTI_INSTANCE_LOOP_CHARACTERISTICS__NONE_BEHAVIOR_EVENT_REF);
		createEReference(multiInstanceLoopCharacteristicsEClass,
				MULTI_INSTANCE_LOOP_CHARACTERISTICS__ONE_BEHAVIOR_EVENT_REF);

		operationEClass = createEClass(OPERATION);
		createEReference(operationEClass, OPERATION__IN_MESSAGE_REF);
		createEReference(operationEClass, OPERATION__OUT_MESSAGE_REF);
		createEReference(operationEClass, OPERATION__ERROR_REFS);
		createEAttribute(operationEClass, OPERATION__NAME);
		createEAttribute(operationEClass, OPERATION__IMPLEMENTATION_REF);

		outputSetEClass = createEClass(OUTPUT_SET);
		createEReference(outputSetEClass, OUTPUT_SET__DATA_OUTPUT_REFS);
		createEReference(outputSetEClass, OUTPUT_SET__OPTIONAL_OUTPUT_REFS);
		createEReference(outputSetEClass, OUTPUT_SET__WHILE_EXECUTING_OUTPUT_REFS);
		createEReference(outputSetEClass, OUTPUT_SET__INPUT_SET_REFS);
		createEAttribute(outputSetEClass, OUTPUT_SET__NAME);

		parallelGatewayEClass = createEClass(PARALLEL_GATEWAY);

		participantEClass = createEClass(PARTICIPANT);
		createEReference(participantEClass, PARTICIPANT__INTERFACE_REFS);
		createEReference(participantEClass, PARTICIPANT__END_POINT_REFS);
		createEReference(participantEClass, PARTICIPANT__PARTICIPANT_MULTIPLICITY);
		createEAttribute(participantEClass, PARTICIPANT__NAME);
		createEReference(participantEClass, PARTICIPANT__PROCESS_REF);

		participantAssociationEClass = createEClass(PARTICIPANT_ASSOCIATION);
		createEReference(participantAssociationEClass, PARTICIPANT_ASSOCIATION__INNER_PARTICIPANT_REF);
		createEReference(participantAssociationEClass, PARTICIPANT_ASSOCIATION__OUTER_PARTICIPANT_REF);

		participantMultiplicityEClass = createEClass(PARTICIPANT_MULTIPLICITY);
		createEAttribute(participantMultiplicityEClass, PARTICIPANT_MULTIPLICITY__MAXIMUM);
		createEAttribute(participantMultiplicityEClass, PARTICIPANT_MULTIPLICITY__MINIMUM);

		partnerEntityEClass = createEClass(PARTNER_ENTITY);
		createEReference(partnerEntityEClass, PARTNER_ENTITY__PARTICIPANT_REF);
		createEAttribute(partnerEntityEClass, PARTNER_ENTITY__NAME);

		partnerRoleEClass = createEClass(PARTNER_ROLE);
		createEReference(partnerRoleEClass, PARTNER_ROLE__PARTICIPANT_REF);
		createEAttribute(partnerRoleEClass, PARTNER_ROLE__NAME);

		performerEClass = createEClass(PERFORMER);

		potentialOwnerEClass = createEClass(POTENTIAL_OWNER);

		processEClass = createEClass(PROCESS);
		createEReference(processEClass, PROCESS__AUDITING);
		createEReference(processEClass, PROCESS__MONITORING);
		createEReference(processEClass, PROCESS__PROPERTIES);
		createEReference(processEClass, PROCESS__ARTIFACTS);
		createEReference(processEClass, PROCESS__RESOURCES);
		createEReference(processEClass, PROCESS__CORRELATION_SUBSCRIPTIONS);
		createEReference(processEClass, PROCESS__SUPPORTS);
		createEReference(processEClass, PROCESS__DEFINITIONAL_COLLABORATION_REF);
		createEAttribute(processEClass, PROCESS__IS_CLOSED);
		createEAttribute(processEClass, PROCESS__IS_EXECUTABLE);
		createEAttribute(processEClass, PROCESS__PROCESS_TYPE);

		propertyEClass = createEClass(PROPERTY);
		createEAttribute(propertyEClass, PROPERTY__NAME);

		receiveTaskEClass = createEClass(RECEIVE_TASK);
		createEAttribute(receiveTaskEClass, RECEIVE_TASK__IMPLEMENTATION);
		createEAttribute(receiveTaskEClass, RECEIVE_TASK__INSTANTIATE);
		createEReference(receiveTaskEClass, RECEIVE_TASK__MESSAGE_REF);
		createEReference(receiveTaskEClass, RECEIVE_TASK__OPERATION_REF);

		relationshipEClass = createEClass(RELATIONSHIP);
		createEReference(relationshipEClass, RELATIONSHIP__SOURCES);
		createEReference(relationshipEClass, RELATIONSHIP__TARGETS);
		createEAttribute(relationshipEClass, RELATIONSHIP__DIRECTION);
		createEAttribute(relationshipEClass, RELATIONSHIP__TYPE);

		renderingEClass = createEClass(RENDERING);

		resourceEClass = createEClass(RESOURCE);
		createEReference(resourceEClass, RESOURCE__RESOURCE_PARAMETERS);
		createEAttribute(resourceEClass, RESOURCE__NAME);

		resourceAssignmentExpressionEClass = createEClass(RESOURCE_ASSIGNMENT_EXPRESSION);
		createEReference(resourceAssignmentExpressionEClass, RESOURCE_ASSIGNMENT_EXPRESSION__EXPRESSION);

		resourceParameterEClass = createEClass(RESOURCE_PARAMETER);
		createEAttribute(resourceParameterEClass, RESOURCE_PARAMETER__IS_REQUIRED);
		createEAttribute(resourceParameterEClass, RESOURCE_PARAMETER__NAME);
		createEReference(resourceParameterEClass, RESOURCE_PARAMETER__TYPE);

		resourceParameterBindingEClass = createEClass(RESOURCE_PARAMETER_BINDING);
		createEReference(resourceParameterBindingEClass, RESOURCE_PARAMETER_BINDING__EXPRESSION);
		createEReference(resourceParameterBindingEClass, RESOURCE_PARAMETER_BINDING__PARAMETER_REF);

		resourceRoleEClass = createEClass(RESOURCE_ROLE);
		createEReference(resourceRoleEClass, RESOURCE_ROLE__RESOURCE_REF);
		createEReference(resourceRoleEClass, RESOURCE_ROLE__RESOURCE_PARAMETER_BINDINGS);
		createEReference(resourceRoleEClass, RESOURCE_ROLE__RESOURCE_ASSIGNMENT_EXPRESSION);
		createEAttribute(resourceRoleEClass, RESOURCE_ROLE__NAME);

		rootElementEClass = createEClass(ROOT_ELEMENT);

		scriptTaskEClass = createEClass(SCRIPT_TASK);
		createEAttribute(scriptTaskEClass, SCRIPT_TASK__SCRIPT);
		createEAttribute(scriptTaskEClass, SCRIPT_TASK__SCRIPT_FORMAT);

		sendTaskEClass = createEClass(SEND_TASK);
		createEAttribute(sendTaskEClass, SEND_TASK__IMPLEMENTATION);
		createEReference(sendTaskEClass, SEND_TASK__MESSAGE_REF);
		createEReference(sendTaskEClass, SEND_TASK__OPERATION_REF);

		sequenceFlowEClass = createEClass(SEQUENCE_FLOW);
		createEReference(sequenceFlowEClass, SEQUENCE_FLOW__CONDITION_EXPRESSION);
		createEAttribute(sequenceFlowEClass, SEQUENCE_FLOW__IS_IMMEDIATE);
		createEReference(sequenceFlowEClass, SEQUENCE_FLOW__SOURCE_REF);
		createEReference(sequenceFlowEClass, SEQUENCE_FLOW__TARGET_REF);

		serviceTaskEClass = createEClass(SERVICE_TASK);
		createEAttribute(serviceTaskEClass, SERVICE_TASK__IMPLEMENTATION);
		createEReference(serviceTaskEClass, SERVICE_TASK__OPERATION_REF);

		signalEClass = createEClass(SIGNAL);
		createEAttribute(signalEClass, SIGNAL__NAME);
		createEReference(signalEClass, SIGNAL__STRUCTURE_REF);

		signalEventDefinitionEClass = createEClass(SIGNAL_EVENT_DEFINITION);
		createEAttribute(signalEventDefinitionEClass, SIGNAL_EVENT_DEFINITION__SIGNAL_REF);

		standardLoopCharacteristicsEClass = createEClass(STANDARD_LOOP_CHARACTERISTICS);
		createEReference(standardLoopCharacteristicsEClass, STANDARD_LOOP_CHARACTERISTICS__LOOP_CONDITION);
		createEReference(standardLoopCharacteristicsEClass, STANDARD_LOOP_CHARACTERISTICS__LOOP_MAXIMUM);
		createEAttribute(standardLoopCharacteristicsEClass, STANDARD_LOOP_CHARACTERISTICS__TEST_BEFORE);

		startEventEClass = createEClass(START_EVENT);
		createEAttribute(startEventEClass, START_EVENT__IS_INTERRUPTING);

		subChoreographyEClass = createEClass(SUB_CHOREOGRAPHY);
		createEReference(subChoreographyEClass, SUB_CHOREOGRAPHY__ARTIFACTS);

		subConversationEClass = createEClass(SUB_CONVERSATION);
		createEReference(subConversationEClass, SUB_CONVERSATION__CONVERSATION_NODES);

		subProcessEClass = createEClass(SUB_PROCESS);
		createEReference(subProcessEClass, SUB_PROCESS__ARTIFACTS);
		createEAttribute(subProcessEClass, SUB_PROCESS__TRIGGERED_BY_EVENT);

		taskEClass = createEClass(TASK);

		terminateEventDefinitionEClass = createEClass(TERMINATE_EVENT_DEFINITION);

		textAnnotationEClass = createEClass(TEXT_ANNOTATION);
		createEAttribute(textAnnotationEClass, TEXT_ANNOTATION__TEXT);
		createEAttribute(textAnnotationEClass, TEXT_ANNOTATION__TEXT_FORMAT);

		throwEventEClass = createEClass(THROW_EVENT);
		createEReference(throwEventEClass, THROW_EVENT__DATA_INPUTS);
		createEReference(throwEventEClass, THROW_EVENT__DATA_INPUT_ASSOCIATION);
		createEReference(throwEventEClass, THROW_EVENT__INPUT_SET);
		createEReference(throwEventEClass, THROW_EVENT__EVENT_DEFINITIONS);
		createEReference(throwEventEClass, THROW_EVENT__EVENT_DEFINITION_REFS);

		timerEventDefinitionEClass = createEClass(TIMER_EVENT_DEFINITION);
		createEReference(timerEventDefinitionEClass, TIMER_EVENT_DEFINITION__TIME_DATE);
		createEReference(timerEventDefinitionEClass, TIMER_EVENT_DEFINITION__TIME_DURATION);
		createEReference(timerEventDefinitionEClass, TIMER_EVENT_DEFINITION__TIME_CYCLE);

		transactionEClass = createEClass(TRANSACTION);
		createEAttribute(transactionEClass, TRANSACTION__PROTOCOL);
		createEAttribute(transactionEClass, TRANSACTION__METHOD);

		userTaskEClass = createEClass(USER_TASK);
		createEReference(userTaskEClass, USER_TASK__RENDERINGS);
		createEAttribute(userTaskEClass, USER_TASK__IMPLEMENTATION);

		eventSubprocessEClass = createEClass(EVENT_SUBPROCESS);

		// Create enums
		adHocOrderingEEnum = createEEnum(AD_HOC_ORDERING);
		associationDirectionEEnum = createEEnum(ASSOCIATION_DIRECTION);
		choreographyLoopTypeEEnum = createEEnum(CHOREOGRAPHY_LOOP_TYPE);
		eventBasedGatewayTypeEEnum = createEEnum(EVENT_BASED_GATEWAY_TYPE);
		gatewayDirectionEEnum = createEEnum(GATEWAY_DIRECTION);
		itemKindEEnum = createEEnum(ITEM_KIND);
		multiInstanceBehaviorEEnum = createEEnum(MULTI_INSTANCE_BEHAVIOR);
		processTypeEEnum = createEEnum(PROCESS_TYPE);
		relationshipDirectionEEnum = createEEnum(RELATIONSHIP_DIRECTION);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized)
			return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage) EPackage.Registry.INSTANCE
				.getEPackage(XMLTypePackage.eNS_URI);
		BpmnDiPackage theBpmnDiPackage = (BpmnDiPackage) EPackage.Registry.INSTANCE.getEPackage(BpmnDiPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		activityEClass.getESuperTypes().add(this.getFlowNode());
		adHocSubProcessEClass.getESuperTypes().add(this.getSubProcess());
		artifactEClass.getESuperTypes().add(this.getBaseElement());
		assignmentEClass.getESuperTypes().add(this.getBaseElement());
		associationEClass.getESuperTypes().add(this.getArtifact());
		auditingEClass.getESuperTypes().add(this.getBaseElement());
		boundaryEventEClass.getESuperTypes().add(this.getCatchEvent());
		businessRuleTaskEClass.getESuperTypes().add(this.getTask());
		callActivityEClass.getESuperTypes().add(this.getActivity());
		callChoreographyEClass.getESuperTypes().add(this.getChoreographyActivity());
		callConversationEClass.getESuperTypes().add(this.getConversationNode());
		callableElementEClass.getESuperTypes().add(this.getRootElement());
		cancelEventDefinitionEClass.getESuperTypes().add(this.getEventDefinition());
		catchEventEClass.getESuperTypes().add(this.getEvent());
		categoryEClass.getESuperTypes().add(this.getRootElement());
		categoryValueEClass.getESuperTypes().add(this.getBaseElement());
		choreographyEClass.getESuperTypes().add(this.getCollaboration());
		choreographyEClass.getESuperTypes().add(this.getFlowElementsContainer());
		choreographyActivityEClass.getESuperTypes().add(this.getFlowNode());
		choreographyTaskEClass.getESuperTypes().add(this.getChoreographyActivity());
		collaborationEClass.getESuperTypes().add(this.getRootElement());
		compensateEventDefinitionEClass.getESuperTypes().add(this.getEventDefinition());
		complexBehaviorDefinitionEClass.getESuperTypes().add(this.getBaseElement());
		complexGatewayEClass.getESuperTypes().add(this.getGateway());
		conditionalEventDefinitionEClass.getESuperTypes().add(this.getEventDefinition());
		conversationEClass.getESuperTypes().add(this.getConversationNode());
		conversationAssociationEClass.getESuperTypes().add(this.getBaseElement());
		conversationLinkEClass.getESuperTypes().add(this.getBaseElement());
		conversationNodeEClass.getESuperTypes().add(this.getBaseElement());
		conversationNodeEClass.getESuperTypes().add(this.getInteractionNode());
		correlationKeyEClass.getESuperTypes().add(this.getBaseElement());
		correlationPropertyEClass.getESuperTypes().add(this.getRootElement());
		correlationPropertyBindingEClass.getESuperTypes().add(this.getBaseElement());
		correlationPropertyRetrievalExpressionEClass.getESuperTypes().add(this.getBaseElement());
		correlationSubscriptionEClass.getESuperTypes().add(this.getBaseElement());
		dataAssociationEClass.getESuperTypes().add(this.getBaseElement());
		dataInputEClass.getESuperTypes().add(this.getItemAwareElement());
		dataInputAssociationEClass.getESuperTypes().add(this.getDataAssociation());
		dataObjectEClass.getESuperTypes().add(this.getFlowElement());
		dataObjectEClass.getESuperTypes().add(this.getItemAwareElement());
		dataObjectReferenceEClass.getESuperTypes().add(this.getFlowElement());
		dataObjectReferenceEClass.getESuperTypes().add(this.getItemAwareElement());
		dataOutputEClass.getESuperTypes().add(this.getItemAwareElement());
		dataOutputAssociationEClass.getESuperTypes().add(this.getDataAssociation());
		dataStateEClass.getESuperTypes().add(this.getBaseElement());
		dataStoreEClass.getESuperTypes().add(this.getItemAwareElement());
		dataStoreEClass.getESuperTypes().add(this.getRootElement());
		dataStoreReferenceEClass.getESuperTypes().add(this.getFlowElement());
		dataStoreReferenceEClass.getESuperTypes().add(this.getItemAwareElement());
		definitionsEClass.getESuperTypes().add(this.getBaseElement());
		documentationEClass.getESuperTypes().add(this.getBaseElement());
		endEventEClass.getESuperTypes().add(this.getThrowEvent());
		endPointEClass.getESuperTypes().add(this.getRootElement());
		errorEClass.getESuperTypes().add(this.getRootElement());
		errorEventDefinitionEClass.getESuperTypes().add(this.getEventDefinition());
		escalationEClass.getESuperTypes().add(this.getRootElement());
		escalationEventDefinitionEClass.getESuperTypes().add(this.getEventDefinition());
		eventEClass.getESuperTypes().add(this.getFlowNode());
		eventEClass.getESuperTypes().add(this.getInteractionNode());
		eventBasedGatewayEClass.getESuperTypes().add(this.getGateway());
		eventDefinitionEClass.getESuperTypes().add(this.getRootElement());
		exclusiveGatewayEClass.getESuperTypes().add(this.getGateway());
		expressionEClass.getESuperTypes().add(this.getBaseElement());
		flowElementEClass.getESuperTypes().add(this.getBaseElement());
		flowElementsContainerEClass.getESuperTypes().add(this.getBaseElement());
		flowNodeEClass.getESuperTypes().add(this.getFlowElement());
		formalExpressionEClass.getESuperTypes().add(this.getExpression());
		gatewayEClass.getESuperTypes().add(this.getFlowNode());
		globalBusinessRuleTaskEClass.getESuperTypes().add(this.getGlobalTask());
		globalChoreographyTaskEClass.getESuperTypes().add(this.getChoreography());
		globalConversationEClass.getESuperTypes().add(this.getCollaboration());
		globalManualTaskEClass.getESuperTypes().add(this.getGlobalTask());
		globalScriptTaskEClass.getESuperTypes().add(this.getGlobalTask());
		globalTaskEClass.getESuperTypes().add(this.getCallableElement());
		globalUserTaskEClass.getESuperTypes().add(this.getGlobalTask());
		groupEClass.getESuperTypes().add(this.getArtifact());
		humanPerformerEClass.getESuperTypes().add(this.getPerformer());
		implicitThrowEventEClass.getESuperTypes().add(this.getThrowEvent());
		inclusiveGatewayEClass.getESuperTypes().add(this.getGateway());
		inputOutputBindingEClass.getESuperTypes().add(this.getBaseElement());
		inputOutputSpecificationEClass.getESuperTypes().add(this.getBaseElement());
		inputSetEClass.getESuperTypes().add(this.getBaseElement());
		interfaceEClass.getESuperTypes().add(this.getRootElement());
		intermediateCatchEventEClass.getESuperTypes().add(this.getCatchEvent());
		intermediateThrowEventEClass.getESuperTypes().add(this.getThrowEvent());
		itemAwareElementEClass.getESuperTypes().add(this.getBaseElement());
		itemDefinitionEClass.getESuperTypes().add(this.getRootElement());
		laneEClass.getESuperTypes().add(this.getBaseElement());
		laneSetEClass.getESuperTypes().add(this.getBaseElement());
		linkEventDefinitionEClass.getESuperTypes().add(this.getEventDefinition());
		loopCharacteristicsEClass.getESuperTypes().add(this.getBaseElement());
		manualTaskEClass.getESuperTypes().add(this.getTask());
		messageEClass.getESuperTypes().add(this.getRootElement());
		messageEventDefinitionEClass.getESuperTypes().add(this.getEventDefinition());
		messageFlowEClass.getESuperTypes().add(this.getBaseElement());
		messageFlowAssociationEClass.getESuperTypes().add(this.getBaseElement());
		monitoringEClass.getESuperTypes().add(this.getBaseElement());
		multiInstanceLoopCharacteristicsEClass.getESuperTypes().add(this.getLoopCharacteristics());
		operationEClass.getESuperTypes().add(this.getBaseElement());
		outputSetEClass.getESuperTypes().add(this.getBaseElement());
		parallelGatewayEClass.getESuperTypes().add(this.getGateway());
		participantEClass.getESuperTypes().add(this.getBaseElement());
		participantEClass.getESuperTypes().add(this.getInteractionNode());
		participantAssociationEClass.getESuperTypes().add(this.getBaseElement());
		participantMultiplicityEClass.getESuperTypes().add(this.getBaseElement());
		partnerEntityEClass.getESuperTypes().add(this.getRootElement());
		partnerRoleEClass.getESuperTypes().add(this.getRootElement());
		performerEClass.getESuperTypes().add(this.getResourceRole());
		potentialOwnerEClass.getESuperTypes().add(this.getHumanPerformer());
		processEClass.getESuperTypes().add(this.getCallableElement());
		processEClass.getESuperTypes().add(this.getFlowElementsContainer());
		propertyEClass.getESuperTypes().add(this.getItemAwareElement());
		receiveTaskEClass.getESuperTypes().add(this.getTask());
		relationshipEClass.getESuperTypes().add(this.getBaseElement());
		renderingEClass.getESuperTypes().add(this.getBaseElement());
		resourceEClass.getESuperTypes().add(this.getRootElement());
		resourceAssignmentExpressionEClass.getESuperTypes().add(this.getBaseElement());
		resourceParameterEClass.getESuperTypes().add(this.getBaseElement());
		resourceParameterBindingEClass.getESuperTypes().add(this.getBaseElement());
		resourceRoleEClass.getESuperTypes().add(this.getBaseElement());
		rootElementEClass.getESuperTypes().add(this.getBaseElement());
		scriptTaskEClass.getESuperTypes().add(this.getTask());
		sendTaskEClass.getESuperTypes().add(this.getTask());
		sequenceFlowEClass.getESuperTypes().add(this.getFlowElement());
		serviceTaskEClass.getESuperTypes().add(this.getTask());
		signalEClass.getESuperTypes().add(this.getRootElement());
		signalEventDefinitionEClass.getESuperTypes().add(this.getEventDefinition());
		standardLoopCharacteristicsEClass.getESuperTypes().add(this.getLoopCharacteristics());
		startEventEClass.getESuperTypes().add(this.getCatchEvent());
		subChoreographyEClass.getESuperTypes().add(this.getChoreographyActivity());
		subChoreographyEClass.getESuperTypes().add(this.getFlowElementsContainer());
		subConversationEClass.getESuperTypes().add(this.getConversationNode());
		subProcessEClass.getESuperTypes().add(this.getActivity());
		subProcessEClass.getESuperTypes().add(this.getFlowElementsContainer());
		taskEClass.getESuperTypes().add(this.getActivity());
		taskEClass.getESuperTypes().add(this.getInteractionNode());
		terminateEventDefinitionEClass.getESuperTypes().add(this.getEventDefinition());
		textAnnotationEClass.getESuperTypes().add(this.getFlowNode());
		throwEventEClass.getESuperTypes().add(this.getEvent());
		timerEventDefinitionEClass.getESuperTypes().add(this.getEventDefinition());
		transactionEClass.getESuperTypes().add(this.getSubProcess());
		userTaskEClass.getESuperTypes().add(this.getTask());
		eventSubprocessEClass.getESuperTypes().add(this.getSubProcess());

		// Initialize classes and features; add operations and parameters
		initEClass(documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDocumentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null,
				"xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null,
				"xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Activity(), this.getActivity(), null, "activity", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_AdHocSubProcess(), this.getAdHocSubProcess(), null, "adHocSubProcess", null, 0,
				-2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_FlowElement(), this.getFlowElement(), null, "flowElement", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Artifact(), this.getArtifact(), null, "artifact", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Assignment(), this.getAssignment(), null, "assignment", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Association(), this.getAssociation(), null, "association", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Auditing(), this.getAuditing(), null, "auditing", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_BaseElement(), this.getBaseElement(), null, "baseElement", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_BaseElementWithMixedContent(), this.getBaseElement(), null,
				"baseElementWithMixedContent", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_BoundaryEvent(), this.getBoundaryEvent(), null, "boundaryEvent", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_BusinessRuleTask(), this.getBusinessRuleTask(), null, "businessRuleTask", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CallableElement(), this.getCallableElement(), null, "callableElement", null, 0,
				-2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CallActivity(), this.getCallActivity(), null, "callActivity", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CallChoreography(), this.getCallChoreography(), null, "callChoreography", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CallConversation(), this.getCallConversation(), null, "callConversation", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ConversationNode(), this.getConversationNode(), null, "conversationNode", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CancelEventDefinition(), this.getCancelEventDefinition(), null,
				"cancelEventDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_EventDefinition(), this.getEventDefinition(), null, "eventDefinition", null, 0,
				-2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_RootElement(), this.getRootElement(), null, "rootElement", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CatchEvent(), this.getCatchEvent(), null, "catchEvent", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Category(), this.getCategory(), null, "category", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CategoryValue(), this.getCategoryValue(), null, "categoryValue", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Choreography(), this.getChoreography(), null, "choreography", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Collaboration(), this.getCollaboration(), null, "collaboration", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ChoreographyActivity(), this.getChoreographyActivity(), null,
				"choreographyActivity", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ChoreographyTask(), this.getChoreographyTask(), null, "choreographyTask", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CompensateEventDefinition(), this.getCompensateEventDefinition(), null,
				"compensateEventDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ComplexBehaviorDefinition(), this.getComplexBehaviorDefinition(), null,
				"complexBehaviorDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ComplexGateway(), this.getComplexGateway(), null, "complexGateway", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ConditionalEventDefinition(), this.getConditionalEventDefinition(), null,
				"conditionalEventDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Conversation(), this.getConversation(), null, "conversation", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ConversationAssociation(), this.getConversationAssociation(), null,
				"conversationAssociation", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ConversationLink(), this.getConversationLink(), null, "conversationLink", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CorrelationKey(), this.getCorrelationKey(), null, "correlationKey", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CorrelationProperty(), this.getCorrelationProperty(), null,
				"correlationProperty", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CorrelationPropertyBinding(), this.getCorrelationPropertyBinding(), null,
				"correlationPropertyBinding", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CorrelationPropertyRetrievalExpression(),
				this.getCorrelationPropertyRetrievalExpression(), null, "correlationPropertyRetrievalExpression", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_CorrelationSubscription(), this.getCorrelationSubscription(), null,
				"correlationSubscription", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_DataAssociation(), this.getDataAssociation(), null, "dataAssociation", null, 0,
				-2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_DataInput(), this.getDataInput(), null, "dataInput", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_DataInputAssociation(), this.getDataInputAssociation(), null,
				"dataInputAssociation", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_DataObject(), this.getDataObject(), null, "dataObject", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_DataObjectReference(), this.getDataObjectReference(), null,
				"dataObjectReference", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_DataOutput(), this.getDataOutput(), null, "dataOutput", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_DataOutputAssociation(), this.getDataOutputAssociation(), null,
				"dataOutputAssociation", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_DataState(), this.getDataState(), null, "dataState", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_DataStore(), this.getDataStore(), null, "dataStore", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_DataStoreReference(), this.getDataStoreReference(), null, "dataStoreReference",
				null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Definitions(), this.getDefinitions(), null, "definitions", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Documentation(), this.getDocumentation(), null, "documentation", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_EndEvent(), this.getEndEvent(), null, "endEvent", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_EndPoint(), this.getEndPoint(), null, "endPoint", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Error(), this.getError(), null, "error", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_ErrorEventDefinition(), this.getErrorEventDefinition(), null,
				"errorEventDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Escalation(), this.getEscalation(), null, "escalation", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_EscalationEventDefinition(), this.getEscalationEventDefinition(), null,
				"escalationEventDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Event(), this.getEvent(), null, "event", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_EventBasedGateway(), this.getEventBasedGateway(), null, "eventBasedGateway",
				null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ExclusiveGateway(), this.getExclusiveGateway(), null, "exclusiveGateway", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Expression(), this.getExpression(), null, "expression", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Extension(), this.getExtension(), null, "extension", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ExtensionElements(), this.getExtensionAttributeValue(), null,
				"extensionElements", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_FlowNode(), this.getFlowNode(), null, "flowNode", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_FormalExpression(), this.getFormalExpression(), null, "formalExpression", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Gateway(), this.getGateway(), null, "gateway", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_GlobalBusinessRuleTask(), this.getGlobalBusinessRuleTask(), null,
				"globalBusinessRuleTask", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_GlobalChoreographyTask(), this.getGlobalChoreographyTask(), null,
				"globalChoreographyTask", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_GlobalConversation(), this.getGlobalConversation(), null, "globalConversation",
				null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_GlobalManualTask(), this.getGlobalManualTask(), null, "globalManualTask", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_GlobalScriptTask(), this.getGlobalScriptTask(), null, "globalScriptTask", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_GlobalTask(), this.getGlobalTask(), null, "globalTask", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_GlobalUserTask(), this.getGlobalUserTask(), null, "globalUserTask", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Group(), this.getGroup(), null, "group", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_HumanPerformer(), this.getHumanPerformer(), null, "humanPerformer", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Performer(), this.getPerformer(), null, "performer", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ResourceRole(), this.getResourceRole(), null, "resourceRole", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ImplicitThrowEvent(), this.getImplicitThrowEvent(), null, "implicitThrowEvent",
				null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Import(), this.getImport(), null, "import", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_InclusiveGateway(), this.getInclusiveGateway(), null, "inclusiveGateway", null,
				0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_InputSet(), this.getInputSet(), null, "inputSet", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Interface(), this.getInterface(), null, "interface", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_IntermediateCatchEvent(), this.getIntermediateCatchEvent(), null,
				"intermediateCatchEvent", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_IntermediateThrowEvent(), this.getIntermediateThrowEvent(), null,
				"intermediateThrowEvent", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_IoBinding(), this.getInputOutputBinding(), null, "ioBinding", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_IoSpecification(), this.getInputOutputSpecification(), null, "ioSpecification",
				null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ItemDefinition(), this.getItemDefinition(), null, "itemDefinition", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Lane(), this.getLane(), null, "lane", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_LaneSet(), this.getLaneSet(), null, "laneSet", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_LinkEventDefinition(), this.getLinkEventDefinition(), null,
				"linkEventDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_LoopCharacteristics(), this.getLoopCharacteristics(), null,
				"loopCharacteristics", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ManualTask(), this.getManualTask(), null, "manualTask", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Message(), this.getMessage(), null, "message", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_MessageEventDefinition(), this.getMessageEventDefinition(), null,
				"messageEventDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_MessageFlow(), this.getMessageFlow(), null, "messageFlow", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_MessageFlowAssociation(), this.getMessageFlowAssociation(), null,
				"messageFlowAssociation", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Monitoring(), this.getMonitoring(), null, "monitoring", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_MultiInstanceLoopCharacteristics(), this.getMultiInstanceLoopCharacteristics(),
				null, "multiInstanceLoopCharacteristics", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Operation(), this.getOperation(), null, "operation", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_OutputSet(), this.getOutputSet(), null, "outputSet", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ParallelGateway(), this.getParallelGateway(), null, "parallelGateway", null, 0,
				-2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Participant(), this.getParticipant(), null, "participant", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ParticipantAssociation(), this.getParticipantAssociation(), null,
				"participantAssociation", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ParticipantMultiplicity(), this.getParticipantMultiplicity(), null,
				"participantMultiplicity", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_PartnerEntity(), this.getPartnerEntity(), null, "partnerEntity", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_PartnerRole(), this.getPartnerRole(), null, "partnerRole", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_PotentialOwner(), this.getPotentialOwner(), null, "potentialOwner", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Process(), this.getProcess(), null, "process", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_Property(), this.getProperty(), null, "property", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ReceiveTask(), this.getReceiveTask(), null, "receiveTask", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Relationship(), this.getRelationship(), null, "relationship", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Rendering(), this.getRendering(), null, "rendering", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Resource(), this.getResource(), null, "resource", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ResourceAssignmentExpression(), this.getResourceAssignmentExpression(), null,
				"resourceAssignmentExpression", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ResourceParameter(), this.getResourceParameter(), null, "resourceParameter",
				null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ResourceParameterBinding(), this.getResourceParameterBinding(), null,
				"resourceParameterBinding", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Script(), ecorePackage.getEObject(), null, "script", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ScriptTask(), this.getScriptTask(), null, "scriptTask", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_SendTask(), this.getSendTask(), null, "sendTask", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_SequenceFlow(), this.getSequenceFlow(), null, "sequenceFlow", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ServiceTask(), this.getServiceTask(), null, "serviceTask", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Signal(), this.getSignal(), null, "signal", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_SignalEventDefinition(), this.getSignalEventDefinition(), null,
				"signalEventDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_StandardLoopCharacteristics(), this.getStandardLoopCharacteristics(), null,
				"standardLoopCharacteristics", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_StartEvent(), this.getStartEvent(), null, "startEvent", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_SubChoreography(), this.getSubChoreography(), null, "subChoreography", null, 0,
				-2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_SubConversation(), this.getSubConversation(), null, "subConversation", null, 0,
				-2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_SubProcess(), this.getSubProcess(), null, "subProcess", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Task(), this.getTask(), null, "task", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_TerminateEventDefinition(), this.getTerminateEventDefinition(), null,
				"terminateEventDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Text(), ecorePackage.getEObject(), null, "text", null, 0, -2, null, IS_TRANSIENT,
				IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED,
				IS_ORDERED);
		initEReference(getDocumentRoot_TextAnnotation(), this.getTextAnnotation(), null, "textAnnotation", null, 0, -2,
				null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_ThrowEvent(), this.getThrowEvent(), null, "throwEvent", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_TimerEventDefinition(), this.getTimerEventDefinition(), null,
				"timerEventDefinition", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_Transaction(), this.getTransaction(), null, "transaction", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_UserTask(), this.getUserTask(), null, "userTask", null, 0, -2, null,
				IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, IS_ORDERED);
		initEReference(getDocumentRoot_EventSubProcess(), this.getEventSubprocess(), null, "eventSubProcess", null, 0,
				-2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(activityEClass, Activity.class, "Activity", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getActivity_IoSpecification(), this.getInputOutputSpecification(), null, "ioSpecification", null,
				0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getActivity_BoundaryEventRefs(), this.getBoundaryEvent(), this.getBoundaryEvent_AttachedToRef(),
				"boundaryEventRefs", null, 0, -1, Activity.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);
		initEReference(getActivity_Properties(), this.getProperty(), null, "properties", null, 0, -1, Activity.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getActivity_DataInputAssociations(), this.getDataInputAssociation(), null,
				"dataInputAssociations", null, 0, -1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getActivity_DataOutputAssociations(), this.getDataOutputAssociation(), null,
				"dataOutputAssociations", null, 0, -1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getActivity_Resources(), this.getResourceRole(), null, "resources", null, 0, -1, Activity.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getActivity_LoopCharacteristics(), this.getLoopCharacteristics(), null, "loopCharacteristics",
				null, 0, 1, Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getActivity_CompletionQuantity(), ecorePackage.getEInt(), "completionQuantity", "1", 1, 1,
				Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEReference(getActivity_Default(), this.getSequenceFlow(), null, "default", null, 0, 1, Activity.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getActivity_IsForCompensation(), ecorePackage.getEBoolean(), "isForCompensation", "false", 1, 1,
				Activity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getActivity_StartQuantity(), ecorePackage.getEInt(), "startQuantity", "1", 1, 1, Activity.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(adHocSubProcessEClass, AdHocSubProcess.class, "AdHocSubProcess", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAdHocSubProcess_CompletionCondition(), this.getExpression(), null, "completionCondition",
				null, 1, 1, AdHocSubProcess.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getAdHocSubProcess_CancelRemainingInstances(), ecorePackage.getEBoolean(),
				"cancelRemainingInstances", "true", 1, 1, AdHocSubProcess.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getAdHocSubProcess_Ordering(), this.getAdHocOrdering(), "ordering", null, 1, 1,
				AdHocSubProcess.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(artifactEClass, Artifact.class, "Artifact", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(assignmentEClass, Assignment.class, "Assignment", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAssignment_From(), this.getExpression(), null, "from", null, 1, 1, Assignment.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getAssignment_To(), this.getExpression(), null, "to", null, 1, 1, Assignment.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(associationEClass, Association.class, "Association", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAssociation_AssociationDirection(), this.getAssociationDirection(), "associationDirection",
				null, 1, 1, Association.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getAssociation_SourceRef(), this.getBaseElement(), null, "sourceRef", null, 1, 1,
				Association.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getAssociation_TargetRef(), this.getBaseElement(), null, "targetRef", null, 1, 1,
				Association.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(auditingEClass, Auditing.class, "Auditing", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(baseElementEClass, BaseElement.class, "BaseElement", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getBaseElement_Documentation(), this.getDocumentation(), null, "documentation", null, 0, -1,
				BaseElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getBaseElement_ExtensionValues(), this.getExtensionAttributeValue(), null, "extensionValues",
				null, 0, -1, BaseElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getBaseElement_ExtensionDefinitions(), this.getExtensionDefinition(), null,
				"extensionDefinitions", null, 0, -1, BaseElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getBaseElement_Id(), theXMLTypePackage.getNCName(), "id", null, 1, 1, BaseElement.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getBaseElement_AnyAttribute(), ecorePackage.getEFeatureMapEntry(), "anyAttribute", null, 0, -1,
				BaseElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(boundaryEventEClass, BoundaryEvent.class, "BoundaryEvent", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getBoundaryEvent_AttachedToRef(), this.getActivity(), this.getActivity_BoundaryEventRefs(),
				"attachedToRef", null, 1, 1, BoundaryEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getBoundaryEvent_CancelActivity(), ecorePackage.getEBoolean(), "cancelActivity", "true", 1, 1,
				BoundaryEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(businessRuleTaskEClass, BusinessRuleTask.class, "BusinessRuleTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBusinessRuleTask_Implementation(), ecorePackage.getEString(), "implementation", null, 1, 1,
				BusinessRuleTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(callActivityEClass, CallActivity.class, "CallActivity", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCallActivity_CalledElement(), theXMLTypePackage.getString(), "calledElement", null, 1, 1,
				CallActivity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(callChoreographyEClass, CallChoreography.class, "CallChoreography", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCallChoreography_ParticipantAssociations(), this.getParticipantAssociation(), null,
				"participantAssociations", null, 0, -1, CallChoreography.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCallChoreography_CalledChoreographyRef(), this.getChoreography(), null,
				"calledChoreographyRef", null, 0, 1, CallChoreography.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(callConversationEClass, CallConversation.class, "CallConversation", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCallConversation_ParticipantAssociations(), this.getParticipantAssociation(), null,
				"participantAssociations", null, 0, -1, CallConversation.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCallConversation_CalledCollaborationRef(), this.getCollaboration(), null,
				"calledCollaborationRef", null, 0, 1, CallConversation.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(callableElementEClass, CallableElement.class, "CallableElement", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCallableElement_SupportedInterfaceRefs(), this.getInterface(), null, "supportedInterfaceRefs",
				null, 0, -1, CallableElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCallableElement_IoSpecification(), this.getInputOutputSpecification(), null,
				"ioSpecification", null, 0, 1, CallableElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCallableElement_IoBinding(), this.getInputOutputBinding(), null, "ioBinding", null, 0, -1,
				CallableElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getCallableElement_Name(), ecorePackage.getEString(), "name", null, 1, 1, CallableElement.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(cancelEventDefinitionEClass, CancelEventDefinition.class, "CancelEventDefinition", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(catchEventEClass, CatchEvent.class, "CatchEvent", IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCatchEvent_DataOutputs(), this.getDataOutput(), null, "dataOutputs", null, 0, -1,
				CatchEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCatchEvent_DataOutputAssociation(), this.getDataOutputAssociation(), null,
				"dataOutputAssociation", null, 0, -1, CatchEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCatchEvent_OutputSet(), this.getOutputSet(), null, "outputSet", null, 0, 1, CatchEvent.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCatchEvent_EventDefinitions(), this.getEventDefinition(), null, "eventDefinitions", null, 0,
				-1, CatchEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCatchEvent_EventDefinitionRefs(), this.getEventDefinition(), null, "eventDefinitionRefs",
				null, 0, -1, CatchEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getCatchEvent_ParallelMultiple(), ecorePackage.getEBoolean(), "parallelMultiple", null, 1, 1,
				CatchEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(categoryEClass, Category.class, "Category", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCategory_CategoryValue(), this.getCategoryValue(), null, "categoryValue", null, 0, -1,
				Category.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getCategory_Name(), ecorePackage.getEString(), "name", null, 1, 1, Category.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(categoryValueEClass, CategoryValue.class, "CategoryValue", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCategoryValue_Value(), ecorePackage.getEString(), "value", null, 1, 1, CategoryValue.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEReference(getCategoryValue_CategorizedFlowElements(), this.getFlowElement(), null,
				"categorizedFlowElements", null, 0, -1, CategoryValue.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
				!IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);

		initEClass(choreographyEClass, Choreography.class, "Choreography", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(choreographyActivityEClass, ChoreographyActivity.class, "ChoreographyActivity", IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getChoreographyActivity_ParticipantRefs(), this.getParticipant(), null, "participantRefs", null,
				2, -1, ChoreographyActivity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getChoreographyActivity_CorrelationKeys(), this.getCorrelationKey(), null, "correlationKeys",
				null, 0, -1, ChoreographyActivity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getChoreographyActivity_InitiatingParticipantRef(), this.getParticipant(), null,
				"initiatingParticipantRef", null, 1, 1, ChoreographyActivity.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getChoreographyActivity_LoopType(), this.getChoreographyLoopType(), "loopType", "None", 1, 1,
				ChoreographyActivity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(choreographyTaskEClass, ChoreographyTask.class, "ChoreographyTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getChoreographyTask_MessageFlowRef(), this.getMessageFlow(), null, "messageFlowRef", null, 1, 2,
				ChoreographyTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(collaborationEClass, Collaboration.class, "Collaboration", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCollaboration_Participants(), this.getParticipant(), null, "participants", null, 0, -1,
				Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCollaboration_MessageFlows(), this.getMessageFlow(), null, "messageFlows", null, 0, -1,
				Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCollaboration_Artifacts(), this.getArtifact(), null, "artifacts", null, 0, -1,
				Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCollaboration_Conversations(), this.getConversationNode(), null, "conversations", null, 0, -1,
				Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCollaboration_ConversationAssociations(), this.getConversationAssociation(), null,
				"conversationAssociations", null, 1, 1, Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCollaboration_ParticipantAssociations(), this.getParticipantAssociation(), null,
				"participantAssociations", null, 0, -1, Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCollaboration_MessageFlowAssociations(), this.getMessageFlowAssociation(), null,
				"messageFlowAssociations", null, 0, -1, Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCollaboration_CorrelationKeys(), this.getCorrelationKey(), null, "correlationKeys", null, 0,
				-1, Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCollaboration_ChoreographyRef(), this.getChoreography(), null, "choreographyRef", null, 0, -1,
				Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCollaboration_ConversationLinks(), this.getConversationLink(), null, "conversationLinks",
				null, 0, -1, Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getCollaboration_IsClosed(), ecorePackage.getEBoolean(), "isClosed", null, 1, 1,
				Collaboration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getCollaboration_Name(), ecorePackage.getEString(), "name", null, 1, 1, Collaboration.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(compensateEventDefinitionEClass, CompensateEventDefinition.class, "CompensateEventDefinition",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCompensateEventDefinition_ActivityRef(), this.getActivity(), null, "activityRef", null, 0, 1,
				CompensateEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getCompensateEventDefinition_WaitForCompletion(), ecorePackage.getEBoolean(),
				"waitForCompletion", null, 1, 1, CompensateEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(complexBehaviorDefinitionEClass, ComplexBehaviorDefinition.class, "ComplexBehaviorDefinition",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getComplexBehaviorDefinition_Condition(), this.getFormalExpression(), null, "condition", null, 1,
				1, ComplexBehaviorDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getComplexBehaviorDefinition_Event(), this.getImplicitThrowEvent(), null, "event", null, 0, 1,
				ComplexBehaviorDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(complexGatewayEClass, ComplexGateway.class, "ComplexGateway", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getComplexGateway_ActivationCondition(), this.getExpression(), null, "activationCondition", null,
				0, 1, ComplexGateway.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getComplexGateway_Default(), this.getSequenceFlow(), null, "default", null, 0, 1,
				ComplexGateway.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(conditionalEventDefinitionEClass, ConditionalEventDefinition.class, "ConditionalEventDefinition",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getConditionalEventDefinition_Condition(), this.getExpression(), null, "condition", null, 1, 1,
				ConditionalEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(conversationEClass, Conversation.class, "Conversation", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(conversationAssociationEClass, ConversationAssociation.class, "ConversationAssociation",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getConversationAssociation_InnerConversationNodeRef(), this.getConversationNode(), null,
				"innerConversationNodeRef", null, 1, 1, ConversationAssociation.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getConversationAssociation_OuterConversationNodeRef(), this.getConversationNode(), null,
				"outerConversationNodeRef", null, 1, 1, ConversationAssociation.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(conversationLinkEClass, ConversationLink.class, "ConversationLink", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getConversationLink_Name(), ecorePackage.getEString(), "name", null, 0, 1,
				ConversationLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEReference(getConversationLink_SourceRef(), this.getInteractionNode(), null, "sourceRef", null, 1, 1,
				ConversationLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getConversationLink_TargetRef(), this.getInteractionNode(), null, "targetRef", null, 1, 1,
				ConversationLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(conversationNodeEClass, ConversationNode.class, "ConversationNode", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getConversationNode_ParticipantRefs(), this.getParticipant(), null, "participantRefs", null, 2,
				-1, ConversationNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getConversationNode_MessageFlowRefs(), this.getMessageFlow(), null, "messageFlowRefs", null, 0,
				-1, ConversationNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getConversationNode_CorrelationKeys(), this.getCorrelationKey(), null, "correlationKeys", null,
				0, -1, ConversationNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getConversationNode_Name(), ecorePackage.getEString(), "name", null, 1, 1,
				ConversationNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(correlationKeyEClass, CorrelationKey.class, "CorrelationKey", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCorrelationKey_CorrelationPropertyRef(), this.getCorrelationProperty(), null,
				"correlationPropertyRef", null, 0, -1, CorrelationKey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getCorrelationKey_Name(), ecorePackage.getEString(), "name", null, 1, 1, CorrelationKey.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(correlationPropertyEClass, CorrelationProperty.class, "CorrelationProperty", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCorrelationProperty_CorrelationPropertyRetrievalExpression(),
				this.getCorrelationPropertyRetrievalExpression(), null, "correlationPropertyRetrievalExpression", null,
				1, -1, CorrelationProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getCorrelationProperty_Name(), ecorePackage.getEString(), "name", null, 1, 1,
				CorrelationProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCorrelationProperty_Type(), this.getItemDefinition(), null, "type", null, 0, 1,
				CorrelationProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(correlationPropertyBindingEClass, CorrelationPropertyBinding.class, "CorrelationPropertyBinding",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCorrelationPropertyBinding_DataPath(), this.getFormalExpression(), null, "dataPath", null, 1,
				1, CorrelationPropertyBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCorrelationPropertyBinding_CorrelationPropertyRef(), this.getCorrelationProperty(), null,
				"correlationPropertyRef", null, 1, 1, CorrelationPropertyBinding.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(correlationPropertyRetrievalExpressionEClass, CorrelationPropertyRetrievalExpression.class,
				"CorrelationPropertyRetrievalExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCorrelationPropertyRetrievalExpression_MessagePath(), this.getFormalExpression(), null,
				"messagePath", null, 1, 1, CorrelationPropertyRetrievalExpression.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getCorrelationPropertyRetrievalExpression_MessageRef(), this.getMessage(), null, "messageRef",
				null, 1, 1, CorrelationPropertyRetrievalExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(correlationSubscriptionEClass, CorrelationSubscription.class, "CorrelationSubscription",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCorrelationSubscription_CorrelationPropertyBinding(), this.getCorrelationPropertyBinding(),
				null, "correlationPropertyBinding", null, 0, -1, CorrelationSubscription.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEReference(getCorrelationSubscription_CorrelationKeyRef(), this.getCorrelationKey(), null,
				"correlationKeyRef", null, 1, 1, CorrelationSubscription.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(dataAssociationEClass, DataAssociation.class, "DataAssociation", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDataAssociation_SourceRef(), this.getItemAwareElement(), null, "sourceRef", null, 0, -1,
				DataAssociation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getDataAssociation_TargetRef(), this.getItemAwareElement(), null, "targetRef", null, 1, 1,
				DataAssociation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getDataAssociation_Transformation(), this.getFormalExpression(), null, "transformation", null, 0,
				1, DataAssociation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getDataAssociation_Assignment(), this.getAssignment(), null, "assignment", null, 0, -1,
				DataAssociation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(dataInputEClass, DataInput.class, "DataInput", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDataInput_InputSetWithOptional(), this.getInputSet(), this.getInputSet_OptionalInputRefs(),
				"inputSetWithOptional", null, 0, -1, DataInput.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);
		initEReference(getDataInput_InputSetWithWhileExecuting(), this.getInputSet(),
				this.getInputSet_WhileExecutingInputRefs(), "inputSetWithWhileExecuting", null, 0, -1, DataInput.class,
				IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, !IS_ORDERED);
		initEReference(getDataInput_InputSetRefs(), this.getInputSet(), this.getInputSet_DataInputRefs(),
				"inputSetRefs", null, 1, -1, DataInput.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);
		initEAttribute(getDataInput_IsCollection(), ecorePackage.getEBoolean(), "isCollection", "false", 1, 1,
				DataInput.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getDataInput_Name(), ecorePackage.getEString(), "name", null, 0, 1, DataInput.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(dataInputAssociationEClass, DataInputAssociation.class, "DataInputAssociation", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(dataObjectEClass, DataObject.class, "DataObject", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDataObject_IsCollection(), ecorePackage.getEBoolean(), "isCollection", "false", 1, 1,
				DataObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(dataObjectReferenceEClass, DataObjectReference.class, "DataObjectReference", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDataObjectReference_DataObjectRef(), this.getDataObject(), null, "dataObjectRef", null, 1, 1,
				DataObjectReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(dataOutputEClass, DataOutput.class, "DataOutput", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDataOutput_OutputSetWithOptional(), this.getOutputSet(),
				this.getOutputSet_OptionalOutputRefs(), "outputSetWithOptional", null, 0, -1, DataOutput.class,
				IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, !IS_ORDERED);
		initEReference(getDataOutput_OutputSetWithWhileExecuting(), this.getOutputSet(),
				this.getOutputSet_WhileExecutingOutputRefs(), "outputSetWithWhileExecuting", null, 0, -1,
				DataOutput.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);
		initEReference(getDataOutput_OutputSetRefs(), this.getOutputSet(), this.getOutputSet_DataOutputRefs(),
				"outputSetRefs", null, 1, -1, DataOutput.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);
		initEAttribute(getDataOutput_IsCollection(), ecorePackage.getEBoolean(), "isCollection", "false", 1, 1,
				DataOutput.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getDataOutput_Name(), ecorePackage.getEString(), "name", null, 0, 1, DataOutput.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(dataOutputAssociationEClass, DataOutputAssociation.class, "DataOutputAssociation", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(dataStateEClass, DataState.class, "DataState", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDataState_Name(), ecorePackage.getEString(), "name", null, 1, 1, DataState.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(dataStoreEClass, DataStore.class, "DataStore", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDataStore_Capacity(), ecorePackage.getEInt(), "capacity", null, 1, 1, DataStore.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getDataStore_IsUnlimited(), ecorePackage.getEBoolean(), "isUnlimited", "true", 1, 1,
				DataStore.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getDataStore_Name(), ecorePackage.getEString(), "name", null, 1, 1, DataStore.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(dataStoreReferenceEClass, DataStoreReference.class, "DataStoreReference", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDataStoreReference_DataStoreRef(), this.getDataStore(), null, "dataStoreRef", null, 0, 1,
				DataStoreReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(definitionsEClass, Definitions.class, "Definitions", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDefinitions_Imports(), this.getImport(), null, "imports", null, 0, -1, Definitions.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getDefinitions_Extensions(), this.getExtension(), null, "extensions", null, 0, -1,
				Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getDefinitions_RootElements(), this.getRootElement(), null, "rootElements", null, 0, -1,
				Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getDefinitions_Diagrams(), theBpmnDiPackage.getBPMNDiagram(), null, "diagrams", null, 0, -1,
				Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getDefinitions_Relationships(), this.getRelationship(), null, "relationships", null, 0, -1,
				Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getDefinitions_Exporter(), ecorePackage.getEString(), "exporter", null, 1, 1, Definitions.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getDefinitions_ExporterVersion(), ecorePackage.getEString(), "exporterVersion", null, 1, 1,
				Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getDefinitions_ExpressionLanguage(), ecorePackage.getEString(), "expressionLanguage",
				"http://www.w3.org/1999/XPath", 1, 1, Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getDefinitions_Name(), ecorePackage.getEString(), "name", null, 1, 1, Definitions.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getDefinitions_TargetNamespace(), ecorePackage.getEString(), "targetNamespace", null, 1, 1,
				Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getDefinitions_TypeLanguage(), ecorePackage.getEString(), "typeLanguage",
				"http://www.w3.org/2001/XMLSchema", 1, 1, Definitions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(documentationEClass, Documentation.class, "Documentation", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDocumentation_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1,
				Documentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getDocumentation_Text(), ecorePackage.getEString(), "text", null, 1, 1, Documentation.class,
				!IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);
		initEAttribute(getDocumentation_TextFormat(), ecorePackage.getEString(), "textFormat", "text/plain", 1, 1,
				Documentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(endEventEClass, EndEvent.class, "EndEvent", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(endPointEClass, EndPoint.class, "EndPoint", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(errorEClass, org.eclipse.bpmn2.Error.class, "Error", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getError_ErrorCode(), ecorePackage.getEString(), "errorCode", null, 1, 1,
				org.eclipse.bpmn2.Error.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getError_Name(), ecorePackage.getEString(), "name", null, 1, 1, org.eclipse.bpmn2.Error.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEReference(getError_StructureRef(), this.getItemDefinition(), null, "structureRef", null, 0, 1,
				org.eclipse.bpmn2.Error.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(errorEventDefinitionEClass, ErrorEventDefinition.class, "ErrorEventDefinition", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getErrorEventDefinition_ErrorRef(), this.getError(), null, "errorRef", null, 0, 1,
				ErrorEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(escalationEClass, Escalation.class, "Escalation", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEscalation_EscalationCode(), ecorePackage.getEString(), "escalationCode", null, 1, 1,
				Escalation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getEscalation_Name(), ecorePackage.getEString(), "name", null, 1, 1, Escalation.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEReference(getEscalation_StructureRef(), this.getItemDefinition(), null, "structureRef", null, 0, 1,
				Escalation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(escalationEventDefinitionEClass, EscalationEventDefinition.class, "EscalationEventDefinition",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEscalationEventDefinition_EscalationRef(), this.getEscalation(), null, "escalationRef", null,
				0, 1, EscalationEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(eventEClass, Event.class, "Event", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEvent_Properties(), this.getProperty(), null, "properties", null, 0, -1, Event.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(eventBasedGatewayEClass, EventBasedGateway.class, "EventBasedGateway", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEventBasedGateway_EventGatewayType(), this.getEventBasedGatewayType(), "eventGatewayType",
				null, 1, 1, EventBasedGateway.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getEventBasedGateway_Instantiate(), ecorePackage.getEBoolean(), "instantiate", "false", 1, 1,
				EventBasedGateway.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(eventDefinitionEClass, EventDefinition.class, "EventDefinition", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(exclusiveGatewayEClass, ExclusiveGateway.class, "ExclusiveGateway", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getExclusiveGateway_Default(), this.getSequenceFlow(), null, "default", null, 0, 1,
				ExclusiveGateway.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(expressionEClass, Expression.class, "Expression", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(extensionEClass, Extension.class, "Extension", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getExtension_Definition(), this.getExtensionDefinition(), null, "definition", null, 1, 1,
				Extension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getExtension_MustUnderstand(), ecorePackage.getEBoolean(), "mustUnderstand", "false", 1, 1,
				Extension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getExtension_XsdDefinition(), theXMLTypePackage.getQName(), "xsdDefinition", null, 0, 1,
				Extension.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(extensionAttributeDefinitionEClass, ExtensionAttributeDefinition.class,
				"ExtensionAttributeDefinition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getExtensionAttributeDefinition_Name(), ecorePackage.getEString(), "name", null, 1, 1,
				ExtensionAttributeDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getExtensionAttributeDefinition_Type(), ecorePackage.getEString(), "type", null, 1, 1,
				ExtensionAttributeDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getExtensionAttributeDefinition_IsReference(), ecorePackage.getEBoolean(), "isReference",
				"false", 1, 1, ExtensionAttributeDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getExtensionAttributeDefinition_ExtensionDefinition(), this.getExtensionDefinition(),
				this.getExtensionDefinition_ExtensionAttributeDefinitions(), "extensionDefinition", null, 1, 1,
				ExtensionAttributeDefinition.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);

		initEClass(extensionAttributeValueEClass, ExtensionAttributeValue.class, "ExtensionAttributeValue",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getExtensionAttributeValue_ValueRef(), ecorePackage.getEObject(), null, "valueRef", null, 0, 1,
				ExtensionAttributeValue.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);
		initEAttribute(getExtensionAttributeValue_Value(), ecorePackage.getEFeatureMapEntry(), "value", null, 0, -1,
				ExtensionAttributeValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				!IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getExtensionAttributeValue_ExtensionAttributeDefinition(),
				this.getExtensionAttributeDefinition(), null, "extensionAttributeDefinition", null, 1, 1,
				ExtensionAttributeValue.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);

		initEClass(extensionDefinitionEClass, ExtensionDefinition.class, "ExtensionDefinition", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getExtensionDefinition_Name(), ecorePackage.getEString(), "name", null, 1, 1,
				ExtensionDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getExtensionDefinition_ExtensionAttributeDefinitions(), this.getExtensionAttributeDefinition(),
				this.getExtensionAttributeDefinition_ExtensionDefinition(), "extensionAttributeDefinitions", null, 0,
				-1, ExtensionDefinition.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);

		initEClass(flowElementEClass, FlowElement.class, "FlowElement", IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getFlowElement_Auditing(), this.getAuditing(), null, "auditing", null, 0, 1, FlowElement.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getFlowElement_Monitoring(), this.getMonitoring(), null, "monitoring", null, 0, 1,
				FlowElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getFlowElement_CategoryValueRef(), this.getCategoryValue(), null, "categoryValueRef", null, 0,
				-1, FlowElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getFlowElement_Name(), ecorePackage.getEString(), "name", null, 1, 1, FlowElement.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(flowElementsContainerEClass, FlowElementsContainer.class, "FlowElementsContainer", IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getFlowElementsContainer_LaneSets(), this.getLaneSet(), null, "laneSets", null, 0, -1,
				FlowElementsContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getFlowElementsContainer_FlowElements(), this.getFlowElement(), null, "flowElements", null, 0,
				-1, FlowElementsContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(flowNodeEClass, FlowNode.class, "FlowNode", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getFlowNode_Incoming(), this.getSequenceFlow(), this.getSequenceFlow_TargetRef(), "incoming",
				null, 0, -1, FlowNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getFlowNode_Lanes(), this.getLane(), this.getLane_FlowNodeRefs(), "lanes", null, 0, -1,
				FlowNode.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);
		initEReference(getFlowNode_Outgoing(), this.getSequenceFlow(), this.getSequenceFlow_SourceRef(), "outgoing",
				null, 0, -1, FlowNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(formalExpressionEClass, FormalExpression.class, "FormalExpression", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFormalExpression_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1,
				FormalExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getFormalExpression_Body(), ecorePackage.getEString(), "body", null, 1, 1,
				FormalExpression.class, !IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				IS_DERIVED, !IS_ORDERED);
		initEReference(getFormalExpression_EvaluatesToTypeRef(), this.getItemDefinition(), null, "evaluatesToTypeRef",
				null, 1, 1, FormalExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getFormalExpression_Language(), ecorePackage.getEString(), "language", null, 1, 1,
				FormalExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(gatewayEClass, Gateway.class, "Gateway", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getGateway_GatewayDirection(), this.getGatewayDirection(), "gatewayDirection", "unspecified", 1,
				1, Gateway.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(globalBusinessRuleTaskEClass, GlobalBusinessRuleTask.class, "GlobalBusinessRuleTask", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getGlobalBusinessRuleTask_Implementation(), ecorePackage.getEString(), "implementation", null, 1,
				1, GlobalBusinessRuleTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(globalChoreographyTaskEClass, GlobalChoreographyTask.class, "GlobalChoreographyTask", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGlobalChoreographyTask_InitiatingParticipantRef(), this.getParticipant(), null,
				"initiatingParticipantRef", null, 1, 1, GlobalChoreographyTask.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(globalConversationEClass, GlobalConversation.class, "GlobalConversation", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(globalManualTaskEClass, GlobalManualTask.class, "GlobalManualTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(globalScriptTaskEClass, GlobalScriptTask.class, "GlobalScriptTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getGlobalScriptTask_Script(), ecorePackage.getEString(), "script", null, 1, 1,
				GlobalScriptTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getGlobalScriptTask_ScriptLanguage(), ecorePackage.getEString(), "scriptLanguage", null, 1, 1,
				GlobalScriptTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(globalTaskEClass, GlobalTask.class, "GlobalTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGlobalTask_Resources(), this.getResourceRole(), null, "resources", null, 0, -1,
				GlobalTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(globalUserTaskEClass, GlobalUserTask.class, "GlobalUserTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGlobalUserTask_Renderings(), this.getRendering(), null, "renderings", null, 0, -1,
				GlobalUserTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getGlobalUserTask_Implementation(), ecorePackage.getEString(), "implementation", null, 1, 1,
				GlobalUserTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(groupEClass, Group.class, "Group", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGroup_CategoryValueRef(), this.getCategoryValue(), null, "categoryValueRef", null, 0, 1,
				Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(humanPerformerEClass, HumanPerformer.class, "HumanPerformer", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(implicitThrowEventEClass, ImplicitThrowEvent.class, "ImplicitThrowEvent", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(importEClass, Import.class, "Import", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getImport_ImportType(), ecorePackage.getEString(), "importType", null, 1, 1, Import.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getImport_Location(), ecorePackage.getEString(), "location", null, 1, 1, Import.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getImport_Namespace(), ecorePackage.getEString(), "namespace", null, 1, 1, Import.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(inclusiveGatewayEClass, InclusiveGateway.class, "InclusiveGateway", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInclusiveGateway_Default(), this.getSequenceFlow(), null, "default", null, 0, 1,
				InclusiveGateway.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(inputOutputBindingEClass, InputOutputBinding.class, "InputOutputBinding", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInputOutputBinding_InputDataRef(), this.getInputSet(), null, "inputDataRef", null, 1, 1,
				InputOutputBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getInputOutputBinding_OperationRef(), this.getOperation(), null, "operationRef", null, 1, 1,
				InputOutputBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getInputOutputBinding_OutputDataRef(), this.getOutputSet(), null, "outputDataRef", null, 1, 1,
				InputOutputBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(inputOutputSpecificationEClass, InputOutputSpecification.class, "InputOutputSpecification",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInputOutputSpecification_DataInputs(), this.getDataInput(), null, "dataInputs", null, 0, -1,
				InputOutputSpecification.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getInputOutputSpecification_DataOutputs(), this.getDataOutput(), null, "dataOutputs", null, 0,
				-1, InputOutputSpecification.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getInputOutputSpecification_InputSets(), this.getInputSet(), null, "inputSets", null, 1, -1,
				InputOutputSpecification.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getInputOutputSpecification_OutputSets(), this.getOutputSet(), null, "outputSets", null, 1, -1,
				InputOutputSpecification.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(inputSetEClass, InputSet.class, "InputSet", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInputSet_DataInputRefs(), this.getDataInput(), this.getDataInput_InputSetRefs(),
				"dataInputRefs", null, 0, -1, InputSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getInputSet_OptionalInputRefs(), this.getDataInput(), this.getDataInput_InputSetWithOptional(),
				"optionalInputRefs", null, 0, -1, InputSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getInputSet_WhileExecutingInputRefs(), this.getDataInput(),
				this.getDataInput_InputSetWithWhileExecuting(), "whileExecutingInputRefs", null, 0, -1, InputSet.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getInputSet_OutputSetRefs(), this.getOutputSet(), this.getOutputSet_InputSetRefs(),
				"outputSetRefs", null, 0, -1, InputSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getInputSet_Name(), ecorePackage.getEString(), "name", null, 1, 1, InputSet.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(interactionNodeEClass, InteractionNode.class, "InteractionNode", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInteractionNode_IncomingConversationLinks(), this.getConversationLink(), null,
				"incomingConversationLinks", null, 0, -1, InteractionNode.class, IS_TRANSIENT, IS_VOLATILE,
				!IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);
		initEReference(getInteractionNode_OutgoingConversationLinks(), this.getConversationLink(), null,
				"outgoingConversationLinks", null, 0, -1, InteractionNode.class, IS_TRANSIENT, IS_VOLATILE,
				!IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, !IS_ORDERED);

		initEClass(interfaceEClass, Interface.class, "Interface", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getInterface_Operations(), this.getOperation(), null, "operations", null, 1, -1, Interface.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getInterface_Name(), ecorePackage.getEString(), "name", null, 1, 1, Interface.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getInterface_ImplementationRef(), ecorePackage.getEString(), "implementationRef", null, 1, 1,
				Interface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(intermediateCatchEventEClass, IntermediateCatchEvent.class, "IntermediateCatchEvent", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(intermediateThrowEventEClass, IntermediateThrowEvent.class, "IntermediateThrowEvent", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(itemAwareElementEClass, ItemAwareElement.class, "ItemAwareElement", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getItemAwareElement_DataState(), this.getDataState(), null, "dataState", null, 0, 1,
				ItemAwareElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getItemAwareElement_ItemSubjectRef(), this.getItemDefinition(), null, "itemSubjectRef", null, 0,
				1, ItemAwareElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(itemDefinitionEClass, ItemDefinition.class, "ItemDefinition", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getItemDefinition_IsCollection(), ecorePackage.getEBoolean(), "isCollection", "false", 1, 1,
				ItemDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEReference(getItemDefinition_Import(), this.getImport(), null, "import", null, 0, 1, ItemDefinition.class,
				IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				IS_DERIVED, !IS_ORDERED);
		initEAttribute(getItemDefinition_ItemKind(), this.getItemKind(), "itemKind", null, 1, 1, ItemDefinition.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getItemDefinition_StructureRef(), ecorePackage.getEString(), "structureRef", null, 1, 1,
				ItemDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(laneEClass, Lane.class, "Lane", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getLane_PartitionElement(), this.getBaseElement(), null, "partitionElement", null, 0, 1,
				Lane.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getLane_FlowNodeRefs(), this.getFlowNode(), this.getFlowNode_Lanes(), "flowNodeRefs", null, 0,
				-1, Lane.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getLane_ChildLaneSet(), this.getLaneSet(), null, "childLaneSet", null, 0, 1, Lane.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getLane_Name(), ecorePackage.getEString(), "name", null, 1, 1, Lane.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getLane_PartitionElementRef(), this.getBaseElement(), null, "partitionElementRef", null, 0, 1,
				Lane.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(laneSetEClass, LaneSet.class, "LaneSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getLaneSet_Lanes(), this.getLane(), null, "lanes", null, 0, -1, LaneSet.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getLaneSet_Name(), ecorePackage.getEString(), "name", null, 0, 1, LaneSet.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(linkEventDefinitionEClass, LinkEventDefinition.class, "LinkEventDefinition", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getLinkEventDefinition_Source(), this.getLinkEventDefinition(),
				this.getLinkEventDefinition_Target(), "source", null, 0, -1, LinkEventDefinition.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEReference(getLinkEventDefinition_Target(), this.getLinkEventDefinition(),
				this.getLinkEventDefinition_Source(), "target", null, 0, 1, LinkEventDefinition.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getLinkEventDefinition_Name(), ecorePackage.getEString(), "name", null, 1, 1,
				LinkEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(loopCharacteristicsEClass, LoopCharacteristics.class, "LoopCharacteristics", IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(manualTaskEClass, ManualTask.class, "ManualTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(messageEClass, Message.class, "Message", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMessage_ItemRef(), this.getItemDefinition(), null, "itemRef", null, 0, 1, Message.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getMessage_Name(), ecorePackage.getEString(), "name", null, 1, 1, Message.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(messageEventDefinitionEClass, MessageEventDefinition.class, "MessageEventDefinition", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMessageEventDefinition_OperationRef(), this.getOperation(), null, "operationRef", null, 0, 1,
				MessageEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMessageEventDefinition_MessageRef(), this.getMessage(), null, "messageRef", null, 0, 1,
				MessageEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(messageFlowEClass, MessageFlow.class, "MessageFlow", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMessageFlow_MessageRef(), this.getMessage(), null, "messageRef", null, 0, 1,
				MessageFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getMessageFlow_Name(), ecorePackage.getEString(), "name", null, 1, 1, MessageFlow.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEReference(getMessageFlow_SourceRef(), this.getInteractionNode(), null, "sourceRef", null, 1, 1,
				MessageFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMessageFlow_TargetRef(), this.getInteractionNode(), null, "targetRef", null, 1, 1,
				MessageFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(messageFlowAssociationEClass, MessageFlowAssociation.class, "MessageFlowAssociation", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMessageFlowAssociation_InnerMessageFlowRef(), this.getMessageFlow(), null,
				"innerMessageFlowRef", null, 1, 1, MessageFlowAssociation.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMessageFlowAssociation_OuterMessageFlowRef(), this.getMessageFlow(), null,
				"outerMessageFlowRef", null, 1, 1, MessageFlowAssociation.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(monitoringEClass, Monitoring.class, "Monitoring", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(multiInstanceLoopCharacteristicsEClass, MultiInstanceLoopCharacteristics.class,
				"MultiInstanceLoopCharacteristics", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMultiInstanceLoopCharacteristics_LoopCardinality(), this.getExpression(), null,
				"loopCardinality", null, 0, 1, MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMultiInstanceLoopCharacteristics_LoopDataInputRef(), this.getItemAwareElement(), null,
				"loopDataInputRef", null, 0, 1, MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMultiInstanceLoopCharacteristics_LoopDataOutputRef(), this.getItemAwareElement(), null,
				"loopDataOutputRef", null, 0, 1, MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMultiInstanceLoopCharacteristics_InputDataItem(), this.getDataInput(), null, "inputDataItem",
				null, 0, 1, MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMultiInstanceLoopCharacteristics_OutputDataItem(), this.getDataOutput(), null,
				"outputDataItem", null, 0, 1, MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMultiInstanceLoopCharacteristics_ComplexBehaviorDefinition(),
				this.getComplexBehaviorDefinition(), null, "complexBehaviorDefinition", null, 0, -1,
				MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMultiInstanceLoopCharacteristics_CompletionCondition(), this.getExpression(), null,
				"completionCondition", null, 0, 1, MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getMultiInstanceLoopCharacteristics_Behavior(), this.getMultiInstanceBehavior(), "behavior",
				"All", 1, 1, MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getMultiInstanceLoopCharacteristics_IsSequential(), ecorePackage.getEBoolean(), "isSequential",
				"false", 1, 1, MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMultiInstanceLoopCharacteristics_NoneBehaviorEventRef(), this.getEventDefinition(), null,
				"noneBehaviorEventRef", null, 0, 1, MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMultiInstanceLoopCharacteristics_OneBehaviorEventRef(), this.getEventDefinition(), null,
				"oneBehaviorEventRef", null, 0, 1, MultiInstanceLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(operationEClass, Operation.class, "Operation", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getOperation_InMessageRef(), this.getMessage(), null, "inMessageRef", null, 1, 1,
				Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getOperation_OutMessageRef(), this.getMessage(), null, "outMessageRef", null, 0, 1,
				Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getOperation_ErrorRefs(), this.getError(), null, "errorRefs", null, 0, -1, Operation.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getOperation_Name(), ecorePackage.getEString(), "name", null, 1, 1, Operation.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getOperation_ImplementationRef(), ecorePackage.getEString(), "implementationRef", null, 1, 1,
				Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(outputSetEClass, OutputSet.class, "OutputSet", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getOutputSet_DataOutputRefs(), this.getDataOutput(), this.getDataOutput_OutputSetRefs(),
				"dataOutputRefs", null, 0, -1, OutputSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getOutputSet_OptionalOutputRefs(), this.getDataOutput(),
				this.getDataOutput_OutputSetWithOptional(), "optionalOutputRefs", null, 0, -1, OutputSet.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getOutputSet_WhileExecutingOutputRefs(), this.getDataOutput(),
				this.getDataOutput_OutputSetWithWhileExecuting(), "whileExecutingOutputRefs", null, 0, -1,
				OutputSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getOutputSet_InputSetRefs(), this.getInputSet(), this.getInputSet_OutputSetRefs(),
				"inputSetRefs", null, 0, -1, OutputSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getOutputSet_Name(), ecorePackage.getEString(), "name", null, 1, 1, OutputSet.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(parallelGatewayEClass, ParallelGateway.class, "ParallelGateway", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(participantEClass, Participant.class, "Participant", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getParticipant_InterfaceRefs(), this.getInterface(), null, "interfaceRefs", null, 0, -1,
				Participant.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getParticipant_EndPointRefs(), this.getEndPoint(), null, "endPointRefs", null, 0, -1,
				Participant.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getParticipant_ParticipantMultiplicity(), this.getParticipantMultiplicity(), null,
				"participantMultiplicity", null, 0, 1, Participant.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getParticipant_Name(), ecorePackage.getEString(), "name", null, 1, 1, Participant.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEReference(getParticipant_ProcessRef(), this.getProcess(), null, "processRef", null, 0, 1,
				Participant.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(participantAssociationEClass, ParticipantAssociation.class, "ParticipantAssociation", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getParticipantAssociation_InnerParticipantRef(), this.getParticipant(), null,
				"innerParticipantRef", null, 1, 1, ParticipantAssociation.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getParticipantAssociation_OuterParticipantRef(), this.getParticipant(), null,
				"outerParticipantRef", null, 1, 1, ParticipantAssociation.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(participantMultiplicityEClass, ParticipantMultiplicity.class, "ParticipantMultiplicity",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParticipantMultiplicity_Maximum(), ecorePackage.getEInt(), "maximum", "1", 0, 1,
				ParticipantMultiplicity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getParticipantMultiplicity_Minimum(), ecorePackage.getEInt(), "minimum", "0", 1, 1,
				ParticipantMultiplicity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(partnerEntityEClass, PartnerEntity.class, "PartnerEntity", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPartnerEntity_ParticipantRef(), this.getParticipant(), null, "participantRef", null, 0, -1,
				PartnerEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getPartnerEntity_Name(), ecorePackage.getEString(), "name", null, 1, 1, PartnerEntity.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(partnerRoleEClass, PartnerRole.class, "PartnerRole", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPartnerRole_ParticipantRef(), this.getParticipant(), null, "participantRef", null, 0, -1,
				PartnerRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getPartnerRole_Name(), ecorePackage.getEString(), "name", null, 1, 1, PartnerRole.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(performerEClass, Performer.class, "Performer", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(potentialOwnerEClass, PotentialOwner.class, "PotentialOwner", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(processEClass, org.eclipse.bpmn2.Process.class, "Process", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getProcess_Auditing(), this.getAuditing(), null, "auditing", null, 0, 1,
				org.eclipse.bpmn2.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getProcess_Monitoring(), this.getMonitoring(), null, "monitoring", null, 0, 1,
				org.eclipse.bpmn2.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getProcess_Properties(), this.getProperty(), null, "properties", null, 0, -1,
				org.eclipse.bpmn2.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getProcess_Artifacts(), this.getArtifact(), null, "artifacts", null, 0, -1,
				org.eclipse.bpmn2.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getProcess_Resources(), this.getResourceRole(), null, "resources", null, 0, -1,
				org.eclipse.bpmn2.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getProcess_CorrelationSubscriptions(), this.getCorrelationSubscription(), null,
				"correlationSubscriptions", null, 0, -1, org.eclipse.bpmn2.Process.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getProcess_Supports(), this.getProcess(), null, "supports", null, 0, -1,
				org.eclipse.bpmn2.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getProcess_DefinitionalCollaborationRef(), this.getCollaboration(), null,
				"definitionalCollaborationRef", null, 0, 1, org.eclipse.bpmn2.Process.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getProcess_IsClosed(), ecorePackage.getEBoolean(), "isClosed", null, 1, 1,
				org.eclipse.bpmn2.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getProcess_IsExecutable(), ecorePackage.getEBoolean(), "isExecutable", null, 1, 1,
				org.eclipse.bpmn2.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getProcess_ProcessType(), this.getProcessType(), "processType", null, 1, 1,
				org.eclipse.bpmn2.Process.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(propertyEClass, Property.class, "Property", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProperty_Name(), ecorePackage.getEString(), "name", null, 1, 1, Property.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(receiveTaskEClass, ReceiveTask.class, "ReceiveTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getReceiveTask_Implementation(), ecorePackage.getEString(), "implementation", null, 1, 1,
				ReceiveTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getReceiveTask_Instantiate(), ecorePackage.getEBoolean(), "instantiate", "false", 1, 1,
				ReceiveTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEReference(getReceiveTask_MessageRef(), this.getMessage(), null, "messageRef", null, 0, 1,
				ReceiveTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getReceiveTask_OperationRef(), this.getOperation(), null, "operationRef", null, 0, 1,
				ReceiveTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(relationshipEClass, Relationship.class, "Relationship", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRelationship_Sources(), ecorePackage.getEObject(), null, "sources", null, 1, -1,
				Relationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getRelationship_Targets(), ecorePackage.getEObject(), null, "targets", null, 1, -1,
				Relationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getRelationship_Direction(), this.getRelationshipDirection(), "direction", null, 1, 1,
				Relationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getRelationship_Type(), ecorePackage.getEString(), "type", null, 1, 1, Relationship.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(renderingEClass, Rendering.class, "Rendering", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(resourceEClass, Resource.class, "Resource", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getResource_ResourceParameters(), this.getResourceParameter(), null, "resourceParameters", null,
				0, -1, Resource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getResource_Name(), ecorePackage.getEString(), "name", null, 1, 1, Resource.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(resourceAssignmentExpressionEClass, ResourceAssignmentExpression.class,
				"ResourceAssignmentExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getResourceAssignmentExpression_Expression(), this.getExpression(), null, "expression", null, 1,
				1, ResourceAssignmentExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(resourceParameterEClass, ResourceParameter.class, "ResourceParameter", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getResourceParameter_IsRequired(), ecorePackage.getEBoolean(), "isRequired", null, 1, 1,
				ResourceParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEAttribute(getResourceParameter_Name(), ecorePackage.getEString(), "name", null, 1, 1,
				ResourceParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEReference(getResourceParameter_Type(), this.getItemDefinition(), null, "type", null, 0, 1,
				ResourceParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(resourceParameterBindingEClass, ResourceParameterBinding.class, "ResourceParameterBinding",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getResourceParameterBinding_Expression(), this.getExpression(), null, "expression", null, 1, 1,
				ResourceParameterBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getResourceParameterBinding_ParameterRef(), this.getResourceParameter(), null, "parameterRef",
				null, 1, 1, ResourceParameterBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(resourceRoleEClass, ResourceRole.class, "ResourceRole", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getResourceRole_ResourceRef(), this.getResource(), null, "resourceRef", null, 0, 1,
				ResourceRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getResourceRole_ResourceParameterBindings(), this.getResourceParameterBinding(), null,
				"resourceParameterBindings", null, 0, -1, ResourceRole.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getResourceRole_ResourceAssignmentExpression(), this.getResourceAssignmentExpression(), null,
				"resourceAssignmentExpression", null, 0, 1, ResourceRole.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getResourceRole_Name(), ecorePackage.getEString(), "name", null, 1, 1, ResourceRole.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(rootElementEClass, RootElement.class, "RootElement", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(scriptTaskEClass, ScriptTask.class, "ScriptTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getScriptTask_Script(), ecorePackage.getEString(), "script", null, 1, 1, ScriptTask.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getScriptTask_ScriptFormat(), ecorePackage.getEString(), "scriptFormat", null, 1, 1,
				ScriptTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(sendTaskEClass, SendTask.class, "SendTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSendTask_Implementation(), ecorePackage.getEString(), "implementation", null, 1, 1,
				SendTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEReference(getSendTask_MessageRef(), this.getMessage(), null, "messageRef", null, 0, 1, SendTask.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getSendTask_OperationRef(), this.getOperation(), null, "operationRef", null, 0, 1,
				SendTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(sequenceFlowEClass, SequenceFlow.class, "SequenceFlow", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSequenceFlow_ConditionExpression(), this.getExpression(), null, "conditionExpression", null,
				0, 1, SequenceFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getSequenceFlow_IsImmediate(), ecorePackage.getEBoolean(), "isImmediate", null, 0, 1,
				SequenceFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEReference(getSequenceFlow_SourceRef(), this.getFlowNode(), this.getFlowNode_Outgoing(), "sourceRef", null,
				1, 1, SequenceFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getSequenceFlow_TargetRef(), this.getFlowNode(), this.getFlowNode_Incoming(), "targetRef", null,
				1, 1, SequenceFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(serviceTaskEClass, ServiceTask.class, "ServiceTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getServiceTask_Implementation(), ecorePackage.getEString(), "implementation", null, 1, 1,
				ServiceTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);
		initEReference(getServiceTask_OperationRef(), this.getOperation(), null, "operationRef", null, 0, 1,
				ServiceTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(signalEClass, Signal.class, "Signal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSignal_Name(), ecorePackage.getEString(), "name", null, 1, 1, Signal.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getSignal_StructureRef(), this.getItemDefinition(), null, "structureRef", null, 0, 1,
				Signal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(signalEventDefinitionEClass, SignalEventDefinition.class, "SignalEventDefinition", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSignalEventDefinition_SignalRef(), theXMLTypePackage.getNCName(), "signalRef", null, 1, 1,
				SignalEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(standardLoopCharacteristicsEClass, StandardLoopCharacteristics.class, "StandardLoopCharacteristics",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getStandardLoopCharacteristics_LoopCondition(), this.getExpression(), null, "loopCondition",
				null, 0, 1, StandardLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getStandardLoopCharacteristics_LoopMaximum(), this.getExpression(), null, "loopMaximum", null, 0,
				1, StandardLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getStandardLoopCharacteristics_TestBefore(), ecorePackage.getEBoolean(), "testBefore", "false",
				1, 1, StandardLoopCharacteristics.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
				!IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(startEventEClass, StartEvent.class, "StartEvent", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStartEvent_IsInterrupting(), ecorePackage.getEBoolean(), "isInterrupting", "true", 1, 1,
				StartEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(subChoreographyEClass, SubChoreography.class, "SubChoreography", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSubChoreography_Artifacts(), this.getArtifact(), null, "artifacts", null, 0, -1,
				SubChoreography.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(subConversationEClass, SubConversation.class, "SubConversation", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSubConversation_ConversationNodes(), this.getConversationNode(), null, "conversationNodes",
				null, 0, -1, SubConversation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(subProcessEClass, SubProcess.class, "SubProcess", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSubProcess_Artifacts(), this.getArtifact(), null, "artifacts", null, 0, -1, SubProcess.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getSubProcess_TriggeredByEvent(), ecorePackage.getEBoolean(), "triggeredByEvent", "false", 1, 1,
				SubProcess.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(taskEClass, Task.class, "Task", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(terminateEventDefinitionEClass, TerminateEventDefinition.class, "TerminateEventDefinition",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(textAnnotationEClass, TextAnnotation.class, "TextAnnotation", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTextAnnotation_Text(), ecorePackage.getEString(), "text", null, 1, 1, TextAnnotation.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getTextAnnotation_TextFormat(), ecorePackage.getEString(), "textFormat", "text/plain", 1, 1,
				TextAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(throwEventEClass, ThrowEvent.class, "ThrowEvent", IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getThrowEvent_DataInputs(), this.getDataInput(), null, "dataInputs", null, 0, -1,
				ThrowEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getThrowEvent_DataInputAssociation(), this.getDataInputAssociation(), null,
				"dataInputAssociation", null, 0, -1, ThrowEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getThrowEvent_InputSet(), this.getInputSet(), null, "inputSet", null, 0, 1, ThrowEvent.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getThrowEvent_EventDefinitions(), this.getEventDefinition(), null, "eventDefinitions", null, 0,
				-1, ThrowEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getThrowEvent_EventDefinitionRefs(), this.getEventDefinition(), null, "eventDefinitionRefs",
				null, 0, -1, ThrowEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
				IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(timerEventDefinitionEClass, TimerEventDefinition.class, "TimerEventDefinition", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTimerEventDefinition_TimeDate(), this.getExpression(), null, "timeDate", null, 0, 1,
				TimerEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getTimerEventDefinition_TimeDuration(), this.getExpression(), null, "timeDuration", null, 0, 1,
				TimerEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getTimerEventDefinition_TimeCycle(), this.getExpression(), null, "timeCycle", null, 0, 1,
				TimerEventDefinition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(transactionEClass, Transaction.class, "Transaction", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTransaction_Protocol(), ecorePackage.getEString(), "protocol", null, 0, 1, Transaction.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);
		initEAttribute(getTransaction_Method(), ecorePackage.getEString(), "method", null, 1, 1, Transaction.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				!IS_ORDERED);

		initEClass(userTaskEClass, UserTask.class, "UserTask", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getUserTask_Renderings(), this.getRendering(), null, "renderings", null, 0, -1, UserTask.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE,
				IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEAttribute(getUserTask_Implementation(), ecorePackage.getEString(), "implementation", null, 1, 1,
				UserTask.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, !IS_ORDERED);

		initEClass(eventSubprocessEClass, EventSubprocess.class, "EventSubprocess", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		// Initialize enums and add enum literals
		initEEnum(adHocOrderingEEnum, AdHocOrdering.class, "AdHocOrdering");
		addEEnumLiteral(adHocOrderingEEnum, AdHocOrdering.PARALLEL);
		addEEnumLiteral(adHocOrderingEEnum, AdHocOrdering.SEQUENTIAL);

		initEEnum(associationDirectionEEnum, AssociationDirection.class, "AssociationDirection");
		addEEnumLiteral(associationDirectionEEnum, AssociationDirection.NONE);
		addEEnumLiteral(associationDirectionEEnum, AssociationDirection.ONE);
		addEEnumLiteral(associationDirectionEEnum, AssociationDirection.BOTH);

		initEEnum(choreographyLoopTypeEEnum, ChoreographyLoopType.class, "ChoreographyLoopType");
		addEEnumLiteral(choreographyLoopTypeEEnum, ChoreographyLoopType.NONE);
		addEEnumLiteral(choreographyLoopTypeEEnum, ChoreographyLoopType.STANDARD);
		addEEnumLiteral(choreographyLoopTypeEEnum, ChoreographyLoopType.MULTI_INSTANCE_SEQUENTIAL);
		addEEnumLiteral(choreographyLoopTypeEEnum, ChoreographyLoopType.MULTI_INSTANCE_PARALLEL);

		initEEnum(eventBasedGatewayTypeEEnum, EventBasedGatewayType.class, "EventBasedGatewayType");
		addEEnumLiteral(eventBasedGatewayTypeEEnum, EventBasedGatewayType.PARALLEL);
		addEEnumLiteral(eventBasedGatewayTypeEEnum, EventBasedGatewayType.EXCLUSIVE);

		initEEnum(gatewayDirectionEEnum, GatewayDirection.class, "GatewayDirection");
		addEEnumLiteral(gatewayDirectionEEnum, GatewayDirection.UNSPECIFIED);
		addEEnumLiteral(gatewayDirectionEEnum, GatewayDirection.CONVERGING);
		addEEnumLiteral(gatewayDirectionEEnum, GatewayDirection.DIVERGING);
		addEEnumLiteral(gatewayDirectionEEnum, GatewayDirection.MIXED);

		initEEnum(itemKindEEnum, ItemKind.class, "ItemKind");
		addEEnumLiteral(itemKindEEnum, ItemKind.PHYSICAL);
		addEEnumLiteral(itemKindEEnum, ItemKind.INFORMATION);

		initEEnum(multiInstanceBehaviorEEnum, MultiInstanceBehavior.class, "MultiInstanceBehavior");
		addEEnumLiteral(multiInstanceBehaviorEEnum, MultiInstanceBehavior.NONE);
		addEEnumLiteral(multiInstanceBehaviorEEnum, MultiInstanceBehavior.ONE);
		addEEnumLiteral(multiInstanceBehaviorEEnum, MultiInstanceBehavior.ALL);
		addEEnumLiteral(multiInstanceBehaviorEEnum, MultiInstanceBehavior.COMPLEX);

		initEEnum(processTypeEEnum, ProcessType.class, "ProcessType");
		addEEnumLiteral(processTypeEEnum, ProcessType.NONE);
		addEEnumLiteral(processTypeEEnum, ProcessType.PUBLIC);
		addEEnumLiteral(processTypeEEnum, ProcessType.PRIVATE);

		initEEnum(relationshipDirectionEEnum, RelationshipDirection.class, "RelationshipDirection");
		addEEnumLiteral(relationshipDirectionEEnum, RelationshipDirection.NONE);
		addEEnumLiteral(relationshipDirectionEEnum, RelationshipDirection.FORWARD);
		addEEnumLiteral(relationshipDirectionEEnum, RelationshipDirection.BACKWARD);
		addEEnumLiteral(relationshipDirectionEEnum, RelationshipDirection.BOTH);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
		addAnnotation(documentRootEClass, source, new String[] { "name", "", "kind", "mixed" });
		addAnnotation(getDocumentRoot_Mixed(), source, new String[] { "kind", "elementWildcard", "name", ":mixed" });
		addAnnotation(getDocumentRoot_XMLNSPrefixMap(), source,
				new String[] { "kind", "attribute", "name", "xmlns:prefix" });
		addAnnotation(getDocumentRoot_XSISchemaLocation(), source,
				new String[] { "kind", "attribute", "name", "xsi:schemaLocation" });
		addAnnotation(getDocumentRoot_Activity(), source, new String[] { "kind", "element", "name", "activity",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_AdHocSubProcess(), source,
				new String[] { "kind", "element", "name", "adHocSubProcess", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_FlowElement(), source, new String[] { "kind", "element", "name", "flowElement",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Artifact(), source, new String[] { "kind", "element", "name", "artifact",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Assignment(), source, new String[] { "kind", "element", "name", "assignment",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Association(), source,
				new String[] { "kind", "element", "name", "association", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#artifact" });
		addAnnotation(getDocumentRoot_Auditing(), source, new String[] { "kind", "element", "name", "auditing",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_BaseElement(), source, new String[] { "kind", "element", "name", "baseElement",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_BaseElementWithMixedContent(), source, new String[] { "kind", "element", "name",
				"baseElementWithMixedContent", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_BoundaryEvent(), source,
				new String[] { "kind", "element", "name", "boundaryEvent", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_BusinessRuleTask(), source,
				new String[] { "kind", "element", "name", "businessRuleTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_CallableElement(), source, new String[] { "kind", "element", "name",
				"callableElement", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_CallActivity(), source,
				new String[] { "kind", "element", "name", "callActivity", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_CallChoreography(), source,
				new String[] { "kind", "element", "name", "callChoreography", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_CallConversation(), source,
				new String[] { "kind", "element", "name", "callConversation", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#conversationNode" });
		addAnnotation(getDocumentRoot_ConversationNode(), source, new String[] { "kind", "element", "name",
				"conversationNode", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_CancelEventDefinition(), source,
				new String[] { "kind", "element", "name", "cancelEventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getDocumentRoot_EventDefinition(), source,
				new String[] { "kind", "element", "name", "eventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_RootElement(), source, new String[] { "kind", "element", "name", "rootElement",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_CatchEvent(), source, new String[] { "kind", "element", "name", "catchEvent",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Category(), source,
				new String[] { "kind", "element", "name", "category", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_CategoryValue(), source, new String[] { "kind", "element", "name",
				"categoryValue", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Choreography(), source,
				new String[] { "kind", "element", "name", "choreography", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#collaboration" });
		addAnnotation(getDocumentRoot_Collaboration(), source,
				new String[] { "kind", "element", "name", "collaboration", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_ChoreographyActivity(), source, new String[] { "kind", "element", "name",
				"choreographyActivity", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ChoreographyTask(), source,
				new String[] { "kind", "element", "name", "choreographyTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_CompensateEventDefinition(), source,
				new String[] { "kind", "element", "name", "compensateEventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getDocumentRoot_ComplexBehaviorDefinition(), source, new String[] { "kind", "element", "name",
				"complexBehaviorDefinition", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ComplexGateway(), source,
				new String[] { "kind", "element", "name", "complexGateway", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_ConditionalEventDefinition(), source,
				new String[] { "kind", "element", "name", "conditionalEventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getDocumentRoot_Conversation(), source,
				new String[] { "kind", "element", "name", "conversation", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#conversationNode" });
		addAnnotation(getDocumentRoot_ConversationAssociation(), source, new String[] { "kind", "element", "name",
				"conversationAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ConversationLink(), source, new String[] { "kind", "element", "name",
				"conversationLink", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_CorrelationKey(), source, new String[] { "kind", "element", "name",
				"correlationKey", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_CorrelationProperty(), source,
				new String[] { "kind", "element", "name", "correlationProperty", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_CorrelationPropertyBinding(), source, new String[] { "kind", "element", "name",
				"correlationPropertyBinding", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_CorrelationPropertyRetrievalExpression(), source,
				new String[] { "kind", "element", "name", "correlationPropertyRetrievalExpression", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_CorrelationSubscription(), source, new String[] { "kind", "element", "name",
				"correlationSubscription", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_DataAssociation(), source, new String[] { "kind", "element", "name",
				"dataAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_DataInput(), source, new String[] { "kind", "element", "name", "dataInput",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_DataInputAssociation(), source, new String[] { "kind", "element", "name",
				"dataInputAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_DataObject(), source,
				new String[] { "kind", "element", "name", "dataObject", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_DataObjectReference(), source,
				new String[] { "kind", "element", "name", "dataObjectReference", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_DataOutput(), source, new String[] { "kind", "element", "name", "dataOutput",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_DataOutputAssociation(), source, new String[] { "kind", "element", "name",
				"dataOutputAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_DataState(), source, new String[] { "kind", "element", "name", "dataState",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_DataStore(), source,
				new String[] { "kind", "element", "name", "dataStore", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_DataStoreReference(), source,
				new String[] { "kind", "element", "name", "dataStoreReference", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_Definitions(), source, new String[] { "kind", "element", "name", "definitions",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Documentation(), source, new String[] { "kind", "element", "name",
				"documentation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_EndEvent(), source,
				new String[] { "kind", "element", "name", "endEvent", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_EndPoint(), source,
				new String[] { "kind", "element", "name", "endPoint", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_Error(), source,
				new String[] { "kind", "element", "name", "error", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_ErrorEventDefinition(), source,
				new String[] { "kind", "element", "name", "errorEventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getDocumentRoot_Escalation(), source,
				new String[] { "kind", "element", "name", "escalation", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_EscalationEventDefinition(), source,
				new String[] { "kind", "element", "name", "escalationEventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getDocumentRoot_Event(), source,
				new String[] { "kind", "element", "name", "event", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_EventBasedGateway(), source,
				new String[] { "kind", "element", "name", "eventBasedGateway", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_ExclusiveGateway(), source,
				new String[] { "kind", "element", "name", "exclusiveGateway", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_Expression(), source, new String[] { "kind", "element", "name", "expression",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Extension(), source, new String[] { "kind", "element", "name", "extension",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ExtensionElements(), source, new String[] { "kind", "element", "name",
				"extensionElements", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_FlowNode(), source, new String[] { "kind", "element", "name", "flowNode",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_FormalExpression(), source,
				new String[] { "kind", "element", "name", "formalExpression", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#expression" });
		addAnnotation(getDocumentRoot_Gateway(), source, new String[] { "kind", "element", "name", "gateway",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_GlobalBusinessRuleTask(), source,
				new String[] { "kind", "element", "name", "globalBusinessRuleTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_GlobalChoreographyTask(), source,
				new String[] { "kind", "element", "name", "globalChoreographyTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#choreography" });
		addAnnotation(getDocumentRoot_GlobalConversation(), source,
				new String[] { "kind", "element", "name", "globalConversation", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#collaboration" });
		addAnnotation(getDocumentRoot_GlobalManualTask(), source,
				new String[] { "kind", "element", "name", "globalManualTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_GlobalScriptTask(), source,
				new String[] { "kind", "element", "name", "globalScriptTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_GlobalTask(), source,
				new String[] { "kind", "element", "name", "globalTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_GlobalUserTask(), source,
				new String[] { "kind", "element", "name", "globalUserTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_Group(), source,
				new String[] { "kind", "element", "name", "group", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#artifact" });
		addAnnotation(getDocumentRoot_HumanPerformer(), source,
				new String[] { "kind", "element", "name", "humanPerformer", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#performer" });
		addAnnotation(getDocumentRoot_Performer(), source,
				new String[] { "kind", "element", "name", "performer", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#resourceRole" });
		addAnnotation(getDocumentRoot_ResourceRole(), source, new String[] { "kind", "element", "name", "resourceRole",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ImplicitThrowEvent(), source,
				new String[] { "kind", "element", "name", "implicitThrowEvent", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_Import(), source, new String[] { "kind", "element", "name", "import", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_InclusiveGateway(), source,
				new String[] { "kind", "element", "name", "inclusiveGateway", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_InputSet(), source, new String[] { "kind", "element", "name", "inputSet",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Interface(), source,
				new String[] { "kind", "element", "name", "interface", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_IntermediateCatchEvent(), source,
				new String[] { "kind", "element", "name", "intermediateCatchEvent", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_IntermediateThrowEvent(), source,
				new String[] { "kind", "element", "name", "intermediateThrowEvent", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_IoBinding(), source, new String[] { "kind", "element", "name", "ioBinding",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_IoSpecification(), source, new String[] { "kind", "element", "name",
				"ioSpecification", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ItemDefinition(), source,
				new String[] { "kind", "element", "name", "itemDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_Lane(), source, new String[] { "kind", "element", "name", "lane", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_LaneSet(), source, new String[] { "kind", "element", "name", "laneSet",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_LinkEventDefinition(), source,
				new String[] { "kind", "element", "name", "linkEventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getDocumentRoot_LoopCharacteristics(), source, new String[] { "kind", "element", "name",
				"loopCharacteristics", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ManualTask(), source,
				new String[] { "kind", "element", "name", "manualTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_Message(), source,
				new String[] { "kind", "element", "name", "message", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_MessageEventDefinition(), source,
				new String[] { "kind", "element", "name", "messageEventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getDocumentRoot_MessageFlow(), source, new String[] { "kind", "element", "name", "messageFlow",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_MessageFlowAssociation(), source, new String[] { "kind", "element", "name",
				"messageFlowAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Monitoring(), source, new String[] { "kind", "element", "name", "monitoring",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_MultiInstanceLoopCharacteristics(), source,
				new String[] { "kind", "element", "name", "multiInstanceLoopCharacteristics", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#loopCharacteristics" });
		addAnnotation(getDocumentRoot_Operation(), source, new String[] { "kind", "element", "name", "operation",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_OutputSet(), source, new String[] { "kind", "element", "name", "outputSet",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ParallelGateway(), source,
				new String[] { "kind", "element", "name", "parallelGateway", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_Participant(), source, new String[] { "kind", "element", "name", "participant",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ParticipantAssociation(), source, new String[] { "kind", "element", "name",
				"participantAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ParticipantMultiplicity(), source, new String[] { "kind", "element", "name",
				"participantMultiplicity", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_PartnerEntity(), source,
				new String[] { "kind", "element", "name", "partnerEntity", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_PartnerRole(), source,
				new String[] { "kind", "element", "name", "partnerRole", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_PotentialOwner(), source,
				new String[] { "kind", "element", "name", "potentialOwner", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#performer" });
		addAnnotation(getDocumentRoot_Process(), source,
				new String[] { "kind", "element", "name", "process", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_Property(), source, new String[] { "kind", "element", "name", "property",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ReceiveTask(), source,
				new String[] { "kind", "element", "name", "receiveTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_Relationship(), source, new String[] { "kind", "element", "name", "relationship",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Rendering(), source, new String[] { "kind", "element", "name", "rendering",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Resource(), source,
				new String[] { "kind", "element", "name", "resource", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_ResourceAssignmentExpression(), source, new String[] { "kind", "element", "name",
				"resourceAssignmentExpression", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ResourceParameter(), source, new String[] { "kind", "element", "name",
				"resourceParameter", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ResourceParameterBinding(), source, new String[] { "kind", "element", "name",
				"resourceParameterBinding", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_Script(), source, new String[] { "kind", "element", "name", "script", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_ScriptTask(), source,
				new String[] { "kind", "element", "name", "scriptTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_SendTask(), source,
				new String[] { "kind", "element", "name", "sendTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_SequenceFlow(), source,
				new String[] { "kind", "element", "name", "sequenceFlow", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_ServiceTask(), source,
				new String[] { "kind", "element", "name", "serviceTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_Signal(), source,
				new String[] { "kind", "element", "name", "signal", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDocumentRoot_SignalEventDefinition(), source,
				new String[] { "kind", "element", "name", "signalEventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getDocumentRoot_StandardLoopCharacteristics(), source,
				new String[] { "kind", "element", "name", "standardLoopCharacteristics", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#loopCharacteristics" });
		addAnnotation(getDocumentRoot_StartEvent(), source,
				new String[] { "kind", "element", "name", "startEvent", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_SubChoreography(), source,
				new String[] { "kind", "element", "name", "subChoreography", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_SubConversation(), source,
				new String[] { "kind", "element", "name", "subConversation", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#conversationNode" });
		addAnnotation(getDocumentRoot_SubProcess(), source,
				new String[] { "kind", "element", "name", "subProcess", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_Task(), source,
				new String[] { "kind", "element", "name", "task", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_TerminateEventDefinition(), source,
				new String[] { "kind", "element", "name", "terminateEventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getDocumentRoot_Text(), source, new String[] { "kind", "element", "name", "text", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_TextAnnotation(), source,
				new String[] { "kind", "element", "name", "textAnnotation", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_ThrowEvent(), source, new String[] { "kind", "element", "name", "throwEvent",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDocumentRoot_TimerEventDefinition(), source,
				new String[] { "kind", "element", "name", "timerEventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getDocumentRoot_Transaction(), source,
				new String[] { "kind", "element", "name", "transaction", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_UserTask(), source,
				new String[] { "kind", "element", "name", "userTask", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(getDocumentRoot_EventSubProcess(), source,
				new String[] { "kind", "element", "name", "subProcess", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "affiliation",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(activityEClass, source,
				new String[] { "name", "tActivity", "kind", "elementOnly", "abstract", "true" });
		addAnnotation(getActivity_IoSpecification(), source, new String[] { "kind", "element", "name",
				"ioSpecification", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getActivity_Properties(), source, new String[] { "kind", "element", "name", "property",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getActivity_DataInputAssociations(), source, new String[] { "kind", "element", "name",
				"dataInputAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getActivity_DataOutputAssociations(), source, new String[] { "kind", "element", "name",
				"dataOutputAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getActivity_Resources(), source,
				new String[] { "kind", "element", "name", "resourceRole", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#resourceRole" });
		addAnnotation(getActivity_LoopCharacteristics(), source,
				new String[] { "kind", "element", "name", "loopCharacteristics", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#loopCharacteristics" });
		addAnnotation(getActivity_CompletionQuantity(), source,
				new String[] { "kind", "attribute", "name", "completionQuantity" });
		addAnnotation(getActivity_Default(), source, new String[] { "kind", "attribute", "name", "default" });
		addAnnotation(getActivity_IsForCompensation(), source,
				new String[] { "kind", "attribute", "name", "isForCompensation" });
		addAnnotation(getActivity_StartQuantity(), source,
				new String[] { "kind", "attribute", "name", "startQuantity" });
		addAnnotation(adHocOrderingEEnum, source, new String[] { "name", "tAdHocOrdering" });
		addAnnotation(adHocSubProcessEClass, source,
				new String[] { "name", "tAdHocSubProcess", "kind", "elementOnly" });
		addAnnotation(getAdHocSubProcess_CompletionCondition(), source, new String[] { "kind", "element", "name",
				"completionCondition", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getAdHocSubProcess_CancelRemainingInstances(), source,
				new String[] { "kind", "attribute", "name", "cancelRemainingInstances" });
		addAnnotation(getAdHocSubProcess_Ordering(), source, new String[] { "kind", "attribute", "name", "ordering" });
		addAnnotation(artifactEClass, source,
				new String[] { "name", "tArtifact", "kind", "elementOnly", "abstract", "true" });
		addAnnotation(assignmentEClass, source, new String[] { "name", "tAssignment", "kind", "elementOnly" });
		addAnnotation(getAssignment_From(), source, new String[] { "kind", "element", "name", "from", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getAssignment_To(), source, new String[] { "kind", "element", "name", "to", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(associationEClass, source, new String[] { "name", "tAssociation", "kind", "elementOnly" });
		addAnnotation(getAssociation_AssociationDirection(), source,
				new String[] { "kind", "attribute", "name", "associationDirection" });
		addAnnotation(getAssociation_SourceRef(), source, new String[] { "kind", "attribute", "name", "sourceRef" });
		addAnnotation(getAssociation_TargetRef(), source, new String[] { "kind", "attribute", "name", "targetRef" });
		addAnnotation(associationDirectionEEnum, source, new String[] { "name", "tAssociationDirection" });
		addAnnotation(auditingEClass, source, new String[] { "name", "tAuditing", "kind", "elementOnly" });
		addAnnotation(baseElementEClass, source,
				new String[] { "name", "tBaseElement", "kind", "elementOnly", "abstract", "true" });
		addAnnotation(getBaseElement_Documentation(), source, new String[] { "kind", "element", "name", "documentation",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getBaseElement_ExtensionValues(), source, new String[] { "kind", "element", "name",
				"extensionElements", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getBaseElement_Id(), source, new String[] { "kind", "attribute", "name", "id" });
		addAnnotation(getBaseElement_AnyAttribute(), source, new String[] { "kind", "attributeWildcard", "wildcards",
				"##other", "name", ":3", "processing", "lax" });
		addAnnotation(boundaryEventEClass, source, new String[] { "name", "tBoundaryEvent", "kind", "elementOnly" });
		addAnnotation(getBoundaryEvent_AttachedToRef(), source,
				new String[] { "kind", "attribute", "name", "attachedToRef" });
		addAnnotation(getBoundaryEvent_CancelActivity(), source,
				new String[] { "kind", "attribute", "name", "cancelActivity" });
		addAnnotation(businessRuleTaskEClass, source,
				new String[] { "name", "tBusinessRuleTask", "kind", "elementOnly" });
		addAnnotation(getBusinessRuleTask_Implementation(), source,
				new String[] { "kind", "attribute", "name", "implementation" });
		addAnnotation(callActivityEClass, source, new String[] { "name", "tCallActivity", "kind", "elementOnly" });
		addAnnotation(callChoreographyEClass, source,
				new String[] { "name", "tCallChoreography", "kind", "elementOnly" });
		addAnnotation(getCallChoreography_ParticipantAssociations(), source, new String[] { "kind", "element", "name",
				"participantAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCallChoreography_CalledChoreographyRef(), source,
				new String[] { "kind", "attribute", "name", "calledChoreographyRef" });
		addAnnotation(callConversationEClass, source,
				new String[] { "name", "tCallConversation", "kind", "elementOnly" });
		addAnnotation(getCallConversation_ParticipantAssociations(), source, new String[] { "kind", "element", "name",
				"participantAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCallConversation_CalledCollaborationRef(), source,
				new String[] { "kind", "attribute", "name", "calledCollaborationRef" });
		addAnnotation(callableElementEClass, source,
				new String[] { "name", "tCallableElement", "kind", "elementOnly", "abstract", "true" });
		addAnnotation(getCallableElement_SupportedInterfaceRefs(), source, new String[] { "kind", "element", "name",
				"supportedInterfaceRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCallableElement_IoSpecification(), source, new String[] { "kind", "element", "name",
				"ioSpecification", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCallableElement_IoBinding(), source, new String[] { "kind", "element", "name", "ioBinding",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCallableElement_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(cancelEventDefinitionEClass, source,
				new String[] { "name", "tCancelEventDefinition", "kind", "elementOnly" });
		addAnnotation(catchEventEClass, source, new String[] { "name", "tCatchEvent", "kind", "elementOnly" });
		addAnnotation(getCatchEvent_DataOutputs(), source, new String[] { "kind", "element", "name", "dataOutput",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCatchEvent_DataOutputAssociation(), source, new String[] { "kind", "element", "name",
				"dataOutputAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCatchEvent_OutputSet(), source, new String[] { "kind", "element", "name", "outputSet",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCatchEvent_EventDefinitions(), source,
				new String[] { "kind", "element", "name", "eventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getCatchEvent_EventDefinitionRefs(), source, new String[] { "kind", "element", "name",
				"eventDefinitionRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCatchEvent_ParallelMultiple(), source,
				new String[] { "kind", "attribute", "name", "parallelMultiple" });
		addAnnotation(categoryEClass, source, new String[] { "name", "tCategory", "kind", "elementOnly" });
		addAnnotation(getCategory_CategoryValue(), source, new String[] { "kind", "element", "name", "categoryValue",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCategory_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(categoryValueEClass, source, new String[] { "name", "tCategoryValue", "kind", "elementOnly" });
		addAnnotation(getCategoryValue_Value(), source, new String[] { "kind", "attribute", "name", "value" });
		addAnnotation(choreographyEClass, source, new String[] { "name", "tChoreography", "kind", "elementOnly" });
		addAnnotation(choreographyActivityEClass, source,
				new String[] { "name", "tChoreographyActivity", "kind", "elementOnly" });
		addAnnotation(getChoreographyActivity_ParticipantRefs(), source, new String[] { "kind", "element", "name",
				"participantRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getChoreographyActivity_CorrelationKeys(), source, new String[] { "kind", "element", "name",
				"correlationKey", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getChoreographyActivity_InitiatingParticipantRef(), source,
				new String[] { "kind", "attribute", "name", "initiatingParticipantRef" });
		addAnnotation(getChoreographyActivity_LoopType(), source,
				new String[] { "kind", "attribute", "name", "loopType" });
		addAnnotation(choreographyLoopTypeEEnum, source, new String[] { "name", "tChoreographyLoopType" });
		addAnnotation(choreographyTaskEClass, source,
				new String[] { "name", "tChoreographyTask", "kind", "elementOnly" });
		addAnnotation(getChoreographyTask_MessageFlowRef(), source, new String[] { "kind", "element", "name",
				"messageFlowRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(collaborationEClass, source, new String[] { "name", "tCollaboration", "kind", "elementOnly" });
		addAnnotation(getCollaboration_Participants(), source, new String[] { "kind", "element", "name", "participant",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCollaboration_MessageFlows(), source, new String[] { "kind", "element", "name", "messageFlow",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCollaboration_Artifacts(), source,
				new String[] { "kind", "element", "name", "artifact", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#artifact" });
		addAnnotation(getCollaboration_Conversations(), source,
				new String[] { "kind", "element", "name", "conversationNode", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#conversationNode" });
		addAnnotation(getCollaboration_ConversationAssociations(), source, new String[] { "kind", "element", "name",
				"conversationAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCollaboration_ParticipantAssociations(), source, new String[] { "kind", "element", "name",
				"participantAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCollaboration_MessageFlowAssociations(), source, new String[] { "kind", "element", "name",
				"messageFlowAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCollaboration_CorrelationKeys(), source, new String[] { "kind", "element", "name",
				"correlationKey", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCollaboration_ChoreographyRef(), source, new String[] { "kind", "element", "name",
				"choreographyRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCollaboration_ConversationLinks(), source, new String[] { "kind", "element", "name",
				"conversationLink", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCollaboration_IsClosed(), source, new String[] { "kind", "attribute", "name", "isClosed" });
		addAnnotation(getCollaboration_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(compensateEventDefinitionEClass, source,
				new String[] { "name", "tCompensateEventDefinition", "kind", "elementOnly" });
		addAnnotation(getCompensateEventDefinition_ActivityRef(), source,
				new String[] { "kind", "attribute", "name", "activityRef" });
		addAnnotation(getCompensateEventDefinition_WaitForCompletion(), source,
				new String[] { "kind", "attribute", "name", "waitForCompletion" });
		addAnnotation(complexBehaviorDefinitionEClass, source,
				new String[] { "name", "tComplexBehaviorDefinition", "kind", "elementOnly" });
		addAnnotation(getComplexBehaviorDefinition_Condition(), source, new String[] { "kind", "element", "name",
				"condition", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getComplexBehaviorDefinition_Event(), source, new String[] { "kind", "element", "name", "event",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(complexGatewayEClass, source, new String[] { "name", "tComplexGateway", "kind", "elementOnly" });
		addAnnotation(getComplexGateway_ActivationCondition(), source, new String[] { "kind", "element", "name",
				"activationCondition", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getComplexGateway_Default(), source, new String[] { "kind", "attribute", "name", "default" });
		addAnnotation(conditionalEventDefinitionEClass, source,
				new String[] { "name", "tConditionalEventDefinition", "kind", "elementOnly" });
		addAnnotation(getConditionalEventDefinition_Condition(), source, new String[] { "kind", "element", "name",
				"condition", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(conversationEClass, source, new String[] { "name", "tConversation", "kind", "elementOnly" });
		addAnnotation(conversationAssociationEClass, source,
				new String[] { "name", "tConversationAssociation", "kind", "elementOnly" });
		addAnnotation(getConversationAssociation_InnerConversationNodeRef(), source,
				new String[] { "kind", "attribute", "name", "innerConversationNodeRef" });
		addAnnotation(getConversationAssociation_OuterConversationNodeRef(), source,
				new String[] { "kind", "attribute", "name", "outerConversationNodeRef" });
		addAnnotation(conversationLinkEClass, source,
				new String[] { "name", "tConversationLink", "kind", "elementOnly" });
		addAnnotation(getConversationLink_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getConversationLink_SourceRef(), source,
				new String[] { "kind", "attribute", "name", "sourceRef" });
		addAnnotation(getConversationLink_TargetRef(), source,
				new String[] { "kind", "attribute", "name", "targetRef" });
		addAnnotation(conversationNodeEClass, source,
				new String[] { "name", "tConversationNode", "kind", "elementOnly", "abstract", "true" });
		addAnnotation(getConversationNode_ParticipantRefs(), source, new String[] { "kind", "element", "name",
				"participantRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getConversationNode_MessageFlowRefs(), source, new String[] { "kind", "element", "name",
				"messageFlowRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getConversationNode_CorrelationKeys(), source, new String[] { "kind", "element", "name",
				"correlationKey", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getConversationNode_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(correlationKeyEClass, source, new String[] { "name", "tCorrelationKey", "kind", "elementOnly" });
		addAnnotation(getCorrelationKey_CorrelationPropertyRef(), source, new String[] { "kind", "element", "name",
				"correlationPropertyRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCorrelationKey_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(correlationPropertyEClass, source,
				new String[] { "name", "tCorrelationProperty", "kind", "elementOnly" });
		addAnnotation(getCorrelationProperty_CorrelationPropertyRetrievalExpression(), source,
				new String[] { "kind", "element", "name", "correlationPropertyRetrievalExpression", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCorrelationProperty_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getCorrelationProperty_Type(), source, new String[] { "kind", "attribute", "name", "type" });
		addAnnotation(correlationPropertyBindingEClass, source,
				new String[] { "name", "tCorrelationPropertyBinding", "kind", "elementOnly" });
		addAnnotation(getCorrelationPropertyBinding_DataPath(), source, new String[] { "kind", "element", "name",
				"dataPath", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCorrelationPropertyBinding_CorrelationPropertyRef(), source,
				new String[] { "kind", "attribute", "name", "correlationPropertyRef" });
		addAnnotation(correlationPropertyRetrievalExpressionEClass, source,
				new String[] { "name", "tCorrelationPropertyRetrievalExpression", "kind", "elementOnly" });
		addAnnotation(getCorrelationPropertyRetrievalExpression_MessagePath(), source, new String[] { "kind", "element",
				"name", "messagePath", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCorrelationPropertyRetrievalExpression_MessageRef(), source,
				new String[] { "kind", "attribute", "name", "messageRef" });
		addAnnotation(correlationSubscriptionEClass, source,
				new String[] { "name", "tCorrelationSubscription", "kind", "elementOnly" });
		addAnnotation(getCorrelationSubscription_CorrelationPropertyBinding(), source, new String[] { "kind", "element",
				"name", "correlationPropertyBinding", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getCorrelationSubscription_CorrelationKeyRef(), source,
				new String[] { "kind", "attribute", "name", "correlationKeyRef" });
		addAnnotation(dataAssociationEClass, source,
				new String[] { "name", "tDataAssociation", "kind", "elementOnly" });
		addAnnotation(getDataAssociation_SourceRef(), source, new String[] { "kind", "element", "name", "sourceRef",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDataAssociation_TargetRef(), source, new String[] { "kind", "element", "name", "targetRef",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDataAssociation_Transformation(), source, new String[] { "kind", "element", "name",
				"transformation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDataAssociation_Assignment(), source, new String[] { "kind", "element", "name", "assignment",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(dataInputEClass, source, new String[] { "name", "tDataInput", "kind", "elementOnly" });
		addAnnotation(getDataInput_IsCollection(), source,
				new String[] { "kind", "attribute", "name", "isCollection" });
		addAnnotation(getDataInput_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(dataInputAssociationEClass, source,
				new String[] { "name", "tDataInputAssociation", "kind", "elementOnly" });
		addAnnotation(dataObjectEClass, source, new String[] { "name", "tDataObject", "kind", "elementOnly" });
		addAnnotation(getDataObject_IsCollection(), source,
				new String[] { "kind", "attribute", "name", "isCollection" });
		addAnnotation(dataObjectReferenceEClass, source,
				new String[] { "name", "tDataObjectReference", "kind", "elementOnly" });
		addAnnotation(getDataObjectReference_DataObjectRef(), source,
				new String[] { "kind", "attribute", "name", "dataObjectRef" });
		addAnnotation(dataOutputEClass, source, new String[] { "name", "tDataOutput", "kind", "elementOnly" });
		addAnnotation(getDataOutput_IsCollection(), source,
				new String[] { "kind", "attribute", "name", "isCollection" });
		addAnnotation(getDataOutput_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(dataOutputAssociationEClass, source,
				new String[] { "name", "tDataOutputAssociation", "kind", "elementOnly" });
		addAnnotation(dataStateEClass, source, new String[] { "name", "tDataState", "kind", "elementOnly" });
		addAnnotation(getDataState_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(dataStoreEClass, source, new String[] { "name", "tDataStore", "kind", "elementOnly" });
		addAnnotation(getDataStore_Capacity(), source, new String[] { "kind", "attribute", "name", "capacity" });
		addAnnotation(getDataStore_IsUnlimited(), source, new String[] { "kind", "attribute", "name", "isUnlimited" });
		addAnnotation(getDataStore_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(dataStoreReferenceEClass, source,
				new String[] { "name", "tDataStoreReference", "kind", "elementOnly" });
		addAnnotation(getDataStoreReference_DataStoreRef(), source,
				new String[] { "kind", "attribute", "name", "dataStoreRef" });
		addAnnotation(definitionsEClass, source, new String[] { "name", "tDefinitions", "kind", "elementOnly" });
		addAnnotation(getDefinitions_Imports(), source, new String[] { "kind", "element", "name", "import", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDefinitions_Extensions(), source, new String[] { "kind", "element", "name", "extension",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDefinitions_RootElements(), source,
				new String[] { "kind", "element", "name", "rootElement", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#rootElement" });
		addAnnotation(getDefinitions_Diagrams(), source, new String[] { "kind", "element", "name", "BPMNDiagram",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/DI" });
		addAnnotation(getDefinitions_Relationships(), source, new String[] { "kind", "element", "name", "relationship",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getDefinitions_Exporter(), source, new String[] { "kind", "attribute", "name", "exporter" });
		addAnnotation(getDefinitions_ExporterVersion(), source,
				new String[] { "kind", "attribute", "name", "exporterVersion" });
		addAnnotation(getDefinitions_ExpressionLanguage(), source,
				new String[] { "kind", "attribute", "name", "expressionLanguage" });
		addAnnotation(getDefinitions_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getDefinitions_TargetNamespace(), source,
				new String[] { "kind", "attribute", "name", "targetNamespace" });
		addAnnotation(getDefinitions_TypeLanguage(), source,
				new String[] { "kind", "attribute", "name", "typeLanguage" });
		addAnnotation(documentationEClass, source, new String[] { "name", "tDocumentation", "kind", "mixed" });
		addAnnotation(getDocumentation_Mixed(), source, new String[] { "kind", "elementWildcard", "name", ":mixed" });
		addAnnotation(getDocumentation_TextFormat(), source,
				new String[] { "kind", "attribute", "name", "textFormat" });
		addAnnotation(endEventEClass, source, new String[] { "name", "tEndEvent", "kind", "elementOnly" });
		addAnnotation(endPointEClass, source, new String[] { "name", "tEndPoint", "kind", "elementOnly" });
		addAnnotation(errorEClass, source, new String[] { "name", "tError", "kind", "elementOnly" });
		addAnnotation(getError_ErrorCode(), source, new String[] { "kind", "attribute", "name", "errorCode" });
		addAnnotation(getError_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getError_StructureRef(), source, new String[] { "kind", "attribute", "name", "structureRef" });
		addAnnotation(errorEventDefinitionEClass, source,
				new String[] { "name", "tErrorEventDefinition", "kind", "elementOnly" });
		addAnnotation(getErrorEventDefinition_ErrorRef(), source,
				new String[] { "kind", "attribute", "name", "errorRef" });
		addAnnotation(escalationEClass, source, new String[] { "name", "tEscalation", "kind", "elementOnly" });
		addAnnotation(getEscalation_EscalationCode(), source,
				new String[] { "kind", "attribute", "name", "escalationCode" });
		addAnnotation(getEscalation_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getEscalation_StructureRef(), source,
				new String[] { "kind", "attribute", "name", "structureRef" });
		addAnnotation(escalationEventDefinitionEClass, source,
				new String[] { "name", "tEscalationEventDefinition", "kind", "elementOnly" });
		addAnnotation(getEscalationEventDefinition_EscalationRef(), source,
				new String[] { "kind", "attribute", "name", "escalationRef" });
		addAnnotation(eventEClass, source, new String[] { "name", "tEvent", "kind", "elementOnly" });
		addAnnotation(getEvent_Properties(), source, new String[] { "kind", "element", "name", "property", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(eventBasedGatewayEClass, source,
				new String[] { "name", "tEventBasedGateway", "kind", "elementOnly" });
		addAnnotation(getEventBasedGateway_EventGatewayType(), source,
				new String[] { "kind", "attribute", "name", "eventGatewayType" });
		addAnnotation(getEventBasedGateway_Instantiate(), source,
				new String[] { "kind", "attribute", "name", "instantiate" });
		addAnnotation(eventBasedGatewayTypeEEnum, source, new String[] { "name", "tEventBasedGatewayType" });
		addAnnotation(eventDefinitionEClass, source,
				new String[] { "name", "tEventDefinition", "kind", "elementOnly", "abstract", "true" });
		addAnnotation(exclusiveGatewayEClass, source,
				new String[] { "name", "tExclusiveGateway", "kind", "elementOnly" });
		addAnnotation(getExclusiveGateway_Default(), source, new String[] { "kind", "attribute", "name", "default" });
		addAnnotation(expressionEClass, source, new String[] { "name", "tExpression", "kind", "mixed" });
		addAnnotation(extensionEClass, source, new String[] { "name", "tExtension", "kind", "elementOnly" });
		addAnnotation(getExtension_MustUnderstand(), source,
				new String[] { "kind", "attribute", "name", "mustUnderstand" });
		addAnnotation(getExtension_XsdDefinition(), source, new String[] { "kind", "attribute", "name", "definition" });
		addAnnotation(extensionAttributeValueEClass, source,
				new String[] { "name", "tExtensionElements", "kind", "elementOnly" });
		addAnnotation(getExtensionAttributeValue_Value(), source,
				new String[] { "kind", "elementWildcard", "wildcards", "##other", "name", ":0", "processing", "lax" });
		addAnnotation(flowElementEClass, source, new String[] { "name", "tFlowElement", "kind", "elementOnly" });
		addAnnotation(getFlowElement_Auditing(), source, new String[] { "kind", "element", "name", "auditing",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getFlowElement_Monitoring(), source, new String[] { "kind", "element", "name", "monitoring",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getFlowElement_CategoryValueRef(), source, new String[] { "kind", "element", "name",
				"categoryValueRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getFlowElement_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getFlowElementsContainer_LaneSets(), source, new String[] { "kind", "element", "name", "laneSet",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getFlowElementsContainer_FlowElements(), source,
				new String[] { "kind", "element", "name", "flowElement", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#flowElement" });
		addAnnotation(flowNodeEClass, source, new String[] { "name", "tFlowNode", "kind", "elementOnly" });
		addAnnotation(getFlowNode_Incoming(), source, new String[] { "kind", "element", "name", "incoming", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getFlowNode_Outgoing(), source, new String[] { "kind", "element", "name", "outgoing", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(formalExpressionEClass, source, new String[] { "name", "tFormalExpression", "kind", "mixed" });
		addAnnotation(getFormalExpression_Mixed(), source,
				new String[] { "kind", "elementWildcard", "name", ":mixed" });
		addAnnotation(getFormalExpression_EvaluatesToTypeRef(), source,
				new String[] { "kind", "attribute", "name", "evaluatesToTypeRef" });
		addAnnotation(getFormalExpression_Language(), source, new String[] { "kind", "attribute", "name", "language" });
		addAnnotation(gatewayEClass, source, new String[] { "name", "tGateway", "kind", "elementOnly" });
		addAnnotation(getGateway_GatewayDirection(), source,
				new String[] { "kind", "attribute", "name", "gatewayDirection" });
		addAnnotation(gatewayDirectionEEnum, source, new String[] { "name", "tGatewayDirection" });
		addAnnotation(globalBusinessRuleTaskEClass, source,
				new String[] { "name", "tGlobalBusinessRuleTask", "kind", "elementOnly" });
		addAnnotation(getGlobalBusinessRuleTask_Implementation(), source,
				new String[] { "kind", "attribute", "name", "implementation" });
		addAnnotation(globalChoreographyTaskEClass, source,
				new String[] { "name", "tGlobalChoreographyTask", "kind", "elementOnly" });
		addAnnotation(getGlobalChoreographyTask_InitiatingParticipantRef(), source,
				new String[] { "kind", "attribute", "name", "initiatingParticipantRef" });
		addAnnotation(globalConversationEClass, source,
				new String[] { "name", "tGlobalConversation", "kind", "elementOnly" });
		addAnnotation(globalManualTaskEClass, source,
				new String[] { "name", "tGlobalManualTask", "kind", "elementOnly" });
		addAnnotation(globalScriptTaskEClass, source,
				new String[] { "name", "tGlobalScriptTask", "kind", "elementOnly" });
		addAnnotation(getGlobalScriptTask_Script(), source, new String[] { "kind", "element", "name", "script",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getGlobalScriptTask_ScriptLanguage(), source,
				new String[] { "kind", "attribute", "name", "scriptLanguage" });
		addAnnotation(globalTaskEClass, source, new String[] { "name", "tGlobalTask", "kind", "elementOnly" });
		addAnnotation(getGlobalTask_Resources(), source,
				new String[] { "kind", "element", "name", "resourceRole", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#resourceRole" });
		addAnnotation(globalUserTaskEClass, source, new String[] { "name", "tGlobalUserTask", "kind", "elementOnly" });
		addAnnotation(getGlobalUserTask_Renderings(), source, new String[] { "kind", "element", "name", "rendering",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getGlobalUserTask_Implementation(), source,
				new String[] { "kind", "attribute", "name", "implementation" });
		addAnnotation(groupEClass, source, new String[] { "name", "tGroup", "kind", "elementOnly" });
		addAnnotation(getGroup_CategoryValueRef(), source,
				new String[] { "kind", "attribute", "name", "categoryValueRef" });
		addAnnotation(humanPerformerEClass, source, new String[] { "name", "tHumanPerformer", "kind", "elementOnly" });
		addAnnotation(implicitThrowEventEClass, source,
				new String[] { "name", "tImplicitThrowEvent", "kind", "elementOnly" });
		addAnnotation(importEClass, source, new String[] { "name", "tImport", "kind", "empty" });
		addAnnotation(getImport_ImportType(), source, new String[] { "kind", "attribute", "name", "importType" });
		addAnnotation(getImport_Location(), source, new String[] { "kind", "attribute", "name", "location" });
		addAnnotation(getImport_Namespace(), source, new String[] { "kind", "attribute", "name", "namespace" });
		addAnnotation(inclusiveGatewayEClass, source,
				new String[] { "name", "tInclusiveGateway", "kind", "elementOnly" });
		addAnnotation(getInclusiveGateway_Default(), source, new String[] { "kind", "attribute", "name", "default" });
		addAnnotation(inputOutputBindingEClass, source,
				new String[] { "name", "tInputOutputBinding", "kind", "elementOnly" });
		addAnnotation(getInputOutputBinding_InputDataRef(), source,
				new String[] { "kind", "attribute", "name", "inputDataRef" });
		addAnnotation(getInputOutputBinding_OperationRef(), source,
				new String[] { "kind", "attribute", "name", "operationRef" });
		addAnnotation(getInputOutputBinding_OutputDataRef(), source,
				new String[] { "kind", "attribute", "name", "outputDataRef" });
		addAnnotation(inputOutputSpecificationEClass, source,
				new String[] { "name", "tInputOutputSpecification", "kind", "elementOnly" });
		addAnnotation(getInputOutputSpecification_DataInputs(), source, new String[] { "kind", "element", "name",
				"dataInput", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getInputOutputSpecification_DataOutputs(), source, new String[] { "kind", "element", "name",
				"dataOutput", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getInputOutputSpecification_InputSets(), source, new String[] { "kind", "element", "name",
				"inputSet", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getInputOutputSpecification_OutputSets(), source, new String[] { "kind", "element", "name",
				"outputSet", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(inputSetEClass, source, new String[] { "name", "tInputSet", "kind", "elementOnly" });
		addAnnotation(getInputSet_DataInputRefs(), source, new String[] { "kind", "element", "name", "dataInputRefs",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getInputSet_OptionalInputRefs(), source, new String[] { "kind", "element", "name",
				"optionalInputRefs", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getInputSet_WhileExecutingInputRefs(), source, new String[] { "kind", "element", "name",
				"whileExecutingInputRefs", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getInputSet_OutputSetRefs(), source, new String[] { "kind", "element", "name", "outputSetRefs",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getInputSet_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(interactionNodeEClass, source, new String[] { "abstract", "true" });
		addAnnotation(interfaceEClass, source, new String[] { "name", "tInterface", "kind", "elementOnly" });
		addAnnotation(getInterface_Operations(), source, new String[] { "kind", "element", "name", "operation",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getInterface_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getInterface_ImplementationRef(), source,
				new String[] { "kind", "attribute", "name", "implementationRef" });
		addAnnotation(intermediateCatchEventEClass, source,
				new String[] { "name", "tIntermediateCatchEvent", "kind", "elementOnly" });
		addAnnotation(intermediateThrowEventEClass, source,
				new String[] { "name", "tIntermediateThrowEvent", "kind", "elementOnly" });
		addAnnotation(getItemAwareElement_DataState(), source, new String[] { "kind", "element", "name", "dataState",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getItemAwareElement_ItemSubjectRef(), source,
				new String[] { "kind", "attribute", "name", "itemSubjectRef" });
		addAnnotation(itemDefinitionEClass, source, new String[] { "name", "tItemDefinition", "kind", "elementOnly" });
		addAnnotation(getItemDefinition_IsCollection(), source,
				new String[] { "kind", "attribute", "name", "isCollection" });
		addAnnotation(getItemDefinition_ItemKind(), source, new String[] { "kind", "attribute", "name", "itemKind" });
		addAnnotation(getItemDefinition_StructureRef(), source,
				new String[] { "kind", "attribute", "name", "structureRef" });
		addAnnotation(itemKindEEnum, source, new String[] { "name", "tItemKind" });
		addAnnotation(laneEClass, source, new String[] { "name", "tLane", "kind", "elementOnly" });
		addAnnotation(getLane_PartitionElement(), source, new String[] { "kind", "element", "name", "partitionElement",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getLane_FlowNodeRefs(), source, new String[] { "kind", "element", "name", "flowNodeRef",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getLane_ChildLaneSet(), source, new String[] { "kind", "element", "name", "childLaneSet",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getLane_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getLane_PartitionElementRef(), source,
				new String[] { "kind", "attribute", "name", "partitionElementRef" });
		addAnnotation(laneSetEClass, source, new String[] { "name", "tLaneSet", "kind", "elementOnly" });
		addAnnotation(getLaneSet_Lanes(), source, new String[] { "kind", "element", "name", "lane", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getLaneSet_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(linkEventDefinitionEClass, source,
				new String[] { "name", "tLinkEventDefinition", "kind", "elementOnly" });
		addAnnotation(getLinkEventDefinition_Source(), source, new String[] { "kind", "element", "name", "source",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getLinkEventDefinition_Target(), source, new String[] { "kind", "element", "name", "target",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getLinkEventDefinition_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(loopCharacteristicsEClass, source,
				new String[] { "name", "tLoopCharacteristics", "kind", "elementOnly" });
		addAnnotation(manualTaskEClass, source, new String[] { "name", "tManualTask", "kind", "elementOnly" });
		addAnnotation(messageEClass, source, new String[] { "name", "tMessage", "kind", "elementOnly" });
		addAnnotation(getMessage_ItemRef(), source, new String[] { "kind", "attribute", "name", "itemRef" });
		addAnnotation(getMessage_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(messageEventDefinitionEClass, source,
				new String[] { "name", "tMessageEventDefinition", "kind", "elementOnly" });
		addAnnotation(getMessageEventDefinition_OperationRef(), source, new String[] { "kind", "element", "name",
				"operationRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getMessageEventDefinition_MessageRef(), source,
				new String[] { "kind", "attribute", "name", "messageRef" });
		addAnnotation(messageFlowEClass, source, new String[] { "name", "tMessageFlow", "kind", "elementOnly" });
		addAnnotation(getMessageFlow_MessageRef(), source, new String[] { "kind", "attribute", "name", "messageRef" });
		addAnnotation(getMessageFlow_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getMessageFlow_SourceRef(), source, new String[] { "kind", "attribute", "name", "sourceRef" });
		addAnnotation(getMessageFlow_TargetRef(), source, new String[] { "kind", "attribute", "name", "targetRef" });
		addAnnotation(messageFlowAssociationEClass, source,
				new String[] { "name", "tMessageFlowAssociation", "kind", "elementOnly" });
		addAnnotation(getMessageFlowAssociation_InnerMessageFlowRef(), source,
				new String[] { "kind", "attribute", "name", "innerMessageFlowRef" });
		addAnnotation(getMessageFlowAssociation_OuterMessageFlowRef(), source,
				new String[] { "kind", "attribute", "name", "outerMessageFlowRef" });
		addAnnotation(monitoringEClass, source, new String[] { "name", "tMonitoring", "kind", "elementOnly" });
		addAnnotation(multiInstanceBehaviorEEnum, source, new String[] { "name", "tMultiInstanceFlowCondition" });
		addAnnotation(multiInstanceLoopCharacteristicsEClass, source,
				new String[] { "name", "tMultiInstanceLoopCharacteristics", "kind", "elementOnly" });
		addAnnotation(getMultiInstanceLoopCharacteristics_LoopCardinality(), source, new String[] { "kind", "element",
				"name", "loopCardinality", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getMultiInstanceLoopCharacteristics_LoopDataInputRef(), source, new String[] { "kind", "element",
				"name", "loopDataInputRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getMultiInstanceLoopCharacteristics_LoopDataOutputRef(), source, new String[] { "kind", "element",
				"name", "loopDataOutputRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getMultiInstanceLoopCharacteristics_InputDataItem(), source, new String[] { "kind", "element",
				"name", "inputDataItem", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getMultiInstanceLoopCharacteristics_OutputDataItem(), source, new String[] { "kind", "element",
				"name", "outputDataItem", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getMultiInstanceLoopCharacteristics_ComplexBehaviorDefinition(), source,
				new String[] { "kind", "element", "name", "complexBehaviorDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getMultiInstanceLoopCharacteristics_CompletionCondition(), source, new String[] { "kind",
				"element", "name", "completionCondition", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getMultiInstanceLoopCharacteristics_Behavior(), source,
				new String[] { "kind", "attribute", "name", "behavior" });
		addAnnotation(getMultiInstanceLoopCharacteristics_IsSequential(), source,
				new String[] { "kind", "attribute", "name", "isSequential" });
		addAnnotation(getMultiInstanceLoopCharacteristics_NoneBehaviorEventRef(), source,
				new String[] { "kind", "attribute", "name", "noneBehaviorEventRef" });
		addAnnotation(getMultiInstanceLoopCharacteristics_OneBehaviorEventRef(), source,
				new String[] { "kind", "attribute", "name", "oneBehaviorEventRef" });
		addAnnotation(operationEClass, source, new String[] { "name", "tOperation", "kind", "elementOnly" });
		addAnnotation(getOperation_InMessageRef(), source, new String[] { "kind", "element", "name", "inMessageRef",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getOperation_OutMessageRef(), source, new String[] { "kind", "element", "name", "outMessageRef",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getOperation_ErrorRefs(), source, new String[] { "kind", "element", "name", "errorRef",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getOperation_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getOperation_ImplementationRef(), source,
				new String[] { "kind", "attribute", "name", "implementationRef" });
		addAnnotation(outputSetEClass, source, new String[] { "name", "tOutputSet", "kind", "elementOnly" });
		addAnnotation(getOutputSet_DataOutputRefs(), source, new String[] { "kind", "element", "name", "dataOutputRefs",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getOutputSet_OptionalOutputRefs(), source, new String[] { "kind", "element", "name",
				"optionalOutputRefs", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getOutputSet_WhileExecutingOutputRefs(), source, new String[] { "kind", "element", "name",
				"whileExecutingOutputRefs", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getOutputSet_InputSetRefs(), source, new String[] { "kind", "element", "name", "inputSetRefs",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getOutputSet_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(parallelGatewayEClass, source,
				new String[] { "name", "tParallelGateway", "kind", "elementOnly" });
		addAnnotation(participantEClass, source, new String[] { "name", "tParticipant", "kind", "elementOnly" });
		addAnnotation(getParticipant_InterfaceRefs(), source, new String[] { "kind", "element", "name", "interfaceRef",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getParticipant_EndPointRefs(), source, new String[] { "kind", "element", "name", "endPointRef",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getParticipant_ParticipantMultiplicity(), source, new String[] { "kind", "element", "name",
				"participantMultiplicity", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getParticipant_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getParticipant_ProcessRef(), source, new String[] { "kind", "attribute", "name", "processRef" });
		addAnnotation(participantAssociationEClass, source,
				new String[] { "name", "tParticipantAssociation", "kind", "elementOnly" });
		addAnnotation(getParticipantAssociation_InnerParticipantRef(), source, new String[] { "kind", "element", "name",
				"innerParticipantRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getParticipantAssociation_OuterParticipantRef(), source, new String[] { "kind", "element", "name",
				"outerParticipantRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(participantMultiplicityEClass, source,
				new String[] { "name", "tParticipantMultiplicity", "kind", "elementOnly" });
		addAnnotation(getParticipantMultiplicity_Maximum(), source,
				new String[] { "kind", "attribute", "name", "maximum" });
		addAnnotation(getParticipantMultiplicity_Minimum(), source,
				new String[] { "kind", "attribute", "name", "minimum" });
		addAnnotation(partnerEntityEClass, source, new String[] { "name", "tPartnerEntity", "kind", "elementOnly" });
		addAnnotation(getPartnerEntity_ParticipantRef(), source, new String[] { "kind", "element", "name",
				"participantRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getPartnerEntity_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(partnerRoleEClass, source, new String[] { "name", "tPartnerRole", "kind", "elementOnly" });
		addAnnotation(getPartnerRole_ParticipantRef(), source, new String[] { "kind", "element", "name",
				"participantRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getPartnerRole_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(performerEClass, source, new String[] { "name", "tPerformer", "kind", "elementOnly" });
		addAnnotation(potentialOwnerEClass, source, new String[] { "name", "tPotentialOwner", "kind", "elementOnly" });
		addAnnotation(processEClass, source, new String[] { "name", "tProcess", "kind", "elementOnly" });
		addAnnotation(getProcess_Auditing(), source, new String[] { "kind", "element", "name", "auditing", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getProcess_Monitoring(), source, new String[] { "kind", "element", "name", "monitoring",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getProcess_Properties(), source, new String[] { "kind", "element", "name", "property",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getProcess_Artifacts(), source,
				new String[] { "kind", "element", "name", "artifact", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#artifact" });
		addAnnotation(getProcess_Resources(), source,
				new String[] { "kind", "element", "name", "resourceRole", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#resourceRole" });
		addAnnotation(getProcess_CorrelationSubscriptions(), source, new String[] { "kind", "element", "name",
				"correlationSubscription", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getProcess_Supports(), source, new String[] { "kind", "element", "name", "supports", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getProcess_DefinitionalCollaborationRef(), source,
				new String[] { "kind", "attribute", "name", "definitionalCollaborationRef" });
		addAnnotation(getProcess_IsClosed(), source, new String[] { "kind", "attribute", "name", "isClosed" });
		addAnnotation(getProcess_IsExecutable(), source, new String[] { "kind", "attribute", "name", "isExecutable" });
		addAnnotation(getProcess_ProcessType(), source, new String[] { "kind", "attribute", "name", "processType" });
		addAnnotation(processTypeEEnum, source, new String[] { "name", "tProcessType" });
		addAnnotation(propertyEClass, source, new String[] { "name", "tProperty", "kind", "elementOnly" });
		addAnnotation(getProperty_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(receiveTaskEClass, source, new String[] { "name", "tReceiveTask", "kind", "elementOnly" });
		addAnnotation(getReceiveTask_Implementation(), source,
				new String[] { "kind", "attribute", "name", "implementation" });
		addAnnotation(getReceiveTask_Instantiate(), source,
				new String[] { "kind", "attribute", "name", "instantiate" });
		addAnnotation(getReceiveTask_MessageRef(), source, new String[] { "kind", "attribute", "name", "messageRef" });
		addAnnotation(getReceiveTask_OperationRef(), source,
				new String[] { "kind", "attribute", "name", "operationRef" });
		addAnnotation(relationshipEClass, source, new String[] { "name", "tRelationship", "kind", "elementOnly" });
		addAnnotation(getRelationship_Sources(), source, new String[] { "kind", "element", "name", "source",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getRelationship_Targets(), source, new String[] { "kind", "element", "name", "target",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getRelationship_Direction(), source, new String[] { "kind", "attribute", "name", "direction" });
		addAnnotation(getRelationship_Type(), source, new String[] { "kind", "attribute", "name", "type" });
		addAnnotation(relationshipDirectionEEnum, source, new String[] { "name", "tRelationshipDirection" });
		addAnnotation(renderingEClass, source, new String[] { "name", "tRendering", "kind", "elementOnly" });
		addAnnotation(resourceEClass, source, new String[] { "name", "tResource", "kind", "elementOnly" });
		addAnnotation(getResource_ResourceParameters(), source, new String[] { "kind", "element", "name",
				"resourceParameter", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getResource_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(resourceAssignmentExpressionEClass, source,
				new String[] { "name", "tResourceAssignmentExpression", "kind", "elementOnly" });
		addAnnotation(getResourceAssignmentExpression_Expression(), source,
				new String[] { "kind", "element", "name", "expression", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#expression" });
		addAnnotation(resourceParameterEClass, source,
				new String[] { "name", "tResourceParameter", "kind", "elementOnly" });
		addAnnotation(getResourceParameter_IsRequired(), source,
				new String[] { "kind", "attribute", "name", "isRequired" });
		addAnnotation(getResourceParameter_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getResourceParameter_Type(), source, new String[] { "kind", "attribute", "name", "type" });
		addAnnotation(resourceParameterBindingEClass, source,
				new String[] { "name", "tResourceParameterBinding", "kind", "elementOnly" });
		addAnnotation(getResourceParameterBinding_Expression(), source,
				new String[] { "kind", "element", "name", "expression", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#expression" });
		addAnnotation(getResourceParameterBinding_ParameterRef(), source,
				new String[] { "kind", "attribute", "name", "parameterRef" });
		addAnnotation(resourceRoleEClass, source, new String[] { "name", "tResourceRole", "kind", "elementOnly" });
		addAnnotation(getResourceRole_ResourceRef(), source, new String[] { "kind", "element", "name", "resourceRef",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getResourceRole_ResourceParameterBindings(), source, new String[] { "kind", "element", "name",
				"resourceParameterBinding", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getResourceRole_ResourceAssignmentExpression(), source, new String[] { "kind", "element", "name",
				"resourceAssignmentExpression", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getResourceRole_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(rootElementEClass, source,
				new String[] { "name", "tRootElement", "kind", "elementOnly", "abstract", "true" });
		addAnnotation(scriptTaskEClass, source, new String[] { "name", "tScriptTask", "kind", "elementOnly" });
		addAnnotation(getScriptTask_Script(), source, new String[] { "kind", "element", "name", "script", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getScriptTask_ScriptFormat(), source,
				new String[] { "kind", "attribute", "name", "scriptFormat" });
		addAnnotation(sendTaskEClass, source, new String[] { "name", "tSendTask", "kind", "elementOnly" });
		addAnnotation(getSendTask_Implementation(), source,
				new String[] { "kind", "attribute", "name", "implementation" });
		addAnnotation(getSendTask_MessageRef(), source, new String[] { "kind", "attribute", "name", "messageRef" });
		addAnnotation(getSendTask_OperationRef(), source, new String[] { "kind", "attribute", "name", "operationRef" });
		addAnnotation(sequenceFlowEClass, source, new String[] { "name", "tSequenceFlow", "kind", "elementOnly" });
		addAnnotation(getSequenceFlow_ConditionExpression(), source, new String[] { "kind", "element", "name",
				"conditionExpression", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getSequenceFlow_IsImmediate(), source,
				new String[] { "kind", "attribute", "name", "isImmediate" });
		addAnnotation(getSequenceFlow_SourceRef(), source, new String[] { "kind", "attribute", "name", "sourceRef" });
		addAnnotation(getSequenceFlow_TargetRef(), source, new String[] { "kind", "attribute", "name", "targetRef" });
		addAnnotation(serviceTaskEClass, source, new String[] { "name", "tServiceTask", "kind", "elementOnly" });
		addAnnotation(getServiceTask_Implementation(), source,
				new String[] { "kind", "attribute", "name", "implementation" });
		addAnnotation(getServiceTask_OperationRef(), source,
				new String[] { "kind", "attribute", "name", "operationRef" });
		addAnnotation(signalEClass, source, new String[] { "name", "tSignal", "kind", "elementOnly" });
		addAnnotation(getSignal_Name(), source, new String[] { "kind", "attribute", "name", "name" });
		addAnnotation(getSignal_StructureRef(), source, new String[] { "kind", "attribute", "name", "structureRef" });
		addAnnotation(signalEventDefinitionEClass, source,
				new String[] { "name", "tSignalEventDefinition", "kind", "elementOnly" });
		addAnnotation(standardLoopCharacteristicsEClass, source,
				new String[] { "name", "tStandardLoopCharacteristics", "kind", "elementOnly" });
		addAnnotation(getStandardLoopCharacteristics_LoopCondition(), source, new String[] { "kind", "element", "name",
				"loopCondition", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getStandardLoopCharacteristics_LoopMaximum(), source,
				new String[] { "kind", "attribute", "name", "loopMaximum" });
		addAnnotation(getStandardLoopCharacteristics_TestBefore(), source,
				new String[] { "kind", "attribute", "name", "testBefore" });
		addAnnotation(startEventEClass, source, new String[] { "name", "tStartEvent", "kind", "elementOnly" });
		addAnnotation(getStartEvent_IsInterrupting(), source,
				new String[] { "kind", "attribute", "name", "isInterrupting" });
		addAnnotation(subChoreographyEClass, source,
				new String[] { "name", "tSubChoreography", "kind", "elementOnly" });
		addAnnotation(getSubChoreography_Artifacts(), source,
				new String[] { "kind", "element", "name", "artifact", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#artifact" });
		addAnnotation(subConversationEClass, source,
				new String[] { "name", "tSubConversation", "kind", "elementOnly" });
		addAnnotation(getSubConversation_ConversationNodes(), source,
				new String[] { "kind", "element", "name", "conversationNode", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#conversationNode" });
		addAnnotation(subProcessEClass, source, new String[] { "name", "tSubProcess", "kind", "elementOnly" });
		addAnnotation(getSubProcess_Artifacts(), source,
				new String[] { "kind", "element", "name", "artifact", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#artifact" });
		addAnnotation(getSubProcess_TriggeredByEvent(), source,
				new String[] { "kind", "attribute", "name", "triggeredByEvent" });
		addAnnotation(taskEClass, source, new String[] { "name", "tTask", "kind", "elementOnly" });
		addAnnotation(terminateEventDefinitionEClass, source,
				new String[] { "name", "tTerminateEventDefinition", "kind", "elementOnly" });
		addAnnotation(textAnnotationEClass, source, new String[] { "name", "tTextAnnotation", "kind", "elementOnly" });
		addAnnotation(getTextAnnotation_Text(), source, new String[] { "kind", "element", "name", "text", "namespace",
				"http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getTextAnnotation_TextFormat(), source,
				new String[] { "kind", "attribute", "name", "textFormat" });
		addAnnotation(throwEventEClass, source, new String[] { "name", "tThrowEvent", "kind", "elementOnly" });
		addAnnotation(getThrowEvent_DataInputs(), source, new String[] { "kind", "element", "name", "dataInput",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getThrowEvent_DataInputAssociation(), source, new String[] { "kind", "element", "name",
				"dataInputAssociation", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getThrowEvent_InputSet(), source, new String[] { "kind", "element", "name", "inputSet",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getThrowEvent_EventDefinitions(), source,
				new String[] { "kind", "element", "name", "eventDefinition", "namespace",
						"http://www.omg.org/spec/BPMN/20100524/MODEL", "group",
						"http://www.omg.org/spec/BPMN/20100524/MODEL#eventDefinition" });
		addAnnotation(getThrowEvent_EventDefinitionRefs(), source, new String[] { "kind", "element", "name",
				"eventDefinitionRef", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(timerEventDefinitionEClass, source,
				new String[] { "name", "tTimerEventDefinition", "kind", "elementOnly" });
		addAnnotation(getTimerEventDefinition_TimeDate(), source, new String[] { "kind", "element", "name", "timeDate",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getTimerEventDefinition_TimeDuration(), source, new String[] { "kind", "element", "name",
				"timeDuration", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getTimerEventDefinition_TimeCycle(), source, new String[] { "kind", "element", "name",
				"timeCycle", "namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(transactionEClass, source, new String[] { "name", "tTransaction", "kind", "elementOnly" });
		addAnnotation(getTransaction_Method(), source, new String[] { "kind", "attribute", "name", "method" });
		addAnnotation(userTaskEClass, source, new String[] { "name", "tUserTask", "kind", "elementOnly" });
		addAnnotation(getUserTask_Renderings(), source, new String[] { "kind", "element", "name", "rendering",
				"namespace", "http://www.omg.org/spec/BPMN/20100524/MODEL" });
		addAnnotation(getUserTask_Implementation(), source,
				new String[] { "kind", "attribute", "name", "implementation" });
		addAnnotation(eventSubprocessEClass, source, new String[] { "name", "tSubProcess", "kind", "elementOnly" });
	}

} //Bpmn2PackageImpl
