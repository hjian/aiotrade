/*
 * PersistenceManager.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.aiotrade.platform.test

import org.aiotrade.lib.indicator.VOLIndicator;
import org.aiotrade.lib.securities.PersistenceManager;
import org.aiotrade.lib.securities.dataserver.QuoteServer;
import org.aiotrade.lib.securities.dataserver.TickerServer;
import org.aiotrade.lib.securities.Quote;
import org.aiotrade.lib.math.timeseries.Frequency;
import org.aiotrade.lib.math.timeseries.computable.Indicator;
import org.aiotrade.lib.math.timeseries.descriptor.AnalysisContents;
import org.aiotrade.platform.modules.dataserver.yahoo._
import org.aiotrade.platform.modules.indicator.basic._
import scala.collection.mutable.ArrayBuffer

/**
 *
 * @author Caoyuan Deng
 */
class PlainPersistenceManager extends PersistenceManager {

    private val quoteServers = new ArrayBuffer[QuoteServer]
    private val tickerServers = new ArrayBuffer[TickerServer]
    private val indicators = new ArrayBuffer[Indicator]

    def saveQuotes(symbol:String, freq:Frequency, quotes:ArrayBuffer[Quote], sourceId:Long) :Unit = {}
    def restoreQuotes(symbol:String, freq:Frequency) :ArrayBuffer[Quote] = new ArrayBuffer[Quote]
    def deleteQuotes(symbol:String, freq:Frequency, fromTime:Long, toTime:Long) :Unit = {}
    def dropAllQuoteTables(symbol:String) :Unit = {}

    def shutdown :Unit = {}

    def restoreProperties :Unit = {}
    def saveProperties :Unit = {}

    def saveContents(contents:AnalysisContents) :Unit = {}
    def restoreContents(symbol:String) :AnalysisContents = new AnalysisContents(symbol)
    def defaultContents :AnalysisContents = new AnalysisContents("<Default>")

    def lookupAllRegisteredServices[T](tpe:Class[T], folderName:String) :Seq[T] = {
        if (tpe == classOf[QuoteServer]) {
            if (quoteServers.isEmpty) {
                quoteServers += new YahooQuoteServer
            }
            quoteServers.asInstanceOf[Seq[T]]
        } else if (tpe == classOf[TickerServer]) {
            if (tickerServers.isEmpty) {
                tickerServers += new YahooTickerServer
            }
            tickerServers.asInstanceOf[Seq[T]]
        } else if (tpe == classOf[Indicator]) {
            if (indicators.isEmpty) {
                indicators += new MAIndicator
                indicators += new RSIIndicator
                indicators += new VOLIndicator
            }
            indicators.asInstanceOf[Seq[T]]
        } else {
            Nil
        }
    }

}