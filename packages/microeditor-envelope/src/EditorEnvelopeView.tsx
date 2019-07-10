import * as React from "react";
import * as ReactDOM from "react-dom";
import * as AppFormer from "appformer-js-core";
import { LoadingScreen } from "./LoadingScreen";

interface Props {
  exposing: (self: EditorEnvelopeView) => void;
  loadingScreenContainer: HTMLElement;
}

interface State {
  editor?: AppFormer.Editor;
  loading: boolean;
}

export class EditorEnvelopeView extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { editor: undefined, loading: true };
    this.props.exposing(this);
  }

  public getEditor() {
    return this.state.editor;
  }

  public setEditor(editor: AppFormer.Editor) {
    return new Promise(res => this.setState({ editor: editor }, res));
  }

  public setLoadingFinished() {
    return new Promise(res => this.setState({ loading: false }, res));
  }

  private LoadingScreenPortal() {
    return ReactDOM.createPortal(<LoadingScreen visible={this.state.loading} />, this.props.loadingScreenContainer!);
  }

  public render() {
    return (
      <>
        {this.LoadingScreenPortal()}
        {this.state.editor && this.state.editor.af_isReact && this.state.editor.af_componentRoot()}
      </>
    );
  }
}
