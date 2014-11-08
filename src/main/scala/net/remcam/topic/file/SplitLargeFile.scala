package net.remcam.topic.file

import java.io.{File, PrintWriter}

import net.remcam.util.Control._

import scala.io.Codec

/**
 * Created by svanschalkwyk on 11/6/14.
 * Reads wikipedia scrape file and writes to individual files
 * My first Scala program
 */

class SplitLargeFile(val folderName: String) {
  val this.folderName = folderName

  def fileObjectsInFolder = new File(this.folderName).listFiles.filter(_.getName.endsWith(".txt")).toIterator

  var newFileName: String = ""
  var newFileContents: String = ""
  var newFileStartFlag = false

  // open and parse
  def openAndParse(): Unit = {
    var fullFilePath = this.folderName
    for (fileObject <- this.fileObjectsInFolder) {
      fullFilePath = fullFilePath.concat(fileObject.getName)
      println(fullFilePath)
      using(io.Source.fromFile(fullFilePath)(Codec.UTF8)) { source => {
        // parse
        for (line <- source.getLines()) {
          println(line)
          if (newFileStartFlag) {
            newFileName = line.replaceAll("([a-z][A-Z]-_/.)+", "_")
            println(newFileName)
            newFileStartFlag = false
          }

          if (line.contains("<doc id")) {
            newFileStartFlag = true
            // next line is title so skip this one
          } else if (line.contains("</doc>")) {
            //newFileEndFlag = true
            // go write file
            writeNewFile(folderName, newFileName, newFileContents)
            newFileName = ""
            newFileContents = ""
          } else {
            // valid line to copy
            newFileContents = newFileContents.concat(line).concat("\n")
          }
        }
      }
      }
    }
  }

  def writeNewFile(folderName: String, fileName: String, content: String): Unit = {
    if (!fileName.isEmpty && !content.isEmpty) {
      var newFileName = folderName.concat("parsed/").concat(fileName).concat(".txt")
      val pw = new PrintWriter(newFileName)
      try {
        pw.write(content)
      }
      finally {
        pw.close()
      }
    }
  }
}

object Application {
  def main(args: Array[String]) {
    val folderName = "/home/svanschalkwyk/Projects/corpora/wikipedia/extracted/"
    val sf = new SplitLargeFile(folderName)
    sf.openAndParse()
  }
}

