package bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lenovo on 2016/7/26.
 */
public class RepaireTable {

    private String rid;  //报修订单唯一id
    private String repaireName; //报修人姓名
    private String mainType;    //报修主类型
    private String type;        //报修类型
    private List<String> goods;   //报修物品
    private String budlingId;    //报修楼编号
    private String roomNum;     //房间号
    private String repaireDes;   // 报修描述
    private ArrayList<String>  imgUrl;  //base64编码图片地址or服务器图片地址

    private int repaireStatus;    //0代表未维修    1 代表维修中    2代表维修完成

    private String confirmStatus;   //0代表维修完成未确认     1代表维修完成确认

    private Date get_repaire_time;  //接单时间
    private Date repaire_time;//报修时间
    private Date over_time;  //完成时间

    private String evaluate;  //客户评价

    private Repaireman repairman;
    public RepaireTable(){}

    public RepaireTable(String budlingId, ArrayList<String> imgUrl, int isRepaire, String mainType, String repaireDes, String repaireName, String repairerId, String roomNum, String type) {
        this.budlingId = budlingId;
        this.imgUrl = imgUrl;
        this.repaireStatus = isRepaire;
        this.mainType = mainType;
        this.repaireDes = repaireDes;
        this.repaireName = repaireName;
        this.rid = repairerId;
        this.roomNum = roomNum;
        this.type = type;
    }

    public Repaireman getRepairman() {
        return repairman;
    }

    public void setRepairman(Repaireman repairman) {
        this.repairman = repairman;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public Date getGet_repaire_time() {
        return get_repaire_time;
    }

    public void setGet_repaire_time(Date get_repaire_time) {
        this.get_repaire_time = get_repaire_time;
    }

    public String getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(String confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public Date getOver_time() {
        return over_time;
    }

    public void setOver_time(Date over_time) {
        this.over_time = over_time;
    }

    public Date getRepaire_time() {
        return repaire_time;
    }

    public void setRepaire_time(Date repaire_time) {
        this.repaire_time = repaire_time;
    }


    public String getBudlingId() {
        return budlingId;
    }

    public void setBudlingId(String budlingId) {
        this.budlingId = budlingId;
    }

    public ArrayList<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(ArrayList<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getRepaireStatus() {
        return repaireStatus;
    }

    public void setRepaireStatus(int isRepaire) {
        this.repaireStatus = isRepaire;
    }

    public String getMainType() {
        return mainType;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }

    public String getRepaireDes() {
        return repaireDes;
    }

    public void setRepaireDes(String repaireDes) {
        this.repaireDes = repaireDes;
    }

    public String getRepaireName() {
        return repaireName;
    }

    public void setRepaireName(String repaireName) {
        this.repaireName = repaireName;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String repairerId) {
        this.rid = repairerId;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getGoods() {
        return goods;
    }

    public void setGoods(List<String> goods) {
        this.goods = goods;
    }


}
