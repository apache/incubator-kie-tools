import * as AppFormer from "appformer-js-core";
import * as React from "react";

export class DummyEditor extends AppFormer.Editor {
  private ref: DummyEditorComponent;

  constructor() {
    super("dummy-editor");
    this.af_componentTitle = "Dummy Editor";
    this.af_isReact = true;
  }

  public af_componentRoot() {
    return <DummyEditorComponent exposing={self => (this.ref = self)} />;
  }

  public getContent() {
    return this.ref!.getContent();
  }

  public isDirty(): boolean {
    return false;
  }

  public setContent(content: string) {
    return this.ref!.setContent(content);
  }
}

interface Props {
  exposing: (self: DummyEditorComponent) => void;
}

interface State {
  content: string;
}

class DummyEditorComponent extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { content: "" };
    this.props.exposing(this);
  }

  public getContent() {
    return Promise.resolve(this.state.content);
  }

  public setContent(content: string) {
    return new Promise<void>(res => this.setState({ content: content }, res));
  }

  public render() {
    return <div>Here's the dummy content: {this.state.content}</div>;
  }
}
