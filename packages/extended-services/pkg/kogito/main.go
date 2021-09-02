package kogito

func Systray(port int, jitexecutor []byte) {
	proxy := NewProxy(port, jitexecutor)
	proxy.view = &KogitoSystray{}
	proxy.view.controller = proxy
	proxy.view.Run()
}
