package com.oracle.infy.grpc

import cats.effect.concurrent.Deferred
import cats.effect.{ConcurrentEffect, ContextShift, IO}
import com.oracle.infy.grpc.common.ConstableCommon
import com.oracle.infy.grpc.contract.ListenerContract
import com.oracle.infy.grpc.impl.{Fs2CloseableImpl, MockHostNameService, WookieeGrpcHostListener}
import com.oracle.infy.grpc.model.Host
import com.oracle.infy.grpc.tests.{GrpcListenerTest, SerdeTest}
import fs2.Stream
import fs2.concurrent.Queue

import scala.concurrent.ExecutionContext

object UnitTestConstable extends ConstableCommon {

  def main(args: Array[String]): Unit = {
    implicit val ec: ExecutionContext = ExecutionContext.global
    implicit val cs: ContextShift[IO] = IO.contextShift(ec)
    implicit val concurrent: ConcurrentEffect[IO] = IO.ioConcurrentEffect

    def pushMessagesFuncAndListenerFactory(
        callback: Set[Host] => IO[Unit]
    ): IO[(Set[Host] => IO[Unit], () => IO[Unit], ListenerContract[IO, Stream])] = {
      for {
        queue <- Queue.unbounded[IO, Set[Host]]
        killswitch <- Deferred[IO, Either[Throwable, Unit]]

      } yield {
        val pushMessagesFunc = { hosts: Set[Host] =>
          queue.enqueue1(hosts)
        }
        val listener: ListenerContract[IO, Stream] =
          new WookieeGrpcHostListener(
            callback,
            new MockHostNameService(Fs2CloseableImpl(queue.dequeue, killswitch)),
            discoveryPath = ""
          )

        val cleanup: () => IO[Unit] = () => {
          IO(())
        }

        (pushMessagesFunc, cleanup, listener)
      }
    }

    val grpcTests = GrpcListenerTest.tests(pushMessagesFuncAndListenerFactory)

    runTestsAsync(
      List(
        (SerdeTest.tests, "Serde"),
        (grpcTests, "GRPC Tests")
      )
    )
    ()
  }

}
