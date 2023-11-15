package com.jerboa.api

import android.content.Context
import android.util.Log
import com.jerboa.DEFAULT_LEMMY_INSTANCES
import com.jerboa.datatypes.types.*
import com.jerboa.db.entity.Account
import com.jerboa.toastException
import com.jerboa.util.CustomHttpLoggingInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit

const val VERSION = "v3"
const val DEFAULT_INSTANCE = "lemmy.ml"
const val MINIMUM_API_VERSION: String = "0.18"
val REDACTED_QUERY_PARAMS = setOf("auth")
val REDACTED_BODY_FIELDS = setOf("jwt", "password", "auth")

interface API {
    @GET("site")
    suspend fun getSite(
        @QueryMap form: Map<String, String>,
    ): Response<GetSiteResponse>

    /**
     * Get / fetch posts, with various filters.
     */
    @GET("post/list")
    suspend fun getPosts(
        @QueryMap form: Map<String, String>,
    ): Response<GetPostsResponse>

    /**
     * Get / fetch a post.
     */
    @GET("post")
    suspend fun getPost(
        @QueryMap form: Map<String, String>,
    ): Response<GetPostResponse>

    /**
     * Log into lemmy.
     */
    @POST("user/login")
    suspend fun login(
        @Body form: Login,
    ): Response<LoginResponse>

    /**
     * Validate an auth
     */
    @GET("user/validate_auth")
    suspend fun validateAuth(): Response<SuccessResponse>

    /**
     * Like / vote on a post.
     */
    @POST("post/like")
    suspend fun likePost(
        @Body form: CreatePostLike,
    ): Response<PostResponse>

    /**
     * Mark post as read.
     */
    @POST("post/mark_as_read")
    suspend fun markAsRead(
        @Body form: MarkPostAsRead,
    ): Response<PostResponse>

    /**
     * Like / vote on a comment.
     */
    @POST("comment/like")
    suspend fun likeComment(
        @Body form: CreateCommentLike,
    ): Response<CommentResponse>

    /**
     * Create a comment.
     */
    @POST("comment")
    suspend fun createComment(
        @Body form: CreateComment,
    ): Response<CommentResponse>

    /**
     * Edit a comment.
     */
    @PUT("comment")
    suspend fun editComment(
        @Body form: EditComment,
    ): Response<CommentResponse>

    /**
     * Delete a comment.
     */
    @POST("comment/delete")
    suspend fun deleteComment(
        @Body form: DeleteComment,
    ): Response<CommentResponse>

    /**
     * Save a post.
     */
    @PUT("post/save")
    suspend fun savePost(
        @Body form: SavePost,
    ): Response<PostResponse>

    /**
     * Save a comment.
     */
    @PUT("comment/save")
    suspend fun saveComment(
        @Body form: SaveComment,
    ): Response<CommentResponse>

    /**
     * Get / fetch comments.
     */
    @GET("comment/list")
    suspend fun getComments(
        @QueryMap form: Map<String, String>,
    ): Response<GetCommentsResponse>

    /**
     * Get / fetch a community.
     */
    @GET("community")
    suspend fun getCommunity(
        @QueryMap form: Map<String, String>,
    ): Response<GetCommunityResponse>

    /**
     * Get the details for a person.
     */
    @GET("user")
    suspend fun getPersonDetails(
        @QueryMap form: Map<String, String>,
    ): Response<GetPersonDetailsResponse>

    /**
     * Get comment replies.
     */
    @GET("user/replies")
    suspend fun getReplies(
        @QueryMap form: Map<String, String>,
    ): Response<GetRepliesResponse>

    /**
     * Mark a comment as read.
     */
    @POST("comment/mark_as_read")
    suspend fun markCommentReplyAsRead(
        @Body form: MarkCommentReplyAsRead,
    ): Response<CommentReplyResponse>

    /**
     * Mark a person mention as read.
     */
    @POST("user/mention/mark_as_read")
    suspend fun markPersonMentionAsRead(
        @Body form: MarkPersonMentionAsRead,
    ): Response<PersonMentionResponse>

    /**
     * Mark a private message as read.
     */
    @POST("private_message/mark_as_read")
    suspend fun markPrivateMessageAsRead(
        @Body form: MarkPrivateMessageAsRead,
    ): Response<PrivateMessageResponse>

    /**
     * Mark all replies as read.
     */
    @POST("user/mark_all_as_read")
    suspend fun markAllAsRead(
        @Body form: MarkAllAsRead,
    ): Response<GetRepliesResponse>

    /**
     * Get mentions for your user.
     */
    @GET("user/mention")
    suspend fun getPersonMentions(
        @QueryMap form: Map<String, String>,
    ): Response<GetPersonMentionsResponse>

    /**
     * Get / fetch private messages.
     */
    @GET("private_message/list")
    suspend fun getPrivateMessages(
        @QueryMap form: Map<String, String>,
    ): Response<PrivateMessagesResponse>

    /**
     * Create a private message.
     */
    @POST("private_message")
    suspend fun createPrivateMessage(
        @Body form: CreatePrivateMessage,
    ): Response<PrivateMessageResponse>

    /**
     * Get your unread counts
     */
    @GET("user/unread_count")
    suspend fun getUnreadCount(
        @QueryMap form: Map<String, String>,
    ): Response<GetUnreadCountResponse>

    /**
     * Follow / subscribe to a community.
     */
    @POST("community/follow")
    suspend fun followCommunity(
        @Body form: FollowCommunity,
    ): Response<CommunityResponse>

    /**
     * Create a post.
     */
    @POST("post")
    suspend fun createPost(
        @Body form: CreatePost,
    ): Response<PostResponse>

    /**
     * Edit a post.
     */
    @PUT("post")
    suspend fun editPost(
        @Body form: EditPost,
    ): Response<PostResponse>

    /**
     * Delete a post.
     */
    @POST("post/delete")
    suspend fun deletePost(
        @Body form: DeletePost,
    ): Response<PostResponse>

    /**
     * Search lemmy.
     */
    @GET("search")
    suspend fun search(
        @QueryMap form: Map<String, String>,
    ): Response<SearchResponse>

    /**
     * Fetch metadata for any given site.
     */
    @GET("post/site_metadata")
    suspend fun getSiteMetadata(
        @QueryMap form: Map<String, String>,
    ): Response<GetSiteMetadataResponse>

    /**
     * Report a comment.
     */
    @POST("comment/report")
    suspend fun createCommentReport(
        @Body form: CreateCommentReport,
    ): Response<CommentReportResponse>

    /**
     * Report a post.
     */
    @POST("post/report")
    suspend fun createPostReport(
        @Body form: CreatePostReport,
    ): Response<PostReportResponse>

    /**
     * Block a person.
     */
    @POST("user/block")
    suspend fun blockPerson(
        @Body form: BlockPerson,
    ): Response<BlockPersonResponse>

    /**
     * Block a community.
     */
    @POST("community/block")
    suspend fun blockCommunity(
        @Body form: BlockCommunity,
    ): Response<BlockCommunityResponse>

    /**
     * Save your user settings.
     */
    @PUT("user/save_user_settings")
    suspend fun saveUserSettings(
        @Body form: SaveUserSettings,
    ): Response<LoginResponse>

    /**
     * Upload an image.
     */
    @Multipart
    @POST
    suspend fun uploadImage(
        @Url url: String,
        @Header("Cookie") token: String,
        @Part filePart: MultipartBody.Part,
    ): Response<PictrsImages>

    companion object {
        private var api: API? = null
        var errorHandler: (Exception) -> Exception? = { it }

        var currentInstance: String = DEFAULT_INSTANCE
            private set

        private val TEMP_RECOGNISED_AS_LEMMY_INSTANCES = mutableSetOf<String>()
        private val TEMP_NOT_RECOGNISED_AS_LEMMY_INSTANCES = mutableSetOf<String>()

        val httpClient: OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor { chain ->
                    chain.request().newBuilder()
                        .header("User-Agent", "Jerboa")
                        .build()
                        .let(chain::proceed)
                }
                .build()

        private fun buildUrl(): String {
            return "https://$currentInstance/api/$VERSION/"
        }

        fun changeLemmyInstance(instance: String): API {
            currentInstance = instance
            api = buildApi(buildUrl())
            return api!!
        }

        fun getInstance(): API {
            if (api == null) {
                api = buildApi(buildUrl())
            }
            return api!!
        }

        fun createTempInstance(
            host: String,
            customErrorHandler: ((Exception) -> Exception?)? = null,
        ): API {
            return buildApi("https://$host/api/$VERSION/", customErrorHandler)
        }

        private fun buildApi(
            baseUrl: String,
            customErrorHandler: ((Exception) -> Exception?)? = null,
        ): API {
            val currErrorHandler = customErrorHandler ?: errorHandler

            val client =
                httpClient.newBuilder()
                    .addInterceptor { chain ->
                        val request = chain.request()
                        try {
                            chain.proceed(request)
                        } catch (e: Exception) {
                            val err = currErrorHandler(e)
                            if (err != null) {
                                throw err
                            }

                            okhttp3.Response.Builder()
                                .request(request)
                                .code(999)
                                .protocol(Protocol.HTTP_1_1)
                                .message("connection error")
                                .body(e.toString().toResponseBody())
                                .build()
                        }
                    }
                    .addInterceptor(CustomHttpLoggingInterceptor(REDACTED_QUERY_PARAMS, REDACTED_BODY_FIELDS))
                    .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(API::class.java)
        }

        suspend fun checkIfLemmyInstance(url: String): Boolean {
            try {
                val host = URL(url).host

                if (DEFAULT_LEMMY_INSTANCES.contains(host) || TEMP_RECOGNISED_AS_LEMMY_INSTANCES.contains(host)) {
                    return true
                } else if (TEMP_NOT_RECOGNISED_AS_LEMMY_INSTANCES.contains(host)) {
                    return false
                } else {
                    val api = createTempInstance(host)
                    return withContext(Dispatchers.IO) {
                        return@withContext when (apiWrapper(api.getSite(emptyMap()))) {
                            is ApiState.Success -> {
                                TEMP_RECOGNISED_AS_LEMMY_INSTANCES.add(host)
                                true
                            }
                            else -> {
                                TEMP_NOT_RECOGNISED_AS_LEMMY_INSTANCES.add(host)
                                false
                            }
                        }
                    }
                }
            } catch (_: MalformedURLException) {
                return false
            }
        }
    }
}

sealed class ApiState<out T> {
    abstract class Holder<T>(val data: T) : ApiState<T>()

    class Success<T>(data: T) : Holder<T>(data)

    class Appending<T>(data: T) : Holder<T>(data)

    class AppendingFailure<T>(data: T) : Holder<T>(data)

    class Failure(val msg: Throwable) : ApiState<Nothing>()

    data object Loading : ApiState<Nothing>()

    data object Refreshing : ApiState<Nothing>()

    data object Empty : ApiState<Nothing>()
}

fun <T> apiWrapper(form: Response<T>): ApiState<T> {
    return try {
        val data = retrofitErrorHandler(form)
        ApiState.Success(data)
    } catch (e: Exception) {
        ApiState.Failure(e)
    }
}

suspend fun uploadPictrsImage(
    account: Account,
    imageIs: InputStream,
    ctx: Context,
): String? {
    var imageUrl: String? = null
    val api = API.getInstance()
    try {
        Log.d("jerboa", "Uploading image....")
        val part =
            MultipartBody.Part.createFormData(
                "images[]",
                "myPic",
                imageIs.readBytes().toRequestBody(),
            )
        val url = "https://${API.currentInstance}/pictrs/image"
        val cookie = "jwt=${account.jwt}"
        val images = retrofitErrorHandler(api.uploadImage(url, cookie, part))
        Log.d("jerboa", "Uploading done.")
        imageUrl = "$url/${images.files?.get(0)?.file}"
    } catch (e: Exception) {
        toastException(ctx = ctx, error = e)
    }
    return imageUrl
}

fun <T> retrofitErrorHandler(res: Response<T>): T {
    if (res.isSuccessful) {
        return res.body()!!
    } else {
        val errMsg =
            res.errorBody()?.string()?.let {
                try {
                    // Prevent Could not convert to JSON messages everywhere
                    JSONObject(it).getString("error")
                } catch (_: JSONException) {
                    it
                }
            } ?: res.code().toString()

        throw Exception(errMsg)
    }
}
