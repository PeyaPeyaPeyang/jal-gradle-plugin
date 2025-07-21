package tokyo.peya.langjal.gradle;

import org.antlr.v4.runtime.ParserRuleContext;
import org.gradle.api.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tokyo.peya.langjal.compiler.CompileReporter;
import tokyo.peya.langjal.compiler.exceptions.CompileErrorException;

import java.nio.file.Path;

public class GradleCompileReporter implements CompileReporter
{
    private final Logger logger;

    public GradleCompileReporter(@NotNull Logger logger)
    {
        this.logger = logger;
    }

    @Override
    public void postWarning(@NotNull String s, @Nullable Path path)
    {
        if (path == null)
            this.logger.warn(s);
        else
            this.logger.warn("{}: {}", path, s);
    }

    @Override
    public void postInfo(@NotNull String s, @Nullable Path path)
    {
        if (path == null)
            this.logger.quiet(s);
        else
            this.logger.quiet("{}: {}", path, s);
    }

    @Override
    public void postError(@NotNull String s, @Nullable Path path)
    {
        if (path == null)
            this.logger.error(s);
        else
            this.logger.error("{}: {}", path, s);
    }

    @Override
    public void postError(@NotNull String s, @NotNull CompileErrorException e, @Nullable Path path)
    {
        if (path == null)
            this.logger.error(s, e);
        else
            this.logger.error("{}: {}", path, s, e);
    }

    @Override
    public void postWarning(@NotNull String s, @Nullable Path path, long l, long l1, long l2)
    {
        if (path == null)
            this.logger.warn(s);
        else
            this.logger.warn("{}: {} at line {}, column {}, offset {}", path, s, l, l1, l2);
    }

    @Override
    public void postWarning(@NotNull String s, @NotNull Path path, @NotNull ParserRuleContext parserRuleContext)
    {
        this.logger.warn("{}: {} at line {}, column {}, offset {}", path, s,
                parserRuleContext.getStart().getLine(),
                parserRuleContext.getStart().getCharPositionInLine(),
                parserRuleContext.getStart().getStartIndex()
        );
    }
}
