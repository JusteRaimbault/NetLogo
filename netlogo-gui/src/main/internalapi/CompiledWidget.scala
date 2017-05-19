// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.internalapi

import org.nlogo.core.{ Button, Chooseable, Chooser, CompilerException, Monitor, Slider, Widget }

sealed trait CompiledWidget {
  def widget: Widget
  def modelLoaded(): Unit = {}
  def modelUnloaded(): Unit = {}
}

trait CompiledButton extends CompiledWidget {
  /** The button from which this was compiled */
  def widget: Button

  /** The unique string identifying the procedure backing this button */
  def procedureTag: String

  /** The error (if any) produced when compiling this button */
  def compilerError: Option[CompilerException]

  /** Causes the job thread to enqueue a task for this button at the given interval
   *
   * @throws IllegalStateException if the job is already running
   */
  @throws(classOf[IllegalStateException])
  def start(): Unit

  /** Causes the job thread to stop running the task for this button
   *
   * @throws IllegalStateException if the job is already running
   */
  @throws(classOf[IllegalStateException])
  def stop(): Unit

  /** Monitorable for whether ticks are or are not enabled */
  def ticksEnabled: Monitorable[Boolean]

  /** Monitorable for whether or not the job is running
   *
   *  Errors thrown by the button code as it runs will be propagated to the button through
   *  the onError callback of this Monitorable
   */
  def isRunning: Monitorable[Boolean]
}

// consider adding an onError callback
trait CompiledMonitor extends CompiledWidget with Monitorable[String] {
  def widget: Monitor
}

trait CompiledSlider extends CompiledWidget {
  def widget: Slider

  /** Monitorable for the value of the slider
   *
   *  Exceptions thrown when the slider is updated will be
   *  propagated through the onError callback of the `value` monitorable.
   */
  def value: Monitorable[Double]
  def min:   Monitorable[Double]
  def max:   Monitorable[Double]
  def inc:   Monitorable[Double]

  /** Updates the value of the slider.
   *
   * @param update The value to which the slider ought to be updated
   */
  def setValue(update: Double): Unit
}

trait CompiledChooser extends CompiledWidget {
  def widget: Chooser

  /** Monitorable for the value of the chooser */
  def value: Monitorable[Chooseable]

  /** Updates the value of the chooser.
   *
   * @param update The value to which the chooser ought to be updated
   */
  def setValue(update: Chooseable): Unit
}

case class NonCompiledWidget(val widget: Widget) extends CompiledWidget {
  def compilerError = None
}
