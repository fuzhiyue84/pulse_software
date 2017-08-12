package cn.edu.fudan.blepulse.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2016/10/13.
 */
public class DataHelper extends OrmLiteSqliteOpenHelper
{

    private static final String TABLE_NAME = "sqlite-test.db";
    /**
     * userDao ，每张表对于一个
     */
    private Dao<ReceiverData, Integer> receiveDao;

    public DataHelper(Context context)
    {
        super(context, TABLE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource)
    {
        try
        {
            TableUtils.createTable(connectionSource, ReceiverData.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        try
        {
            TableUtils.dropTable(connectionSource, ReceiverData.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static DataHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DataHelper getHelper(Context context)
    {
        if (instance == null)
        {
            synchronized (DataHelper.class)
            {
                if (instance == null)
                    instance = new DataHelper(context);
            }
        }

        return instance;
    }

    /**
     * 获得userDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<ReceiverData, Integer> getReceiverDao() throws SQLException
    {
        if (receiveDao == null)
        {
            receiveDao = getDao(ReceiverData.class);
        }
        return receiveDao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close()
    {
        super.close();
        receiveDao = null;
    }

    public List<ReceiverData> queryOneDateData(Date date) {
        QueryBuilder<ReceiverData, Integer> builder = receiveDao.queryBuilder();
        try {
            builder.where().eq("startdate", date);
            PreparedQuery<ReceiverData> preparedQuery = builder.prepare();
            List<ReceiverData> dataList = receiveDao.query(preparedQuery);
            return dataList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
