import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoggoViewModel : ViewModel() {

    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> = _imageUrl

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchRandomDogImage() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getRandomDogImage()
                if (response.isSuccessful) {
                    _imageUrl.value = response.body()?.message
                } else {
                    _error.value = "Nie można pobrać zdjęcia"
                    _imageUrl.value = null
                }
            } catch (e: Exception) {
                _error.value = "Błąd sieci: ${e.message}"
                _imageUrl.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun clearState() {
        _imageUrl.value = null
        _isLoading.value = false
        _error.value = null
    }
}
