package home.automation.model

import com.pi4j.io.gpio.RaspiPin.{GPIO_00, GPIO_01}
import com.pi4j.io.gpio._
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.mockito.Mockito._
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.scalatest.Matchers
import org.scalatest.mockito.MockitoSugar


@RunWith(classOf[PowerMockRunner])
@PrepareForTest(Array(classOf[GpioFactory]))
class SensorTest extends MockitoSugar with Matchers {

  var echoPin = mock[GpioPinDigitalInput]
  var triggerPin = mock[GpioPinDigitalOutput]

  @Before
  def before(): Unit = {
    PowerMockito.mockStatic(classOf[GpioFactory])
    when(GpioFactory.getInstance()).thenReturn(Context.gpioController)
  }

  @Test
  def trigger_shouldTriggerTheSensorAndReturnDistance(): Unit = {

    when(echoPin.isLow).thenReturn(false)
    when(echoPin.isHigh).thenReturn(false)

    val sensor = Sensor(echoPin, triggerPin)
    sensor.trigger()

    verify(triggerPin, times(1)).setState(true)
    verify(triggerPin, times(1)).setState(false)
    verify(echoPin, timeout(1)).isHigh
    verify(echoPin, timeout(1)).isLow
  }


  @Test
  def sensor_shouldCreateSensorWithNewProvisionedPin(): Unit = {

    when(Context.gpioController.getProvisionedPin(GPIO_00.getName)).thenReturn(null)
    when(Context.gpioController.getProvisionedPin(GPIO_01.getName)).thenReturn(null)

    when(Context.gpioController.provisionDigitalInputPin(GPIO_00)).thenReturn(echoPin)
    when(Context.gpioController.provisionDigitalOutputPin(GPIO_01)).thenReturn(triggerPin)

    Sensor(GPIO_00, GPIO_01)

    verify(triggerPin, times(1)).setState(false)
    verify(Context.gpioController, atLeastOnce()).provisionDigitalInputPin(GPIO_00)
    verify(Context.gpioController, atLeastOnce()).provisionDigitalOutputPin(GPIO_01)
  }

  @Test
  def sensor_shouldCreateNewSensorWithExistingProvisionedPin(): Unit = {

    when(Context.gpioController.getProvisionedPin(GPIO_00.getName)).thenReturn(echoPin)
    when(Context.gpioController.getProvisionedPin(GPIO_01.getName)).thenReturn(triggerPin)

    Sensor(GPIO_00, GPIO_01)

    verify(triggerPin, times(1)).setState(false)
  }
}

object Context extends MockitoSugar {
  var gpioController = mock[GpioController]
}