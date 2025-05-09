import React, { useCallback, useEffect, useState } from "react";
import { Card, CardBody, TextInput, FormGroup, Split, SplitItem, Button } from "@patternfly/react-core";
import { PlusCircleIcon, MinusCircleIcon } from "@patternfly/react-icons";
const Form__hiring: React.FC<any> = (props: any) => {
  const [formApi, setFormApi] = useState<any>();
  const [candidateData__email, set__candidateData__email] = useState<string>("");
  const [candidateData__experience, set__candidateData__experience] = useState<number>();
  const [candidateData__lastName, set__candidateData__lastName] = useState<string>("");
  const [candidateData__name, set__candidateData__name] = useState<string>("");
  const [candidateData__skills, set__candidateData__skills] = useState<string[]>([]);
  /* Utility function that fills the form with the data received from the kogito runtime */
  const setFormData = (data) => {
    if (!data) {
      return;
    }
    set__candidateData__email(data?.candidateData?.email ?? "");
    set__candidateData__experience(data?.candidateData?.experience);
    set__candidateData__lastName(data?.candidateData?.lastName ?? "");
    set__candidateData__name(data?.candidateData?.name ?? "");
    set__candidateData__skills(data?.candidateData?.skills ?? []);
  };
  /* Utility function to generate the expected form output as a json object */
  const getFormData = useCallback(() => {
    const formData: any = {};
    formData.candidateData = {};
    formData.candidateData.email = candidateData__email;
    formData.candidateData.experience = candidateData__experience;
    formData.candidateData.lastName = candidateData__lastName;
    formData.candidateData.name = candidateData__name;
    formData.candidateData.skills = candidateData__skills;
    return formData;
  }, [
    candidateData__email,
    candidateData__experience,
    candidateData__lastName,
    candidateData__name,
    candidateData__skills,
  ]);
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
      <Card>
        <CardBody className="pf-c-form">
          <label>
            <b>Candidate data</b>
          </label>
          <FormGroup fieldId={"uniforms-0000-0002"} label={"Email"} isRequired={false}>
            <TextInput
              name={"candidateData.email"}
              id={"uniforms-0000-0002"}
              isDisabled={false}
              placeholder={""}
              type={"text"}
              value={candidateData__email}
              onChange={set__candidateData__email}
            />
          </FormGroup>
          <FormGroup fieldId={"uniforms-0000-0004"} label={"Experience"} isRequired={false}>
            <TextInput
              type={"number"}
              name={"candidateData.experience"}
              isDisabled={false}
              id={"uniforms-0000-0004"}
              placeholder={""}
              step={1}
              value={candidateData__experience}
              onChange={(newValue) => set__candidateData__experience(Number(newValue))}
            />
          </FormGroup>
          <FormGroup fieldId={"uniforms-0000-0005"} label={"Last name"} isRequired={false}>
            <TextInput
              name={"candidateData.lastName"}
              id={"uniforms-0000-0005"}
              isDisabled={false}
              placeholder={""}
              type={"text"}
              value={candidateData__lastName}
              onChange={set__candidateData__lastName}
            />
          </FormGroup>
          <FormGroup fieldId={"uniforms-0000-0006"} label={"Name"} isRequired={false}>
            <TextInput
              name={"candidateData.name"}
              id={"uniforms-0000-0006"}
              isDisabled={false}
              placeholder={""}
              type={"text"}
              value={candidateData__name}
              onChange={set__candidateData__name}
            />
          </FormGroup>
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
                  disabled={false}
                  onClick={() => {
                    !false && set__candidateData__skills((candidateData__skills ?? []).concat([""]));
                  }}
                >
                  <PlusCircleIcon color="#0088ce" />
                </Button>
              </SplitItem>
            </Split>
            <div>
              {candidateData__skills?.map((_, itemIndex) => (
                <div
                  key={itemIndex}
                  style={{
                    marginBottom: "1rem",
                    display: "flex",
                    justifyContent: "space-between",
                  }}
                >
                  <div style={{ width: "100%", marginRight: "10px" }}>
                    <FormGroup fieldId={"uniforms-0000-0009"} label={""} isRequired={false}>
                      <TextInput
                        name={`candidateData__skills.${itemIndex}`}
                        id={"uniforms-0000-0009"}
                        isDisabled={false}
                        placeholder={""}
                        type={"text"}
                        value={candidateData__skills?.[itemIndex]}
                        onChange={(newValue) => {
                          set__candidateData__skills((s) => {
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
                      disabled={false}
                      variant="plain"
                      style={{ paddingLeft: "0", paddingRight: "0" }}
                      onClick={() => {
                        const value = [...candidateData__skills];
                        value.splice(itemIndex, 1);
                        !false && set__candidateData__skills(value);
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
export default Form__hiring;
