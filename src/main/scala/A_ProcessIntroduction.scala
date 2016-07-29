object A_ProcessIntroduction extends App {
  import scalaz.concurrent.Task
  import scalaz.stream._
  import Process._

  val p: Process[Task, Int] = emitAll(Seq(1, 5, 10, 20))
  println(p.runLog.run) // should print Seq(1,5, 10, 20)

}
