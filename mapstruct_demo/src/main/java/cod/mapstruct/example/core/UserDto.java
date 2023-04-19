package cod.mapstruct.example.core;

import java.util.Date;

/**
 * @author zhangxiaojun10
 * @title: UserDTO
 * @projectName OpenSource
 * @description: TODO
 * @date 2023/4/17 10:34 上午
 */
public class UserDto {
    private String name;
    private int age;
    private Date birthday;
    private int gender;
    private String idCard;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
}
