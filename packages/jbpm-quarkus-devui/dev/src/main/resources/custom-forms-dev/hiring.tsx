import React, { useCallback, useEffect, useState } from "react";
import { TextInput, FormGroup } from "@patternfly/react-core";
const Form__hiring: React.FC<any> = (props: any) => {
  const [formApi, setFormApi] = useState<any>();
  const [candidate, set__candidate] = useState<string>("");
  const [experience, set__experience] = useState<number>();
  const [skills, set__skills] = useState<string>("");
  /* Utility function that fills the form with the data received from the kogito runtime */
  const setFormData = (data) => {
    if (!data) {
      return;
    }
    set__candidate(data?.candidate ?? "");
    set__experience(data?.experience);
    set__skills(data?.skills ?? "");
  };
  /* Utility function to generate the expected form output as a json object */
  const getFormData = useCallback(() => {
    const formData: any = {};
    formData.candidate = candidate;
    formData.experience = experience;
    formData.skills = skills;
    return formData;
  }, [candidate, experience, skills]);
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
      <FormGroup fieldId={"uniforms-0006-0000"} label={"Candidate"} isRequired={false}>
        <TextInput
          name={"candidate"}
          id={"uniforms-0006-0000"}
          isDisabled={false}
          placeholder={""}
          type={"text"}
          value={candidate}
          onChange={set__candidate}
        />
      </FormGroup>
      <FormGroup fieldId={"uniforms-0006-0002"} label={"Experience Level"} isRequired={false}>
        <TextInput
          type={"number"}
          name={"experience"}
          isDisabled={false}
          id={"uniforms-0006-0002"}
          placeholder={""}
          step={1}
          value={experience}
          onChange={(newValue) => set__experience(Number(newValue))}
        />
      </FormGroup>
      <FormGroup fieldId={"uniforms-0006-0003"} label={"Skills"} isRequired={false}>
        <TextInput
          name={"skills"}
          id={"uniforms-0006-0003"}
          isDisabled={false}
          placeholder={""}
          type={"text"}
          value={skills}
          onChange={set__skills}
        />
      </FormGroup>
    </div>
  );
};
export default Form__hiring;
