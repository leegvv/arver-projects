package ${appObjectPackage};

import java.io.Serializable;
import ${entityPackage}.${baseEntityName};

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 应用实体 - ${entityName}.
 *
 * <p>
 * 该类于 ${dateTime} 首次生成，后由手工维护。
 * </p>
 *
 * @author Xu Dong Li
 * @version 1.0.0.0, ${date}
 */
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class ${entityName} extends ${baseEntityName} implements Serializable {

    /**
     * 默认的序列化 id.
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
