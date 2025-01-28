package com.project.irhs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.irhs.R

/**
 * A simple [Fragment] subclass.
 * Use the [HomeContainerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeContainerFragment : Fragment() {

    private val homeFragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().supportFragmentManager.beginTransaction().apply {
            add(R.id.homeContainer, homeFragment, "fragment_home")
        }.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_container, container, false)
    }

}