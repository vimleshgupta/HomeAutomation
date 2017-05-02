package home.automation.resource

import javax.ws.rs.{GET, Path}

import home.automation.service.RasPiService

class RasPiResource {

  private val rasPiService = RasPiService()

  @GET
  @Path("/startSensor")
  def startSensor(): Unit = {
    rasPiService.startSensor()
  }

  @GET
  @Path("/stopSensor")
  def stopSensor(): Unit = {
    rasPiService.stopSensor()
  }

  @GET
  @Path("/on")
  def on(): Unit = {
    rasPiService.switchOn()
  }

  @GET
  @Path("/off")
  def off(): Unit = {
    rasPiService.switchOff()
  }


  @GET
  @Path("/shutdown")
  def shutdown(): Unit = {
    rasPiService.cleanup()
  }
}
