package com.mostafan3ma.android.menupro10

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.mostafan3ma.android.menupro10.databinding.ActivityMainBinding
import com.mostafan3ma.android.menupro10.oporations.DataManagment.remoteDataSource.RemoteDataSource
import com.mostafan3ma.android.menupro10.oporations.DataManagment.repository.ShopRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: ShopRepository

    @Inject
    lateinit var remoteDataSource: RemoteDataSource

    private lateinit var binding: ActivityMainBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


       val userStatus: String = remoteDataSource.getUserStatus()
        Toast.makeText(this,"$userStatus",Toast.LENGTH_SHORT).show()

        binding.requestCodeBtn.setOnClickListener {
            if (binding.phonNumberEt.text.isEmpty()){
                Toast.makeText(this,"please enter phone number",Toast.LENGTH_SHORT).show()
            }else{
                val phoneNumber:String=binding.phonNumberEt.text.toString()
                remoteDataSource.requestPhoneNumberVerificationCode(phoneNumber,this)
            }
        }

        binding.resendCodeBtn.setOnClickListener {
            if (binding.phonNumberEt.text.isEmpty()){
                Toast.makeText(this,"please enter phone number",Toast.LENGTH_SHORT).show()
            }else{
                val phoneNumber:String=binding.phonNumberEt.text.toString()
                remoteDataSource.resendCodeToVerifyPhoneNumber(phoneNumber,this)
            }
        }

        binding.singInBtn.setOnClickListener {
            if (binding.codeEt.text.isEmpty()){
                Toast.makeText(this,"please enter the code",Toast.LENGTH_SHORT).show()
            }else{
                val code:String=binding.codeEt.text.toString()
                lifecycleScope.launch{
                    remoteDataSource.signIn(code)
                }
            }
        }


        binding.logoutBtn.setOnClickListener {
            remoteDataSource.logOut()
            binding.userStatusTv.apply {
                text=remoteDataSource.getUserStatus()
                visibility=View.VISIBLE
            }
        }

    }

}

