package com.vustuntas.artbookfragment.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.vustuntas.artbookfragment.Art
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ArtDao {
    @Query("select * from Arts")
    fun getAll() : Flowable<List<Art>>

    @Query("Select * from Arts where ID = :id")
    fun getSelectArt(id : Int) : Flowable<Art>

    @Insert
    fun insertArt(art : Art) : Completable

    @Delete
    fun deleteArt(art: Art) : Completable
}