export class HomePageSelector {
    public openNewBpmnDiagramButtonSelector(): string {
      return this.articleComponent('create-new-bpmn');
    }
  
    public openSampleBpmnDiagramButtonSelector(): string {
      return this.articleComponent('open-sample-bpmn');
    }
  
    public openNewDmnDiagramButtonSelector(): string {
      return this.articleComponent('create-new-dmn');
    }
  
    public openSampleDmnDiagramButtonSelector(): string {
      return this.articleComponent('open-sample-dmn');
    }
  
    private articleComponent = (componentId: string) => {
      return `//article[@data-testid=\'${componentId}\']`
    }
  } 