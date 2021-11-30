package net.arver;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.maven.model.Resource;
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
import org.mybatis.generator.config.DomainObjectRenamingRule;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

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
     * Output Directory.
     */
    @Parameter(property = "mybatis.generator.outputDirectory",
            defaultValue = "${project.build.directory}/generated-sources/mybatis-generator", required = true)
    private File outputDirectory;

    /**
     * Location of the configuration file.
     */
    @Parameter(property = "mybatis.generator.configurationFile",
            defaultValue = "${project.basedir}/src/main/resources/generatorConfig.xml", required = true)
    private File configurationFile;

    /**
     * Comma delimited list of table names to generate.
     */
    @Parameter(property = "mybatis.generator.tableNames")
    private String tableNames;

    /**
     * Comma delimited list of contexts to generate.
     */
    @Parameter(property = "mybatis.generator.contexts")
    private String contexts;

    /**
     * Specifies whether the mojo overwrites existing Java files. Default is false.
     * <br>
     * Note that XML files are always merged.
     */
    @Parameter(property = "mybatis.generator.overwrite", defaultValue = "true")
    private boolean overwrite;

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

        final Set<String> fullyqualifiedTables = new HashSet<String>();
        if (StringUtility.stringHasValue(tableNames)) {
            final StringTokenizer st = new StringTokenizer(tableNames, ","); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    fullyqualifiedTables.add(s);
                }
            }
        }

        final Set<String> contextsToRun = new HashSet<String>();
        if (StringUtility.stringHasValue(contexts)) {
            final StringTokenizer st = new StringTokenizer(contexts, ","); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    contextsToRun.add(s);
                }
            }
        }

        try {
            ConfigurationParser cp = new ConfigurationParser(
                    project.getProperties(), warnings);
            Configuration config = cp.parseConfiguration(configurationFile);

            final List<Context> contexts = config.getContexts();
            for (final Context context : contexts) {
                final List<TableConfiguration> tableConfigurations = context.getTableConfigurations();
                for (final TableConfiguration tableConfiguration : tableConfigurations) {
                    if (tableConfiguration.getDomainObjectRenamingRule() == null) {
                        final DomainObjectRenamingRule domainObjectRenamingRule = new DomainObjectRenamingRule();
                        domainObjectRenamingRule.setSearchString("^");
                        domainObjectRenamingRule.setReplaceString("Base");
                        tableConfiguration.setDomainObjectRenamingRule(domainObjectRenamingRule);
                    }
                }
            }

            ShellCallback callback = new DefaultShellCallback(true);

            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                    callback, warnings);

            myBatisGenerator.generate(new NullProgressCallback(), contextsToRun, fullyqualifiedTables);

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

        if (project != null && outputDirectory != null
                && outputDirectory.exists()) {
            project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

            Resource resource = new Resource();
            resource.setDirectory(outputDirectory.getAbsolutePath());
            resource.addInclude("**/*.xml");
            project.addResource(resource);
        }
    }
}
