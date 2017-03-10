package home.automation.resource

import javax.ws.rs.{GET, Path}

import home.automation.service.RasPiService

class RasPiResource {

  @GET
  @Path("/startSensor")
  def startSensor(): Unit = {
    RasPiService().startSensor()
  }

  @GET
  @Path("/stopSensor")
  def stopSensor(): Unit = {
    RasPiService().stopSensor()
  }

  @GET
  @Path("/on")
  def on(): Unit = {
    RasPiService().switch.on()
  }

  @GET
  @Path("/off")
  def off(): Unit = {
    RasPiService().switch.off()
  }


  @GET
  @Path("/shutdown")
  def shutdown(): Unit = {
    RasPiService().cleanup()
  }
}
