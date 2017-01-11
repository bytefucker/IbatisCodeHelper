package ${packageName};

import ${beanFullType};
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;


@Mapper
public interface ${daoname} {

    int insert(@Param("pojo") ${beanShortType} pojo);

    int insertList(@Param("pojos") List<${beanShortType}> pojo);

    int update(@Param("pojo") ${beanShortType} pojo);

    ${beanShortType} findFirst();
}