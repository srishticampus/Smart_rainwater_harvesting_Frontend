package com.project.irhs.api

import com.project.irhs.analysis.model.AnalysisByDateYear
import com.project.irhs.analysis.model.DateMonthResponse
import com.project.irhs.analysis.model.RecentAnalysisResponse
import com.project.irhs.fragmentdataclasses.ProfileResponse
import com.project.irhs.fragmentdataclasses.ProfileUpdateResponse
import com.project.irhs.login.model.ForgotPasswordResponse
import com.project.irhs.login.model.LoginResponse
import com.project.irhs.login.model.ResetPasswordResponse
import com.project.irhs.signup.model.Registration
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface APIInterface {

    @FormUrlEncoded
    @POST("registration.php")
    suspend fun userSignup(
        @FieldMap params: HashMap<String?, String>
    ): Response<Registration>

    @FormUrlEncoded
    @POST("login.php")
    suspend fun userLogin(
        @FieldMap params: HashMap<String?, String>
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("change_password.php")
    suspend fun resetPassword(
        @FieldMap params: HashMap<String?, String>

    ): Response<ResetPasswordResponse>

    @POST("view_profile.php")
    suspend fun viewProfile(
        @Query("userid") userId: String
    ): Response<ProfileResponse>

    @FormUrlEncoded
    @POST("profile_update.php")
    suspend fun updateUserProfile(

        @Field("userid") id: Int,
        @Field("first_name") firstname: String,
        @Field("last_name") lastname: String,
        @Field("email") email: String,
        @Field("phone") mobile: String,
    ): Response<ProfileUpdateResponse>

    @FormUrlEncoded
    @POST("reset_password.php")
    suspend fun forgotPassword(
        @FieldMap params: HashMap<String?, String>
    ): Response<ForgotPasswordResponse>


    @GET("view_analysis.php")
    suspend fun recentAnalysis(

    ): Response<RecentAnalysisResponse>

    @GET("month_list.php")

    suspend fun selectDateAndMonth(

    ): Response<DateMonthResponse>

    @FormUrlEncoded
    @POST("monthly_analysis.php")
    suspend fun getAnalysisByMonthYear(
        @Field("month") month:String,
        @Field("year") year:String
    ): Response<AnalysisByDateYear>


//    @Multipart
//    @POST("profile_update.php")
//    suspend fun updateUserProfile(
//        @PartMap params: HashMap<String?,RequestBody?>,
//        @Part image: MultipartBody.Part
//    ):Response<ProfileUpdateResponse>

}