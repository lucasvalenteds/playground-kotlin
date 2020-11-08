rootProject.name = "playground-kotlin"

fun module(name: String) {
    include(name)
    project(":$name").projectDir = File("modules/$name")
}

module("apache_activemq")
module("apache_mahout")
module("beyondeye_reduks")
module("cdimascio_java-dotenv")
module("deeplearning4j_deeplearning4j")
module("dialogflow_dialogflow-java-client")
module("fasterxml_jackson-module-kotlin")
module("h0tk3y_regex-dsl")
module("jetbrains_exposed")
module("jetbrains_kotlin")
module("kizitonwose_time")
module("kodein-framework_kodein-di")
module("kotlin_kotlinx.coroutines")
module("kotlin_kotlinx.serialization")
module("mockk_mockk")
module("npryce_hamkrest")
module("robstoll_atrium")
module("spoptchev_kotlin-preconditions")
module("stanfordnlp_corenlp")
