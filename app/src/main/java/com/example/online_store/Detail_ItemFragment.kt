package com.example.online_store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.online_store.databinding.DetailItemLayoutBinding

class Detail_ItemFragment : Fragment() {

    private var _binding : DetailItemLayoutBinding?  = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailItemLayoutBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("item")?.let {
            val item = ItemManager.items[it]

            binding.name3.text = item.name

            binding.released3.text = item.released
            binding.rating3.text = item.rating

            binding.price3.text= item.price
            binding.quantity3.text= item.quantity

            Glide.with(requireContext()).load(item.background_image).circleCrop()
                .into(binding.backgroundImage4)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}