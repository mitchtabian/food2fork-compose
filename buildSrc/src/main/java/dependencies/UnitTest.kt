package dependencies

object UnitTest {

  const val jupiter_api = "org.junit.jupiter:junit-jupiter-api:${Versions.junit_jupiter}"
  const val jupiter_params = "org.junit.jupiter:junit-jupiter-params:${Versions.junit_jupiter}"
  const val jupiter_engine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit_jupiter}"

  const val mock_web_server = "com.squareup.okhttp3:mockwebserver:${Versions.okHttp}"
  const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
}