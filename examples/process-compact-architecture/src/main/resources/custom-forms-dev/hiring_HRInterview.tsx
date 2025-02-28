import React, { useCallback, useEffect, useState } from "react";
import { Checkbox, FormGroup, Card, CardBody, TextInput, Split, SplitItem, Button } from "@patternfly/react-core";
import { PlusCircleIcon, MinusCircleIcon } from "@patternfly/react-icons";
const Form__hiring_HRInterview: React.FC<any> = (props: any) => {
  const [formApi, setFormApi] = useState<any>();
  const [approve, set__approve] = useState<boolean>(false);
  const [candidate__email, set__candidate__email] = useState<string>("");
  const [candidate__experience, set__candidate__experience] = useState<number>();
  const [candidate__lastName, set__candidate__lastName] = useState<string>("");
  const [candidate__name, set__candidate__name] = useState<string>("");
  const [candidate__offers, set__candidate__offers] = useState<object[]>([]);
  const [candidate__offers__$__category, set__candidate__offers__$__category] = useState<string>("");
  const set__candidate_offers__salary = useCallback((newValue, index) => {
    set__candidate__offers((prev) => {
      const newState = [...prev];
      newState[index] ??= [];
      newState[index].salary = newValue;
      return newState;
    });
  }, []);
  const [candidate__skills, set__candidate__skills] = useState<string[]>([]);
  const [candidate__skills__$, set__candidate__skills__$] = useState<string>("");
  const [offer__category, set__offer__category] = useState<string>("");
  const [offer__salary, set__offer__salary] = useState<object[]>([]);
  const [offer__salary__$__currency, set__offer__salary__$__currency] = useState<string>("");
  const [offer__salary__$__value, set__offer__salary__$__value] = useState<string>("");
  /* Utility function that fills the form with the data received from the kogito runtime */
  const setFormData = (data) => {
    if (!data) {
      return;
    }
    set__approve(data?.approve ?? false);
    set__candidate__email(data?.candidate?.email ?? "");
    set__candidate__experience(data?.candidate?.experience);
    set__candidate__lastName(data?.candidate?.lastName ?? "");
    set__candidate__name(data?.candidate?.name ?? "");
    set__candidate__offers(data?.candidate?.offers ?? []);
    set__candidate__skills(data?.candidate?.skills ?? []);
    set__offer__category(data?.offer?.category ?? "");
    set__offer__salary(data?.offer?.salary ?? []);
  };
  /* Utility function to generate the expected form output as a json object */
  const getFormData = useCallback(() => {
    const formData: any = {};
    formData.approve = approve;
    formData.offer = {};
    formData.offer.category = offer__category;
    formData.offer.salary = offer__salary;
    return formData;
  }, [approve, offer__category, offer__salary]);
  /* Utility function to validate the form on the 'beforeSubmit' Lifecycle Hook */
  const validateForm = useCallback(() => {}, []);
  /* Utility function to perform actions on the on the 'afterSubmit' Lifecycle Hook */
  const afterSubmit = useCallback((result) => {}, []);
  useEffect(() => {
    if (formApi) {
      /*
        Form Lifecycle Hook that will be executed before the form is submitted.
        Throwing an error will stop the form submit. Usually should be used to validate the form.
      */
      formApi.beforeSubmit = () => validateForm();
      /*
        Form Lifecycle Hook that will be executed after the form is submitted.
        It will receive a response object containing the `type` flag indicating if the submit has been successful and `info` with extra information about the submit result.
      */
      formApi.afterSubmit = (result) => afterSubmit(result);
      /* Generates the expected form output object to be posted */
      formApi.getFormData = () => getFormData();
    }
  }, [getFormData, validateForm, afterSubmit]);
  useEffect(() => {
    /*
      Call to the Kogito console form engine. It will establish the connection with the console embeding the form
      and return an instance of FormAPI that will allow hook custom code into the form lifecycle.
      The `window.Form.openForm` call expects an object with the following entries:
        - onOpen: Callback that will be called after the connection with the console is established. The callback
        will receive the following arguments:
          - data: the data to be bound into the form
          - ctx: info about the context where the form is being displayed. This will contain information such as the form JSON Schema, process/task, user...
    */
    const api = window.Form.openForm({
      onOpen: (data, context) => {
        setFormData(data);
      },
    });
    setFormApi(api);
  }, []);
  return (
    <div className={"pf-c-form"}>
      <FormGroup fieldId="uniforms-0002-0001">
        <Checkbox
          isChecked={approve}
          isDisabled={false}
          id={"uniforms-0002-0001"}
          name={"approve"}
          label={"Approve"}
          onChange={set__approve}
        />
      </FormGroup>
      <Card>
        <CardBody className="pf-c-form">
          <label>
            <b>Candidate</b>
          </label>
          <FormGroup fieldId={"uniforms-0002-0004"} label={"Email"} isRequired={false}>
            <TextInput
              name={"candidate.email"}
              id={"uniforms-0002-0004"}
              isDisabled={true}
              placeholder={""}
              type={"text"}
              value={candidate__email}
              onChange={set__candidate__email}
            />
          </FormGroup>
          <FormGroup fieldId={"uniforms-0002-0006"} label={"Experience"} isRequired={false}>
            <TextInput
              type={"number"}
              name={"candidate.experience"}
              isDisabled={true}
              id={"uniforms-0002-0006"}
              placeholder={""}
              step={1}
              value={candidate__experience}
              onChange={(newValue) => set__candidate__experience(Number(newValue))}
            />
          </FormGroup>
          <FormGroup fieldId={"uniforms-0002-0007"} label={"Last name"} isRequired={false}>
            <TextInput
              name={"candidate.lastName"}
              id={"uniforms-0002-0007"}
              isDisabled={true}
              placeholder={""}
              type={"text"}
              value={candidate__lastName}
              onChange={set__candidate__lastName}
            />
          </FormGroup>
          <FormGroup fieldId={"uniforms-0002-0008"} label={"Name"} isRequired={false}>
            <TextInput
              name={"candidate.name"}
              id={"uniforms-0002-0008"}
              isDisabled={true}
              placeholder={""}
              type={"text"}
              value={candidate__name}
              onChange={set__candidate__name}
            />
          </FormGroup>
          <div>
            <Split hasGutter>
              <SplitItem>
                {"Offers" && (
                  <label className={"pf-c-form__label"}>
                    <span className={"pf-c-form__label-text"}>Offers</span>
                  </label>
                )}
              </SplitItem>
              <SplitItem isFilled />
              <SplitItem>
                <Button
                  name="$"
                  variant="plain"
                  style={{ paddingLeft: "0", paddingRight: "0" }}
                  disabled={true}
                  onClick={() => {
                    !true && set__candidate__offers((candidate__offers ?? []).concat([{}]));
                  }}
                >
                  <PlusCircleIcon color="#0088ce" />
                </Button>
              </SplitItem>
            </Split>
            <div>
              {candidate__offers?.map((_, itemIndex) => (
                <div
                  key={itemIndex}
                  style={{
                    marginBottom: "1rem",
                    display: "flex",
                    justifyContent: "space-between",
                  }}
                >
                  <div style={{ width: "100%", marginRight: "10px" }}>
                    <Card>
                      <CardBody className="pf-c-form">
                        <FormGroup fieldId={"uniforms-0002-000d"} label={"Category"} isRequired={false}>
                          <TextInput
                            name={`candidate__offers.${itemIndex}.category`}
                            id={"uniforms-0002-000d"}
                            isDisabled={true}
                            placeholder={""}
                            type={"text"}
                            value={candidate__offers?.[itemIndex].category}
                            onChange={(newValue) => {
                              set__candidate__offers((s) => {
                                const newState = [...s];
                                newState[itemIndex].category = newValue;
                                return newState;
                              });
                            }}
                          />
                        </FormGroup>
                        <div>
                          <Split hasGutter>
                            <SplitItem>
                              {"Salary" && (
                                <label className={"pf-c-form__label"}>
                                  <span className={"pf-c-form__label-text"}>Salary</span>
                                </label>
                              )}
                            </SplitItem>
                            <SplitItem isFilled />
                            <SplitItem>
                              <Button
                                name="$"
                                variant="plain"
                                style={{ paddingLeft: "0", paddingRight: "0" }}
                                disabled={true}
                                onClick={() => {
                                  !true &&
                                    set__candidate_offers__salary((candidate__offers ?? [], itemIndex).concat([{}]));
                                }}
                              >
                                <PlusCircleIcon color="#0088ce" />
                              </Button>
                            </SplitItem>
                          </Split>
                          <div>
                            {candidate__offers?.map((_, nested__itemIndex) => (
                              <div
                                key={nested__itemIndex}
                                style={{
                                  marginBottom: "1rem",
                                  display: "flex",
                                  justifyContent: "space-between",
                                }}
                              >
                                <div
                                  style={{
                                    width: "100%",
                                    marginRight: "10px",
                                  }}
                                >
                                  <Card>
                                    <CardBody className="pf-c-form">
                                      <FormGroup fieldId={"uniforms-0002-000i"} label={"Currency"} isRequired={false}>
                                        <TextInput
                                          name={`candidate__offers.${nested__itemIndex}.currency`}
                                          id={"uniforms-0002-000i"}
                                          isDisabled={true}
                                          placeholder={""}
                                          type={"text"}
                                          value={candidate__offers?.[nested__itemIndex].currency}
                                          onChange={(newValue) =>
                                            set__candidate_offers__salary(newValue, nested__itemIndex)
                                          }
                                        />
                                      </FormGroup>
                                      <FormGroup fieldId={"uniforms-0002-000j"} label={"Value"} isRequired={false}>
                                        <TextInput
                                          name={`candidate__offers.${nested__itemIndex}.value`}
                                          id={"uniforms-0002-000j"}
                                          isDisabled={true}
                                          placeholder={""}
                                          type={"text"}
                                          value={candidate__offers?.[nested__itemIndex].value}
                                          onChange={(newValue) =>
                                            set__candidate_offers__salary(newValue, nested__itemIndex)
                                          }
                                        />
                                      </FormGroup>
                                    </CardBody>
                                  </Card>
                                </div>
                                <div>
                                  <Button
                                    disabled={true}
                                    variant="plain"
                                    style={{
                                      paddingLeft: "0",
                                      paddingRight: "0",
                                    }}
                                    onClick={() => {
                                      const value = [...candidate__offers];
                                      value.splice(nested__itemIndex, 1);
                                      !true && set__candidate_offers__salary(value);
                                    }}
                                  >
                                    <MinusCircleIcon color="#cc0000" />
                                  </Button>
                                </div>
                              </div>
                            ))}
                          </div>
                        </div>
                      </CardBody>
                    </Card>
                  </div>
                  <div>
                    <Button
                      disabled={true}
                      variant="plain"
                      style={{ paddingLeft: "0", paddingRight: "0" }}
                      onClick={() => {
                        const value = [...candidate__offers];
                        value.splice(itemIndex, 1);
                        !true && set__candidate__offers(value);
                      }}
                    >
                      <MinusCircleIcon color="#cc0000" />
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </div>
          <div>
            <Split hasGutter>
              <SplitItem>
                {"Skills" && (
                  <label className={"pf-c-form__label"}>
                    <span className={"pf-c-form__label-text"}>Skills</span>
                  </label>
                )}
              </SplitItem>
              <SplitItem isFilled />
              <SplitItem>
                <Button
                  name="$"
                  variant="plain"
                  style={{ paddingLeft: "0", paddingRight: "0" }}
                  disabled={true}
                  onClick={() => {
                    !true && set__candidate__skills((candidate__skills ?? []).concat([""]));
                  }}
                >
                  <PlusCircleIcon color="#0088ce" />
                </Button>
              </SplitItem>
            </Split>
            <div>
              {candidate__skills?.map((_, itemIndex) => (
                <div
                  key={itemIndex}
                  style={{
                    marginBottom: "1rem",
                    display: "flex",
                    justifyContent: "space-between",
                  }}
                >
                  <div style={{ width: "100%", marginRight: "10px" }}>
                    <FormGroup fieldId={"uniforms-0002-000m"} label={""} isRequired={false}>
                      <TextInput
                        name={`candidate__skills.${itemIndex}`}
                        id={"uniforms-0002-000m"}
                        isDisabled={true}
                        placeholder={""}
                        type={"text"}
                        value={candidate__skills?.[itemIndex]}
                        onChange={(newValue) => {
                          set__candidate__skills((s) => {
                            const newState = [...s];
                            newState[itemIndex] = newValue;
                            return newState;
                          });
                        }}
                      />
                    </FormGroup>
                  </div>
                  <div>
                    <Button
                      disabled={true}
                      variant="plain"
                      style={{ paddingLeft: "0", paddingRight: "0" }}
                      onClick={() => {
                        const value = [...candidate__skills];
                        value.splice(itemIndex, 1);
                        !true && set__candidate__skills(value);
                      }}
                    >
                      <MinusCircleIcon color="#cc0000" />
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </CardBody>
      </Card>
      <Card>
        <CardBody className="pf-c-form">
          <label>
            <b>Offer</b>
          </label>
          <FormGroup fieldId={"uniforms-0002-000p"} label={"Category"} isRequired={false}>
            <TextInput
              name={"offer.category"}
              id={"uniforms-0002-000p"}
              isDisabled={false}
              placeholder={""}
              type={"text"}
              value={offer__category}
              onChange={set__offer__category}
            />
          </FormGroup>
          <div>
            <Split hasGutter>
              <SplitItem>
                {"Salary" && (
                  <label className={"pf-c-form__label"}>
                    <span className={"pf-c-form__label-text"}>Salary</span>
                  </label>
                )}
              </SplitItem>
              <SplitItem isFilled />
              <SplitItem>
                <Button
                  name="$"
                  variant="plain"
                  style={{ paddingLeft: "0", paddingRight: "0" }}
                  disabled={false}
                  onClick={() => {
                    !false && set__offer__salary((offer__salary ?? []).concat([{}]));
                  }}
                >
                  <PlusCircleIcon color="#0088ce" />
                </Button>
              </SplitItem>
            </Split>
            <div>
              {offer__salary?.map((_, itemIndex) => (
                <div
                  key={itemIndex}
                  style={{
                    marginBottom: "1rem",
                    display: "flex",
                    justifyContent: "space-between",
                  }}
                >
                  <div style={{ width: "100%", marginRight: "10px" }}>
                    <Card>
                      <CardBody className="pf-c-form">
                        <FormGroup fieldId={"uniforms-0002-000u"} label={"Currency"} isRequired={false}>
                          <TextInput
                            name={`offer__salary.${itemIndex}.currency`}
                            id={"uniforms-0002-000u"}
                            isDisabled={false}
                            placeholder={""}
                            type={"text"}
                            value={offer__salary?.[itemIndex].currency}
                            onChange={(newValue) => {
                              set__offer__salary((s) => {
                                const newState = [...s];
                                newState[itemIndex].currency = newValue;
                                return newState;
                              });
                            }}
                          />
                        </FormGroup>
                        <FormGroup fieldId={"uniforms-0002-000v"} label={"Value"} isRequired={false}>
                          <TextInput
                            name={`offer__salary.${itemIndex}.value`}
                            id={"uniforms-0002-000v"}
                            isDisabled={false}
                            placeholder={""}
                            type={"text"}
                            value={offer__salary?.[itemIndex].value}
                            onChange={(newValue) => {
                              set__offer__salary((s) => {
                                const newState = [...s];
                                newState[itemIndex].value = newValue;
                                return newState;
                              });
                            }}
                          />
                        </FormGroup>
                      </CardBody>
                    </Card>
                  </div>
                  <div>
                    <Button
                      disabled={false}
                      variant="plain"
                      style={{ paddingLeft: "0", paddingRight: "0" }}
                      onClick={() => {
                        const value = [...offer__salary];
                        value.splice(itemIndex, 1);
                        !false && set__offer__salary(value);
                      }}
                    >
                      <MinusCircleIcon color="#cc0000" />
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </CardBody>
      </Card>
    </div>
  );
};
export default Form__hiring_HRInterview;
