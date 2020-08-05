package com.example.chap.internal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.chap.model.Address

class DbAddressHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        const val DATABASE_NAME = "Address.db"
        const val TABLE_NAME = "addresses"
        const val COLUMN_LAT = "lat"
        const val COLUMN_LNG = "lng"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_PHONE = "phone"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table " + "$TABLE_NAME (id integer primary key, $COLUMN_LAT text not null, " +
                    "$COLUMN_LNG text not null, $COLUMN_ADDRESS text not null, $COLUMN_PHONE text not null)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun save(address: Address): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_LAT, address.lat.toString())
        contentValues.put(COLUMN_LNG, address.lng.toString())
        contentValues.put(COLUMN_ADDRESS, address.address)
        contentValues.put(COLUMN_PHONE, address.phone)
        return db.insert(TABLE_NAME, null, contentValues) > 0
    }

    fun getAll(): ArrayList<Address> {
        val array = ArrayList<Address>()
        val db = this.readableDatabase
        val c = db.rawQuery("select * from $TABLE_NAME", null)
        c.moveToFirst()
        while (!c.isAfterLast) {
            val lat = c.getString(c.getColumnIndex(COLUMN_LAT))
            val lng = c.getString(c.getColumnIndex(COLUMN_LNG))
            val address = c.getString(c.getColumnIndex(COLUMN_ADDRESS))
            val phone = c.getString(c.getColumnIndex(COLUMN_PHONE))
            val adr = Address(lat.toFloat(), lng.toFloat(), address, phone)
            array.add(adr)
            c.moveToNext()
        }
        c.close()
        return array
    }

    fun deleteAddress(lat: Float, lng: Float, phone: String): Boolean {
        val db = this.writableDatabase
        return db.delete(
            TABLE_NAME,
            "$COLUMN_LAT = $lat AND $COLUMN_LNG = $lng AND $COLUMN_PHONE = $phone",
            null
        ) > 0
    }

    fun deleteAll() {
        val db = this.readableDatabase
        db.delete(TABLE_NAME, null, null)
    }

}