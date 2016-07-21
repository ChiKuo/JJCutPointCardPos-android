package tw.chikuo.jjcutpointcardpos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Chi on 2016/7/21.
 */
public class Point {
    private String owner;
    private String branch ;
    private Date date ;
    private Boolean exchangeable ;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public Point() {
    }

    public Point(String owner) {
        this.owner = owner;
        this.branch = JJCutPointCard.CURRENT_BRANCH;
        this.exchangeable = false;
        this.date = new Date();
//        Date date = new Date();
//        this.date = format.format(date);
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getExchangeable() {
        return exchangeable;
    }

    public void setExchangeable(Boolean exchangeable) {
        this.exchangeable = exchangeable;
    }
//
//    public Date getDate() {
//
//        try {
//            Date date = format.parse(this.date);
//            return date;
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//        }
//
//    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Point{" +
                "owner='" + owner + '\'' +
                ", branch='" + branch + '\'' +
                ", date=" + date +
                ", exchangeable=" + exchangeable +
                ", format=" + format +
                '}';
    }
}
