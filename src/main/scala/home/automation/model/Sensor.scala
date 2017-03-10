package home.automation.model

import com.pi4j.io.gpio.{GpioFactory, GpioPinDigitalInput, GpioPinDigitalOutput, Pin}

import scala.util.control.Breaks._


case class Sensor(echoPin: GpioPinDigitalInput, triggerPin: GpioPinDigitalOutput) {

  val speedOfSound = 34029
  var isSenssorEnabled = false

  val tolerance = 10
  var maxDeadTarget = 0D
  var minDeadTarget = 0D

  def trigger(): Double = {

    triggerPin.setState(true)
    var start = System.nanoTime()
    while (start + 10000l >= System.nanoTime()) {}
    triggerPin.setState(false)

    while (echoPin.isLow) {}
    start = System.nanoTime()
    while (echoPin.isHigh) {}

    val stop = System.nanoTime()

    val distance = (speedOfSound * (stop - start)) / 2.0

    val distanceInCM = distance / 1000000000L
    distanceInCM
  }

  def isAnyObjectDetected = {

    var count = 0
    var result = true
    breakable {
      for (i <- 1 to 5) {
        val distance = trigger()
        if (isReachableToDeadTarget(distance, tolerance)) count += 1

        if (count == 3) {
          result = false
          break
        }
      }
    }
    result
  }

  def isReachableToDeadTarget(distance: Double, tolerance: Int) = {
    distance < maxDeadTarget && distance > minDeadTarget
  }

  def setDeadTarget() = {
    val target = trigger()
    maxDeadTarget = target + 10
    minDeadTarget = target - 10
  }

}

object Sensor {

  lazy val gpioController = GpioFactory.getInstance()

  def apply(echo: Pin, trigger: Pin): Sensor = {
    val triggerPin = getProvisionedDigitalOutputPin(trigger)
    triggerPin.setState(false)
    Sensor(getProvisionedDigitalInputPin(echo), triggerPin)
  }

  def getProvisionedDigitalInputPin(pin: Pin) = {
    {
      Option(gpioController.getProvisionedPin(pin.getName)) getOrElse gpioController.provisionDigitalInputPin(pin)
    }.asInstanceOf[GpioPinDigitalInput]
  }

  def getProvisionedDigitalOutputPin(pin: Pin) = {
    {
      Option(gpioController.getProvisionedPin(pin.getName)) getOrElse gpioController.provisionDigitalOutputPin(pin)
    }.asInstanceOf[GpioPinDigitalOutput]
  }

}