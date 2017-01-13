
        INSERT INTO ${tableName}(
        <include refid="all_column"/>
        )VALUES
        <foreach collection="pojos" item="pojo" index="index" separator=",">
            (
        <#list finalFields as filedAndColumn>
            <#if filedAndColumn?is_last>
            ${r"#"}{pojo.${filedAndColumn.field}}
            <#else>
            ${r"#"}pojo.${filedAndColumn.field}},
            </#if>
        </#list>
            )
        </foreach>
