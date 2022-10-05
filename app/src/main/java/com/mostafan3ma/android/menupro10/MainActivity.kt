package com.mostafan3ma.android.menupro10

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mostafan3ma.android.menupro10.databinding.ActivityMainBinding
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.ShopRepository
import com.mostafan3ma.android.menupro10.oporations.utils.SuperImageController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var superImageController: SuperImageController

    @Inject
    lateinit var repository: ShopRepository


    private lateinit var binding: ActivityMainBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)










    }

}

