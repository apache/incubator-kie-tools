package v1alpha08

import "fmt"

func (e StateType) String() string {
	switch e {
	case EventStateType:
		return "event"
	case OperationStateType:
		return "operation"
	case SwitchStateType:
		return "switch"
	case SleepStateType:
		return "sleep"
	case ParallelStateType:
		return "parallel"
	case InjectStateType:
		return "inject"
	case ForEachStateType:
		return "foreach"
	default:
		return fmt.Sprintf("%s", string(e))
	}
}
