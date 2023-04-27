/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import com.amihaiemil.eoyaml.Comment;
import com.amihaiemil.eoyaml.CompatibilityWrapper;
import com.amihaiemil.eoyaml.EmptyYamlMapping;
import com.amihaiemil.eoyaml.EmptyYamlSequence;
import com.amihaiemil.eoyaml.GWTYamlNode;
import com.amihaiemil.eoyaml.Node;
import com.amihaiemil.eoyaml.Scalar;
import com.amihaiemil.eoyaml.ScalarComment;
import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlPrinter;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.YamlStream;
import com.amihaiemil.eoyaml.exceptions.YamlPrintException;
import com.amihaiemil.eoyaml.exceptions.YamlReadingException;
import jakarta.json.GwtIncompatible;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLWriter;

/**
 * DefaultYAMLWriter class.
 *
 * @author nicolasmorel
 * @version $Id: $
 */
public class DefaultYAMLWriter implements YAMLWriter {

  private YamlMappingBuilder writer = Yaml.createYamlMappingBuilder();

  @Override
  public YAMLWriter value(String name, String value) {
    writer = writer.add(name, value);
    return this;
  }

  @Override
  public YAMLWriter value(String name, YamlMapping value) {
    writer = writer.add(name, value);
    return this;
  }

  @Override
  public YAMLWriter value(String name, YamlSequence value) {
    writer = writer.add(name, value);
    return this;
  }

  @Override
  public String getOutput() {
    final StringWriter stringWriter = new StringWriter();
    NoExtraLineYamlSequencePrintWriter printer =
        new NoExtraLineYamlSequencePrintWriter(stringWriter);
    try {
      printer.print(writer.build());
      return stringWriter.toString();
    } catch (final IOException ex) {
      throw new YamlPrintException("IOException when printing YAML", ex);
    }
  }

  @Override
  public void nullValue(String name) {
    writer = writer.add(name, "~");
  }

  @Override
  public YamlMappingBuilder getWriter() {
    return writer;
  }

  private static class NoExtraLineYamlSequencePrintWriter implements YamlPrinter {

    /** Writer where the given YAML will be printed. */
    private final Writer writer;

    /**
     * Constructor.
     *
     * @param writer Destination writer.
     */
    private NoExtraLineYamlSequencePrintWriter(final Writer writer) {
      this.writer = writer;
    }

    @Override
    public void print(final YamlNode node) throws IOException {
      try {
        if (node instanceof Scalar) {
          this.writer.append("---").append(CompatibilityWrapper.lineSeparator());
          this.printPossibleComment(node, "");
          this.printScalar((Scalar) node, 0);
          this.writer.append(CompatibilityWrapper.lineSeparator()).append("...");
        } else if (node instanceof YamlSequence) {
          boolean documentComment = this.printPossibleComment(node, "");
          if (documentComment) {
            this.writer.append("---").append(CompatibilityWrapper.lineSeparator());
          }
          this.printSequence((YamlSequence) node, 0);
        } else if (node instanceof YamlMapping) {
          boolean documentComment = this.printPossibleComment(node, "");
          if (documentComment) {
            this.writer.append("---").append(CompatibilityWrapper.lineSeparator());
          }
          this.printMapping((YamlMapping) node, 0);
        } else if (node instanceof YamlStream) {
          this.printStream((YamlStream) node, 0);
        }
      } finally {
        this.writer.close();
      }
    }

    /**
     * Print a YAML Stream of documents.
     *
     * @param stream Given YamlStream.
     * @param indentation Level of indentation of the printed stream.
     * @throws IOException If an I/O problem occurs.
     */
    private void printStream(final YamlStream stream, final int indentation) throws IOException {
      final String newLine = CompatibilityWrapper.lineSeparator();
      int spaces = indentation;
      final StringBuilder indent = new StringBuilder();
      while (spaces > 0) {
        indent.append(" ");
        spaces--;
      }
      final Iterator<YamlNode> valuesIt = stream.values().iterator();
      while (valuesIt.hasNext()) {
        final YamlNode document = valuesIt.next();
        this.writer.append(indent).append("---");
        this.printNode(document, true, indentation + 2);
        if (valuesIt.hasNext()) {
          this.writer.append(newLine);
        }
      }
    }

    /**
     * Print a YAML Mapping.
     *
     * @param mapping Given YamlMapping.
     * @param indentation Level of indentation of the printed mapping.
     * @throws IOException If an I/O problem occurs.
     */
    private void printMapping(final YamlMapping mapping, final int indentation) throws IOException {
      final String newLine = CompatibilityWrapper.lineSeparator();
      int spaces = indentation;
      final StringBuilder alignment = new StringBuilder();
      while (spaces > 0) {
        alignment.append(" ");
        spaces--;
      }
      final Iterator<YamlNode> keysIt = mapping.keys().iterator();
      while (keysIt.hasNext()) {
        final YamlNode key = keysIt.next();
        final YamlNode value = mapping.value(key);
        this.printPossibleComment(value, alignment.toString());
        this.writer.append(alignment);
        if (key instanceof Scalar) {
          this.writer.append(this.indent(new Escaped((Scalar) key).value(), 0));
          this.writer.append(":");
        } else {
          this.writer.append("?");
          this.printNode(key, true, indentation + 2);
          this.writer.append(newLine).append(alignment).append(":");
        }
        if (value instanceof Scalar) {
          this.printNode(value, false, indentation);
        } else {
          this.printNode(value, true, indentation + 2);
        }
        if (keysIt.hasNext()) {
          this.writer.append(newLine);
        }
      }
    }

    /**
     * Print a YAML Sequence.
     *
     * @param sequence Given YamlSequence.
     * @param indentation Level of indentation of the printed Scalar.
     * @throws IOException If an I/O problem occurs.
     */
    private void printSequence(final YamlSequence sequence, final int indentation)
        throws IOException {
      final String newLine = CompatibilityWrapper.lineSeparator();
      int spaces = indentation;
      final StringBuilder alignment = new StringBuilder();
      while (spaces > 0) {
        alignment.append(" ");
        spaces--;
      }
      final Iterator<YamlNode> valuesIt = sequence.values().iterator();
      while (valuesIt.hasNext()) {
        final YamlNode node = valuesIt.next();
        this.printPossibleComment(node, alignment.toString());
        this.writer.append(alignment).append("-");
        if (node instanceof Scalar) {
          this.printNode(node, false, 0);
        } else if (node instanceof YamlMapping) {
          this.printYamlMappingAsSequenceNode(node, indentation + 2);
        } else {
          this.printNode(node, true, indentation + 2);
        }
        if (valuesIt.hasNext()) {
          this.writer.append(newLine);
        }
      }
    }

    private void printYamlMappingAsSequenceNode(final YamlNode node, final int indentation)
        throws IOException {
      if (node == null || node.isEmpty()) {
        if (node instanceof EmptyYamlMapping) {
          this.writer.append(" ").append("{}");
        } else {
          this.writer.append(" ").append("null");
        }
      } else {
        this.writer.append(" ");
        YamlMapping mapping = (YamlMapping) node;
        final String newLine = CompatibilityWrapper.lineSeparator();
        int spaces = indentation;
        final StringBuilder alignment = new StringBuilder();
        boolean first = true;

        final Iterator<YamlNode> keysIt = mapping.keys().iterator();
        while (keysIt.hasNext()) {
          final YamlNode key = keysIt.next();
          final YamlNode value = mapping.value(key);
          this.printPossibleComment(value, alignment.toString());

          if (first) {
            first = false;
            while (spaces > 0) {
              alignment.append(" ");
              spaces--;
            }
          } else {
            this.writer.append(alignment);
          }
          if (key instanceof Scalar) {
            this.writer.append(this.indent(new Escaped((Scalar) key).value(), 0));
            this.writer.append(":");
          } else {
            this.writer.append("?");
            this.printNode(key, true, indentation + 2);
            this.writer.append(newLine).append(alignment).append(":");
          }
          if (value instanceof Scalar) {
            this.printNode(value, false, indentation);
          } else {
            this.printNode(value, true, indentation + 2);
          }
          if (keysIt.hasNext()) {
            this.writer.append(newLine);
          }
        }
      }
    }

    /**
     * Print a YAML Scalar.
     *
     * @param scalar Given Scalar.
     * @param indentation Level of indentation of the printed Scalar.
     * @throws IOException If an I/O problem occurs.
     */
    private void printScalar(final Scalar scalar, final int indentation) throws IOException {
      // skipped folded
      if (scalar
              .getClass()
              .getCanonicalName()
              .equals("com.amihaiemil.eoyaml.RtYamlScalarBuilder.BuiltLiteralBlockScalar")
          || scalar
              .getClass()
              .getCanonicalName()
              .equals("com.amihaiemil.eoyaml.ReadLiteralBlockScalar")) {
        this.writer.append("|");
        if (!scalar.comment().value().isEmpty()) {
          this.writer.append(" # ").append(scalar.comment().value());
        }
        this.writer
            .append(CompatibilityWrapper.lineSeparator())
            .append(this.indent(scalar.value(), indentation + 2));
      } else {
        final Comment comment = scalar.comment();
        if (comment instanceof ScalarComment) {
          this.writer.append(this.indent(new Escaped(scalar).value(), 0));
          final ScalarComment scalarComment = (ScalarComment) comment;

          if (!scalarComment.inline().value().isEmpty()) {
            this.writer.append(" # ").append(scalarComment.inline().value());
          }
        }
      }
    }

    /**
     * This method should be used when printing children nodes of a complex Node (mapping, scalar,
     * stream etc).
     *
     * @param node YAML Node to print.
     * @param onNewLine Should the child node be printed on a new line?
     * @param indentation Indentation of the print.
     * @throws IOException If any I/O error occurs.
     */
    private void printNode(final YamlNode node, final boolean onNewLine, final int indentation)
        throws IOException {
      if (node == null || node.isEmpty()) {
        if (node instanceof EmptyYamlSequence) {
          this.writer.append(" ").append("[]");
        } else if (node instanceof EmptyYamlMapping) {
          this.writer.append(" ").append("{}");
        } else {
          this.writer.append(" ").append("null");
        }
      } else {
        if (onNewLine) {
          this.writer.append(CompatibilityWrapper.lineSeparator());
        } else {
          this.writer.append(" ");
        }
        if (node instanceof Scalar) {
          this.printScalar((Scalar) node, indentation);
        } else if (node instanceof YamlSequence) {
          this.printSequence((YamlSequence) node, indentation);
        } else if (node instanceof YamlMapping) {
          this.printMapping((YamlMapping) node, indentation);
        } else if (node instanceof YamlStream) {
          this.printStream((YamlStream) node, indentation);
        }
      }
    }

    /**
     * Print a comment. Make sure to split the lines if there are more lines separated by NewLine
     * and also add a '# ' in front of each line.
     *
     * @param node Node containing the Comment.
     * @param alignment Indentation.
     * @return True if a comment was printed, false otherwise.
     * @throws IOException If any I/O problem occurs.
     */
    private boolean printPossibleComment(final YamlNode node, final String alignment)
        throws IOException {
      boolean printed = false;
      if (node != null && node.comment() != null) {
        final Comment tmpComment;
        if (node.comment() instanceof ScalarComment) {
          tmpComment = ((ScalarComment) node.comment()).above();
        } else {
          tmpComment = node.comment();
        }
        final String com = tmpComment.value();
        if (com.trim().length() != 0) {
          String[] lines = com.split(CompatibilityWrapper.lineSeparator());
          for (final String line : lines) {
            this.writer
                .append(alignment)
                .append("# ")
                .append(line)
                .append(CompatibilityWrapper.lineSeparator());
          }
          printed = true;
        }
      }
      return printed;
    }

    /**
     * Indent a given String value. If the String has multiple lines, they will all be indented
     * together.
     *
     * @param value String to indent.
     * @param indentation Indentation level.
     * @return Indented String.
     */
    private String indent(final String value, final int indentation) {
      StringBuilder alignment = new StringBuilder();
      int spaces = indentation;
      while (spaces > 0) {
        alignment.append(" ");
        spaces--;
      }
      String[] lines = value.split(CompatibilityWrapper.lineSeparator());
      StringBuilder printed = new StringBuilder();
      for (int idx = 0; idx < lines.length; idx++) {
        printed.append(alignment);
        printed.append(lines[idx]);
        if (idx < lines.length - 1) {
          printed.append(CompatibilityWrapper.lineSeparator());
        }
      }
      return printed.toString();
    }

    /**
     * A scalar which escapes its value.
     *
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id: afa3de41d3c85ac89058e3fcb674e009672d8b2f $
     * @since 4.3.1
     */
    private static class Escaped extends GWTYamlNode {

      /** Original unescaped scalar. */
      private final Scalar original;

      /**
       * Ctor.
       *
       * @param original Unescaped scalar.
       */
      private Escaped(final Scalar original) {
        this.original = original;
      }

      private String value() {
        final String value = this.original.value();
        String escaped = value;
        boolean quoted =
            (value.startsWith("'") && value.endsWith("'"))
                || (value.startsWith("\"") && value.endsWith("\""));
        if (!quoted && value.matches(".*[?\\-#:>|$%&{}\\[\\]]+.*|[ ]+")) {
          if (value.contains("\"")) {
            escaped = "'" + value + "'";
          } else {
            escaped = "\"" + value + "\"";
          }
        }
        return escaped;
      }

      @Override
      public boolean isEmpty() {
        return this.value() == null || this.value().isEmpty();
      }

      @Override
      public Comment comment() {
        return this.original.comment();
      }

      @Override
      public final Node type() {
        return Node.SCALAR;
      }

      @Override
      public final Scalar asScalar() throws YamlReadingException, ClassCastException {
        return this.asClass(Scalar.class, Node.SCALAR);
      }

      @Override
      public final YamlMapping asMapping() throws YamlReadingException, ClassCastException {
        return this.asClass(YamlMapping.class, Node.MAPPING);
      }

      @Override
      public final YamlSequence asSequence() throws YamlReadingException, ClassCastException {
        return this.asClass(YamlSequence.class, Node.SEQUENCE);
      }

      @Override
      public final YamlStream asStream() throws YamlReadingException, ClassCastException {
        return this.asClass(YamlStream.class, Node.STREAM);
      }

      @Override
      @GwtIncompatible
      public final <T extends YamlNode> T asClass(final Class<T> clazz, final Node type)
          throws YamlReadingException, ClassCastException {
        if (this.type() != type) {
          throw new YamlReadingException("The YamlNode is not a " + clazz.getSimpleName() + '!');
        }
        return clazz.cast(this);
      }

      /**
       * Print this YamlNode using a StringWriter to create its String representation.
       *
       * @return String print of this YamlNode.
       * @throws YamlPrintException If there is any I/O problem when printing the YAML.
       */
      @Override
      public final String toString() {
        throw new YamlPrintException("toString() is not supported for Escaped nodes!", null);
      }

      @Override
      public boolean equals(final Object other) {
        final boolean result;
        if (other == null || !(other instanceof Scalar)) {
          result = false;
        } else if (this == other) {
          result = true;
        } else {
          result = this.compareTo((Scalar) other) == 0;
        }
        return result;
      }

      @Override
      public int hashCode() {
        return this.value().hashCode();
      }

      @Override
      public int compareTo(final YamlNode other) {
        int result = -1;
        if (this == other) {
          result = 0;
        } else if (other == null) {
          result = 1;
        } else if (other instanceof Scalar) {
          final String value = this.value();
          final String otherVal = ((Scalar) other).value();
          if (value == null && otherVal == null) {
            result = 0;
          } else if (value != null && otherVal == null) {
            result = 1;
          } else if (value == null && otherVal != null) {
            result = -1;
          } else {
            result = this.value().compareTo(otherVal);
          }
        }
        return result;
      }
    }
  }
}
