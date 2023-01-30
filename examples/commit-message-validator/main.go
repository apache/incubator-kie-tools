package main

import (
	"flag"
	"io/ioutil"
	"net/http"

	"github.com/gin-gonic/gin"

	"github.com/kiegroup/kie-tools/examples/commit-message-validator/pkg"
	"github.com/kiegroup/kie-tools/examples/commit-message-validator/pkg/metadata"
)

func main() {
	port := flag.String("p", metadata.Port, "KIE Sandbox Extended Services Port")

	//initialises a router with the default functions.
	router := gin.Default()

	router.POST("/validate", func(context *gin.Context) {
		body, err := ioutil.ReadAll(context.Request.Body)
		if err != nil {
			context.String(http.StatusBadRequest, "Wrong input")
		}
		context.JSON(http.StatusOK, pkg.Validate(string(body)))
	})

	// starts the server at port 8080
	router.Run(":" + *port)
}
