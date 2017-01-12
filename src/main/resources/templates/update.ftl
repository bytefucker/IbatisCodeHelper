UPDATE ${tableName}
<set>
<#list finalFields as filedAndColumn>
    <#if filedAndColumn?is_last>
    <if test="pojo.${filedAndColumn.field} != null"> ${filedAndColumn.column} = ${r"#"}{pojo.${filedAndColumn.field}}
    <#else>
        <if test="pojo.${filedAndColumn.field} != null"> ${filedAndColumn.column} = ${r"#"}{pojo.${filedAndColumn.field}},</if>
    </#if>
</#list>
</set>
WHERE id = #{pojo.id}