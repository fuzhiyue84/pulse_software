package cn.edu.fudan.blepulse.model;

/**
 * Created by dell on 2017-09-10.
 */

public interface IPulseModel {

     void saveData();

     int[] getDataToFile(String startDate);

}
