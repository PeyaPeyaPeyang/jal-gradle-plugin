package tokyo.peya.langjal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.tasks.Jar;
import org.jetbrains.annotations.NotNull;

public class JALPlugin implements Plugin<Project>
{
    @Override
    public void apply(@NotNull Project target)
    {
        target.getTasks().register(
                "compileJAL",
                JALCompileTask.class,
                task -> {
                    task.setGroup("build");
                    task.setDescription("Compiles JAL source files into JVM class files.");
                }
        );

        target.afterEvaluate(project -> {
            if (project.getPlugins().hasPlugin("java")) {
                project.getTasks().named("compileJava", JavaCompile.class, JALPlugin::hookJavaCompileTask);
                project.getTasks().named("jar", Jar.class, JALPlugin::hookJarTask);
            }
        });
    }

    private static void hookJavaCompileTask(@NotNull JavaCompile task)
    {
        Project project = task.getProject();
        JALCompileTask jalCompileTask = (JALCompileTask) project.getTasks().getByName("compileJAL");
        task.dependsOn("compileJAL");

        // クラスパスに追加
        task.getClasspath().plus(project.files(jalCompileTask.getOutputDir()));
    }

    private static void hookJarTask(@NotNull Jar task)
    {
        Project project = task.getProject();
        JALCompileTask jalCompileTask = (JALCompileTask) project.getTasks().getByName("compileJAL");
        task.dependsOn("compileJAL");

        // JARに含めるクラスファイルを設定
        task.from(jalCompileTask.getOutputDir(), copySpec -> {
            copySpec.include("**/*.class");
        });
    }
}
