package bean;

import java.io.Serializable;

/**
 * Created by Lenovo on 2016/7/24.
 */
public class User implements Serializable{
    private String uid; // 账号
    private String name;// 昵称
    private String password; // 密码
    private String phone;// 用户手机号
    private String sex;   //性别
    private String college;   //学院
    private String avatarUrl; //头像url

    public User (){}

    public User(String avatarUrl, String college, String name, String password, String phone, String sex, String uid) {
        this.avatarUrl = avatarUrl;
        this.college = college;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.sex = sex;
        this.uid = uid;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String sid) {
        this.uid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
