package server

import zio.test.*
import zio.test.Assertion.equalTo
import zio.http.*

object ServerTests extends ZIOSpecDefault {
  def spec =
    suite("api")(
      test("health") {
        val app = server.routes.HealthRoutes.routes.toHttpApp
        val req = Request.get(URL(Path("/api/health")))
        assertZIO(app.runZIO(req))(equalTo(Response.ok))
      }
    )
}
