package home.automation.service

import com.pi4j.io.gpio.{GpioController, GpioFactory}
import home.automation.model.{Sensor, Switch}
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
class RasPiServiceTest extends MockitoSugar with Matchers {

  val sensor = mock[Sensor]
  val switch = mock[Switch]
  val gpioController = mock[GpioController]
  val rasPiService = RasPiService(sensor, switch)

  @Before
  def before(): Unit = {
    PowerMockito.mockStatic(classOf[GpioFactory])
    when(GpioFactory.getInstance()).thenReturn(gpioController)
  }

  @Test
  def startSensorShouldStartTheSensor() {

    when(sensor.isAnyObjectDetected).thenReturn(true)

    rasPiService.startSensor()

    rasPiService.isSensorEnabled should be(true)

    verify(switch, atLeastOnce()).on()

    when(sensor.isAnyObjectDetected).thenReturn(false)

    Thread.sleep(500)
    verify(switch, atLeastOnce()).off()
  }

  @Test
  def shouldNotStartTheSensorIfItIsAlreadyRunning() {

    rasPiService.isSensorEnabled = true

    rasPiService.startSensor()

    verify(switch, never()).on()
    verify(switch, never()).off()
    rasPiService.isSensorEnabled should be(true)
  }

  @Test
  def cleanupShouldSwitchOffAndDisableSensor() {

    rasPiService.isSensorEnabled = true

    rasPiService.cleanup()

    verify(switch, atLeastOnce()).off()
    verify(gpioController, atLeastOnce()).shutdown()

    rasPiService.isSensorEnabled should be(false)
  }

  @Test
  def stopSensorShouldDisableSensor() {

    rasPiService.isSensorEnabled = true

    rasPiService.stopSensor()

    rasPiService.isSensorEnabled should be(false)
  }

  @Test
  def switchOnShouldTurnOnTheSwitch() {

    rasPiService.switchOn()

    verify(switch, atLeastOnce()).on()
  }

  @Test
  def switchOffShouldTurnOnTheSwitch() {

    rasPiService.switchOff()

    verify(switch, atLeastOnce()).off()
  }
}