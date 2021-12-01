package net.arver;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.util.messages.Messages;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(
        name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.TEST
)
public class GenMojo extends AbstractMojo {

    /**
     * Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    /**
     * Location of the configuration file.
     */
    @Parameter(property = "mybatis.generator.configurationFile",
            defaultValue = "${project.basedir}/src/main/resources/generatorConfig.xml", required = true)
    private File configurationFile;

    /**
     * 模版配置.
     */
    private static final freemarker.template.Configuration TEMPLATE_CFG =
            new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_22);

    public void execute() throws MojoExecutionException {
        if (configurationFile == null) {
            throw new MojoExecutionException(
                    Messages.getString("RuntimeError.0")); //$NON-NLS-1$
        }

        List<String> warnings = new ArrayList<String>();

        if (!configurationFile.exists()) {
            throw new MojoExecutionException(Messages.getString(
                    "RuntimeError.1", configurationFile.toString())); //$NON-NLS-1$
        }

        try {
            ConfigurationParser cp = new ConfigurationParser(
                    project.getProperties(), warnings);
            Configuration config = cp.parseConfiguration(configurationFile);

            final List<Context> contextList = config.getContexts();
            for (final Context context : contextList) {
                final SqlMapGeneratorConfiguration sqlMapConfiguration = context.getSqlMapGeneratorConfiguration();
                if (sqlMapConfiguration != null) {
                    final String targetPackage = String.join(".", sqlMapConfiguration.getTargetPackage(), "gen");
                    sqlMapConfiguration.setTargetPackage(targetPackage);
                }
                final JavaModelGeneratorConfiguration javaModelConfiguration = context.getJavaModelGeneratorConfiguration();
                if (javaModelConfiguration != null) {
                    final String exampleTargetPackage = javaModelConfiguration.getProperty("exampleTargetPackage");
                    if (exampleTargetPackage == null) {
                        javaModelConfiguration.addProperty("exampleTargetPackage", String.join(".", javaModelConfiguration.getTargetPackage(), "gen"));
                    }
                }
                final JavaClientGeneratorConfiguration javaClientConfiguration = context.getJavaClientGeneratorConfiguration();
                if (javaClientConfiguration != null) {
                    final String targetPackage = String.join(".", javaClientConfiguration.getTargetPackage(), "gen");
                    javaClientConfiguration.setTargetPackage(targetPackage);
                }
            }

            ShellCallback callback = new DefaultShellCallback(true);

            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                    callback, warnings);
            Set<String> fullyqualifiedTables = new HashSet<>();
            Set<String> contexts = new HashSet<>();
            myBatisGenerator.generate(new NullProgressCallback(), contexts, fullyqualifiedTables);

        } catch (XMLParserException e) {
            for (String error : e.getErrors()) {
                getLog().error(error);
            }

            throw new MojoExecutionException(e.getMessage());
        } catch (SQLException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (InvalidConfigurationException e) {
            for (String error : e.getErrors()) {
                getLog().error(error);
            }

            throw new MojoExecutionException(e.getMessage());
        } catch (InterruptedException e) {
            // ignore (will never happen with the DefaultShellCallback)
        }

        for (String error : warnings) {
            getLog().warn(error);
        }
    }

}
