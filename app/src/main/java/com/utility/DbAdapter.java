package com.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bean.CountResultItem;
import com.bean.CountResultWithArticleItem;
import com.bean.PhysicalCountResultItem;

import java.util.ArrayList;

/**
 * Created by Rahul on 5/1/2015.
 */
public class DbAdapter {
    private static final String TAG_DB_NAME = "db_count";
    private static final String TAG_TABLE_COUNT = "tb_count";
    private static final int TAG_DB_VERSION = 1;
    private static final String TAG_ID = "id";
    private static final String TAG_BARCODE = "barcode";
    private static final String TAG_ARTICLE_NO = "article_no";
    private static final String TAG_PRODUCT_NAME = "product_name";
    private static final String TAG_PRODUCT_ID = "product_id";
    private static final String TAG_ITEM_CATEGORY = "item_category";
    private static final String TAG_SURVEY_ID = "survey_id";
    private static final String TAG_ARTICLE_AVAILABLE = "article_available";
    private static final String TAG_PRODUCT_AVAILABLE = "product_available";
    private static final String TAG_ITEM_COUNT = "item_count";
    private static final String TAG_DATE = "date";
    private static final String TAG_TIME = "time";

    private static final String TAG_TABLE_PHYSICAL_COUNT = "tb_physical_count";
    private static final String TAG_ID_P = "id";
    private static final String TAG_SURVEY_ID_P = "survey_id";
    private static final String TAG_CAT_ID_P = "cat_id";
    private static final String TAG_CAT_TYPE_P = "cat_type";
    private static final String TAG_RACK_P = "rack";
    private static final String TAG_COUNT_P = "count";

    private static final String TAG_DELETE_ARTICLE = "article_available = 1 and article_no = '";
    private static final String TAG_DELETE_PRODUCT = "product_available = 1 and product_name = '";
    private static final String TAG_DELETE_BARCODE = "barcode = '";

    private String columnName[] = {TAG_ID, TAG_BARCODE, TAG_ARTICLE_NO,
            TAG_ITEM_COUNT, TAG_DATE, TAG_TIME, TAG_ARTICLE_AVAILABLE};

    private String columnName_P[] = new String[]{TAG_ID_P, TAG_SURVEY_ID_P, TAG_CAT_ID_P, TAG_CAT_TYPE_P, TAG_RACK_P, TAG_COUNT_P};

    private String order_by = TAG_ID;
    private String group_by = TAG_BARCODE;
    private String order_by_p = TAG_ID_P;
    private String group_by_p = TAG_RACK_P;

    private SQLiteDatabase db;
    private static final String TAG_CREATE_COUNT_TABLE = "create table tb_count" +
            "(id integer primary key autoincrement," +
            " barcode text not null, article_no text, item_category text," +
            " survey_id text not null," +
            " item_count integer not null," +
            " product_name text not null," +
            " product_id text not null," +
            " date text not null," +
            " time text not null," +
            " article_available text not null," +
            " product_available text not null)";

    private static final String TAG_CREATE_TABLE_P = "create table tb_physical_count(id integer primary key autoincrement," +
            " survey_id text not null, cat_id text not null, cat_type text not null, rack text not null, count integer not null)";


    private DbHandler handler;
    private Context context;
    private Pref _pref;

    public DbAdapter(Context context){
        this.context = context;
        handler = new DbHandler(context);
        _pref = new Pref(context);
    }

    public DbAdapter open(){
        handler = new DbHandler(context);
        db = handler.getWritableDatabase();
        return this;
    }

    public void close(){
        handler.close();
    }

    public void insertValue(String barcode, int itemCount, String articleNo,
                            String productName, String productId,
                            String artAvlb, String productAvlb){
        ContentValues contentValues = new ContentValues();

        contentValues.put(TAG_BARCODE, barcode);
        contentValues.put(TAG_ITEM_COUNT, itemCount);
        contentValues.put(TAG_ARTICLE_NO, articleNo);
        contentValues.put(TAG_PRODUCT_NAME, productName);
        contentValues.put(TAG_PRODUCT_ID, productId);
        contentValues.put(TAG_ITEM_CATEGORY, "");
        contentValues.put(TAG_SURVEY_ID, _pref.getSession("current_surveyid"));
        contentValues.put(TAG_DATE, "20/2/2015");
        contentValues.put(TAG_TIME, "12:00 PM");
        contentValues.put(TAG_ARTICLE_AVAILABLE, artAvlb);
        contentValues.put(TAG_PRODUCT_AVAILABLE, productAvlb);

        Long ll = db.insert(TAG_TABLE_COUNT, null, contentValues);


    }

    public void insertValuePhysicalCount(String cat_id, String cat_type, String rack, int count) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(TAG_SURVEY_ID_P, _pref.getSession("current_surveyid"));
        contentValues.put(TAG_CAT_ID_P, cat_id);
        contentValues.put(TAG_CAT_TYPE_P, cat_type);
        contentValues.put(TAG_RACK_P, rack);
        contentValues.put(TAG_COUNT_P, count);

        Long ll = db.insert(TAG_TABLE_PHYSICAL_COUNT, null, contentValues);
    }

    public boolean deleteRecord(String id) {
        //dbSqLiteDatabase.delete(table, whereClause, whereArgs)

        return db.delete(TAG_TABLE_COUNT, TAG_SURVEY_ID + "=" + id, null) > 0;

    }

    public boolean deleteRecordScanCount(String id, String itemType) {

        //dbSqLiteDatabase.delete(table, whereClause, whereArgs)
        if(itemType.equals("article")){
            return db.delete(TAG_TABLE_COUNT, TAG_DELETE_ARTICLE + id + "'", null) > 0;
        } else if(itemType.equals("product")){
            return db.delete(TAG_TABLE_COUNT, TAG_DELETE_PRODUCT + id + "'", null) > 0;
        } else {
            return db.delete(TAG_TABLE_COUNT, TAG_DELETE_BARCODE + id + "'", null) > 0;
        }
    }

    public boolean deleteRecordPhysicalCount(String id) {

        //dbSqLiteDatabase.delete(table, whereClause, whereArgs)

        return db.delete(TAG_TABLE_PHYSICAL_COUNT, TAG_SURVEY_ID_P + "=" + id, null) > 0;
    }

    public boolean deleteRecordPhysicalCountById(String id) {

        //dbSqLiteDatabase.delete(table, whereClause, whereArgs)

        return db.delete(TAG_TABLE_PHYSICAL_COUNT, TAG_ID_P + "=" + id, null) > 0;
    }

    public boolean deleteAllRecord() {

        //dbSqLiteDatabase.delete(table, whereClause, whereArgs)

        return db.delete(TAG_TABLE_COUNT, null, null) > 0;

    }

    public ArrayList<CountResultItem> getRecords(){

        ArrayList<CountResultItem> resultItems = new ArrayList<CountResultItem>();
        Cursor cursorArticleCode;

        cursorArticleCode = db.rawQuery("select article_no, sum(item_count) as" +
                    " item_count from tb_count where article_available = 1 and" +
                    " survey_id = " + _pref.getSession("current_surveyid") + " group by article_no", null);


        int pos_brcodeNo = cursorArticleCode.getColumnIndex(TAG_ARTICLE_NO);
        int pos_item_countNo = cursorArticleCode.getColumnIndex(TAG_ITEM_COUNT);

        Log.e("no barcode", String.valueOf(cursorArticleCode.getCount()));

        for(cursorArticleCode.moveToFirst(); !(cursorArticleCode.isAfterLast());
            cursorArticleCode.moveToNext()){

            CountResultItem _item = new CountResultItem();

            _item.setBarcode(cursorArticleCode.getString(pos_brcodeNo));
            _item.setCountNo(cursorArticleCode.getString(pos_item_countNo));
            _item.setItemType("article");

            resultItems.add(_item);

        }

        Cursor cursorProductCode;

        cursorProductCode = db.rawQuery("select product_name, sum(item_count) as" +
                " item_count from tb_count where product_available = 1 and" +
                " survey_id = " + _pref.getSession("current_surveyid") + " group by product_name", null);


        int pos_productname = cursorProductCode.getColumnIndex(TAG_PRODUCT_NAME);
        int pos_item_productNo = cursorProductCode.getColumnIndex(TAG_ITEM_COUNT);

        for(cursorProductCode.moveToFirst(); !(cursorProductCode.isAfterLast());
            cursorProductCode.moveToNext()){

            CountResultItem _item = new CountResultItem();

            _item.setBarcode(cursorProductCode.getString(pos_productname));
            _item.setCountNo(cursorProductCode.getString(pos_item_productNo));
            _item.setItemType("product");

            resultItems.add(_item);

        }

        Cursor cursorScan;

        cursorScan = db.rawQuery("select barcode, count(item_count) as item_count" +
                    " from tb_count where barcode != 'NA' and" +
                    " survey_id = " + _pref.getSession("current_surveyid") + " group by " + group_by +
                    " order by " + order_by, null);

        int pos_brcode = cursorScan.getColumnIndex(TAG_BARCODE);
        int pos_item_count = cursorScan.getColumnIndex(TAG_ITEM_COUNT);

        for(cursorScan.moveToFirst(); !(cursorScan.isAfterLast()); cursorScan.moveToNext()){

            CountResultItem _item = new CountResultItem();

            _item.setBarcode(cursorScan.getString(pos_brcode));
            _item.setCountNo(cursorScan.getString(pos_item_count));
            _item.setItemType("scan");

            resultItems.add(_item);

        }

        return resultItems;

    }

    public ArrayList<CountResultWithArticleItem> getRecordsForSubmit(){

        ArrayList<CountResultWithArticleItem> resultItems = new ArrayList<CountResultWithArticleItem>();
        Cursor cursorArticleCode;

        cursorArticleCode = db.rawQuery("select barcode, sum(item_count) as" +
                " item_count, article_no, product_id from tb_count" +
                " where article_available = 1 and" +
                " survey_id = " + _pref.getSession("current_surveyid") + " group by article_no", null);

        int pos_brcode_art = cursorArticleCode.getColumnIndex(TAG_BARCODE);
        int pos_item_count_art = cursorArticleCode.getColumnIndex(TAG_ITEM_COUNT);
        int pos_article_art = cursorArticleCode.getColumnIndex(TAG_ARTICLE_NO);
        int pos_product_art = cursorArticleCode.getColumnIndex(TAG_PRODUCT_ID);


        for(cursorArticleCode.moveToFirst(); !(cursorArticleCode.isAfterLast());
            cursorArticleCode.moveToNext()){

            CountResultWithArticleItem _item = new CountResultWithArticleItem();

            _item.setBarcode(cursorArticleCode.getString(pos_brcode_art));
            _item.setCountNo(cursorArticleCode.getString(pos_item_count_art));
            _item.setArticleNo(cursorArticleCode.getString(pos_article_art));
            _item.setCategoryId(cursorArticleCode.getString(pos_product_art));

            resultItems.add(_item);

        }

        Cursor cursorScanProd;

        cursorScanProd = db.rawQuery("select barcode, sum(item_count) as" +
                " item_count, article_no, product_id from tb_count" +
                " where product_available = 1 and" +
                " survey_id = " + _pref.getSession("current_surveyid") + " group by article_no", null);

        int pos_brcode_prod = cursorScanProd.getColumnIndex(TAG_BARCODE);
        int pos_item_count_prod = cursorScanProd.getColumnIndex(TAG_ITEM_COUNT);
        int pos_article_no_prod = cursorScanProd.getColumnIndex(TAG_ARTICLE_NO);
        int pos_category_no_prod = cursorScanProd.getColumnIndex(TAG_PRODUCT_ID);


        for(cursorScanProd.moveToFirst(); !(cursorScanProd.isAfterLast());
            cursorScanProd.moveToNext()){

            CountResultWithArticleItem _item = new CountResultWithArticleItem();

            _item.setBarcode(cursorScanProd.getString(pos_brcode_prod));
            _item.setCountNo(cursorScanProd.getString(pos_item_count_prod));
            _item.setArticleNo(cursorScanProd.getString(pos_article_no_prod));
            _item.setCategoryId(cursorScanProd.getString(pos_category_no_prod));

            resultItems.add(_item);

        }

        Cursor cursorScan;
        cursorScan = db.rawQuery("select barcode, count(item_count)" +
                    " as item_count, article_no, article_no, product_id" +
                    " from tb_count where barcode != 'NA'  and" +
                    " survey_id = " + _pref.getSession("current_surveyid") +
                    " group by " + group_by +
                    " order by " + order_by, null);

        int pos_brcode = cursorScan.getColumnIndex(TAG_BARCODE);
        int pos_item_count = cursorScan.getColumnIndex(TAG_ITEM_COUNT);
        int pos_article_no = cursorScan.getColumnIndex(TAG_ARTICLE_NO);
        int pos_prod_id = cursorScan.getColumnIndex(TAG_PRODUCT_ID);

        Log.e("scan barcode", String.valueOf(cursorScan.getCount()));

        for(cursorScan.moveToFirst(); !(cursorScan.isAfterLast()); cursorScan.moveToNext()){

            CountResultWithArticleItem _item = new CountResultWithArticleItem();

            _item.setBarcode(cursorScan.getString(pos_brcode));
            _item.setCountNo(cursorScan.getString(pos_item_count));
            _item.setArticleNo(cursorScan.getString(pos_article_no));
            _item.setCategoryId(cursorScan.getString(pos_prod_id));

            resultItems.add(_item);

        }

        return resultItems;

    }

    public ArrayList<PhysicalCountResultItem> getPhysicalCountRecords(){

        ArrayList<PhysicalCountResultItem> resultItems = new ArrayList<PhysicalCountResultItem>();

        Cursor cursorScan;

        cursorScan = db.rawQuery("select id, cat_id, cat_type, rack, count" +
                " from tb_physical_count where survey_id = " + _pref.getSession("current_surveyid") +
                " group by " + group_by_p +
                " order by " + order_by_p, null);

        int pos_id = cursorScan.getColumnIndex(TAG_ID_P);
        int pos_cat_id = cursorScan.getColumnIndex(TAG_CAT_ID_P);
        int pos_cat_type = cursorScan.getColumnIndex(TAG_CAT_TYPE_P);
        int pos_rack = cursorScan.getColumnIndex(TAG_RACK_P);
        int pos_count = cursorScan.getColumnIndex(TAG_COUNT_P);

        for(cursorScan.moveToFirst(); !(cursorScan.isAfterLast()); cursorScan.moveToNext()){

            PhysicalCountResultItem _item = new PhysicalCountResultItem();

                _item.setId(cursorScan.getString(pos_id));
                _item.setCatId(cursorScan.getString(pos_cat_id));
                _item.setCatType(cursorScan.getString(pos_cat_type));
                _item.setRackNo(cursorScan.getString(pos_rack));
                _item.setNoOfItems(cursorScan.getInt(pos_count));

                resultItems.add(_item);

        }

        return resultItems;

    }

    private class DbHandler extends SQLiteOpenHelper{

        private DbHandler(Context context) {
            super(context, TAG_DB_NAME, null, TAG_DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(TAG_CREATE_COUNT_TABLE);
            sqLiteDatabase.execSQL(TAG_CREATE_TABLE_P);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

            db.execSQL("DROP TABLE IF EXISTS tb_count");
            db.execSQL("DROP TABLE IF EXISTS tb_physical_count");
            onCreate(db);

        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onDowngrade(db, oldVersion, newVersion);
        }
    }
}
