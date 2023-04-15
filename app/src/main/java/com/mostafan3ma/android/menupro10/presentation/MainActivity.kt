package com.mostafan3ma.android.menupro10.presentation

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.mostafan3ma.android.menupro10.databinding.ActivityMainBinding
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.ShopRepository
import com.mostafan3ma.android.menupro10.oporations.utils.Style
import com.mostafan3ma.android.menupro10.oporations.utils.getPrefStyle
import com.mostafan3ma.android.menupro10.oporations.utils.putPrefStyle
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    @Inject
    lateinit var repository: ShopRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try{
            val style= getPrefStyle(this)
        }catch (e:Exception){
            putPrefStyle(this, Style())
        }

    }

}

