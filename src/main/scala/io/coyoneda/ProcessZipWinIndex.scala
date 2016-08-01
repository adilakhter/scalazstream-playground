package io.coyoneda

import scalaz.concurrent.Task
import scalaz.stream._
import Process._
import scalaz._
import Scalaz._

object ProcessZipWinIndex extends App {

  val sampleInput5 = emitAll(List("uno", "dos", "tres", "cuatro", "cinco", "seis")).toSource

  def indexer: Process1[String, (Int, String)] = {
    def indexProcess(i: Int): Process1[String, (Int, String)] = {
      await1[String].map(s => (i, s)) fby indexProcess(i + 1)
    }
    indexProcess(1)
  }

  def zippedWithIndexProcess = sampleInput5 |> indexer

  zippedWithIndexProcess.runLog.run.println
}


