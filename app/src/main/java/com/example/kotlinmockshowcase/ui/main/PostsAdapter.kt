package com.example.kotlinmockshowcase.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmockshowcase.data.main.response.MockyPost
import com.example.kotlinmockshowcase.databinding.PostItemBinding
import com.example.kotlinmockshowcase.general.AutoUpdatableAdapter
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates


/**
 * RecyclerView.Adapter that loads the posts data into posts items for display in the RecyclerView.
 */
class PostsAdapter() : RecyclerView.Adapter<PostsAdapter.PostViewHolder>(), AutoUpdatableAdapter {

    /**
     * List of posts which runs the specified function when the list is changed.
     */
    var posts : List<MockyPost> by Delegates.observable(emptyList()) {
        property, old, new ->
        autoNotify(old, new) {o, n -> o.id == n.id}
    }

    /**
     * Inflates the item, using ViewBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemBinding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    /**
     * Holds the item ViewBinding and assigns data to it.
     */
    class PostViewHolder(private val binding : PostItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mockyPost: MockyPost) {
            binding.title.text = mockyPost.title
            binding.description.text = mockyPost.description
            binding.published.text = "Published: ${mockyPost.published_at}"
            Picasso.get().load(mockyPost.imageUrl).into(binding.image)
        }
    }


}