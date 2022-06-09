import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class BootstrapPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        val bootstrapDependencies by configurations.creating {
            isCanBeConsumed = false
            isCanBeResolved = true
            isTransitive = false
        }

        tasks.register<BootstrapTask>("releaseClient", "stable")

        tasks.withType<BootstrapTask> {
            this.group = "rsps"
            this.clientJar.fileProvider(provider { tasks["jar"].outputs.files.singleFile })

            dependsOn(bootstrapDependencies)
            dependsOn("jar")

            doLast {
                copy {
                    from(bootstrapDependencies)
                    into("${buildDir}/bootstrap/${type}/")
                }
                copy {
                    from(
                        "${buildDir}/repo/.",
                        "/libs/.",
                        "${buildDir}/libs/.",
                    )
                    into("${buildDir}/bootstrap/repo/")
                }
            }
        }
    }
}