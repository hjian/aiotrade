package org.aiotrade.lib.io

import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.InputStream
import java.util.Timer
import java.util.logging.Level
import java.util.logging.Logger
import java.util.zip.ZipFile

/**
 *
 * @author Caoyuan Deng
 * @author Guibin Zhang
 */
class DirWatchedFileFeeder(watchingDir: String, fileFilter: FileFilter, period: Int = 500) {
  protected val log = Logger.getLogger(this.getClass.getName)

  protected val fileQueue = new java.util.concurrent.ConcurrentLinkedQueue[File]

  private var zipFile: ZipFile = _
  private var zipEntries: java.util.LinkedList[String] = _

  private val workingDir = new File(watchingDir)
  if (!workingDir.exists) workingDir.mkdirs
  
  private val dirWatcher = new DirWatcher(workingDir, fileFilter, true) {
    override protected def onChange(event: FileEvent) {
      event match {
        case FileAdded(file, _) =>
          fileQueue.add(file)
          log.info("Added file " + file.getName)
        case FileModified(file, _) =>
        case _ =>
      }
    }
  }

  private val timer = new Timer("Dir watcher timer")
  timer.schedule(dirWatcher, 1000, period)

  def hasNext: Boolean = {
    if (zipFile != null && !zipEntries.isEmpty) {
      true
    } else {

      if (zipFile != null && zipEntries.isEmpty) {
        zipFile = null
        zipEntries = null
      }

      if (!fileQueue.isEmpty) {
        val workingFile = fileQueue.peek
        if (workingFile.length > 1024) {
          if (workingFile.getName.indexOf(".zip") > -1) {
            try {
              zipFile = new ZipFile(workingFile)
              zipEntries = getZipEntries(zipFile)
              log.info("Unziping " + zipFile.getName)
            } catch {
              case ex => 
                log.log(Level.WARNING, "Bad zip file, please check the zip format of file using jar " + 
                        "or did you copy a zip file to here directly? You sould copy it to " + 
                        "here with a tmp file name (not end with .zip, then rename it to .zip file). " +
                        "The Exception is: " + ex.getMessage, ex)
            }
            fileQueue.poll
            hasNext
          } else true
        } else {
          // drop this one
          fileQueue.poll
          hasNext
        }
      } else false
    }
  }

  /**
   * @return InputStream to read, file name, file to delete
   */
  def next: (InputStream, String, Either[File, ZipFile]) = {
    if (zipEntries != null) {
      nextZipEntry
    } else {
      val workingFile = fileQueue.poll

      log.info("Reading dbf " + workingFile)
      (new FileInputStream(workingFile), workingFile.getName, Left(workingFile))
    }
  }

  private def nextZipEntry: (InputStream, String, Either[File, ZipFile]) = {
    try {
      val entry = zipEntries.removeFirst
      log.info("Reading entry " + entry)
      val fileToDelete = if (zipEntries.isEmpty) Right(zipFile) else null

      (zipFile.getInputStream(zipFile.getEntry(entry)), entry, fileToDelete)
    } catch {
      case ex => log.log(Level.WARNING, ex.getMessage, ex); (null, "", null)
    }
  }

  private def getZipEntries(zFile: ZipFile) = {
    val zFileList = new java.util.LinkedList[String]()
    val entries = zFile.entries
    while (entries.hasMoreElements){
      zFileList.add(entries.nextElement.getName)
    }
    // sort by the file name.
    java.util.Collections.sort(zFileList)
    zFileList
  }
}


