package utils;

/**
 * Created by Lenovo on 2016/7/24.
 */
public class MyConstant {
//    public static String BASE_URL = "http://192.168.23.1:8080/qlrjds";
    public static String BASE_URL = "http://123.206.94.20:8080/qlrjds";


//    public static String BASE_URL = "http://192.168.1.117:8080/qlrjds";


    //注册
    public static String REGISTER_URL = "/user/registUser";
    //修改资料
    public static String UPDATE_USER = "/user/updateInfo";
    //修改密码
    public static String UPDATE_Pass = "/user/updatePassword";
    //查找报修记录
    public static String REPAIRE_RECORD = "/repaire/getAllRepaire";
    //根据rid查找单条记录
    public static String REPAIRE_RECORD_BYRID = "/repaire/getRepaireByRid";
    //获得用户信息
    public static String GET_USER_INFO_URL = "/user/loginUser";
    //获取在线维修员
    public static String GET_CONTACT = "/repairman/getRepairmansStatus";
    //检查账号是否合理
    public static String CHECK_IS_ADMIN="/repairman/findRepairmanById";
    //报修单上传
    public static String REPAIRE_URL = "/repaire/save";

    public static String ADD_EVALUATE = "/repaire/addEvaluate";

    public static String DOWNLOAD_IMG = "/repaire/download";
}
