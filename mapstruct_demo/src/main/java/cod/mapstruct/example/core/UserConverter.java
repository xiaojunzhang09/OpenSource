package cod.mapstruct.example.core;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author zhangxiaojun10
 * @title: UserConverter
 * @projectName OpenSource
 * @description: TODO
 * @date 2023/4/17 10:35 上午
 */
@Mapper
public interface UserConverter {

    @Mappings({
            @Mapping(source = "name", target = "userName")
    })
    UserVo userDtoToVo(UserDto userDto);

}