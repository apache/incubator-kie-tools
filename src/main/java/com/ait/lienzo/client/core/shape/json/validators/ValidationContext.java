/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape.json.validators;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.i18n.MessageConstants;
import com.ait.lienzo.client.core.util.StringFormatter;

/**
 * ValidationContext is used during deserialization of a JSON string
 * into a Node.
 * <p>
 * All error messages use {@link MessageConstants} so that they can
 * be internationalized.
 * 
 * @see JSONDeserializer
 */
public class ValidationContext
{
    private boolean                     m_stopOnError;

    private boolean                     m_validate = true;

    private final List<String>          m_stack    = new ArrayList<>();

    private final List<ValidationError> m_errors   = new ArrayList<>();

    /**
     * Push the context (e.g. attribute name) that is being deserialized 
     * onto the context.
     * 
     * The context stack tracks where we are in the JSON tree
     * so we can give useful error messages.
     * 
     * @param context e.g. attribute name
     * @see #pop()
     */
    public void push(final String context)
    {
        m_stack.add("." + context);
    }

    /**
     * Push the index of the array that is being deserialized 
     * onto the context.
     * 
     * The context stack tracks where we are in the JSON tree
     * so we can give useful error messages.
     * 
     * @param index of the child node that is being deserialized
     * @see #pop()
     */
    public void pushIndex(final int index)
    {
        m_stack.add("[" + index + "]");
    }

    /**
     * Pops the context stack.
     * 
     * @see #push(String)
     * @see #pushIndex(int)
     */
    public void pop()
    {
        m_stack.remove(m_stack.size() - 1);
    }

    /**
     * Adds a ValidationError.
     * 
     * If stopOnError is true, it will immediately throw a ValidationException
     * to stop the deserialization process.
     * 
     * @param e
     * @throws ValidationException
     */
    protected void addError(final ValidationError e) throws ValidationException
    {
        m_errors.add(e);

        if (m_stopOnError)
        {
            throw new ValidationException(this);
        }
    }

    /**
     * Adds a ValidationError with the specified message and the current context stack.
     * If stopOnError is true, it will immediately throw a ValidationException
     * to stop the deserialization process.
     * 
     * @param msg Validation error message
     * 
     * @throws ValidationException
     */
    public void addError(final String msg) throws ValidationException
    {
        addError(new ValidationError(msg, joinContext(m_stack)));
    }

    /**
     * Calls {@link #addError(String)} with a message that indicates
     * that a required attribute is missing.
     * 
     * @throws ValidationException
     */
    public void addRequiredError() throws ValidationException
    {
        addError(MessageConstants.MESSAGES.attributeIsRequired());
    }

    /**
     * Calls {@link #addError(String)} with a message that indicates
     * that a node has the wrong JSON type, 
     * e.g. we're expecting a String but the value was a Number.
     * 
     * @param type expected Node or Shape type
     * @param val JSONValue that caused the error
     * 
     * @throws ValidationException
     */
    public void addBadValueError(final String type, final Object val) throws ValidationException
    {
        addError(StringFormatter.format(MessageConstants.MESSAGES.invalidValueForType(), type, val));
    }

    /**
     * Calls {@link #addError(String)} with a message that indicates
     * that an invalid node type or shape type name was encountered.
     * <p>
     * If you're writing your own Node class, you may need to register the
     * type in {@link FactoryRegistry}.
     * 
     * @param type Node or Shape type
     * 
     * @throws ValidationException
     */
    public void addBadTypeError(final String type) throws ValidationException
    {
        addError(StringFormatter.format(MessageConstants.MESSAGES.invalidType(), type));
    }

    /**
     * Calls {@link #addError(String)} with a message that indicates
     * that the specified Node or Shape type does not have the attribute
     * that is on the context stack.
     * 
     * @throws ValidationException
     */
    public void addInvalidAttributeError(final String type) throws ValidationException
    {
        addError(StringFormatter.format(MessageConstants.MESSAGES.attributeIsInvalidForType(), type));
    }

    /**
     * Calls {@link #addError(String)} with a message that indicates
     * that the attribute on the context stack must have a specific (hardcoded) value.
     * 
     * @param val The value that was found (and was wrong)
     * 
     * @throws ValidationException
     */
    public void addRequiredAttributeValueError(final String val) throws ValidationException
    {
        addError(StringFormatter.format(MessageConstants.MESSAGES.attributeValueMustBeFixed(), val));
    }

    /**
     * Calls {@link #addError(String)} with a message that indicates
     * that there is no NodeFactory for the given Node or Shape type.
     * <p>
     * If you're writing your own Node class, you may need to register the
     * type in {@link FactoryRegistry}.
     * 
     * @param type Node or Shape type
     * @throws ValidationException
     */
    public void addMissingNodeFactoryError(final String type) throws ValidationException
    {
        addError(StringFormatter.format(MessageConstants.MESSAGES.missingNodeFactory(), type));
    }

    /**
     * Calls {@link #addError(String)} with a message that indicates
     * that an array has the wrong number of elements
     * 
     * @param expectedSize
     * @param actualSize
     * @throws ValidationException
     */
    public void addBadArraySizeError(final int expectedSize, final int actualSize) throws ValidationException
    {
        addError(StringFormatter.format(MessageConstants.MESSAGES.invalidArraySize(), expectedSize, actualSize));
    }

    /**
     * Returns whether to stop the deserialization process when an error is encountered.
     * 
     * @return boolean
     */
    public boolean isStopOnError()
    {
        return m_stopOnError;
    }

    /**
     * Sets whether to stop the deserialization process when an error is encountered.
     * 
     * @return this ValidationContext
     */
    public ValidationContext setStopOnError(final boolean stopOnError)
    {
        m_stopOnError = stopOnError;

        return this;
    }

    /**
     * Returns whether we should validate the node structure 
     * (i.e. attribute values, required attributes and valid child node types)
     * during the deserialization process.
     * 
     * @return boolean
     */
    public boolean isValidate()
    {
        return m_validate;
    }

    /**
     * Sets whether we should validate the node structure 
     * (i.e. attribute values, required attributes and valid child node types)
     * during the deserialization process.
     * 
     * @param validate
     * @return this ValidationContext
     */
    public ValidationContext setValidate(final boolean validate)
    {
        m_validate = validate;

        return this;
    }

    /**
     * Returns the number of errors that were encountered.
     * 
     * @return int
     */
    public int getErrorCount()
    {
        return m_errors.size();
    }

    /**
     * Returns the list of ValidationErrors that were found.
     * 
     * @return List<ValidationError>
     */
    public List<ValidationError> getErrors()
    {
        return m_errors;
    }

    /**
     * Returns a string with all error messages for debugging purposes.
     * 
     * @return String
     */
    public String getDebugString()
    {
        final StringBuilder b = new StringBuilder();

        boolean first = true;

        for (ValidationError e : m_errors)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                b.append("\n");
            }
            b.append(e.getContext()).append(" - ").append(e.getMessage());
        }
        return b.toString();
    }

    private static String joinContext(final List<String> stack)
    {
        final StringBuilder b = new StringBuilder();

        for (String s : stack)
        {
            b.append(s);
        }
        return b.toString();
    }
}
