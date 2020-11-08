import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStopContainer
import com.bmuschko.gradle.docker.tasks.container.DockerRemoveContainer
import com.bmuschko.gradle.docker.tasks.image.DockerPullImage

apply {
    plugin("com.bmuschko.docker-remote-api")
}

dependencies {
    implementation("org.apache.activemq", "activemq-all", "5.15.8")
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.11.1")
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9.8")
}

tasks {
    val serverContainerName = "activemq"
    val (name, version) = "rmohr/activemq" to "5.15.6-alpine"

    val pullDockerImage by creating(DockerPullImage::class) {
        image.set("$name:$version")
    }

    val createContainer by creating(DockerCreateContainer::class) {
        dependsOn(pullDockerImage)
        targetImageId("$name:$version")
        containerName.set(serverContainerName)
        portSpecs.set(listOf("61616:61616", "8161:8161"))
    }

    val startContainer by creating(DockerStartContainer::class) {
        dependsOn(createContainer)
        targetContainerId(createContainer.getContainerId())
    }

    val stopContainer by creating(DockerStopContainer::class) {
        dependsOn(startContainer)
        targetContainerId(createContainer.getContainerId())
    }

    val removeContainer by creating(DockerRemoveContainer::class) {
        dependsOn(stopContainer)
        targetContainerId(createContainer.getContainerId())
    }

    withType<Test> {
        if (name == "integTest") {
            dependsOn(startContainer)
            finalizedBy(removeContainer)
        }
    }

    withType<JavaExec> {
        dependsOn(startContainer)
        finalizedBy(removeContainer)
    }
}
