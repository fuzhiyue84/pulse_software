package cn.edu.fudan.blepulse.ormlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by dell on 2016/10/13.
 */
@DatabaseTable(tableName = "tb_receiverdata")
public class ReceiverData {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "startdate")
    private Date startDate;
    @DatabaseField(columnName = "content")
    private String content;

    public ReceiverData() {

    }

    public ReceiverData(Date startDate, String content) {
        this.startDate = startDate;
        this.content = content;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

