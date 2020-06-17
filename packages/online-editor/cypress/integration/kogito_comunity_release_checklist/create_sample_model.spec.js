context('Check sample models successfully created', () => {

  beforeEach(() => {
    // visit home page of the Online Editor
    cy.visit('/')
  })

  it('Try BPMN sample', () => {
    cy.trySampleModel('bpmn')

    cy.getDiagramEditorBody().find('[data-title="Explore Diagram"]').click();
    cy.getDiagramEditorBody()
      .find('[data-field="explorerPanelBody"]')
      .contains("Process travelers")
  })

  it('Try DMN sample', () => {
    cy.trySampleModel('dmn')

    cy.getDiagramEditorBody().find('.fa-chevron-right').click();
    cy.getDiagramEditorBody()
      .find('li[data-i18n-prefix="DecisionNavigatorTreeView."]')
      .should("have.attr", "title", "loan_pre_qualification");

  })
})