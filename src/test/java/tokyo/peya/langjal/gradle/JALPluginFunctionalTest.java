package tokyo.peya.langjal.gradle;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.*;

public class JALPluginFunctionalTest
{
    @TempDir
    Path projectDir;

    @Test
    void compileJAL_generatesClassFiles() throws IOException
    {
        writeFile(this.projectDir.resolve("settings.gradle"), "rootProject.name = 'test-project'\n");
        writeFile(
                this.projectDir.resolve("build.gradle"), """
                        plugins {
                          id 'java'
                          id 'tokyo.peya.langjal'
                        }
                        repositories { mavenCentral(); mavenLocal() }
                        """
        );

        Path jalDir = this.projectDir.resolve("src/main/jal");
        Files.createDirectories(jalDir);
        writeFile(jalDir.resolve("HelloWorld.jal"), """
                public class HelloWorld {
                  public static main([Ljava/lang/String;)V {
                    getstatic java/lang/System->out:Ljava/io/PrintStream;
                    ldc "Hello, World!"
                    invokevirtual java/io/PrintStream->println(Ljava/lang/String;)V
                    return
                  }
                }
                """
        );

        BuildResult result = GradleRunner.create()
                .withProjectDir(this.projectDir.toFile())
                .withArguments("compileJAL", "--stacktrace")
                .withPluginClasspath()
                .build();

        assertNotNull(result.task(":compileJAL"));
        assertEquals(TaskOutcome.SUCCESS, result.task(":compileJAL").getOutcome());

        Path outputClass = this.projectDir.resolve("build/classes/jal/HelloWorld.class");
        assertTrue(Files.exists(outputClass), "Expected class file to be generated: " + outputClass);
    }

    @Test
    void jar_includesJalCompiledClasses() throws IOException
    {
        writeFile(this.projectDir.resolve("settings.gradle"), "rootProject.name = 'test-project'\n");
        writeFile(
                this.projectDir.resolve("build.gradle"), """
                        plugins {
                          id 'java'
                          id 'tokyo.peya.langjal'
                        }
                        repositories { mavenCentral(); mavenLocal() }
                        """
        );

        Path jalDir = this.projectDir.resolve("src/main/jal");
        Files.createDirectories(jalDir);
        writeFile(jalDir.resolve("HelloWorld.jal"), """
                public class HelloWorld {
                  public static main([Ljava/lang/String;)V {
                    return
                  }
                }
                """
        );

        BuildResult result = GradleRunner.create()
                .withProjectDir(this.projectDir.toFile())
                .withArguments("jar", "--stacktrace")
                .withPluginClasspath()
                .build();

        assertNotNull(result.task(":jar"));
        assertEquals(TaskOutcome.SUCCESS, result.task(":jar").getOutcome());

        Path jarPath = this.projectDir.resolve("build/libs/test-project.jar");
        assertTrue(Files.exists(jarPath), "Expected jar to exist: " + jarPath);

        try (JarFile jar = new JarFile(jarPath.toFile()))
        {
            assertNotNull(jar.getEntry("HelloWorld.class"), "Expected HelloWorld.class to be present in jar");
        }
    }

    private static void writeFile(Path path, String content) throws IOException
    {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }
}

