rootProject.name = "bismuth"

sequenceOf(
    "core",
    "server",
    "client",
).forEach {
    val project = ":bismuth-$it"
    include(project)
    project(project).projectDir = file(it)
}
