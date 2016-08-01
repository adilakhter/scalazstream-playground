// Discussion: https://www.reddit.com/r/scala/comments/3xavvi/scalaz_and_scalazstream_simplify_things/
// Copied from:  http://lpaste.net/raw/147309

package io.coyoneda

import java.io.File
import concurrent.duration._
import scalaz.{ \/, Nondeterminism }
import scalaz.concurrent.Strategy.{ DefaultStrategy, DefaultTimeoutScheduler }
import scalaz.concurrent.Task
import scalaz.stream._
import scalaz.syntax.either._
import it.sauronsoftware.ftp4j.{ FTPClient, FTPDataTransferListener }

object FTP {
  val target = "/tmp/ughxml/"
  implicit val S = DefaultTimeoutScheduler

  def download(host: String, user: String, password: String, directory: String, pattern: String, target: String): Task[List[File]] = for {
    listC <- Task.delay(new FTPClient())
    _ <- Task.delay(listC.connect(host))
    _ <- Task.delay(listC.login(user, password))
    _ <- Task.delay(listC.changeDirectory(directory))
    task <- Task.delay(listC.list(pattern).map(_.getName).toList).onFinish(_ => Task.delay(listC.disconnect(true))).flatMap { files =>
      Nondeterminism[Task].gatherUnordered(for {
        file <- files
      } yield Task.async {
        (cb: Throwable \/ File => Unit) =>
          val dlC = new FTPClient()
          val result = new File(target + file)
          dlC.connect(host)
          dlC.login(user, password)
          dlC.changeDirectory(directory)
          dlC.download(file, result, new FTPDataTransferListener {
            def started(): Unit = ()

            def transferred(size: Int): Unit = ()

            def completed(): Unit = cb {
              dlC.disconnect(true); result.right
            }

            def aborted(): Unit = cb {
              dlC.disconnect(true); new RuntimeException(s"FTP transfer of $file was aborted.").left
            }

            def failed(): Unit = cb {
              dlC.disconnect(true); new RuntimeException(s"FTP transfer of $file failed.").left
            }
          })
      })
    }
  } yield task

  def attemptRepeatedly[A](schedule: Process[Task, Any])(p: Process[Task, A]): Process[Task, A] = {
    val step    = p.append(schedule.kill).attempt()
    val retries = schedule.zip(step.repeat).map(_._2)
    (step ++ retries).last.flatMap(_.fold(Process.fail, Process.emit))
  }

  def clean(dirPath: String): Task[Unit] = {
    val dir = new File(dirPath)
    Task.delay {
      for {
        file <- dir.listFiles()
      } file.delete()
    }
  }

  val cleanDL: Process[Task, List[File]] =
    Process.eval_(clean(target)) ++
    Process.eval(download("example.com", "user", "password", "server/directory", "*.xml.gz", target))

  val periodicDL: Process[Task, List[File]] = time.awakeEvery(1.day).zip(cleanDL.repeat).map(_._2)
}
