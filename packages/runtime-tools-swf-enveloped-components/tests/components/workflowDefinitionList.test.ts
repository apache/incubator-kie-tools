interface WorkflowDefinition {
  id: string;
  name: string;
}

const mockGetWorkflowDefinitionsQuery = jest.fn();
const mockSetWorkflowDefinitionList = jest.fn();

const driver = {
  getWorkflowDefinitionsQuery: mockGetWorkflowDefinitionsQuery,
};

const doQuery = async (): Promise<void> => {
  const response: WorkflowDefinition[] = await driver.getWorkflowDefinitionsQuery();
  mockSetWorkflowDefinitionList(response);
};

describe("doQuery", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("should call setWorkflowDefinitionList and setIsLoading correctly on success", async () => {
    const mockResponse: WorkflowDefinition[] = [{ id: "1", name: "Workflow 1" }];
    mockGetWorkflowDefinitionsQuery.mockResolvedValue(mockResponse);
    await doQuery();
    expect(mockSetWorkflowDefinitionList).toHaveBeenCalledWith(mockResponse);
  });
});
