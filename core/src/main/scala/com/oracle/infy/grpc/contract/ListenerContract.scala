package com.oracle.infy.grpc.contract

import cats.data.EitherT
import com.oracle.infy.grpc.errors.Errors.WookieeGrpcError

protected[grpc] abstract case class ListenerContract[F[_], S[_[_], _]](
    hostnameServiceContract: HostnameServiceContract[F, S]
) {
  def shutdown: EitherT[F, WookieeGrpcError, Unit] = hostnameServiceContract.shutdown

  def startListening: EitherT[F, WookieeGrpcError, Unit]
}
