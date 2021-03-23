import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.apply

fun Project.applyPublishing(): Unit = apply(from = "../publishing.gradle.kts")

val Project.publishing: PublishingExtension
    get() =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("publishing") as PublishingExtension
