// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app.codetab

import java.awt.Component
import java.awt.event.ActionEvent
import javax.swing.{ AbstractAction, Action, JCheckBox }

import org.nlogo.app.common.{ Events => AppEvents, TabsInterface }
import org.nlogo.core.I18N
import org.nlogo.editor.EditorMenu
import org.nlogo.window.{ Events => WindowEvents, GUIWorkspace }

// This is THE Code tab.  Certain settings and things that are only accessible here.
// Other Code tabs come and go.

class MainCodeTab(workspace: GUIWorkspace, tabs: TabsInterface, editorMenu: EditorMenu)
extends CodeTab(workspace, tabs)
with WindowEvents.LoadModelEvent.Handler
{
  val mainCodeTab = this
  var tabbing: JCheckBox = null
  val smartTabAction: Action = new SmartTabAction

  private class SmartTabAction extends AbstractAction(I18N.gui.get("tabs.code.indentAutomatically")) {
    def actionPerformed(e: ActionEvent) {
      setIndenter(tabbing.isSelected)
      new AppEvents.IndenterChangedEvent(tabbing.isSelected)
        .raise(MainCodeTab.this)
    }
  }

  var poppingCheckBox: JCheckBox = null
  val codeTabPopOutAction: Action = new CodeTabPopOutAction

  def getPoppingCheckBox = { poppingCheckBox }
  private class CodeTabPopOutAction extends AbstractAction("Code Tab in separate window") {
    def actionPerformed(e: ActionEvent) {
      tabs.switchToSpecifiedCodeWindowState(poppingCheckBox.isSelected)
    }
  }

  override def editorConfiguration =
    super.editorConfiguration.withMenu(editorMenu)

  def smartTabbingEnabled = tabbing.isSelected

  override def getAdditionalToolBarComponents: Seq[Component] = {
    tabbing = new JCheckBox(smartTabAction)
    // turning it on by default (for now, anyway ~Forrest)
    tabbing.setSelected(true)
    // hack, to get it to realize it's really checked. ~Forrest (10/23/2007)
    smartTabAction.actionPerformed(null)
    // The state of the check box is set in Tabs.handle(e: AfterLoadEvent). AAB 10/2020
    poppingCheckBox = new JCheckBox(codeTabPopOutAction)
    Seq(tabbing, poppingCheckBox)
  }

  override def dirty_=(b: Boolean) = {
    super.dirty_=(b)
    if (b) {
      // TODO: uncomment the following line when ready.
      // tabs.asInstanceOf[AbstractTabsPanel].getTabManager.setDirtyMonitorCodeWindow
      // What it does is cause the '*' dirty marker to appear in the separate code window,
      // when that window exists. It does that correctly.
      // The problem is that the code to properly clear the '*' does not exist.
      // There is a TODO in /netlogo-gui/src/main/app/FileManager.scala
      // at a location where the needed code might be added
      new WindowEvents.DirtyEvent(None).raise(this)
    }
  }

  def handle(e: WindowEvents.LoadModelEvent) {
    innerSource = e.model.code
    compile()
  }
}
