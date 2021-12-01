package net.arver;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
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
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
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
     * Line separator.
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Date time formatter.
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Java source base directory.
     */
    @Parameter(property = "gen.javaBaseDir", defaultValue ="${basedir}/src/main/java/")
    private String javaBaseDir;

    /**
     * Resource base directory.
     */
    @Parameter(property = "gen.resourceBaseDir", defaultValue ="${basedir}/src/main/resources/")
    private String resourceBaseDir;

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

    /**
     * Entity source directory.
     */
    @Parameter(property = "gen.entityPackage")
    private String entityPackage;

    /**
     * Application object package name.
     */
    @Parameter(property = "gen.appObjectPackage")
    private String appObjectPackage;

    /**
     * Mapper directory.
     */
    @Parameter(property = "gen.mapperPackage", defaultValue = "")
    private String mapperPackage;

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

            final List<Context> contextList = config.getContexts();
            for (final Context context : contextList) {
                final PluginConfiguration demoPlugin = new PluginConfiguration();
                demoPlugin.setConfigurationType(DemoPlugin.class.getName());
                context.addPluginConfiguration(demoPlugin);
                /*final List<TableConfiguration> tableConfigurations = context.getTableConfigurations();
                for (final TableConfiguration tableConfiguration : tableConfigurations) {
                    if (tableConfiguration.getDomainObjectRenamingRule() == null) {
                        final DomainObjectRenamingRule domainObjectRenamingRule = new DomainObjectRenamingRule();
                        domainObjectRenamingRule.setSearchString("^");
                        domainObjectRenamingRule.setReplaceString("Base");
                        tableConfiguration.setDomainObjectRenamingRule(domainObjectRenamingRule);
                    }
                }*/
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
    }

}
