package tokyo.peya.langjal.gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JALCompileTaskTest {
    private Project project;
    private JALCompileTask task;

    @SuppressWarnings("deprecation")
    @BeforeEach
    void setUp() {
        this.project = ProjectBuilder.builder().build();
        this.task = this.project.getTasks().create("jalCompile", JALCompileTask.class);
    }

    @Test
    void testInputDirGetterSetter() {
        File testDir = new File("test/input");
        this.task.setInputDir(testDir);
        assertEquals(testDir, this.task.getInputDir());
    }

    @Test
    void testOutputDirGetterSetter() {
        File testDir = new File("test/output");
        this.task.setOutputDir(testDir);
        assertEquals(testDir, this.task.getOutputDir());
    }

    @Test
    void testDefaultDirs() {
        assertTrue(this.task.getInputDir().getPath().endsWith("src/main/jal"));
        assertTrue(this.task.getOutputDir().getPath().contains("classes/jal"));
    }

    @Test
    void testCompileJalFile() throws Exception {
        Path tempInput = Files.createTempDirectory("jalTestInput");
        Path tempOutput = Files.createTempDirectory("jalTestOutput");
        // テスト用JALファイル作成
        Path jalFile = tempInput.resolve("Test.jal");
        Files.writeString(jalFile, """
                public class Test {}
                """);

        this.task.setInputDir(tempInput.toFile());
        this.task.setOutputDir(tempOutput.toFile());

        // コンパイル実行
        this.task.compile();

        // 出力ファイルが生成されているか確認
        boolean found = Files.walk(tempOutput)
                .anyMatch(p -> p.getFileName().toString().equals("Test.class"));
        assertTrue(found, "コンパイル後にTest.classが生成されていること");
    }
}
