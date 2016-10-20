package bean;

/**
 * Created by Lenovo on 2016/8/2.
 */
public class Repaireman {
    private String id;    //账号环信唯一id
    private String nickName; //昵称
    private String avatarUrl;     //头像url

    public Repaireman(){}

    public Repaireman(String aid, String avatarUrl, String nickName) {
        this.id = aid;
        this.avatarUrl = avatarUrl;
        this.nickName = nickName;
    }

    public String getId() {
        return id;
    }

    public void setId(String aid) {
        this.id = aid;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
