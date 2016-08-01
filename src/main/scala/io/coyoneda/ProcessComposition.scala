package io.coyoneda

import scalaz.concurrent.Task
import scalaz.stream._
import Process._
import scalaz._
import Scalaz._

object ProcessComposition extends App {

  // Composing Process =>
  // Given a Process: you can run its content via |> which takes a Process1
  // Process1 takes two type parameters, `I` and `O`, and it basically is a transformation from I to O.
  // To create a process1, you can use method await1[A], which creates a Process1[A,A].
  // All it does is that it awaits for 1 value. It has a map and flatMap method which gives you the ability to transform the accumulated I into O.
  // By default, process1 halts after receiving 1 element. You can, however, use it's repeat method to make it run as long as there are any input values available.

  val sampleInput3: Process[Task, Int] = emitAll((0 to 10).toList).toSource
  def p1Squares: Process1[Int, Int] = await1[Int].map(i => i * i)
  def p1SquaresRepeated: Process1[Int, Int] = p1Squares.repeat
  def squaresStream = sampleInput3  |> p1SquaresRepeated

  squaresStream.runLog.run.println


  // with FBY
  // Now that we know about process1 and piping, we should learn about another cool thing about processes.
  // It is possible to define process1 by composing two processes1.
  // For example, given two processes p1 and p2, you can define new process that
  // will do whatever p1 does and once p1 halts, it will do whatever p2 does. You can use `fby` (short for 'follow by') operator on process1 to implement this behavior.

  val sampleInput4: Process[Task, Int] = emitAll(List(1,2,3,0,2,-1,1,2)).toSource

  def positiveStream: Process1[Int, Int] = await1[Int].flatMap { number =>
    if(number >= 0) emit(number) fby positiveStream
    else halt
  }

  (sampleInput4 |> positiveStream).runLog.run.println

}
