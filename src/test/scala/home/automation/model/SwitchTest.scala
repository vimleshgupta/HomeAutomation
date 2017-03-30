package home.automation.model

import com.pi4j.io.gpio.RaspiPin._
import com.pi4j.io.gpio.{GpioFactory, GpioPinDigitalOutput, PinState}
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
class SwitchTest extends MockitoSugar with Matchers {

  var pin = mock[GpioPinDigitalOutput]
  val switch = new Switch(pin)


  @Before
  def before(): Unit = {
    PowerMockito.mockStatic(classOf[GpioFactory])
    when(GpioFactory.getInstance()).thenReturn(Context.gpioController)
  }

  @Test
  def on_shouldOnTheSwitchWhenSwitchIsOff(): Unit = {
    when(pin.isLow).thenReturn(true)
    switch.on()
    verify(pin, times(1)).setState(PinState.HIGH)
  }

  @Test
  def on_shouldNotOnSwitchWhenSwitchIsOn(): Unit = {
    when(pin.isLow).thenReturn(false)
    switch.on()
    verify(pin, never()).setState(PinState.HIGH)
  }

  @Test
  def off_shouldOffSwitchWhenSwitchIsOn(): Unit = {
    when(pin.isHigh).thenReturn(true)
    switch.off()
    verify(pin, times(1)).setState(PinState.LOW)
  }

  @Test
  def off_shouldNotOffSwitchWhenSwitchIsOff(): Unit = {
    when(pin.isHigh).thenReturn(false)
    switch.off()
    verify(pin, never()).setState(PinState.LOW)
  }

  @Test
  def switch_shouldCreateSwitchWithNewProvisionedPin(): Unit = {

    when(Context.gpioController.getProvisionedPin(GPIO_00.getName)).thenReturn(null)
    when(Context.gpioController.provisionDigitalOutputPin(GPIO_00)).thenReturn(pin)

    Switch(GPIO_00)

    verify(Context.gpioController, atLeastOnce()).provisionDigitalOutputPin(GPIO_00)
  }

  @Test
  def switch_shouldCreateNewSwitchWithExistingProvisionedPin(): Unit = {

    when(Context.gpioController.getProvisionedPin(GPIO_00.getName)).thenReturn(pin)

    Switch(GPIO_00)

    verify(Context.gpioController, atLeastOnce()).getProvisionedPin(GPIO_00.getName)
  }

}