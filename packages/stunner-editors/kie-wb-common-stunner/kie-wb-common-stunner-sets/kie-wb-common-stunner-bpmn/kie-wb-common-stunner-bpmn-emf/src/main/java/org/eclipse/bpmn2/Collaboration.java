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
package org.eclipse.bpmn2;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Collaboration</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getParticipants <em>Participants</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getMessageFlows <em>Message Flows</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getArtifacts <em>Artifacts</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getConversations <em>Conversations</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getConversationAssociations <em>Conversation Associations</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getParticipantAssociations <em>Participant Associations</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getMessageFlowAssociations <em>Message Flow Associations</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getCorrelationKeys <em>Correlation Keys</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getChoreographyRef <em>Choreography Ref</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getConversationLinks <em>Conversation Links</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#isIsClosed <em>Is Closed</em>}</li>
 *   <li>{@link org.eclipse.bpmn2.Collaboration#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration()
 * @model extendedMetaData="name='tCollaboration' kind='elementOnly'"
 * @generated
 */
public interface Collaboration extends RootElement {
	/**
	 * Returns the value of the '<em><b>Participants</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.bpmn2.Participant}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Participants</em>' containment reference list.
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_Participants()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='participant' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL'"
	 * @generated
	 */
	EList<Participant> getParticipants();

	/**
	 * Returns the value of the '<em><b>Message Flows</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.bpmn2.MessageFlow}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Message Flows</em>' containment reference list.
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_MessageFlows()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='messageFlow' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL'"
	 * @generated
	 */
	EList<MessageFlow> getMessageFlows();

	/**
	 * Returns the value of the '<em><b>Artifacts</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.bpmn2.Artifact}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Artifacts</em>' containment reference list.
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_Artifacts()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='artifact' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL' group='http://www.omg.org/spec/BPMN/20100524/MODEL#artifact'"
	 * @generated
	 */
	EList<Artifact> getArtifacts();

	/**
	 * Returns the value of the '<em><b>Conversations</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.bpmn2.ConversationNode}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conversations</em>' containment reference list.
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_Conversations()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='conversationNode' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL' group='http://www.omg.org/spec/BPMN/20100524/MODEL#conversationNode'"
	 * @generated
	 */
	EList<ConversationNode> getConversations();

	/**
	 * Returns the value of the '<em><b>Conversation Associations</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conversation Associations</em>' containment reference.
	 * @see #setConversationAssociations(ConversationAssociation)
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_ConversationAssociations()
	 * @model containment="true" required="true" ordered="false"
	 *        extendedMetaData="kind='element' name='conversationAssociation' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL'"
	 * @generated
	 */
	ConversationAssociation getConversationAssociations();

	/**
	 * Sets the value of the '{@link org.eclipse.bpmn2.Collaboration#getConversationAssociations <em>Conversation Associations</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Conversation Associations</em>' containment reference.
	 * @see #getConversationAssociations()
	 * @generated
	 */
	void setConversationAssociations(ConversationAssociation value);

	/**
	 * Returns the value of the '<em><b>Participant Associations</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.bpmn2.ParticipantAssociation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Participant Associations</em>' containment reference list.
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_ParticipantAssociations()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='participantAssociation' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL'"
	 * @generated
	 */
	EList<ParticipantAssociation> getParticipantAssociations();

	/**
	 * Returns the value of the '<em><b>Message Flow Associations</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.bpmn2.MessageFlowAssociation}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Message Flow Associations</em>' containment reference list.
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_MessageFlowAssociations()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='messageFlowAssociation' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL'"
	 * @generated
	 */
	EList<MessageFlowAssociation> getMessageFlowAssociations();

	/**
	 * Returns the value of the '<em><b>Correlation Keys</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.bpmn2.CorrelationKey}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Correlation Keys</em>' containment reference list.
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_CorrelationKeys()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='correlationKey' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL'"
	 * @generated
	 */
	EList<CorrelationKey> getCorrelationKeys();

	/**
	 * Returns the value of the '<em><b>Choreography Ref</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.bpmn2.Choreography}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Choreography Ref</em>' reference list.
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_ChoreographyRef()
	 * @model ordered="false"
	 *        extendedMetaData="kind='element' name='choreographyRef' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL'"
	 * @generated
	 */
	EList<Choreography> getChoreographyRef();

	/**
	 * Returns the value of the '<em><b>Conversation Links</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.bpmn2.ConversationLink}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conversation Links</em>' containment reference list.
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_ConversationLinks()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='conversationLink' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL'"
	 * @generated
	 */
	EList<ConversationLink> getConversationLinks();

	/**
	 * Returns the value of the '<em><b>Is Closed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Closed</em>' attribute.
	 * @see #setIsClosed(boolean)
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_IsClosed()
	 * @model required="true" ordered="false"
	 *        extendedMetaData="kind='attribute' name='isClosed'"
	 * @generated
	 */
	boolean isIsClosed();

	/**
	 * Sets the value of the '{@link org.eclipse.bpmn2.Collaboration#isIsClosed <em>Is Closed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Closed</em>' attribute.
	 * @see #isIsClosed()
	 * @generated
	 */
	void setIsClosed(boolean value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.bpmn2.Bpmn2Package#getCollaboration_Name()
	 * @model required="true" ordered="false"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.bpmn2.Collaboration#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // Collaboration
