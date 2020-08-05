package com.example.chap.internal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.chap.model.Address

class DbAddressHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        const val DATABASE_NAME = "Address.db"
        const val TABLE_NAME = "addresses"
        const val COLUMN_LAT = "lat"
        const val COLUMN_LNG = "lng"
        const val COLUMN_ADDRESS = "address"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table " + "$TABLE_NAME ($COLUMN_LAT REAL, " +
                    "$COLUMN_LNG REAL, $COLUMN_ADDRESS text, primary key ($COLUMN_LNG, $COLUMN_ADDRESS))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun save(address: Address): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_LAT, address.lat)
        contentValues.put(COLUMN_LNG, address.lng)
        contentValues.put(COLUMN_ADDRESS, address.address)
        db.insert(TABLE_NAME, null, contentValues)
        return true
    }

    fun getAll(): ArrayList<Address> {
        val array = ArrayList<Address>()
        val db = this.readableDatabase
        val c = db.rawQuery("select * from $TABLE_NAME", null)
        c.moveToFirst()
        while (!c.isAfterLast) {
            val lat = c.getFloat(c.getColumnIndex(COLUMN_LAT))
            val lng = c.getFloat(c.getColumnIndex(COLUMN_LNG))
            val address = c.getString(c.getColumnIndex(COLUMN_ADDRESS))
            val adr = Address(lat, lng, address)
            array.add(adr)
            c.moveToNext()
        }

        c.close()
        return array
    }

    fun deleteAddress(lat: Float, lng: Float): Int {
        val db = this.readableDatabase
        return db.delete(
            TABLE_NAME,
            "$COLUMN_LAT = $lat AND $COLUMN_LNG = $lng",
            null
        )
    }

    fun deleteAll() {
        val db = this.readableDatabase
        db.delete(TABLE_NAME, null, null)
    }

}