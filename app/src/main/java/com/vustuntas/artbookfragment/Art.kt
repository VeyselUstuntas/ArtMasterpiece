package com.vustuntas.artbookfragment

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Arts")
data class Art (
    @ColumnInfo(name = "art_name")
    var artName : String,

    @ColumnInfo(name = "art_year")
    var artYear : String,

    @ColumnInfo(name = "artOwner")
    var artOwner : String,

    @ColumnInfo(name = "artImage")
    var artImage : ByteArray


) : Serializable{
    @PrimaryKey(autoGenerate = true)
    var ID : Int = 0
}