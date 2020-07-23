package com.oracle.infy.grpc.tests

import cats.implicits.{catsSyntaxEq => _}
import com.oracle.infy.grpc.common.{HostGenerator, UTestScalaCheck}
import com.oracle.infy.grpc.json.HostSerde._
import com.oracle.infy.grpc.model.Host
import com.oracle.infy.grpc.utils.implicits._
import org.scalacheck.Prop
import org.scalacheck.Prop.forAll
import utest.{Tests, test}

object SerdeTest extends UTestScalaCheck with HostGenerator {

  implicit private class ScalacheckUtils[L](maybeError: Either[L, Boolean]) {
    def toProp: Prop = Prop(maybeError.getOrElse(false))
  }

  def tests: Tests = {
    val hostsSerdeIsSymmetric = {
      forAll { data: Host =>
        deserialize(serialize(data)).map(_ === data).toProp
      }
    }

    Tests {
      test("Host serde must be symmetric") {
        hostsSerdeIsSymmetric.checkUTest()
      }

    }
  }
}
