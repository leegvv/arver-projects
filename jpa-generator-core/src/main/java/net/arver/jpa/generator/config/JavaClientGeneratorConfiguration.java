package net.arver.jpa.generator.config;

import java.util.List;
import net.arver.jpa.generator.util.MessageUtil;
import net.arver.jpa.generator.util.StringUtil;

/**
 * .
 * @author li gu
 * @version 1.0.0.0
 **/
public class JavaClientGeneratorConfiguration extends TypedPropertyHolder{

    private String targetPackage;

    private String targetProject;

    public JavaClientGeneratorConfiguration() {
        super();
    }

    public void validate(final List<String> errors, final String contextId) {
        if (StringUtil.isEmpty(targetProject)) {
            errors.add(MessageUtil.getString("ValidationError.2", contextId));
        }
        if (StringUtil.isEmpty(targetPackage)) {
            errors.add(MessageUtil.getString("ValidationError.12","javaClientGenerator", contextId));
        }
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(final String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public String getTargetProject() {
        return targetProject;
    }

    public void setTargetProject(final String targetProject) {
        this.targetProject = targetProject;
    }
}
