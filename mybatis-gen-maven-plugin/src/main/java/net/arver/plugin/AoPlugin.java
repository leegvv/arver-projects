package net.arver.plugin;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AoPlugin.
 * @author leegvv
 */
public class AoPlugin extends PluginAdapter {

    public AoPlugin () {
        shellCallback = new DefaultShellCallback(false);
    }

    private static final FullyQualifiedJavaType SERIALIZABLE_TYPE = new FullyQualifiedJavaType("java.io.Serializable");

    private ShellCallback shellCallback = null;

    private List<Method> methods = new ArrayList<>();

    private List<Field> fields = new ArrayList<>();

    @Override
    public boolean validate(final List<String> warnings) {
        return true;
    }

    @Override
    public void setContext(final Context context) {
        final SqlMapGeneratorConfiguration sqlMapConfiguration = context.getSqlMapGeneratorConfiguration();
        if (sqlMapConfiguration != null) {
            final String targetPackage = sqlMapConfiguration.getTargetPackage() + ".gen";
            sqlMapConfiguration.setTargetPackage(targetPackage);
        }
        final JavaClientGeneratorConfiguration clientConfiguration = context.getJavaClientGeneratorConfiguration();
        if (clientConfiguration != null) {
            final String targetPackage = clientConfiguration.getTargetPackage() + ".gen";
            clientConfiguration.setTargetPackage(targetPackage);
        }
        final JavaModelGeneratorConfiguration modelConfiguration = context.getJavaModelGeneratorConfiguration();
        if (modelConfiguration != null) {
            final String exampleTargetPackage = modelConfiguration.getProperty("exampleTargetPackage");
            if (exampleTargetPackage == null) {
                modelConfiguration.addProperty("exampleTargetPackage", modelConfiguration.getTargetPackage() + ".gen");
            }
        }
        super.setContext(context);
    }

    @Override
    public void initialized(final IntrospectedTable introspectedTable) {
        final String oldMapperType = introspectedTable.getMyBatis3JavaMapperType();
        final Pattern mapperPattern = Pattern.compile("Mapper$");
        final Matcher mapperMatcher = mapperPattern.matcher(oldMapperType);
        final String mapperType = mapperMatcher.replaceAll("GeneratedMapper");
        introspectedTable.setMyBatis3JavaMapperType(mapperType);

        final String oldXmlMapperName = introspectedTable.getMyBatis3XmlMapperFileName();
        final Pattern xmlPattern = Pattern.compile("Mapper$");
        final Matcher xmlMatcher = xmlPattern.matcher(oldXmlMapperName);
        final String xmlFileName = xmlMatcher.replaceAll("GeneratedMapper");
        introspectedTable.setMyBatis3XmlMapperFileName(xmlFileName);
    }

    @Override
    public boolean modelFieldGenerated(final Field field, final TopLevelClass topLevelClass, final IntrospectedColumn introspectedColumn, final IntrospectedTable introspectedTable, final ModelClassType modelClassType) {
        fields.add(field);
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(final Method method, final TopLevelClass topLevelClass, final IntrospectedColumn introspectedColumn, final IntrospectedTable introspectedTable, final ModelClassType modelClassType) {
        methods.add(method);
        return true;
    }

    @Override
    public boolean modelSetterMethodGenerated(final Method method, final TopLevelClass topLevelClass, final IntrospectedColumn introspectedColumn, final IntrospectedTable introspectedTable, final ModelClassType modelClassType) {
        methods.add(method);
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(final IntrospectedTable introspectedTable) {
        final JavaFormatter javaFormatter = context.getJavaFormatter();
        final String targetProject = context.getJavaModelGeneratorConfiguration().getTargetProject();
        final String targetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        final String javaFileEncoding = context.getProperty("javaFileEncoding");
        final String baseRecordType = introspectedTable.getBaseRecordType();
        final String shortName = baseRecordType.substring(baseRecordType.lastIndexOf(".") + 1);
        final Interface mapperInterface = new Interface(targetPackage + ".gen.Base" + shortName);

        final List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<>();
        if (stringHasValue(targetPackage)) {
            mapperInterface.addImportedType(SERIALIZABLE_TYPE);

            mapperInterface.setVisibility(JavaVisibility.PUBLIC);
            final FullyQualifiedJavaType baseJavaType = mapperInterface.getType();
            if (!this.methods.isEmpty()) {
                for (final Method method : methods) {
                    mapperInterface.addMethod(method);
                }
            }
            if (!this.fields.isEmpty()) {
                for (final Field field : fields) {
                    mapperInterface.addField(field);
                }
            }

            try {
                final GeneratedJavaFile mapperJavaFile = new GeneratedJavaFile(mapperInterface, targetProject, javaFileEncoding, javaFormatter);
                final File mapperDir = shellCallback.getDirectory(targetProject, targetPackage);
                final File mapperFile = new File(mapperDir, mapperJavaFile.getFileName());
                if (!mapperFile.exists()) {
                    mapperJavaFiles.add(mapperJavaFile);
                }
            } catch (ShellException e) {
                e.printStackTrace();
            }
        }
        this.methods.clear();
        this.methods.clear();
        return mapperJavaFiles;
    }
}
