package com.example.cityconnect.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cityconnect.model.repositories.AuthRepository
import com.example.cityconnect.model.repositories.PostRepository
import com.example.cityconnect.model.schemas.Post

class FeedViewModel(
    private val postRepository: PostRepository = PostRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {

    // Cache uid once for this VM instance (minimal, avoids repeated auth calls / null timing)
    private val uid: String? = authRepository.currentUid()

    val currentUserId: String? get() = uid

    private val allPosts: LiveData<List<Post>> = postRepository.observePosts()

    private val _showOnlyMine = MutableLiveData(false)
    val showOnlyMine: LiveData<Boolean> = _showOnlyMine

    // Posts shown in UI (optionally filtered)
    val posts: LiveData<List<Post>> = MediatorLiveData<List<Post>>().apply {
        fun recompute(all: List<Post>?, onlyMine: Boolean?) {
            val list = all ?: emptyList()
            val mine = onlyMine == true
            val me = uid
            value = if (mine && !me.isNullOrBlank()) list.filter { it.ownerId == me } else list
        }

        addSource(allPosts) { list ->
            recompute(list, _showOnlyMine.value)
        }
        addSource(_showOnlyMine) { mine ->
            recompute(allPosts.value, mine)
        }
    }

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun setShowOnlyMine(enabled: Boolean) {
        if (_showOnlyMine.value != enabled) {
            _showOnlyMine.value = enabled
        }
    }

    fun toggleOnlyMine() {
        _showOnlyMine.value = !(_showOnlyMine.value ?: false)
    }

    fun refresh() {
        _loading.value = true
        _error.value = null
        postRepository.refreshPosts { result ->
            _loading.postValue(false)
            result.onFailure { e ->
                _error.postValue(e.message ?: "Failed to refresh posts")
            }
        }
    }

    fun create(text: String, imageUri: Uri?, callback: (Result<Unit>) -> Unit) {
        _loading.value = true
        _error.value = null
        postRepository.createPost(text, imageUri) { result ->
            _loading.postValue(false)
            result.onFailure { e -> _error.postValue(e.message ?: "Create failed") }
            callback(result)
        }
    }

    fun update(postId: String, newText: String, newImageUri: Uri?, callback: (Result<Unit>) -> Unit) {
        _loading.value = true
        _error.value = null
        postRepository.updatePost(postId, newText, newImageUri) { result ->
            _loading.postValue(false)
            result.onFailure { e -> _error.postValue(e.message ?: "Update failed") }
            callback(result)
        }
    }

    // Keep old signatures
    fun create(text: String, callback: (Result<Unit>) -> Unit) = create(text, null, callback)

    fun update(postId: String, newText: String, callback: (Result<Unit>) -> Unit) =
        update(postId, newText, null, callback)

    fun delete(postId: String, callback: (Result<Unit>) -> Unit) {
        _loading.value = true
        _error.value = null
        postRepository.deletePost(postId) { result ->
            _loading.postValue(false)
            result.onFailure { e -> _error.postValue(e.message ?: "Delete failed") }
            callback(result)
        }
    }

    fun observeLocalPost(postId: String): LiveData<Post?> = postRepository.observeLocalPost(postId)
}