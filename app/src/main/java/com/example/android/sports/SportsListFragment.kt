/*
 * Copyright (c) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.android.sports.adapter.SportsAdapter
import com.example.android.sports.databinding.FragmentSportsListBinding
import com.example.android.sports.model.SportsViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

class SportsListFragment : Fragment() {

    private val sportsViewModel: SportsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentSportsListBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSportsListBinding.bind(view)
        val slidingPaneLayout = binding.slidingPaneLayout

        // Ensure that the sliding pane layout does not switch pane with a swipe gesture
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED

        // Initialize the adapter and set it to the RecyclerView.
        val adapter = SportsAdapter {
            // Update the user selected sport as the current sport in the shared viewmodel
            // This will automatically update the dual pane content
            sportsViewModel.updateCurrentSport(it)
            // Navigate to the details screen by using the SlidingPaneLayout
            slidingPaneLayout.openPane()
        }
        binding.recyclerView.adapter = adapter
        adapter.submitList(sportsViewModel.sportsData)

        // Connect the SlidingPaneLayout to the system back button for correct back navigation:
        // if the back button is pressed when the current activity/fragment is in active state,
        // then perform what is added in the Callback object added in addCallback()
        requireActivity().onBackPressedDispatcher.addCallback(
            // The lifecycle owner ensures that the callback is enabled only on STARTED and RESUMED states
            viewLifecycleOwner,
            // When the back button is pressed, the addCallback() method creates an instance of
            // the indicated class and then calls the handleOnBackPressed() method
            SportsListOnBackPressedCallback(slidingPaneLayout)
        )
    }
}


/*  The class overrides the default behaviour of the back button, according to certain conditions
    When the boolean passed in input to the OnBackPressedCallback() class is true, the method
    handleOnBackPressed() is called

    slidingPaneLayout.isSlideable indicates whether the second pane is on a "small" screen or not,
    in other words, if the layout displays a single pane at a time
    slidingPaneLayout.isOpen indicates
 */
class SportsListOnBackPressedCallback(
    private val slidingPaneLayout: SlidingPaneLayout
) : OnBackPressedCallback(slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen),
    SlidingPaneLayout.PanelSlideListener {

    // add the PanelSlideListener to the slidingPaneLayout in order to observe changes
    // "this" identifies the listener of the interface/class that has been implemented
    init {
        slidingPaneLayout.addPanelSlideListener(this)
    }

    override fun handleOnBackPressed() {
        // The openPane() and closePane() methods have only effect if only one pane at a time is visible
        slidingPaneLayout.closePane()
    }

    override fun onPanelSlide(panel: View, slideOffset: Float) {
    }

    override fun onPanelOpened(panel: View) {
        isEnabled = true
    }

    override fun onPanelClosed(panel: View) {
        isEnabled = false
    }

}