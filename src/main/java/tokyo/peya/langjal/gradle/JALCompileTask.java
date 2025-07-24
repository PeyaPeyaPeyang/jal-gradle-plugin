package tokyo.peya.langjal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.intellij.lang.annotations.MagicConstant;
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
    @Input
    private final DirectoryProperty inputDir = this.getProject().getObjects().directoryProperty()
                                                   .convention(this.getProject().getLayout().getProjectDirectory()
                                                             .dir("src/main/jal")
                                             );
    @Input
    private final DirectoryProperty outputDir = this.getProject().getObjects().directoryProperty()
                                                    .convention(this.getProject().getLayout().getProjectDirectory()
                                                             .dir("classes/jal")
                                             );
    @Input
    private final Property<Boolean> computeStackFrameMap = this.getProject().getObjects().property(Boolean.class)
                                                               .convention(true);

    @Input
    private final Property<Boolean> includeLineNumberTable = this.getProject().getObjects().property(Boolean.class)
                                                               .convention(true);
    @Input
    private final Property<Boolean> noDebugInfo = this.getProject().getObjects().property(Boolean.class)
                                                                 .convention(false);

    @TaskAction
    public void compile() throws IOException
    {
        File outputDirFile = this.outputDir.get().getAsFile();
        if (!outputDirFile.exists())
            if (!outputDirFile.mkdirs())
                throw new IOException("Failed to create output directory: " + outputDirFile.getAbsolutePath());

        File inputDirFile = this.inputDir.get().getAsFile();
        Logger logger = this.getProject().getLogger();
        logger.info("Compiling JAL files from {} to {}", inputDirFile.getAbsolutePath(), inputDirFile.getAbsolutePath());

        this.clearOutputDirectory(outputDirFile, logger);
        this.actualCompile(inputDirFile, outputDirFile, logger);
    }

    private void clearOutputDirectory(@NotNull File outputDirFile, @NotNull Logger logger)
    {
        logger.info("Cleaning the output directory..l.");
        try (Stream<Path> files = Files.walk(outputDirFile.toPath()))
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
            logger.error("Failed to clean output directory: {}", outputDirFile.getAbsolutePath(), e);
        }
    }

    @MagicConstant(valuesFromClass = CompileSettings.class)
    private int getCompileFlags()
    {
        if (this.noDebugInfo.get())
            return CompileSettings.REQUIRED_ONLY;

        @MagicConstant(valuesFromClass = CompileSettings.class)
        int flags = CompileSettings.NONE;
        if (this.computeStackFrameMap.get())
            flags |= CompileSettings.COMPUTE_STACK_FRAME_MAP;
        if (this.includeLineNumberTable.get())
            flags |= CompileSettings.INCLUDE_LINE_NUMBER_TABLE;

        return flags;
    }

    private void actualCompile(@NotNull File inputDirFile, @NotNull File outputDirFile, @NotNull Logger logger) throws IOException
    {
        GradleCompileReporter reporter = new GradleCompileReporter(logger);
        JALFileCompiler compiler = new JALFileCompiler(
                reporter,
                outputDirFile.toPath(),
                this.getCompileFlags()
        );

        try(Stream<Path> files = Files.walk(inputDirFile.toPath()))
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
