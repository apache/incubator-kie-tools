/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common.command;


import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.CommonPlugin;


/**
 * A command that always produces the same result.
 */
public class IdentityCommand extends AbstractCommand 
{
  /**
   * An empty instance of this object.
   */
  public static final IdentityCommand INSTANCE = new IdentityCommand();

  /**
   * Keeps track of the result returned from {@link #getResult}.
   */
  protected Collection<?> result;

  {
    // This ensures that these useless state variables at least reflect the right value.
    //
    isPrepared = true;
    isExecutable = true;
  }

  /**
   * Creates an empty instance.
   */
  public IdentityCommand() 
  {
    super();
    this.result = Collections.EMPTY_LIST;
  }

  /**
   * Creates an instance with a result collection containing the given result object.
   * @param result the one object in the result collection.
   */
  public IdentityCommand(Object result) 
  {
    super();
    this.result = Collections.singleton(result);
  }

  /**
   * Creates an instance with the given result collection.
   * @param result the result collection.
   */
  public IdentityCommand(Collection<?> result) 
  {
    super();
    this.result = result;
  }

  /**
   * Creates an instance with the given label.
   * @param label the label.
   */
  public IdentityCommand(String label)
  {
    this.label = label;
    this.result = Collections.EMPTY_LIST;
  }

  /**
   * Creates an instance with the given label and a result collection containing the given result object.
   * @param label the label.
   * @param result the one object in the result collection.
   */
  public IdentityCommand(String label, Object result)
  {
    this.label = label;
    this.result = Collections.singleton(result);
  }

  /**
   * Creates an instance with the given label the result collection.
   * @param label the label.
   * @param result the result collection.
   */
  public IdentityCommand(String label, Collection<?> result)
  {
    this.label = label;
    this.result = result;
  }

  /**
   * Creates an instance with the given label and description.
   * @param label the label.
   * @param description the description.
   */
  public IdentityCommand(String label, String description)
  {
    this.label = label;
    this.description = description;
    this.result = Collections.EMPTY_LIST;
  }

  /**
   * Creates an instance with the given label, description, and a result collection containing the given result object.
   * @param label the label.
   * @param description the description.
   * @param result the one object in the result collection.
   */
  public IdentityCommand(String label, String description, Object result)
  {
    this.label = label;
    this.description = description;
    this.result = Collections.singleton(result);
  }

  /**
   * Creates an instance with the given label, description, result collection.
   * @param label the label.
   * @param description the description.
   * @param result the result collection.
   */
  public IdentityCommand(String label, String description, Collection<?> result)
  {
    this.label = label;
    this.description = description;
    this.result = result;
  }

  /**
   * Returns <code>true</code>.
   * @return <code>true</code>.
   */
  @Override
  public boolean canExecute() 
  {
    return true;
  }

  /**
   * Do nothing.
   */
  public void execute() 
  {
    // Do nothing.
  }

  /**
   * Do nothing.
   */
  @Override
  public void undo() 
  {
    // Do nothing.
  }

  /**
   * Do nothing.
   */
  public void redo() 
  {
    // Do nothing.
  }

  @Override
  public String getLabel()
  {
    return label == null ? CommonPlugin.INSTANCE.getString("_UI_IdentityCommand_label") : label;
  }

  @Override
  public String getDescription()
  {
    return description == null ? CommonPlugin.INSTANCE.getString("_UI_IdentityCommand_description") : description;
  }

  /**
   * Return the identity result.
   * @return the identity result.
   */
  @Override
  public Collection<?> getResult()
  {
    return result;
  }
}
