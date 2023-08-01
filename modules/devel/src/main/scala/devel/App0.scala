package devel
import turbolift.Extensions._
import enterprise._
import enterprise.server._


object App0:
  def run() =
    Server.serve:
      Response(Status.Ok).pure_!!
    .handleWith:
      Server.undertow &&&!
      Config.localhost(9000).toHandler
    .unsafeRunST
