package home.automation.service

import com.pi4j.io.gpio.RaspiPin.{GPIO_00, GPIO_02, GPIO_07}
import com.pi4j.io.gpio._
import home.automation.model.{Sensor, Switch}

case class RasPiService(sensor: Sensor, switch: Switch) {

  lazy val gpioController = GpioFactory.getInstance()

  var isSensorEnabled = false

  def cleanup() = {
    isSensorEnabled = false
    switch.off()
    gpioController.shutdown()
  }

  def startSensor(): Unit = {

    if (!isSensorEnabled) {

      val runnable = new Runnable {
        override def run(): Unit = {
          sensor.setDeadTarget()
          while (isSensorEnabled) {
            if (sensor.isAnyObjectDetected) switch.on() else switch.off()
            Thread.sleep(500)
          }

        }
      }

      isSensorEnabled = true
      new Thread(runnable).start()
    }
  }

  def stopSensor() = isSensorEnabled = false

  def switchOn() = switch.on()

  def switchOff() = switch.off()
}

object RasPiService {

  private val instance = RasPiService(Sensor(GPIO_00, GPIO_07), Switch(GPIO_02))

  def apply(): RasPiService = instance
}