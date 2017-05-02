package home.automation.model

import com.pi4j.io.gpio.{GpioFactory, GpioPinDigitalInput, GpioPinDigitalOutput, Pin}

import scala.util.control.Breaks._


case class Sensor(echoPin: GpioPinDigitalInput, triggerPin: GpioPinDigitalOutput) {

  private val speedOfSound = 34029

  private val tolerance = 10
  private var maxDeadTarget = 0D
  private var minDeadTarget = 0D

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

  def isAnyObjectDetected: Boolean = {

    var reachedDeadTarget = 0

    for (i <- 1 to 5) {
      val distance = trigger()
      reachedDeadTarget = if (isReachableToDeadTarget(distance)) reachedDeadTarget + 1 else reachedDeadTarget
      if (reachedDeadTarget == 3) return false
      if (reachedDeadTarget == 0 && i == 3) return true
    }
    true
  }

  def isReachableToDeadTarget(distance: Double) = distance < maxDeadTarget && distance > minDeadTarget


  def setDeadTarget() = {
    val target = trigger()
    maxDeadTarget = target + tolerance
    minDeadTarget = target - tolerance
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