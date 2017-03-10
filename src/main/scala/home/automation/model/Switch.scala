package home.automation.model

import com.pi4j.io.gpio.{GpioFactory, GpioPinDigitalOutput, Pin}
import com.pi4j.io.gpio.PinState._

case class Switch(switch: GpioPinDigitalOutput) {

  def on() = if (switch.isLow) switch.setState(HIGH)

  def off() = if (switch.isHigh) switch.setState(LOW)

}

object Switch {

  lazy val gpioController = GpioFactory.getInstance()

  def apply(pin: Pin): Switch = {
    Switch(getProvisionedDigitalOutputPin(pin))
  }

  def getProvisionedDigitalOutputPin(pin: Pin) = {
    {
      Option(gpioController.getProvisionedPin(pin.getName)) getOrElse gpioController.provisionDigitalOutputPin(pin)
    }.asInstanceOf[GpioPinDigitalOutput]
  }

}