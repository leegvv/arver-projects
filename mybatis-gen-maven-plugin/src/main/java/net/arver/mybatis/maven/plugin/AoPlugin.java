package net.arver.mybatis.maven.plugin;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.api.XmlFormatter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.DocType;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.StringUtility;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * AoPlugin.
 * @author leegvv
 */
public class AoPlugin extends PluginAdapter {

    public AoPlugin () {
        shellCallback = new DefaultShellCallback(false);
    }

    private static final FullyQualifiedJavaType SERIALIZABLE_TYPE = new FullyQualifiedJavaType("java.io.Serializable");

    private static final FullyQualifiedJavaType MAPPER_TYPE = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper");

    private ShellCallback shellCallback = null;

    private List<Method> methods = new ArrayList<>();

    private List<Field> fields = new ArrayList<>();

    private static final String GEN_PACKAGE = "gen";

    private static final String MAPPER_SUFFIX = "Mapper";

    private static final String GENERATED_STR = "Generated";

    private static final String SUPER_MODEL_PREFIX = "Base";

    @Override
    public boolean validate(final List<String> warnings) {
        return true;
    }

    @Override
    public void setContext(final Context context) {
        final SqlMapGeneratorConfiguration sqlMapConfiguration = context.getSqlMapGeneratorConfiguration();
        if (sqlMapConfiguration != null) {
            final String targetPackage = sqlMapConfiguration.getTargetPackage() + "." + GEN_PACKAGE;
            sqlMapConfiguration.setTargetPackage(targetPackage);
        }
        final JavaClientGeneratorConfiguration clientConfiguration = context.getJavaClientGeneratorConfiguration();
        if (clientConfiguration != null) {
            final String targetPackage = clientConfiguration.getTargetPackage() + "." + GEN_PACKAGE;
            clientConfiguration.setTargetPackage(targetPackage);
        }
        final JavaModelGeneratorConfiguration modelConfiguration = context.getJavaModelGeneratorConfiguration();
        if (modelConfiguration != null) {
            final String exampleTargetPackage = modelConfiguration.getProperty("exampleTargetPackage");
            if (exampleTargetPackage == null) {
                modelConfiguration.addProperty("exampleTargetPackage", modelConfiguration.getTargetPackage() + "." + GEN_PACKAGE);
            }
        }
        super.setContext(context);
    }

    @Override
    public void initialized(final IntrospectedTable introspectedTable) {
        final String oldMapperType = introspectedTable.getMyBatis3JavaMapperType();
        final String mapperType = oldMapperType.replaceAll(MAPPER_SUFFIX + "$", GENERATED_STR + MAPPER_SUFFIX);
        introspectedTable.setMyBatis3JavaMapperType(mapperType);

        final String oldXmlMapperName = introspectedTable.getMyBatis3XmlMapperFileName();
        final String xmlFileName = oldXmlMapperName.replaceAll(MAPPER_SUFFIX + ".xml$", GENERATED_STR + MAPPER_SUFFIX + ".xml");
        introspectedTable.setMyBatis3XmlMapperFileName(xmlFileName);
    }

    @Override
    public boolean modelFieldGenerated(final Field field, final TopLevelClass topLevelClass, final IntrospectedColumn introspectedColumn, final IntrospectedTable introspectedTable, final ModelClassType modelClassType) {
        fields.add(field);
        return false;
    }

    @Override
    public boolean modelGetterMethodGenerated(final Method method, final TopLevelClass topLevelClass, final IntrospectedColumn introspectedColumn, final IntrospectedTable introspectedTable, final ModelClassType modelClassType) {
        methods.add(method);
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(final Method method, final TopLevelClass topLevelClass, final IntrospectedColumn introspectedColumn, final IntrospectedTable introspectedTable, final ModelClassType modelClassType) {
        methods.add(method);
        return false;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(final IntrospectedTable introspectedTable) {
        final JavaFormatter javaFormatter = context.getJavaFormatter();
        final String modelTargetProject = context.getJavaModelGeneratorConfiguration().getTargetProject();
        final String modelTargetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        final String javaFileEncoding = context.getProperty("javaFileEncoding");
        final String baseRecordType = introspectedTable.getBaseRecordType();
        final String shortName = baseRecordType.substring(baseRecordType.lastIndexOf(".") + 1);
        final TopLevelClass baseModelClass = new TopLevelClass(modelTargetPackage + "." + GEN_PACKAGE + "." + SUPER_MODEL_PREFIX + shortName);

        final List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<>();
        if (stringHasValue(modelTargetPackage)) {
            baseModelClass.addImportedType(SERIALIZABLE_TYPE);
            baseModelClass.addSuperInterface(SERIALIZABLE_TYPE);
            baseModelClass.setVisibility(JavaVisibility.PUBLIC);

            String remarks = introspectedTable.getRemarks();
            if (!StringUtility.stringHasValue(remarks)) {
                remarks = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
            }
            baseModelClass.addJavaDocLine("/**");
            baseModelClass.addJavaDocLine(" * " + remarks + ".");
            baseModelClass.addJavaDocLine(" * " + "由MybatisGenerator自动生成请勿修改");
            baseModelClass.addJavaDocLine(" */");

            if (!this.methods.isEmpty()) {
                for (final Method method : methods) {
                    baseModelClass.addMethod(method);
                }
            }
            if (!this.fields.isEmpty()) {
                for (final Field field : fields) {
                    baseModelClass.addField(field);
                }
            }
            final GeneratedJavaFile mapperJavaFile = new GeneratedJavaFile(baseModelClass, modelTargetProject, javaFileEncoding, javaFormatter);
            mapperJavaFiles.add(mapperJavaFile);
        }

        final String targetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();
        final String targetPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
        String mapperType = introspectedTable.getMyBatis3JavaMapperType();
        mapperType = mapperType.replaceAll("." + GEN_PACKAGE + "(.\\S+)" + GENERATED_STR + MAPPER_SUFFIX + "$", "$1" + MAPPER_SUFFIX);
        final Interface mapperInterface = new Interface(mapperType);

        if (stringHasValue(targetPackage)) {
            mapperInterface.addJavaDocLine("/**");
            mapperInterface.addJavaDocLine(" * " + mapperInterface.getType().getShortName() + ".");
            mapperInterface.addJavaDocLine(" * @author " + System.getProperty("user.name"));
            mapperInterface.addJavaDocLine(" */");
            mapperInterface.setVisibility(JavaVisibility.PUBLIC);
            final FullyQualifiedJavaType mapperSuperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
            mapperInterface.addImportedType(mapperSuperType);
            mapperInterface.addSuperInterface(mapperSuperType);
            mapperInterface.addImportedType(MAPPER_TYPE);
            mapperInterface.addAnnotation("@Mapper");
            try {
                final GeneratedJavaFile mapperJavaFile = new GeneratedJavaFile(mapperInterface, targetProject, javaFileEncoding, javaFormatter);
                final File mapperDir = shellCallback.getDirectory(targetProject, targetPackage.replaceAll("." + GEN_PACKAGE + "$", ""));
                final File mapperFile = new File(mapperDir, mapperJavaFile.getFileName());
                if (!mapperFile.exists()) {
                    mapperJavaFiles.add(mapperJavaFile);
                }
            } catch (ShellException e) {
                e.printStackTrace();
            }
        }
        this.methods.clear();
        this.fields.clear();
        return mapperJavaFiles;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(final IntrospectedTable introspectedTable) {
        final List<GeneratedXmlFile> generatedXmlFiles = new ArrayList<>();

        final XmlFormatter xmlFormatter = context.getXmlFormatter();
        final String targetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();

        final Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        final XmlElement rootElement = new XmlElement("mapper");
        final String generatedMapperType = introspectedTable.getMyBatis3JavaMapperType();
        final String mapperType = generatedMapperType.replaceAll(GEN_PACKAGE + ".(\\S+)" + GENERATED_STR + MAPPER_SUFFIX + "$", "$1" + MAPPER_SUFFIX);
        rootElement.addAttribute(new Attribute("namespace", mapperType));
        final XmlElement resultMapElement = new XmlElement("resultMap");
        resultMapElement.addAttribute(new Attribute("id", "BaseResultMap"));

        final String baseRecordType = introspectedTable.getBaseRecordType();
        resultMapElement.addAttribute(new Attribute("type", baseRecordType));
        resultMapElement.addAttribute(new Attribute("extends", generatedMapperType + ".BaseResultMap"));
        rootElement.addElement(resultMapElement);
        document.setRootElement(rootElement);
        String xmlPackage = introspectedTable.getMyBatis3XmlMapperPackage();
        xmlPackage = xmlPackage.replaceAll("." + GEN_PACKAGE + "$", "");
        String xmlFileName = introspectedTable.getMyBatis3XmlMapperFileName();
        xmlFileName = xmlFileName.replaceAll(GENERATED_STR + MAPPER_SUFFIX + ".xml$", MAPPER_SUFFIX + ".xml");
        try {
            final GeneratedXmlFile generatedXmlFile = new GeneratedXmlFile(document, xmlFileName, xmlPackage, targetProject, true, xmlFormatter);
            final File mapperDir = shellCallback.getDirectory(targetProject, xmlPackage);
            final File mapperFile = new File(mapperDir, generatedXmlFile.getFileName());
            if (!mapperFile.exists()) {
                generatedXmlFiles.add(generatedXmlFile);
            }
        } catch (ShellException e) {
            e.printStackTrace();
        }
        return generatedXmlFiles;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(final TopLevelClass topLevelClass, final IntrospectedTable introspectedTable) {
        final String mapperSuperClass = topLevelClass.getType().getPackageName() + "." + GEN_PACKAGE + "." + SUPER_MODEL_PREFIX + topLevelClass.getType().getShortName();
        final FullyQualifiedJavaType mapperSuperType = new FullyQualifiedJavaType(mapperSuperClass);
        topLevelClass.addImportedType(mapperSuperType);
        topLevelClass.setSuperClass(mapperSuperType);
        topLevelClass.addImportedType(SERIALIZABLE_TYPE);
        topLevelClass.addSuperInterface(SERIALIZABLE_TYPE);

        final String targetProject = context.getJavaModelGeneratorConfiguration().getTargetProject();
        final String targetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        try {
            final File mapperDir = shellCallback.getDirectory(targetProject, targetPackage);
            final File mapperFile = new File(mapperDir, topLevelClass.getType().getShortName() + ".java");
            if (mapperFile.exists()) {
                return false;
            }
        } catch (ShellException e) {
            e.printStackTrace();
        }
        return true;
    }
}
