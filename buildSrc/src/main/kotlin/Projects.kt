import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

val NamedDomainObjectContainer<KotlinSourceSet>.jvmMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named<KotlinSourceSet>("jvmMain")

val NamedDomainObjectContainer<KotlinSourceSet>.jvmTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named<KotlinSourceSet>("jvmTest")

val NamedDomainObjectContainer<KotlinSourceSet>.jsMain: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named<KotlinSourceSet>("jsMain")

val NamedDomainObjectContainer<KotlinSourceSet>.jsTest: NamedDomainObjectProvider<KotlinSourceSet>
    get() = named<KotlinSourceSet>("jsTest")
