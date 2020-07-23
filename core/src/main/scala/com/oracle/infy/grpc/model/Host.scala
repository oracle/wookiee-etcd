package com.oracle.infy.grpc.model

final case class Host(version: Long, address: String, port: Int, metadata: Map[String, String])
