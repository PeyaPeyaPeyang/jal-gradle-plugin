package tokyo.peya.langjal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;
import tokyo.peya.langjal.compiler.CompileSettings;
import tokyo.peya.langjal.compiler.JALFileCompiler;
import tokyo.peya.langjal.compiler.exceptions.CompileErrorException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class JALCompileTask extends DefaultTask
{
    private File inputDir = new File(this.getProject().getLayout().getProjectDirectory().getAsFile(), "src/main/jal");
    private File outputDir = new File(this.getProject().getLayout().getBuildDirectory().getAsFile().get(), "classes/jal");

    @InputDirectory
    public File getInputDir()
    {
        return this.inputDir;
    }

    public void setInputDir(File inputDir)
    {
        this.inputDir = inputDir;
    }

    @OutputDirectory
    public File getOutputDir()
    {
        return this.outputDir;
    }

    public void setOutputDir(File outputDir)
    {
        this.outputDir = outputDir;
    }

    @TaskAction
    public void compile() throws IOException
    {
        if (!this.outputDir.exists())
            if (!this.outputDir.mkdirs())
                throw new IOException("Failed to create output directory: " + this.outputDir.getAbsolutePath());

        Logger logger = this.getProject().getLogger();
        logger.info("Compiling JAL files from {} to {}", this.inputDir.getAbsolutePath(), this.outputDir.getAbsolutePath());

        this.clearOutputDirectory(logger);
        this.actualCompile(logger);
    }

    private void clearOutputDirectory(@NotNull Logger logger)
    {
        logger.info("Cleaning the output directory..l.");
        try (Stream<Path> files = Files.walk(this.outputDir.toPath()))
        {

            files.sorted(Comparator.reverseOrder()) // Sort in reverse order to delete files before directories
                 .forEach(path -> {
                     try
                     {
                         Files.deleteIfExists(path);
                     }
                     catch (IOException e)
                     {
                         logger.error("Failed to delete file: {}", path, e);
                     }
                 });
        }
        catch (IOException e)
        {
            logger.error("Failed to clean output directory: " + this.outputDir.getAbsolutePath(), e);
        }
    }

    private void actualCompile(@NotNull Logger logger) throws IOException
    {
        GradleCompileReporter reporter = new GradleCompileReporter(logger);
        JALFileCompiler compiler = new JALFileCompiler(
                reporter,
                this.outputDir.toPath(),
                CompileSettings.COMPUTE_STACK_FRAME_MAP
        );

        try(Stream<Path> files = Files.walk(this.inputDir.toPath()))
        {
            files.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".jal"))
                 .forEach(file -> {
                     try
                     {
                         logger.info("Compiling file: {}", file);
                         compiler.compile(file);
                     }
                     catch (CompileErrorException e)
                     {
                         reporter.postError("Failed to compile file: " + file, e, file);
                     }
                 });
        }

    }
}
