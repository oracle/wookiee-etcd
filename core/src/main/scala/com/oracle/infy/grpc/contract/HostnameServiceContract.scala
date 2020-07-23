package com.oracle.infy.grpc.contract

import cats.data.EitherT
import com.oracle.infy.grpc.errors.Errors.WookieeGrpcError
import com.oracle.infy.grpc.model.Host

protected[grpc] trait HostnameServiceContract[F[_], S[_[_], _]] {
  def shutdown: EitherT[F, WookieeGrpcError, Unit]

  def hostStream(serviceId: String): EitherT[F, WookieeGrpcError, CloseableStreamContract[F, Set[Host], S]]
}
