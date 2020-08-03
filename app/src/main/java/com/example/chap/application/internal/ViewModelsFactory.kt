package com.example.chap.application.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chap.application.viewModel.AuthActivityViewModel
import com.example.chap.application.viewModel.CommentsFragmentViewModel
import com.example.chap.application.viewModel.MainActivityViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelsFactory(private val sharedPref: SharedPref) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CommentsFragmentViewModel::class.java) -> CommentsFragmentViewModel(
                sharedPref
            ) as T
//            modelClass.isAssignableFrom(CourseFragmentViewModel::class.java) -> CourseFragmentViewModel(
//                sharedPref
//            ) as T
//            modelClass.isAssignableFrom(CoursesFragmentViewModel::class.java) -> CoursesFragmentViewModel(
//                sharedPref
//            ) as T
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel(
                sharedPref
            ) as T
//            modelClass.isAssignableFrom(PodcastFragmentViewModel::class.java) -> PodcastFragmentViewModel(
//                sharedPref
//            ) as T
//            modelClass.isAssignableFrom(ProfileFragmentViewModel::class.java) -> ProfileFragmentViewModel(
//                sharedPref
//            ) as T
            modelClass.isAssignableFrom(AuthActivityViewModel::class.java) -> AuthActivityViewModel(
                sharedPref
            ) as T
//            modelClass.isAssignableFrom(FavouriteActivityViewModel::class.java) -> FavouriteActivityViewModel(
//                sharedPref
//            ) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}