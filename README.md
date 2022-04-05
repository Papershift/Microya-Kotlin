<p align="center">
    <img src="https://raw.githubusercontent.com/Flinesoft/Microya/main/Logo.png"
      width=396>
</p>

<p align="center">
  • <a href="#usage">Usage</a>
  • <a href="https://github.com/Papershift/Microya-Kotlin/issues">Issues</a>
  • <a href="#contributing">Contributing</a>
  • <a href="#license">License</a>
</p>

# Microya

This is the kotlin version of [Microya](https://github.com/Flinesoft/Microya). A network abstraction layer.

## Usage

### Step 1: Defining your Endpoints
Create an Api `sealed class` with all supported endpoints as `data class` with the request parameters/data specified as parameters.

In order to allow Microya-Kotlin serialize your request classes, you need to declare a custom `SerializersModule` and register your classes as polymorphic subclasses of `Any`.

```Kotlin
object PostmanSerializer {
    private val module = SerializersModule {
        polymorphic(Any::class) {
            subclass(FooBar::class)
        }
    }

    val requestJsonFormatter = Json {
        serializersModule = module
    }
}
```

For example, when writing a client for the [Postman Echo](https://www.postman.com/postman/workspace/published-postman-templates/documentation/631643-f695cab7-6878-eb55-7943-ad88e1ccfd65):

```Kotlin
sealed class PostmanEchoEndpoint : Endpoint() {
    data class Index(val sortedBy: String) : PostmanEchoEndpoint()
    data class Post(val fooBar: FooBar) : PostmanEchoEndpoint()
    data class Get(val fooBarID: String) : PostmanEchoEndpoint()
    data class Patch(val fooBarID: String, val fooBar: FooBar) : PostmanEchoEndpoint()
    object Delete : PostmanEchoEndpoint()
}
```

### Step 2: Making your Api `Endpoint` compliant by overriding variables in the `Endpoint` abstract class.

```Kotlin
sealed class PostmanEchoEndpoint : Endpoint() {
    data class Index(val sortedBy: String) : PostmanEchoEndpoint()
    data class Post(val fooBar: FooBar) : PostmanEchoEndpoint()
    data class Get(val fooBarID: String) : PostmanEchoEndpoint()
    data class Patch(val fooBarID: String, val fooBar: FooBar) : PostmanEchoEndpoint()
    object Delete : PostmanEchoEndpoint()

    override val subpath: String
        get() = when (this) {
            is Get -> "get/${fooBarID}"
            is Index -> "get"
            is Patch -> "patch/${fooBarID}"
            is Post -> "post"
            Delete -> "delete"
        }

    override val method: HttpMethod
        get() = when (this) {
            is Get -> HttpMethod.Get
            is Index -> HttpMethod.Get
            is Patch -> HttpMethod.Patch(fooBar)
            is Post -> HttpMethod.Post(fooBar)
            Delete -> HttpMethod.Delete
        }
    override val headers: Map<String, String>
        get() = mapOf(
            "Content-Type" to "application/json",
            "Accept" to "application/json",
            "Accept-Language" to Locale.getDefault().language
        )

    override val queryParameters: Map<String, String>
        get() = when (this) {
            is Index -> mapOf("sortedBy" to sortedBy)
            else -> emptyMap()
        }
    override val mockedResponse: MockedResponse?
        get() = null
}

```
### Step 3a: Calling your API endpoint with the Result type
Call an API endpoint providing a response type and an also an error type.

```Kotlin
performRequest<PostmanEchoResponse, PostmanEchoError>(PostmanEchoEndpoint.Index(sortedBy = "updatedAt"))
```

Here's a full example of a call you could make with Mircoya-Kotlin:

```Kotlin
val provider = ApiProvider.Builder().baseUrl("https://postman-echo.com")
                   .client(OkHttpClient())
                   .plugins(listOf(HttpAuthPlugin(HttpAuthPlugin.Scheme.BASIC, "abc123")
                   .requestJsonFormatter(PostmanSerializer.requestJsonFormatter)
                   .responseJsonFormatter(PostmanSerializer.responseJsonFormatter)
                   .build()

provider.performRequest<PostmanEchoResponse, PostmanEchoError>(
                         PostmanEchoEndpoint.Index(sortedBy = "updatedAt")
                     ).onSuccess { postmanEchoResponse: PostmanEchoResponse ->
                         // use the already decoded result
                     }.onFailure { jsonApiException: JsonApiException ->
                         // error handling
                     }
```

Note that you can use a throwing `get()` function instead of `onSuccess` and  `onFailure` callbacks or a `when` statement.

```Kotlin
val result = sampleApiProvider.performRequest<PostmanEchoResponse, PostmanEchoError>(PostmanEchoEndpoint.Index(sortedBy = "updatedAt")).get()
```
You can also use more functional methods like `map()`, `andThen()`, `mapEither()`, `mapError()`.

Microya-Kotlin returns a `JsonApiException` when the request is unsuccessful. You can call `.getBody()` after type casting the `jsonApiException` to a `ClientError`

### Step 3b: Multipart File Upload

Microya-Kotlin supports file uploads using multipart requests. To perform multipart request with Microya-kotlin, you'll need to use the `performUploadRequest` function.
For example, here's how to upload an image to Imgur with Microya-Kotlin.
Model you request class. The request class takes a list of `FileDataPart`. These are file parts to be included in multipart request.
Take note of the `@Transient` annotation. This means the `fileDataParts` won't be serialized. The `fileDataParts` would be included in a multipart request by Microya-Kotlin.

```kotlin
@Serializable
data class UploadImageRequest(
    val title: String,
    val description: String,
    @Transient
    val fileDataParts: List<FileDataPart> = emptyList()
)
```

Next step is modeling the ImgurEndpoint. The only difference between endpoint with file uploads and normal endpoints is the `fileDataParts` variable. Endpoints with uploads override this variable.

```kotlin
sealed class ImgurEndpoint : Endpoint() {
    data class UploadImageEndpoint(val body: UploadImageRequest) : ImgurEndpoint()
    override val subpath: String
        get() = when (this) {
            is UploadImageEndpoint -> "3/image"
        }
    override val method: HttpMethod
        get() = when (this) {
            is UploadImageEndpoint -> HttpMethod.Post(body)
        }
    override val headers: Map<String, String>
        get() = emptyMap()
    override val queryParameters: Map<String, String>
        get() = emptyMap()
    override val mockedResponse: MockedResponse?
        get() = null
    override val fileDataParts: List<FileDataPart>
        get() = when (this) {
            is UploadImageEndpoint -> body.fileDataParts
        }
}
```

Make the uploadRequest using `performUploadRequest`.
```kotlin
  val result: ImgurResponse<ImgurSuccessData> =
                uploadFileSampleApiProvider.performUploadRequest<ImgurResponse<ImgurSuccessData>, ImgurResponse<ImgurErrorData>>(
                                 uploadEndpoint
                             ).get()!!
```

### Plugins
The builder of ApiProvider accepts a list of Plugin objects. You can implement your own plugins or use one of the existing ones in the Plugins directory. Here's are the callbacks a custom Plugin subclass can override:

```Kotlin
    // Called to modify a request before sending.
    fun modifyRequest(request: Request, endpoint: Endpoint): Request

    // Called immediately before a request is sent.
    fun beforeRequest(request: Request)

    // Called after a response has been received & decoded, but before calling the completion handler.
    fun <T> afterRequest(response: Response, typedResult: T? = null, endpoint: Endpoint)
```

<details>
    <summary>Toggle me to see a full custom plugin example</summary>
   Here's a possible implementation of a RequestResponseLoggerPlugin that logs using `Log.d()`

   ```Kotlin
   object RequestResponseLoggerPlugin : Plugin {
       override fun <T> afterRequest(response: Response, typedResult: T?, endpoint: Endpoint) {
           Log.d("Network Logger:", response.toString())

       }

       override fun beforeRequest(request: Request) {
           Log.d("Network Logger:", request.toString())
       }

       override fun modifyRequest(request: Request, endpoint: Endpoint): Request = request
   }
   ```
</details>

### Testing
Microya-Kotlin supports mocking responses in your tests. To do that, just initialize a different `ApiProvider` in your tests and specify with a given `delay` and `scheduler` as the `mockingBehavior` parameter.

Now, instead of making actual calls, Microya-Kotlin will respond with the provided  `mockedResponse` computed property in your `Endpoint` type.

```Kotlin
ApiProvider.Builder().baseUrl("https://postman-echo.com")
    .client(OkHttpClient())
    .plugins(listOf(HttpAuthPlugin(HttpAuthPlugin.Scheme.BASIC, "abc123"))
    .requestJsonFormatter(PostmanSerializer.requestJsonFormatter)
    .responseJsonFormatter(PostmanSerializer.responseJsonFormatter)
    .mockingBehaviour(MockingBehaviour(Duration.ZERO))
    .build()
```

You can also define custom `mockedResponse` with in your `mockingBehavior`

```Kotlin
ApiProvider.Builder().baseUrl("https://postman-echo.com")
    .client(OkHttpClient())
    .plugins(listOf(HttpAuthPlugin(HttpAuthPlugin.Scheme.BASIC, "abc123")))
    .requestJsonFormatter(PostmanSerializer.requestJsonFormatter)
    .responseJsonFormatter(PostmanSerializer.responseJsonFormatter)
    .mockingBehaviour(MockingBehaviour(Duration.ZERO) { endpoint: Endpoint ->
        when (endpoint) {
            is PostmanEchoEndpoint.Index -> {
                endpoint.mockResponseObject(200, EmptyBodyResponse())
            }
            is PostmanEchoEndpoint.Post -> {
                null
            }
            is PostmanEchoEndpoint.Get -> {
                endpoint.mockResponseObject(200, FooBar("Sakata", "Gintoki"))
            }

            is PostmanEchoEndpoint.Patch -> {
                endpoint.mockResponseObject(200, FooBar("test", "1234"))
            }
            else -> throw  IllegalArgumentException("Endpoint doesn't exist.")
        }
    })
    .build()
```
## Contributing

See the file [CONTRIBUTING.md](https://github.com/Papershift/Microya-Kotlin/blob/main/CONTRIBUTING.md).


## License
This library is released under the [MIT License](http://opensource.org/licenses/MIT). See LICENSE for details.