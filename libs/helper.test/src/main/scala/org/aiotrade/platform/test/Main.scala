/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aiotrade.platform.test

import java.awt.Color
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.UIManager
import org.aiotrade.lib.dataserver.yahoo.YahooQuoteServer
import org.aiotrade.lib.dataserver.yahoo.YahooTickerServer
import org.aiotrade.lib.util.swing.plaf.HighContrastLAF


/**
 *
 * @author Caoyuan Deng
 */
object Main {
  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]) {
    UIManager.setLookAndFeel(new HighContrastLAF)

    val frame = new JFrame
    val w = 800
    val h = 600

    frame.setSize(w, h)

    val pane = frame.getContentPane
    pane.setBackground(Color.WHITE)
        
    val symbol = "GOOG"
    val containers = (new Util).init(pane, w, h, symbol, "", "", classOf[YahooQuoteServer], classOf[YahooTickerServer])

    for (viewContainer <- containers) {
      viewContainer.get.setPreferredSize(new Dimension(w, h))
    }

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.pack

    frame.setVisible(true)
  }
}
