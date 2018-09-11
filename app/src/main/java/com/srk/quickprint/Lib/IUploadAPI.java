package com.srk.quickprint.Lib;


import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IUploadAPI
{
    @Multipart
    @POST("order.php")
    Call<String> uploadFile(@Part MultipartBody.Part file,
                            @Part("name")String name,
                            @Part("Address")String Address,
                            @Part("mobile")String mobile,
                            @Part("Pagesize")String Pagesize);

    @POST("mac.php")
    @FormUrlEncoded
    Call<POST> isUser(@Field("macid ")String macid);
}
