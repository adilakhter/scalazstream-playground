package io.coyoneda

import scalaz.concurrent.Task
import scalaz.stream._
import Process._
import scalaz._
import Scalaz._

object A_ProcessIntroduction extends App {

  val p: Process[Task, Int] = emitAll(Seq(1, 5, 10, 20))
  val result = p.runLog.run
  assert(result == Vector(1, 5, 10, 20))

  result.println
  val sampleInput2: Process[Task, Int] = emitAll(List(1,2,3,0,2,-1,1,2)).toSource

  // Following creates a stream of positive integers
  // from sample input 2
  // Use flatMap for that. To halt the stream you can use `halt` helper function.
  def positiveStream = sampleInput2.flatMap { number =>
    if(number >= 0) emit(number)
    else halt
  }

}

