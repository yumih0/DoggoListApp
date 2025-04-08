import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DogApiService {
    @GET("breeds/image/random")
    suspend fun getRandomDogImage(): Response<DogApiResponse>

}

data class DogApiResponse(
    val message: String,
    val status: String
)
