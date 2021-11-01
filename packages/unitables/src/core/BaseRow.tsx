import * as React from "react";
import { BaseForm, BaseFormProps, BaseFormState, context as UniformsContext } from "uniforms";

interface Props<Model> extends BaseFormProps<Model> {
  children: React.ReactElement;
}

export class BaseRow<Model> extends BaseForm<Model, Props<Model>, BaseFormState<Model>> {
  constructor(props: Props<Model>) {
    super(props);
  }

  render() {
    return <UniformsContext.Provider value={{ ...this.getContext() }}>{this.props.children}</UniformsContext.Provider>;
  }
}
