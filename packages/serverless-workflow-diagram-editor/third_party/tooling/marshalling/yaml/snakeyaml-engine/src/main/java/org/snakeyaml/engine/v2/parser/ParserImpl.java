/*
 * Copyright (c) 2018, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.snakeyaml.engine.v2.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.comments.CommentType;
import org.snakeyaml.engine.v2.common.Anchor;
import org.snakeyaml.engine.v2.common.ArrayStack;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.common.SpecVersion;
import org.snakeyaml.engine.v2.events.AliasEvent;
import org.snakeyaml.engine.v2.events.CommentEvent;
import org.snakeyaml.engine.v2.events.DocumentEndEvent;
import org.snakeyaml.engine.v2.events.DocumentStartEvent;
import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.events.ImplicitTuple;
import org.snakeyaml.engine.v2.events.MappingEndEvent;
import org.snakeyaml.engine.v2.events.MappingStartEvent;
import org.snakeyaml.engine.v2.events.ScalarEvent;
import org.snakeyaml.engine.v2.events.SequenceEndEvent;
import org.snakeyaml.engine.v2.events.SequenceStartEvent;
import org.snakeyaml.engine.v2.events.StreamEndEvent;
import org.snakeyaml.engine.v2.events.StreamStartEvent;
import org.snakeyaml.engine.v2.exceptions.Mark;
import org.snakeyaml.engine.v2.exceptions.ParserException;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.scanner.Scanner;
import org.snakeyaml.engine.v2.scanner.ScannerImpl;
import org.snakeyaml.engine.v2.scanner.StreamReader;
import org.snakeyaml.engine.v2.tokens.AliasToken;
import org.snakeyaml.engine.v2.tokens.AnchorToken;
import org.snakeyaml.engine.v2.tokens.BlockEntryToken;
import org.snakeyaml.engine.v2.tokens.CommentToken;
import org.snakeyaml.engine.v2.tokens.DirectiveToken;
import org.snakeyaml.engine.v2.tokens.ScalarToken;
import org.snakeyaml.engine.v2.tokens.StreamEndToken;
import org.snakeyaml.engine.v2.tokens.StreamStartToken;
import org.snakeyaml.engine.v2.tokens.TagToken;
import org.snakeyaml.engine.v2.tokens.TagTuple;
import org.snakeyaml.engine.v2.tokens.Token;

/**
 *
 *
 * <pre>
 * # The following YAML grammar is LL(1) and is parsed by a recursive descent parser.
 *
 * stream            ::= STREAM-START implicit_document? explicit_document* STREAM-END
 * implicit_document ::= block_node DOCUMENT-END*
 * explicit_document ::= DIRECTIVE* DOCUMENT-START block_node? DOCUMENT-END*
 * block_node_or_indentless_sequence ::=
 *                       ALIAS
 *                       | properties (block_content | indentless_block_sequence)?
 *                       | block_content
 *                       | indentless_block_sequence
 * block_node        ::= ALIAS
 *                       | properties block_content?
 *                       | block_content
 * flow_node         ::= ALIAS
 *                       | properties flow_content?
 *                       | flow_content
 * properties        ::= TAG ANCHOR? | ANCHOR TAG?
 * block_content     ::= block_collection | flow_collection | SCALAR
 * flow_content      ::= flow_collection | SCALAR
 * block_collection  ::= block_sequence | block_mapping
 * flow_collection   ::= flow_sequence | flow_mapping
 * block_sequence    ::= BLOCK-SEQUENCE-START (BLOCK-ENTRY block_node?)* BLOCK-END
 * indentless_sequence   ::= (BLOCK-ENTRY block_node?)+
 * block_mapping     ::= BLOCK-MAPPING_START
 *                       ((KEY block_node_or_indentless_sequence?)?
 *                       (VALUE block_node_or_indentless_sequence?)?)*
 *                       BLOCK-END
 * flow_sequence     ::= FLOW-SEQUENCE-START
 *                       (flow_sequence_entry FLOW-ENTRY)*
 *                       flow_sequence_entry?
 *                       FLOW-SEQUENCE-END
 * flow_sequence_entry   ::= flow_node | KEY flow_node? (VALUE flow_node?)?
 * flow_mapping      ::= FLOW-MAPPING-START
 *                       (flow_mapping_entry FLOW-ENTRY)*
 *                       flow_mapping_entry?
 *                       FLOW-MAPPING-END
 * flow_mapping_entry    ::= flow_node | KEY flow_node? (VALUE flow_node?)?
 * #
 * FIRST sets:
 * #
 * stream: { STREAM-START }
 * explicit_document: { DIRECTIVE DOCUMENT-START }
 * implicit_document: FIRST(block_node)
 * block_node: { ALIAS TAG ANCHOR SCALAR BLOCK-SEQUENCE-START BLOCK-MAPPING-START FLOW-SEQUENCE-START FLOW-MAPPING-START }
 * flow_node: { ALIAS ANCHOR TAG SCALAR FLOW-SEQUENCE-START FLOW-MAPPING-START }
 * block_content: { BLOCK-SEQUENCE-START BLOCK-MAPPING-START FLOW-SEQUENCE-START FLOW-MAPPING-START SCALAR }
 * flow_content: { FLOW-SEQUENCE-START FLOW-MAPPING-START SCALAR }
 * block_collection: { BLOCK-SEQUENCE-START BLOCK-MAPPING-START }
 * flow_collection: { FLOW-SEQUENCE-START FLOW-MAPPING-START }
 * block_sequence: { BLOCK-SEQUENCE-START }
 * block_mapping: { BLOCK-MAPPING-START }
 * block_node_or_indentless_sequence: { ALIAS ANCHOR TAG SCALAR BLOCK-SEQUENCE-START BLOCK-MAPPING-START FLOW-SEQUENCE-START FLOW-MAPPING-START BLOCK-ENTRY }
 * indentless_sequence: { ENTRY }
 * flow_collection: { FLOW-SEQUENCE-START FLOW-MAPPING-START }
 * flow_sequence: { FLOW-SEQUENCE-START }
 * flow_mapping: { FLOW-MAPPING-START }
 * flow_sequence_entry: { ALIAS ANCHOR TAG SCALAR FLOW-SEQUENCE-START FLOW-MAPPING-START KEY }
 * flow_mapping_entry: { ALIAS ANCHOR TAG SCALAR FLOW-SEQUENCE-START FLOW-MAPPING-START KEY }
 * </pre>
 *
 * <p>Since writing a recursive-descendant parser is a straightforward task, we do not give many
 * comments here.
 */
public class ParserImpl implements Parser {

  private static final Map<String, String> DEFAULT_TAGS = new HashMap<>();

  static {
    DEFAULT_TAGS.put("!", "!");
    DEFAULT_TAGS.put("!!", Tag.PREFIX);
  }

  /** tokeniser */
  protected final Scanner scanner;

  private final LoadSettings settings;
  private final ArrayStack<Production> states;
  private final ArrayStack<Optional<Mark>> marksStack;
  private Optional<Event> currentEvent; // parsed event
  private Optional<Production> state;
  private Map<String, String> directiveTags;

  /**
   * @param reader - the input
   * @param settings - the configuration options
   * @deprecated use the other constructor with LoadSettings first
   */
  @Deprecated
  public ParserImpl(StreamReader reader, LoadSettings settings) {
    this(settings, reader);
  }

  /**
   * Create
   *
   * @param settings - configuration options
   * @param reader - the input
   */
  public ParserImpl(LoadSettings settings, StreamReader reader) {
    this(settings, new ScannerImpl(settings, reader));
  }

  /**
   * @param scanner - input
   * @param settings - configuration options
   * @deprecated use the other constructor with LoadSettings first
   */
  @Deprecated
  public ParserImpl(Scanner scanner, LoadSettings settings) {
    this(settings, scanner);
  }

  /**
   * Create
   *
   * @param settings - configuration options
   * @param scanner - input
   */
  public ParserImpl(LoadSettings settings, Scanner scanner) {
    this.scanner = scanner;
    this.settings = settings;
    currentEvent = Optional.empty();
    directiveTags = new HashMap<>(DEFAULT_TAGS);
    states = new ArrayStack<>(100);
    marksStack = new ArrayStack<>(10);
    state = Optional.of(new ParseStreamStart()); // prepare the next state
  }

  /** Check the ID of the next event. */
  public boolean checkEvent(Event.ID id) {
    peekEvent();
    return currentEvent.isPresent() && currentEvent.get().getEventId() == id;
  }

  /** Get the next event (and keep it). Produce the event if not yet present. */
  public Event peekEvent() {
    produce();
    return currentEvent.orElseThrow(() -> new NoSuchElementException("No more Events found."));
  }

  /** Consume the event (get the next event and removed it). */
  public Event next() {
    Event value = peekEvent();
    currentEvent = Optional.empty();
    return value;
  }

  /**
   * Produce the event if not yet present.
   *
   * @return true if there is another event
   */
  @Override
  public boolean hasNext() {
    produce();
    return currentEvent.isPresent();
  }

  private void produce() {
    if (!currentEvent.isPresent()) {
      state.ifPresent(production -> currentEvent = Optional.of(production.produce()));
    }
  }

  private CommentEvent produceCommentEvent(CommentToken token) {
    String value = token.getValue();
    CommentType type = token.getCommentType();

    // state = state, that no change in state

    return new CommentEvent(type, value, token.getStartMark(), token.getEndMark());
  }

  @SuppressWarnings("unchecked")
  private VersionTagsTuple processDirectives() {
    Optional<SpecVersion> yamlSpecVersion = Optional.empty();
    HashMap<String, String> tagHandles = new HashMap<>();
    while (scanner.checkToken(Token.ID.Directive)) {
      @SuppressWarnings("rawtypes")
      DirectiveToken token = (DirectiveToken) scanner.next();
      Optional<List<?>> dirOption = token.getValue();
      if (dirOption.isPresent()) {
        // the value must be present
        List<?> directiveValue = dirOption.get();
        if (token.getName().equals(DirectiveToken.YAML_DIRECTIVE)) {
          if (yamlSpecVersion.isPresent()) {
            throw new ParserException("found duplicate YAML directive", token.getStartMark());
          }
          List<Integer> value = (List<Integer>) directiveValue;
          Integer major = value.get(0);
          Integer minor = value.get(1);
          yamlSpecVersion =
              Optional.of(settings.getVersionFunction().apply(new SpecVersion(major, minor)));
        } else if (token.getName().equals(DirectiveToken.TAG_DIRECTIVE)) {
          List<String> value = (List<String>) directiveValue;
          String handle = value.get(0);
          String prefix = value.get(1);
          if (tagHandles.containsKey(handle)) {
            throw new ParserException("duplicate tag handle " + handle, token.getStartMark());
          }
          tagHandles.put(handle, prefix);
        }
      }
    }
    HashMap<String, String> detectedTagHandles = new HashMap<>();
    if (!tagHandles.isEmpty()) {
      // copy from tagHandles
      detectedTagHandles.putAll(tagHandles);
    }
    for (Map.Entry<String, String> entry : DEFAULT_TAGS.entrySet()) {
      // do not overwrite re-defined tags
      if (!tagHandles.containsKey(entry.getKey())) {
        tagHandles.put(entry.getKey(), entry.getValue());
      }
    }
    directiveTags = tagHandles;
    // data for the event (no default tags added)
    return new VersionTagsTuple(yamlSpecVersion, detectedTagHandles);
  }

  private Event parseFlowNode() {
    return parseNode(false, false);
  }

  private Event parseBlockNodeOrIndentlessSequence() {
    return parseNode(true, true);
  }

  private Event parseNode(boolean block, boolean indentlessSequence) {
    Event event;
    Optional<Mark> startMark = Optional.empty();
    Optional<Mark> endMark = Optional.empty();
    Optional<Mark> tagMark = Optional.empty();
    if (scanner.checkToken(Token.ID.Alias)) {
      AliasToken token = (AliasToken) scanner.next();
      event =
          new AliasEvent(Optional.of(token.getValue()), token.getStartMark(), token.getEndMark());
      state = Optional.of(states.pop());
    } else {
      Optional<Anchor> anchor = Optional.empty();
      TagTuple tagTupleValue = null;
      if (scanner.checkToken(Token.ID.Anchor)) {
        AnchorToken token = (AnchorToken) scanner.next();
        startMark = token.getStartMark();
        endMark = token.getEndMark();
        anchor = Optional.of(token.getValue());
        if (scanner.checkToken(Token.ID.Tag)) {
          TagToken tagToken = (TagToken) scanner.next();
          tagMark = tagToken.getStartMark();
          endMark = tagToken.getEndMark();
          tagTupleValue = tagToken.getValue();
        }
      } else if (scanner.checkToken(Token.ID.Tag)) {
        TagToken tagToken = (TagToken) scanner.next();
        startMark = tagToken.getStartMark();
        tagMark = startMark;
        endMark = tagToken.getEndMark();
        tagTupleValue = tagToken.getValue();
        if (scanner.checkToken(Token.ID.Anchor)) {
          AnchorToken token = (AnchorToken) scanner.next();
          endMark = token.getEndMark();
          anchor = Optional.of(token.getValue());
        }
      }
      Optional<String> tag = Optional.empty();
      if (tagTupleValue != null) {
        Optional<String> handleOpt = tagTupleValue.getHandle();
        String suffix = tagTupleValue.getSuffix();
        if (handleOpt.isPresent()) {
          String handle = handleOpt.get();
          if (!directiveTags.containsKey(handle)) {
            throw new ParserException(
                "while parsing a node", startMark, "found undefined tag handle " + handle, tagMark);
          }
          tag = Optional.of(directiveTags.get(handle) + suffix);
        } else {
          tag = Optional.of(suffix);
        }
      }
      if (!startMark.isPresent()) {
        startMark = scanner.peekToken().getStartMark();
        endMark = startMark;
      }
      boolean implicit = (!tag.isPresent());
      if (indentlessSequence && scanner.checkToken(Token.ID.BlockEntry)) {
        endMark = scanner.peekToken().getEndMark();
        event = new SequenceStartEvent(anchor, tag, implicit, FlowStyle.BLOCK, startMark, endMark);
        state = Optional.of(new ParseIndentlessSequenceEntryKey());
      } else {
        if (scanner.checkToken(Token.ID.Scalar)) {
          ScalarToken token = (ScalarToken) scanner.next();
          endMark = token.getEndMark();
          ImplicitTuple implicitValues;
          if ((token.isPlain() && !tag.isPresent())) {
            implicitValues = new ImplicitTuple(true, false);
          } else if (!tag.isPresent()) {
            implicitValues = new ImplicitTuple(false, true);
          } else {
            implicitValues = new ImplicitTuple(false, false);
          }
          event =
              new ScalarEvent(
                  anchor,
                  tag,
                  implicitValues,
                  token.getValue(),
                  token.getStyle(),
                  startMark,
                  endMark);
          state = Optional.of(states.pop());
        } else if (scanner.checkToken(Token.ID.FlowSequenceStart)) {
          endMark = scanner.peekToken().getEndMark();
          event = new SequenceStartEvent(anchor, tag, implicit, FlowStyle.FLOW, startMark, endMark);
          state = Optional.of(new ParseFlowSequenceFirstEntry());
        } else if (scanner.checkToken(Token.ID.FlowMappingStart)) {
          endMark = scanner.peekToken().getEndMark();
          event = new MappingStartEvent(anchor, tag, implicit, FlowStyle.FLOW, startMark, endMark);
          state = Optional.of(new ParseFlowMappingFirstKey());
        } else if (block && scanner.checkToken(Token.ID.BlockSequenceStart)) {
          endMark = scanner.peekToken().getStartMark();
          event =
              new SequenceStartEvent(anchor, tag, implicit, FlowStyle.BLOCK, startMark, endMark);
          state = Optional.of(new ParseBlockSequenceFirstEntry());
        } else if (block && scanner.checkToken(Token.ID.BlockMappingStart)) {
          endMark = scanner.peekToken().getStartMark();
          event = new MappingStartEvent(anchor, tag, implicit, FlowStyle.BLOCK, startMark, endMark);
          state = Optional.of(new ParseBlockMappingFirstKey());
        } else if (anchor.isPresent() || tag.isPresent()) {
          // Empty scalars are allowed even if a tag or an anchor is specified.
          event =
              new ScalarEvent(
                  anchor,
                  tag,
                  new ImplicitTuple(implicit, false),
                  "",
                  ScalarStyle.PLAIN,
                  startMark,
                  endMark);
          state = Optional.of(states.pop());
        } else {
          Token token = scanner.peekToken();
          throw new ParserException(
              "while parsing a " + (block ? "block" : "flow") + " node",
              startMark,
              "expected the node content, but found '" + token.getTokenId() + "'",
              token.getStartMark());
        }
      }
    }
    return event;
  }

  /**
   *
   *
   * <pre>
   * block_mapping     ::= BLOCK-MAPPING_START
   *           ((KEY block_node_or_indentless_sequence?)?
   *           (VALUE block_node_or_indentless_sequence?)?)*
   *           BLOCK-END
   * </pre>
   */
  private Event processEmptyScalar(Optional<Mark> mark) {
    return new ScalarEvent(
        Optional.empty(),
        Optional.empty(),
        new ImplicitTuple(true, false),
        "",
        ScalarStyle.PLAIN,
        mark,
        mark);
  }

  private Optional<Mark> markPop() {
    return marksStack.pop();
  }

  private void markPush(Optional<Mark> mark) {
    marksStack.push(mark);
  }

  private class ParseStreamStart implements Production {

    public Event produce() {
      // Parse the stream start.
      StreamStartToken token = (StreamStartToken) scanner.next();
      Event event = new StreamStartEvent(token.getStartMark(), token.getEndMark());
      // Prepare the next state.
      state = Optional.of(new ParseImplicitDocumentStart());
      return event;
    }
  }

  private class ParseImplicitDocumentStart implements Production {

    public Event produce() {
      if (scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(new ParseImplicitDocumentStart());
        return produceCommentEvent((CommentToken) scanner.next());
      }
      if (!scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd)) {
        // Parse an implicit document.
        Token token = scanner.peekToken();
        Optional<Mark> startMark = token.getStartMark();
        Event event =
            new DocumentStartEvent(
                false, Optional.empty(), Collections.emptyMap(), startMark, startMark);
        // Prepare the next state.
        states.push(new ParseDocumentEnd());
        state = Optional.of(new ParseBlockNode());
        return event;
      } else {
        // explicit document detected
        return new ParseDocumentStart().produce();
      }
    }
  }

  private class ParseDocumentStart implements Production {

    public Event produce() {
      if (scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(new ParseDocumentStart());
        return produceCommentEvent((CommentToken) scanner.next());
      }
      // Parse any extra document end indicators.
      while (scanner.checkToken(Token.ID.DocumentEnd)) {
        scanner.next();
      }
      if (scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(new ParseDocumentStart());
        return produceCommentEvent((CommentToken) scanner.next());
      }
      // Parse an explicit document.
      Event event;
      if (!scanner.checkToken(Token.ID.StreamEnd)) {
        Token token = scanner.peekToken();
        Optional<Mark> startMark = token.getStartMark();
        VersionTagsTuple tuple = processDirectives();
        while (scanner.checkToken(Token.ID.Comment)) {
          scanner.next();
        }
        if (!scanner.checkToken(Token.ID.StreamEnd)) {
          if (!scanner.checkToken(Token.ID.DocumentStart)) {
            throw new ParserException(
                "expected '<document start>', but found '" + scanner.peekToken().getTokenId() + "'",
                scanner.peekToken().getStartMark());
          }
          token = scanner.next();
          Optional<Mark> endMark = token.getEndMark();
          event =
              new DocumentStartEvent(
                  true, tuple.getSpecVersion(), tuple.getTags(), startMark, endMark);
          states.push(new ParseDocumentEnd());
          state = Optional.of(new ParseDocumentContent());
          return event;
        } else {
          throw new ParserException(
              "expected '<document start>', but found '" + scanner.peekToken().getTokenId() + "'",
              scanner.peekToken().getStartMark());
        }
      }
      // Parse the end of the stream.
      StreamEndToken token = (StreamEndToken) scanner.next();
      event = new StreamEndEvent(token.getStartMark(), token.getEndMark());
      if (!states.isEmpty()) {
        throw new YamlEngineException("Unexpected end of stream. States left: " + states);
      }
      if (!markEmpty()) {
        throw new YamlEngineException("Unexpected end of stream. Marks left: " + marksStack);
      }
      state = Optional.empty();
      return event;
    }

    private boolean markEmpty() {
      return marksStack.isEmpty();
    }
  }

  // block_sequence ::= BLOCK-SEQUENCE-START (BLOCK-ENTRY block_node?)*
  // BLOCK-END

  private class ParseDocumentEnd implements Production {

    public Event produce() {
      // Parse the document end.
      Token token = scanner.peekToken();
      Optional<Mark> startMark = token.getStartMark();
      Optional<Mark> endMark = startMark;
      boolean explicit = false;
      if (scanner.checkToken(Token.ID.DocumentEnd)) {
        token = scanner.next();
        endMark = token.getEndMark();
        explicit = true;
      } else if (scanner.checkToken(Token.ID.Directive)) {
        throw new ParserException(
            "expected '<document end>' before directives, but found '"
                + scanner.peekToken().getTokenId()
                + "'",
            scanner.peekToken().getStartMark());
      }
      directiveTags.clear(); // directive tags do not survive between the documents
      scanner.resetDocumentIndex();
      Event event = new DocumentEndEvent(explicit, startMark, endMark);
      // Prepare the next state.
      state = Optional.of(new ParseDocumentStart());
      return event;
    }
  }

  private class ParseDocumentContent implements Production {

    public Event produce() {
      if (scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(new ParseDocumentContent());
        return produceCommentEvent((CommentToken) scanner.next());
      }
      if (scanner.checkToken(
          Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd)) {
        Event event = processEmptyScalar(scanner.peekToken().getStartMark());
        state = Optional.of(states.pop());
        return event;
      } else {
        return new ParseBlockNode().produce();
      }
    }
  }

  /**
   *
   *
   * <pre>
   *  block_node_or_indentless_sequence ::= ALIAS
   *                | properties (block_content | indentless_block_sequence)?
   *                | block_content
   *                | indentless_block_sequence
   *  block_node    ::= ALIAS
   *                    | properties block_content?
   *                    | block_content
   *  flow_node     ::= ALIAS
   *                    | properties flow_content?
   *                    | flow_content
   *  properties    ::= TAG ANCHOR? | ANCHOR TAG?
   *  block_content     ::= block_collection | flow_collection | SCALAR
   *  flow_content      ::= flow_collection | SCALAR
   *  block_collection  ::= block_sequence | block_mapping
   *  flow_collection   ::= flow_sequence | flow_mapping
   * </pre>
   */
  private class ParseBlockNode implements Production {

    public Event produce() {
      return parseNode(true, false);
    }
  }

  // indentless_sequence ::= (BLOCK-ENTRY block_node?)+

  private class ParseBlockSequenceFirstEntry implements Production {

    public Event produce() {
      Token token = scanner.next();
      markPush(token.getStartMark());
      return new ParseBlockSequenceEntryKey().produce();
    }
  }

  private class ParseBlockSequenceEntryKey implements Production {

    public Event produce() {
      if (scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(new ParseBlockSequenceEntryKey());
        return produceCommentEvent((CommentToken) scanner.next());
      }
      if (scanner.checkToken(Token.ID.BlockEntry)) {
        BlockEntryToken token = (BlockEntryToken) scanner.next();
        return new ParseBlockSequenceEntryValue(token).produce();
      }
      if (!scanner.checkToken(Token.ID.BlockEnd)) {
        Token token = scanner.peekToken();
        throw new ParserException(
            "while parsing a block collection",
            markPop(),
            "expected <block end>, but found '" + token.getTokenId() + "'",
            token.getStartMark());
      }
      Token token = scanner.next();
      Event event = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
      state = Optional.of(states.pop());
      markPop();
      return event;
    }
  }

  private class ParseBlockSequenceEntryValue implements Production {

    BlockEntryToken token;

    public ParseBlockSequenceEntryValue(final BlockEntryToken token) {
      this.token = token;
    }

    public Event produce() {
      if (scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(new ParseBlockSequenceEntryValue(token));
        return produceCommentEvent((CommentToken) scanner.next());
      }
      if (!scanner.checkToken(Token.ID.BlockEntry, Token.ID.BlockEnd)) {
        states.push(new ParseBlockSequenceEntryKey());
        return new ParseBlockNode().produce();
      } else {
        state = Optional.of(new ParseBlockSequenceEntryKey());
        return processEmptyScalar(token.getEndMark());
      }
    }
  }

  private class ParseIndentlessSequenceEntryKey implements Production {

    public Event produce() {
      if (scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(new ParseIndentlessSequenceEntryKey());
        return produceCommentEvent((CommentToken) scanner.next());
      }
      if (scanner.checkToken(Token.ID.BlockEntry)) {
        BlockEntryToken token = (BlockEntryToken) scanner.next();
        return new ParseIndentlessSequenceEntryValue(token).produce();
      }
      Token token = scanner.peekToken();
      Event event = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
      state = Optional.of(states.pop());
      return event;
    }
  }

  private class ParseIndentlessSequenceEntryValue implements Production {

    BlockEntryToken token;

    public ParseIndentlessSequenceEntryValue(final BlockEntryToken token) {
      this.token = token;
    }

    public Event produce() {
      if (scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(new ParseIndentlessSequenceEntryValue(token));
        return produceCommentEvent((CommentToken) scanner.next());
      }
      if (!scanner.checkToken(
          Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
        states.push(new ParseIndentlessSequenceEntryKey());
        return new ParseBlockNode().produce();
      } else {
        state = Optional.of(new ParseIndentlessSequenceEntryKey());
        return processEmptyScalar(token.getEndMark());
      }
    }
  }

  private class ParseBlockMappingFirstKey implements Production {

    public Event produce() {
      Token token = scanner.next();
      markPush(token.getStartMark());
      return new ParseBlockMappingKey().produce();
    }
  }

  private class ParseBlockMappingKey implements Production {

    public Event produce() {
      if (scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(new ParseBlockMappingKey());
        return produceCommentEvent((CommentToken) scanner.next());
      }
      if (scanner.checkToken(Token.ID.Key)) {
        Token token = scanner.next();
        if (!scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
          states.push(new ParseBlockMappingValue());
          return parseBlockNodeOrIndentlessSequence();
        } else {
          state = Optional.of(new ParseBlockMappingValue());
          return processEmptyScalar(token.getEndMark());
        }
      }
      if (!scanner.checkToken(Token.ID.BlockEnd)) {
        Token token = scanner.peekToken();
        throw new ParserException(
            "while parsing a block mapping",
            markPop(),
            "expected <block end>, but found '" + token.getTokenId() + "'",
            token.getStartMark());
      }
      Token token = scanner.next();
      Event event = new MappingEndEvent(token.getStartMark(), token.getEndMark());
      state = Optional.of(states.pop());
      markPop();
      return event;
    }
  }

  private class ParseBlockMappingValue implements Production {

    public Event produce() {
      if (scanner.checkToken(Token.ID.Value)) {
        Token token = scanner.next();
        if (scanner.checkToken(Token.ID.Comment)) {
          Production p = new ParseBlockMappingValueComment();
          state = Optional.of(p);
          return p.produce();
        } else if (!scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
          states.push(new ParseBlockMappingKey());
          return parseBlockNodeOrIndentlessSequence();
        } else {
          state = Optional.of(new ParseBlockMappingKey());
          return processEmptyScalar(token.getEndMark());
        }
      } else if (scanner.checkToken(Token.ID.Scalar)) {
        states.push(new ParseBlockMappingKey());
        return parseBlockNodeOrIndentlessSequence();
      }
      state = Optional.of(new ParseBlockMappingKey());
      Token token = scanner.peekToken();
      return processEmptyScalar(token.getStartMark());
    }
  }

  private class ParseBlockMappingValueComment implements Production {

    List<CommentToken> tokens = new LinkedList<>();

    public Event produce() {
      if (scanner.checkToken(Token.ID.Comment)) {
        tokens.add((CommentToken) scanner.next());
        return produce();
      } else if (!scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
        if (!tokens.isEmpty()) {
          return produceCommentEvent(tokens.remove(0));
        }
        states.push(new ParseBlockMappingKey());
        return parseBlockNodeOrIndentlessSequence();
      } else {
        state = Optional.of(new ParseBlockMappingValueCommentList(tokens));
        return processEmptyScalar(scanner.peekToken().getStartMark());
      }
    }
  }

  private class ParseBlockMappingValueCommentList implements Production {

    List<CommentToken> tokens;

    public ParseBlockMappingValueCommentList(final List<CommentToken> tokens) {
      this.tokens = tokens;
    }

    public Event produce() {
      if (!tokens.isEmpty()) {
        return produceCommentEvent(tokens.remove(0));
      }
      return new ParseBlockMappingKey().produce();
    }
  }

  /**
   *
   *
   * <pre>
   * flow_sequence     ::= FLOW-SEQUENCE-START
   *                       (flow_sequence_entry FLOW-ENTRY)*
   *                       flow_sequence_entry?
   *                       FLOW-SEQUENCE-END
   * flow_sequence_entry   ::= flow_node | KEY flow_node? (VALUE flow_node?)?
   * Note that while production rules for both flow_sequence_entry and
   * flow_mapping_entry are equal, their interpretations are different.
   * For `flow_sequence_entry`, the part `KEY flow_node? (VALUE flow_node?)?`
   * generate an inline mapping (set syntax).
   * </pre>
   */
  private class ParseFlowSequenceFirstEntry implements Production {

    public Event produce() {
      Token token = scanner.next();
      markPush(token.getStartMark());
      return new ParseFlowSequenceEntry(true).produce();
    }
  }

  private class ParseFlowSequenceEntry implements Production {

    private final boolean first;

    public ParseFlowSequenceEntry(boolean first) {
      this.first = first;
    }

    public Event produce() {
      if (scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(new ParseFlowSequenceEntry(first));
        return produceCommentEvent((CommentToken) scanner.next());
      }
      if (!scanner.checkToken(Token.ID.FlowSequenceEnd)) {
        if (!first) {
          if (scanner.checkToken(Token.ID.FlowEntry)) {
            scanner.next();
            if (scanner.checkToken(Token.ID.Comment)) {
              state = Optional.of(new ParseFlowSequenceEntry(true));
              return produceCommentEvent((CommentToken) scanner.next());
            }
          } else {
            Token token = scanner.peekToken();
            throw new ParserException(
                "while parsing a flow sequence",
                markPop(),
                "expected ',' or ']', but got " + token.getTokenId(),
                token.getStartMark());
          }
        }
        if (scanner.checkToken(Token.ID.Key)) {
          Token token = scanner.peekToken();
          Event event =
              new MappingStartEvent(
                  Optional.empty(),
                  Optional.empty(),
                  true,
                  FlowStyle.FLOW,
                  token.getStartMark(),
                  token.getEndMark());
          state = Optional.of(new ParseFlowSequenceEntryMappingKey());
          return event;
        } else if (!scanner.checkToken(Token.ID.FlowSequenceEnd)) {
          states.push(new ParseFlowSequenceEntry(false));
          return parseFlowNode();
        }
      }
      Token token = scanner.next();
      Event event = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
      if (!scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(states.pop());
      } else {
        state = Optional.of(new ParseFlowEndComment());
      }
      markPop();
      return event;
    }
  }

  private class ParseFlowEndComment implements Production {

    public Event produce() {
      Event event = produceCommentEvent((CommentToken) scanner.next());
      if (!scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(states.pop());
      }
      return event;
    }
  }

  private class ParseFlowSequenceEntryMappingKey implements Production {

    public Event produce() {
      Token token = scanner.next();
      if (!scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
        states.push(new ParseFlowSequenceEntryMappingValue());
        return parseFlowNode();
      } else {
        state = Optional.of(new ParseFlowSequenceEntryMappingValue());
        return processEmptyScalar(token.getEndMark());
      }
    }
  }

  private class ParseFlowSequenceEntryMappingValue implements Production {

    public Event produce() {
      if (scanner.checkToken(Token.ID.Value)) {
        Token token = scanner.next();
        if (!scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
          states.push(new ParseFlowSequenceEntryMappingEnd());
          return parseFlowNode();
        } else {
          state = Optional.of(new ParseFlowSequenceEntryMappingEnd());
          return processEmptyScalar(token.getEndMark());
        }
      } else {
        state = Optional.of(new ParseFlowSequenceEntryMappingEnd());
        Token token = scanner.peekToken();
        return processEmptyScalar(token.getStartMark());
      }
    }
  }

  private class ParseFlowSequenceEntryMappingEnd implements Production {

    public Event produce() {
      state = Optional.of(new ParseFlowSequenceEntry(false));
      Token token = scanner.peekToken();
      return new MappingEndEvent(token.getStartMark(), token.getEndMark());
    }
  }

  /**
   *
   *
   * <pre>
   *   flow_mapping  ::= FLOW-MAPPING-START
   *          (flow_mapping_entry FLOW-ENTRY)*
   *          flow_mapping_entry?
   *          FLOW-MAPPING-END
   *   flow_mapping_entry    ::= flow_node | KEY flow_node? (VALUE flow_node?)?
   * </pre>
   */
  private class ParseFlowMappingFirstKey implements Production {

    public Event produce() {
      Token token = scanner.next();
      markPush(token.getStartMark());
      return new ParseFlowMappingKey(true).produce();
    }
  }

  private class ParseFlowMappingKey implements Production {

    private final boolean first;

    public ParseFlowMappingKey(boolean first) {
      this.first = first;
    }

    public Event produce() {
      if (!scanner.checkToken(Token.ID.FlowMappingEnd)) {
        if (!first) {
          if (scanner.checkToken(Token.ID.FlowEntry)) {
            scanner.next();
          } else {
            Token token = scanner.peekToken();
            throw new ParserException(
                "while parsing a flow mapping",
                markPop(),
                "expected ',' or '}', but got " + token.getTokenId(),
                token.getStartMark());
          }
        }
        if (scanner.checkToken(Token.ID.Key)) {
          Token token = scanner.next();
          if (!scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
            states.push(new ParseFlowMappingValue());
            return parseFlowNode();
          } else {
            state = Optional.of(new ParseFlowMappingValue());
            return processEmptyScalar(token.getEndMark());
          }
        } else if (!scanner.checkToken(Token.ID.FlowMappingEnd)) {
          states.push(new ParseFlowMappingEmptyValue());
          return parseFlowNode();
        }
      }
      Token token = scanner.next();
      Event event = new MappingEndEvent(token.getStartMark(), token.getEndMark());
      markPop();
      if (!scanner.checkToken(Token.ID.Comment)) {
        state = Optional.of(states.pop());
      } else {
        state = Optional.of(new ParseFlowEndComment());
      }
      return event;
    }
  }

  private class ParseFlowMappingValue implements Production {

    public Event produce() {
      if (scanner.checkToken(Token.ID.Value)) {
        Token token = scanner.next();
        if (!scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
          states.push(new ParseFlowMappingKey(false));
          return parseFlowNode();
        } else {
          state = Optional.of(new ParseFlowMappingKey(false));
          return processEmptyScalar(token.getEndMark());
        }
      } else {
        state = Optional.of(new ParseFlowMappingKey(false));
        Token token = scanner.peekToken();
        return processEmptyScalar(token.getStartMark());
      }
    }
  }

  private class ParseFlowMappingEmptyValue implements Production {

    public Event produce() {
      state = Optional.of(new ParseFlowMappingKey(false));
      return processEmptyScalar(scanner.peekToken().getStartMark());
    }
  }
}
