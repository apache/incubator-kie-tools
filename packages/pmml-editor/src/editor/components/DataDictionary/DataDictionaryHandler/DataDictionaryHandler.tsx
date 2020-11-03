import * as React from "react";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  Button,
  ButtonVariant,
  Modal,
  ModalVariant,
  Split,
  SplitItem,
  Title,
  TitleSizes
} from "@patternfly/react-core";
import { CloseIcon } from "@patternfly/react-icons";
import { isEqual } from "lodash";
import { DataDictionary, PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { Actions } from "../../../reducers";
import DataDictionaryContainer, { DataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import { convertDD2PMML, convertPMML2DD } from "../dataDictionaryUtils";

const DataDictionaryHandler = () => {
  const [isDataDictionaryOpen, setIsDataDictionaryOpen] = useState(false);
  const dispatch = useDispatch();
  const pmmlDataDictionary = useSelector<PMML, DataDictionary | undefined>((state: PMML) => state.DataDictionary);
  const [dictionary, setDictionary] = useState<DataField[]>(convertPMML2DD(pmmlDataDictionary));
  const handleDataDictionaryUpdate = (updatedDictionary: DataField[]) => {
    setDictionary(updatedDictionary);
  };

  const handleDataDictionaryToggle = () => {
    if (isDataDictionaryOpen) {
      const convertedDataDictionary = convertDD2PMML(dictionary);
      // temporary: checking if they are equals to prevent dispatching actions with no data changes
      if (!isEqual(pmmlDataDictionary?.DataField, convertedDataDictionary)) {
        dispatch({
          type: Actions.SetDataFields,
          payload: {
            dataFields: convertedDataDictionary
          }
        });
      }
    }
    setIsDataDictionaryOpen(!isDataDictionaryOpen);
  };

  useEffect(() => {
    setDictionary(convertPMML2DD(pmmlDataDictionary));
  }, [pmmlDataDictionary]);

  const header = (
    <Split hasGutter={true}>
      <SplitItem isFilled={true}>
        <Title headingLevel="h1" size={TitleSizes["2xl"]}>
          Data Dictionary
        </Title>
      </SplitItem>
      <SplitItem>
        <Button type="button" variant={ButtonVariant.plain} onClick={handleDataDictionaryToggle}>
          <CloseIcon />
        </Button>
      </SplitItem>
    </Split>
  );

  return (
    <>
      <Button variant="secondary" onClick={handleDataDictionaryToggle}>
        Set Data Dictionary
      </Button>
      <Modal
        aria-label="data-dictionary"
        title="Data Dictionary"
        header={header}
        isOpen={isDataDictionaryOpen}
        showClose={false}
        variant={ModalVariant.large}
        onEscapePress={() => false}
      >
        <DataDictionaryContainer dataDictionary={dictionary} onUpdate={handleDataDictionaryUpdate} />
      </Modal>
    </>
  );
};

export default DataDictionaryHandler;
