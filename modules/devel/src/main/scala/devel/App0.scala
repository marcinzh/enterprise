package devel
import turbolift.Extensions._
import enterprise._
import enterprise.model._


object App0:
  def run() =
    Server.serve:
      Response(Status.Ok).pure_!!
    .handleWith:
      Server.undertow &&&!
      Config.localhost(9000).toHandler
    .unsafeRunST
