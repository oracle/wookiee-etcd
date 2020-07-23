package com.oracle.infy.grpc.contract

import cats.data.EitherT
import com.oracle.infy.grpc.contract.StreamContract.StreamError

protected[grpc] trait CloseableStreamContract[F[_], A, S[_[_], _]] extends StreamContract[F, A, S] {

  def shutdown(): EitherT[F, StreamError, Unit]
}
