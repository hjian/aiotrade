package org.aiotrade.lib.info.model

import ru.circumflex.orm.Table
import ru.circumflex.orm._
import org.aiotrade.lib.securities.model.Flag
import org.aiotrade.lib.math.timeseries.TVal
import org.aiotrade.lib.collection.ArrayList
import scala.collection.JavaConversions._
import scala.collection.mutable.Map

object Filing {

    val PDF = 1
    val TXT = 2
    val WORD = 3
    val OTHERS = 99

  
  def formatFromExtName(ext : String) = {
    ext.toUpperCase match {
      case "PDF" => PDF
      case "TXT" => TXT
      case "DOC" => WORD
      case "DOCX" =>WORD
      case _ =>   OTHERS
    }
  }

  def extNameFromFormat(format : Int) = {
    format match {
      case 1 => "PDF"
      case 2 => "TXT"
      case 3 => "DOC"
      case 4 => "DOCX"
      case _ =>   "OTHERS"
    }
  }

}

class Filing extends TVal with Flag with InfoContent{
  var generalInfo : GeneralInfo = _

  var publisher : String = ""
  var format : Int = _
  var size : Long = 0L

  private var _filings: ArrayList[Filing] = ArrayList[Filing]()

  def filings = _filings

  def += [Filings](value: org.aiotrade.lib.info.model.Filing){
    assert(value.generalInfo.publishTime == this.time,
           value + " is appended to a different TVal with time=" + this.time)

    value +=: _filings
  }

  def ++= [Filings](values: ArrayList[org.aiotrade.lib.info.model.Filing]){
    values.foreach(v => assert(v.generalInfo.publishTime == this.time,
                               v + " is appended to a different TVal with time=" + this.time))

    values ++=: _filings
  }

  def publishTime: Long = this.time
  //def weight: Float = 0F
  def link: String = if(generalInfo != null ) generalInfo.url else ""

  def exportToMap: Map[String, String] = {
    val map = Map[String, String]()
    if(generalInfo.title != null ) map += ("TITLE" -> generalInfo.title)
    map += ("PUBLISH_TIME" -> publishTime.toString)
    //map += ("weight" -> weight.toString)
    if(link != null) map += ("LINK" -> link)
    if(publisher != null) map += ("SOURCE_NAME" -> publisher)
    map += ("FILE_SIZE" -> size.toString)
    map += ("FILE_TYPE" -> Filing.extNameFromFormat(format))
<<<<<<< HEAD
    if(generalInfo.infoSecs != null) map += ("SECURITY_CODE" -> generalInfo.infoSecs(0).name)
    if(generalInfo.infoCategorys != null) map += ("SUBJECT" -> generalInfo.infoCategorys(0).name)
=======
    if(generalInfo.secs(0) != null) map += ("SECURITY_CODE" -> generalInfo.secs(0).secInfo.uniSymbol)
    if(generalInfo.categories(0) != null) map += ("SUBJECT" -> generalInfo.categories(0).name)
>>>>>>> e951cd1e0921fea7c02891924acb6af06e3b807f

    map
  }

  def exportToJavaMap: java.util.Map[String, String] = {
    exportToMap
  }
}

object Filings extends Table[Filing]{
  val generalInfo =  "generalInfos_id" REFERENCES(GeneralInfos)
  
  val publisher = "publisher" VARCHAR(30) DEFAULT("''")
  val format = "format" TINYINT
  val size = "size" BIGINT

}



