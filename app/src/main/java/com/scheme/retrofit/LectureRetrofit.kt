package com.scheme.retrofit

import com.scheme.models.LectureResponse
import com.scheme.models.OptionsResponse
import com.scheme.models.VersionResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface LectureRetrofit {

    @GET("/universities")
    suspend fun getUniversities(): OptionsResponse

    @GET("/{university}/faculties")
    suspend fun getFaculties(@Path("university")university: String): OptionsResponse

    @GET("/{university}/{faculty}/years")
    suspend fun getYears(@Path("university") university: String,
                         @Path("faculty") faculty: String): OptionsResponse

    @GET("/{university}/{faculty}/{year}/sections")
    suspend fun getSections(@Path("university") university: String,
                        @Path("faculty") faculty: String,
                        @Path("year") year: String): OptionsResponse

    @GET("/{university}/{faculty}/{year}/version")
    suspend fun getVersion(@Path("university") university: String,
                            @Path("faculty") faculty: String,
                            @Path("year") year: String): VersionResponse

    @GET("/{university}/{faculty}/{year}")
    suspend fun getData(@Path("university") university: String,
                        @Path("faculty") faculty: String,
                        @Path("year") year: String): LectureResponse
}