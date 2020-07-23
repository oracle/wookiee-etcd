package com.oracle.infy.grpc.utils

import java.util
import java.util.concurrent.ConcurrentHashMap

import cats.Monad
import cats.data.EitherT
import cats.effect.Sync
import cats.implicits._

protected[grpc] object implicits {

  implicit class MultiversalEquality[T](left: T) {
    def ===(right: T): Boolean = left == right //scalafix:ok
    def /==(right: T): Boolean = left != right //scalafix:ok
    def =/=(right: T): Boolean = left /== right
  }

  implicit class ToEitherT[A, F[_]: Monad: Sync](lhs: F[A]) {

    def toEitherT[B](handler: Throwable => B): EitherT[F, B, A] = {
      EitherT(
        lhs
          .map(_.asRight[B])
          .handleErrorWith(t => handler(t).asLeft[A].pure[F])
      )
    }
  }

  implicit class Java2ScalaConverterList[T](lhs: java.util.List[T]) {

    def asScala: List[T] = {
      val buf = scala.collection.mutable.Buffer[T]()
      val itr = lhs.iterator()
      while (itr.hasNext) {
        buf += itr.next()
      }
      buf.toList
    }
  }

  implicit class Scala2JavaConverterList[T](lhs: Seq[T]) {

    def asJava: java.util.List[T] = {
      val list = new util.ArrayList[T]()
      lhs.foreach(list.add)
      list
    }
  }

  implicit class Scala2JavaConverterConcurrentHashMap[V](lhs: ConcurrentHashMap[_, V]) {

    def valueSet: Set[V] = {
      val itr = lhs.values().iterator()
      val buf = scala.collection.mutable.Buffer[V]()
      while (itr.hasNext) {
        buf += itr.next()
      }
      buf.toList.toSet
    }
  }
}
