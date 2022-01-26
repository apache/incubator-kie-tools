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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.WrappedException;


/**
 * A command that comprises a sequence of subcommands.
 * Derived classes can control the way results are accumulated from the individual commands;
 * the default behaviour is to return the result of the last command.
 */
public class CompoundCommand extends AbstractCommand 
{
  /**
   * The list of subcommands.
   */
  protected List<Command> commandList;

  /**
   * When {@link #resultIndex} is set to this, 
   * {@link #getResult} and {@link #getAffectedObjects} are delegated to the last command, if any, in the list.
   */
  public static final int LAST_COMMAND_ALL = Integer.MIN_VALUE;

  /**
   * When {@link #resultIndex} is set to this, 
   * {@link #getResult} and {@link #getAffectedObjects}
   * are set to the result of merging the corresponding collection of each command in the list.
   */
  public static final int MERGE_COMMAND_ALL = Integer.MIN_VALUE - 1;

  /**
   * The index of the command whose result and affected objects are forwarded.
   * Negative values have special meaning, as defined by the static constants.
   * A value of -1 indicates that the last command in the list should be used.
   * We could have more special behaviours implemented for other negative values.
   */
  protected int resultIndex = MERGE_COMMAND_ALL;

  /**
   * Creates an empty instance.
   */
  public CompoundCommand()
  {
    super();
    commandList = new ArrayList<Command>();
  }

  /**
   * Creates an instance with the given label.
   * @param label the label.
   */
  public CompoundCommand(String label) 
  {
    super(label);
    commandList = new ArrayList<Command>();
  }
  
  /**
   * Creates an instance with the given label and description.
   * @param label the label.
   * @param description the description.
   */
  public CompoundCommand(String label, String description) 
  {
    super(label, description);
    commandList = new ArrayList<Command>();
  }
  
  /**
   * Creates an instance with the given list.
   * @param commandList the list of commands.
   */
  public CompoundCommand(List<Command> commandList)
  {
    super();
    this.commandList = commandList;
  }

  /**
   * Creates instance with the given label and list.
   * @param label the label.
   * @param commandList the list of commands.
   */
  public CompoundCommand(String label, List<Command> commandList)
  {
    super(label);
    this.commandList = commandList;
  }

  /**
   * Creates an instance with the given label, description, and list.
   * @param label the label.
   * @param description the description.
   * @param commandList the list of commands.
   */
  public CompoundCommand(String label, String description, List<Command> commandList)
  {
    super(label, description);
    this.commandList = commandList;
  }

  /**
   * Creates an empty instance with the given result index.
   * @param resultIndex the {@link #resultIndex}.
   */
  public CompoundCommand(int resultIndex)
  {
    super();
    this.resultIndex = resultIndex;
    commandList = new ArrayList<Command>();
  }

  /**
   * Creates an instance with the given result index and label.
   * @param resultIndex the {@link #resultIndex}.
   * @param label the label.
   */
  public CompoundCommand(int resultIndex, String label) 
  {
    super(label);
    this.resultIndex = resultIndex;
    commandList = new ArrayList<Command>();
  }
  
  /**
   * Creates an instance with the given result index, label, and description.
   * @param resultIndex the {@link #resultIndex}.
   * @param label the label.
   * @param description the description.
   */
  public CompoundCommand(int resultIndex, String label, String description) 
  {
    super(label, description);
    this.resultIndex = resultIndex;
    commandList = new ArrayList<Command>();
  }
  
  /**
   * Creates an instance with the given result index and list.
   * @param resultIndex the {@link #resultIndex}.
   * @param commandList the list of commands.
   */
  public CompoundCommand(int resultIndex, List<Command> commandList)
  {
    super();
    this.resultIndex = resultIndex;
    this.commandList = commandList;
  }

  /**
   * Creates an instance with the given resultIndex, label, and list.
   * @param resultIndex the {@link #resultIndex}.
   * @param label the label.
   * @param commandList the list of commands.
   */
  public CompoundCommand(int resultIndex, String label, List<Command> commandList)
  {
    super(label);
    this.resultIndex = resultIndex;
    this.commandList = commandList;
  }

  /**
   * Creates an instance with the given result index, label, description, and list.
   * @param resultIndex the {@link #resultIndex}.
   * @param label the label.
   * @param description the description.
   * @param commandList the list of commands.
   */
  public CompoundCommand(int resultIndex, String label, String description, List<Command> commandList)
  {
    super(label, description);
    this.resultIndex = resultIndex;
    this.commandList = commandList;
  }

  /**
   * Returns whether there are commands in the list.
   * @return whether there are commands in the list.
   */
  public boolean isEmpty()
  {
    return commandList.isEmpty(); 
  }

  /**
   * Returns an unmodifiable view of the commands in the list.
   * @return an unmodifiable view of the commands in the list.
   */
  public List<Command> getCommandList()
  {
    return Collections.unmodifiableList(commandList);
  }

  /**
   * Returns the index of the command whose result and affected objects are forwarded.
   * Negative values have special meaning, as defined by the static constants.
   * @return the index of the command whose result and affected objects are forwarded.
   * @see #LAST_COMMAND_ALL
   * @see #MERGE_COMMAND_ALL
   */
  public int getResultIndex()
  {
    return resultIndex;
  }

  /**
   * Returns whether all the commands can execute so that {@link #isExecutable} can be cached.
   * An empty command list causes <code>false</code> to be returned.
   * @return whether all the commands can execute.
   */
  @Override
  protected boolean prepare()
  {
    if (commandList.isEmpty())
    {
      return false;
    }
    else
    {
      for (Command command : commandList)
      {
        if (!command.canExecute())
        {
          return false;
        }
      }

      return true;
    }
  }

  /**
   * Calls {@link Command#execute} for each command in the list.
   */
  public void execute() 
  {
    for (ListIterator<Command> commands = commandList.listIterator(); commands.hasNext(); ) 
    {
      try
      {
        Command command = commands.next();
        command.execute();
      }
      catch (RuntimeException exception)
      {
        // Skip over the command that threw the exception.
        //
        commands.previous();

        try
        {
          // Iterate back over the executed commands to undo them.
          //
          while (commands.hasPrevious())
          {
            Command command = commands.previous();
            if (command.canUndo())
            {
              command.undo();
            }
            else
            {
              break;
            }
          }
        }
        catch (RuntimeException nestedException)
        {
          CommonPlugin.INSTANCE.log
            (new WrappedException
              (CommonPlugin.INSTANCE.getString("_UI_IgnoreException_exception"), nestedException).fillInStackTrace());
        }

        throw exception;
      }
    }
  }

  /**
   * Returns <code>false</code> if any of the commands return <code>false</code> for {@link Command#canUndo}.
   * @return <code>false</code> if any of the commands return <code>false</code> for <code>canUndo</code>.
   */
  @Override
  public boolean canUndo() 
  {
    for (Command command : commandList)
    {
      if (!command.canUndo())
      {
        return false;
      }
    }

    return true;
  }

  /**
   * Calls {@link Command#undo} for each command in the list, in reverse order.
   */
  @Override
  public void undo() 
  {
    for (ListIterator<Command> commands = commandList.listIterator(commandList.size()); commands.hasPrevious(); ) 
    {
      try
      {
        Command command = commands.previous();
        command.undo();
      }
      catch (RuntimeException exception)
      {
        // Skip over the command that threw the exception.
        //
        commands.next();

        try
        {
          // Iterate forward over the undone commands to redo them.
          //
          while (commands.hasNext())
          {
            Command command = commands.next();
            command.redo();
          }
        }
        catch (RuntimeException nestedException)
        {
          CommonPlugin.INSTANCE.log
            (new WrappedException
              (CommonPlugin.INSTANCE.getString("_UI_IgnoreException_exception"), nestedException).fillInStackTrace());
        }


        throw exception;
      }
    }
  }

  /**
   * Calls {@link Command#redo} for each command in the list.
   */
  public void redo() 
  {
    for (ListIterator<Command> commands = commandList.listIterator(); commands.hasNext(); ) 
    {
      try
      {
        Command command = commands.next();
        command.redo();
      }
      catch (RuntimeException exception)
      {
        // Skip over the command that threw the exception.
        //
        commands.previous();

        try
        {
          // Iterate back over the executed commands to undo them.
          //
          while (commands.hasPrevious())
          {
            Command command = commands.previous();
            command.undo();
          }
        }
        catch (RuntimeException nestedException)
        {
          CommonPlugin.INSTANCE.log
            (new WrappedException
              (CommonPlugin.INSTANCE.getString("_UI_IgnoreException_exception"), nestedException).fillInStackTrace());
        }

        throw exception;
      }
    }
  }

  /**
   * Determines the result by composing the results of the commands in the list;
   * this is affected by the setting of {@link #resultIndex}.
   * @return the result.
   */
  @Override
  public Collection<?> getResult()
  {
    if (commandList.isEmpty())
    {
      return Collections.EMPTY_LIST;
    }
    else if (resultIndex == LAST_COMMAND_ALL)
    {
      return commandList.get(commandList.size() - 1).getResult();
    }
    else if (resultIndex == MERGE_COMMAND_ALL)
    {
      return getMergedResultCollection();
    }
    else if (resultIndex < commandList.size())
    {
      return commandList.get(resultIndex).getResult();
    }
    else
    {
      return Collections.EMPTY_LIST;
    }
  }

  /**
   * Returns the merged collection of all command results.
   * @return the merged collection of all command results.
   */
  protected Collection<?> getMergedResultCollection()
  {
    Collection<Object> result = new ArrayList<Object>();

    for (Command command : commandList)
    {
      result.addAll(command.getResult());
    }

    return result;
  }


  /**
   * Determines the affected objects by composing the affected objects of the commands in the list;
   * this is affected by the setting of {@link #resultIndex}.
   * @return the affected objects.
   */
  @Override
  public Collection<?> getAffectedObjects()
  {
    if (commandList.isEmpty())
    {
      return Collections.EMPTY_LIST;
    }
    else if (resultIndex == LAST_COMMAND_ALL)
    {
      return commandList.get(commandList.size() - 1).getAffectedObjects();
    }
    else if (resultIndex == MERGE_COMMAND_ALL)
    {
      return getMergedAffectedObjectsCollection();
    }
    else if (resultIndex < commandList.size())
    {
      return commandList.get(resultIndex).getAffectedObjects();
    }
    else
    {
      return Collections.EMPTY_LIST;
    }
  }

  /**
   * Returns the merged collection of all command affected objects.
   * @return the merged collection of all command affected objects.
   */
  protected Collection<?> getMergedAffectedObjectsCollection()
  {
    Collection<Object> result = new ArrayList<Object>();

    for (Command command : commandList)
    {
      result.addAll(command.getAffectedObjects());
    }

    return result;
  }

  /**
   * Determines the label by composing the labels of the commands in the list;
   * this is affected by the setting of {@link #resultIndex}.
   * @return the label.
   */
  @Override
  public String getLabel()
  {
    if (label != null)
    {
      return label;
    } 
    else if (commandList.isEmpty())
    {
      return CommonPlugin.INSTANCE.getString("_UI_CompoundCommand_label");
    }
    else if (resultIndex == LAST_COMMAND_ALL || resultIndex == MERGE_COMMAND_ALL)
    {
      return commandList.get(commandList.size() - 1).getLabel();
    }
    else if (resultIndex < commandList.size())
    {
      return commandList.get(resultIndex).getLabel();
    }
    else
    {
      return CommonPlugin.INSTANCE.getString("_UI_CompoundCommand_label");
    }
  }

  /**
   * Determines the description by composing the descriptions of the commands in the list;
   * this is affected by the setting of {@link #resultIndex}.
   * @return the description.
   */
  @Override
  public String getDescription()
  {
    if (description != null)
    {
      return description;
    } 
    else if (commandList.isEmpty())
    {
      return CommonPlugin.INSTANCE.getString("_UI_CompoundCommand_description");
    }
    else if (resultIndex == LAST_COMMAND_ALL || resultIndex == MERGE_COMMAND_ALL)
    {
      return commandList.get(commandList.size() - 1).getDescription();
    }
    else if (resultIndex < commandList.size())
    {
      return commandList.get(resultIndex).getDescription();
    }
    else
    {
      return CommonPlugin.INSTANCE.getString("_UI_CompoundCommand_description");
    }
  }

  /**
   * Adds a command to this compound command's list of commands.
   * @param command the command to append.
   */
  public void append(Command command) 
  {
    if (isPrepared)
    {
      throw new IllegalStateException("The command is already prepared");
    }

    if (command != null)
    {
      commandList.add(command);
    }
  }

  /**
   * Checks if the command can execute; 
   * if so, it is executed, appended to the list, and true is returned,
   * if not, it is just disposed and false is returned.
   * A typical use for this is to execute commands created during the execution of another command, e.g.,
   * <pre>
   *   class MyCommand extends CommandBase
   *   {
   *     protected Command subcommand;
   *
   *     //...
   *
   *     public void execute()
   *     {
   *       // ...
   *       Compound subcommands = new CompoundCommand();
   *       subcommands.appendAndExecute(new AddCommand(...));
   *       if (condition) subcommands.appendAndExecute(new AddCommand(...));
   *       subcommand = subcommands.unwrap();
   *     }
   *
   *     public void undo()
   *     {
   *       // ...
   *       subcommand.undo();
   *     }
   *
   *     public void redo()
   *     {
   *       // ...
   *       subcommand.redo();
   *     }
   *
   *     public void dispose()
   *     {
   *       // ...
   *       if (subcommand != null)
   *      {
   *         subcommand.dispose();
   *       }
   *     }
   *   }
   * </pre>
   * Another use is in an execute override of compound command itself:
   * <pre>
   *   class MyCommand extends CompoundCommand
   *   {
   *     public void execute()
   *     {
   *       // ...
   *       appendAndExecute(new AddCommand(...));
   *       if (condition) appendAndExecute(new AddCommand(...));
   *     }
   *   }
   * </pre>
   * Note that appending commands will modify what getResult and getAffectedObjects return,
   * so you may want to set the resultIndex flag.
   * @param command the command.
   * @return whether the command was successfully executed and appended.
   */
  public boolean appendAndExecute(Command command)
  {
    if (command != null)
    {
      if (!isPrepared)
      {
        if (commandList.isEmpty())
        {
          isPrepared = true;
          isExecutable = true;
        }
        else
        {
          isExecutable = prepare();
          isPrepared = true;
          if (isExecutable)
          {
            execute();
          }
        }
      }

      if (command.canExecute())
      {
        try
        {
          command.execute();
          commandList.add(command);
          return true;
        }
        catch (RuntimeException exception)
        {
          CommonPlugin.INSTANCE.log
            (new WrappedException
              (CommonPlugin.INSTANCE.getString("_UI_IgnoreException_exception"), exception).fillInStackTrace());
        }
      }

      command.dispose();
    }

    return false;
  }

  /**
   * Adds a command to this compound command's the list of commands and returns <code>true</code>, 
   * if <code>command.{@link org.eclipse.emf.common.command.Command#canExecute() canExecute()}</code> returns true;
   * otherwise, it simply calls <code>command.{@link org.eclipse.emf.common.command.Command#dispose() dispose()}</code> 
   * and returns <code>false</code>.
   * @param command the command.
   * @return whether the command was executed and appended.
   */
  public boolean appendIfCanExecute(Command command) 
  {
    if (command == null)
    {
      return false;
    }
    else if (command.canExecute())
    {
      commandList.add(command);
      return true;
    }
    else
    {
      command.dispose();
      return false;
    }
  }

  /**
   * Calls {@link Command#dispose} for each command in the list.
   */
  @Override
  public void dispose()
  {
    for (Command command : commandList)
    {
      command.dispose();
    }
  }

  /**
   * Returns one of three things: 
   * {@link org.eclipse.emf.common.command.UnexecutableCommand#INSTANCE}, if there are no commands,
   * the one command, if there is exactly one command,
   * or <code>this</code>, if there are multiple commands;
   * this command is {@link #dispose}d in the first two cases.
   * You should only unwrap a compound command if you created it for that purpose, e.g.,
   * <pre>
   *   CompoundCommand subcommands = new CompoundCommand();
   *   subcommands.append(x);
   *   if (condition) subcommands.append(y);
   *   Command result = subcommands.unwrap();
   * </pre>
   * is a good way to create an efficient accumulated result.
   * @return the unwrapped command.
   */
  public Command unwrap()
  {
    switch (commandList.size())
    {
      case 0:
      {
        dispose();
        return UnexecutableCommand.INSTANCE;
      }
      case 1:
      {
        Command result = commandList.remove(0);
        dispose();
        return result;
      }
      default:
      {
        return this;
      }
    }
  }

  @Override
  public String toString()
  {
    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (commandList: #" + commandList.size() + ")");
    result.append(" (resultIndex: " + resultIndex + ")");

    return result.toString();
  }
}
