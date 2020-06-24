export class HomePage {
  public openNewBpmnDiagramButtonSelector(): string {
    return this.articleComponent('create-new-bpmn');
  }

  public openSampleBpmnDiagramButtonSelector(): string {
    return this.articleComponent('create-sample-bpmn');
  }

  public openNewDmnDiagramButtonSelector(): string {
    return this.articleComponent('create-new-dmn');
  }

  public openSampleDmnDiagramButtonSelector(): string {
    return this.articleComponent('create-sample-dmn');
  }

  private articleComponent = (componentId) => {
    return `//article[@data-ouia-component-id = \'${componentId}\']`
  }
}