import React, { useCallback, useEffect, useState } from "react";
import { Checkbox, FormGroup, TextInput } from "@patternfly/react-core";
const Form__hiring_ITInterview: React.FC<any> = (props: any) => {
  const [formApi, setFormApi] = useState<any>();
  const [approve, set__approve] = useState<boolean>(false);
  const [baseSalary, set__baseSalary] = useState<number>();
  const [bonus, set__bonus] = useState<number>();
  const [candidate, set__candidate] = useState<string>("");
  const [category, set__category] = useState<string>("");
  /* Utility function that fills the form with the data received from the kogito runtime */
  const setFormData = (data) => {
    if (!data) {
      return;
    }
    set__approve(data?.approve ?? false);
    set__baseSalary(data?.baseSalary);
    set__bonus(data?.bonus);
    set__candidate(data?.candidate ?? "");
    set__category(data?.category ?? "");
  };
  /* Utility function to generate the expected form output as a json object */
  const getFormData = useCallback(() => {
    const formData: any = {};
    formData.approve = approve;
    return formData;
  }, [approve]);
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
      <FormGroup fieldId="uniforms-000a-0001">
        <Checkbox
          isChecked={approve}
          isDisabled={false}
          id={"uniforms-000a-0001"}
          name={"approve"}
          label={"Approve"}
          onChange={set__approve}
        />
      </FormGroup>
      <FormGroup fieldId={"uniforms-000a-0003"} label={"Base salary"} isRequired={false}>
        <TextInput
          type={"number"}
          name={"baseSalary"}
          isDisabled={true}
          id={"uniforms-000a-0003"}
          placeholder={""}
          step={1}
          value={baseSalary}
          onChange={(newValue) => set__baseSalary(Number(newValue))}
        />
      </FormGroup>
      <FormGroup fieldId={"uniforms-000a-0005"} label={"Bonus"} isRequired={false}>
        <TextInput
          type={"number"}
          name={"bonus"}
          isDisabled={true}
          id={"uniforms-000a-0005"}
          placeholder={""}
          step={1}
          value={bonus}
          onChange={(newValue) => set__bonus(Number(newValue))}
        />
      </FormGroup>
      <FormGroup fieldId={"uniforms-000a-0006"} label={"Candidate"} isRequired={false}>
        <TextInput
          name={"candidate"}
          id={"uniforms-000a-0006"}
          isDisabled={true}
          placeholder={""}
          type={"text"}
          value={candidate}
          onChange={set__candidate}
        />
      </FormGroup>
      <FormGroup fieldId={"uniforms-000a-0007"} label={"Category"} isRequired={false}>
        <TextInput
          name={"category"}
          id={"uniforms-000a-0007"}
          isDisabled={true}
          placeholder={""}
          type={"text"}
          value={category}
          onChange={set__category}
        />
      </FormGroup>
    </div>
  );
};
export default Form__hiring_ITInterview;
