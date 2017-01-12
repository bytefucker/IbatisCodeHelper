INSERT INTO ${tableName}
<trim prefix="(" suffix=")" suffixOverrides=",">
<#list finalFields as filedAndColumn>
    <#if filedAndColumn?is_last>
        <if test="pojo.${filedAndColumn.field}!=null"> ${filedAndColumn.column}</if>
    <#else>
        <if test="pojo.${filedAndColumn.field}!=null"> ${filedAndColumn.column},</if>
    </#if>
</#list>
</trim>
VALUES
<trim prefix="(" suffix=")" suffixOverrides=",">
<#list finalFields as filedAndColumn>
    <#if filedAndColumn?is_last>
        <if test="pojo.${filedAndColumn.field}!=null"> ${r"#"}{pojo.${filedAndColumn.field}}</if>
    <#else>
        <if test="pojo.${filedAndColumn.field}!=null"> ${r"#"}{pojo.${filedAndColumn.field},}</if>
    </#if>
</#list>
</trim>