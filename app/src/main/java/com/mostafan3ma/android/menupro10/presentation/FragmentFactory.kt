package com.mostafan3ma.android.menupro10.presentation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.DefaultShopRepository
import com.mostafan3ma.android.menupro10.oporations.di.RealRepository
import com.mostafan3ma.android.menupro10.oporations.utils.SuperImageController
import com.mostafan3ma.android.menupro10.presentation.fragments.DefaultLastMenuFragment
import com.mostafan3ma.android.menupro10.presentation.fragments.startingFragments.*
import javax.inject.Inject

class FragmentFactory
@Inject
constructor(
    private val superImageController: SuperImageController,
    @RealRepository private val repository: DefaultShopRepository,
    ) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {

            CreateAccountFragment::class.java.name-> {
                CreateAccountFragment(superImageController)
            }
            OtpFragment::class.java.name->{
                OtpFragment(repository)
            }
            DefaultLastMenuFragment::class.java.name->{
                DefaultLastMenuFragment()
            }



            else -> {
                return super.instantiate(classLoader, className)
            }
        }

    }

}