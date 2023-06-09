package com.investment.finkart.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.investment.finkart.R
import com.investment.finkart.databinding.FragmentKycDetailsBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.investment.finkart.config.AuthConfig
import com.investment.finkart.models.GenericResponse
import com.investment.finkart.models.KycData
import com.investment.finkart.models.KycItems
import com.investment.finkart.network.RetrofitBuilder
import com.investment.finkart.utils.Constants
import com.investment.finkart.utils.FinkartLoading
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class KycDetailsFragment : Fragment() {
    private lateinit var binding: FragmentKycDetailsBinding
    private var isPanFormatCorrect = false
    private lateinit var authConfig: AuthConfig
    private lateinit var retrofitBuilder: RetrofitBuilder
    private var aadharFrontRequest = false
    private var aadharBackRequest = false
    private var panCardRequest = false
    private var selfieRequest = false
    private var imageFiles = HashMap<String, Uri>()   // this will hold all the picked images Uri
    private var imageUploadedName = ""                // this will give the name of which image has picked from gallery
    private var fileCount = 0                         // this is required to check how many files uploaded to cloudinary. if all four files has uploaded then stop the process below
    private val kycDetails = HashMap<String, Any>()   // this will hold the request body of the kyc form
    private lateinit var finkartLoading: FinkartLoading
    private lateinit var storageRef: StorageReference
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentKycDetailsBinding.inflate(inflater, container, false)

        initView()
        return binding.root
    }

    private fun initView() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        authConfig = AuthConfig(requireContext())
        retrofitBuilder = RetrofitBuilder()
        storageRef = FirebaseStorage.getInstance().reference.child("Kyc").child(userId)
        finkartLoading = FinkartLoading()
        navController = findNavController()

        val name = authConfig.getName()
        val occupation = authConfig.getOccupation()
        binding.etName.text = Editable.Factory().newEditable(name)
        binding.etWhatYouDo.text = Editable.Factory().newEditable(occupation)

        binding.etPanCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val panNumber: String = binding.etPanCardNumber.text.toString()

                val regex = "[A-Z]{5}[0-9]{4}[A-Z]"
                val p = Pattern.compile(regex)
                val m = p.matcher(panNumber)
                if (m.matches()) {
                    if (panNumber.length >= 10) {
                        binding.etPanCardNumber.error = null
                    }

                    isPanFormatCorrect = true
                } else {
                    if (panNumber.length >= 10) {
                        binding.etPanCardNumber.error = "Format is not correct"
                    }

                    isPanFormatCorrect = false
                }
            }
        })

        binding.ibBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener{
            if (isValidInput()) {
                showNextAnimation()
                binding.viewFlipper.showNext()
            }
        }

        binding.btnNext2.setOnClickListener {
            if (isValidInputNominee()){
                binding.viewFlipper.showNext()
            }
        }

        initImageViews()

        getKyc()
    }

    private fun getKyc(){
        val authToken = Constants.TOKEN_PREFIX_BEARER + authConfig.getToken()
        val call = retrofitBuilder.getFinkartService().getKyc(authToken)
        try {
            call.enqueue(object : Callback<KycData>{
                override fun onResponse(call: Call<KycData>, response: Response<KycData>) {
                    if (response.isSuccessful){
                        val data = response.body()
                        if (data != null){
                            when(data.subCode){
                                Constants.RESPONSE_SUCCESS -> {
                                    val kycItems = data.items
                                    kycItems?.let { items ->

                                        binding.viewFlipper.visibility = View.GONE
//                                        binding.frameButton.visibility = View.GONE

                                        val isVerified = items.kyc?.isVerified!!
                                        if (isVerified){
                                            binding.kycDetails.visibility = View.VISIBLE
                                            setDetails(items)
                                        }else{
                                            binding.lvPending.visibility = View.VISIBLE
                                        }
                                    }
                                }

                                Constants.RESPONSE_BAD_REQUEST -> {
                                    binding.viewFlipper.visibility = View.VISIBLE
//                                    binding.frameButton.visibility = View.VISIBLE
                                }

                                else -> {
                                    // something went wrong
                                    onError("", "get")
                                }
                            }
                        }else{
                            // an error occurred
                            onError(response.message(), "get")
                        }
                    }else{
                        onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, "get")
                    }


                    binding.shimmerContainer.visibility = View.GONE
                    binding.shimmerContainer.stopShimmer()
                }

                override fun onFailure(call: Call<KycData>, t: Throwable) {
                    onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, "get")

                    binding.shimmerContainer.visibility = View.GONE
                    binding.shimmerContainer.stopShimmer()
                }

            })
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun isValidInput(): Boolean{
        var isInputValid = true

        val name = binding.etName.text.toString()
        val aadhaarCard = binding.etAadhaarCard.text.toString()
        val panCard = binding.etPanCardNumber.text.toString()
        val whatYouDo = binding.etWhatYouDo.text.toString()

        val errorMessage = "Please provide valid input"
        val errorMessage2 = "Field is Required"

        if (name.isEmpty()){
            binding.etNomineeName.error = errorMessage2
            isInputValid = false
        }

        if (aadhaarCard.isEmpty()){
            binding.etAadhaarCard.error = errorMessage2
            isInputValid = false
        }

        if (aadhaarCard.length < 12){
            binding.etAadhaarCard.error = errorMessage
            isInputValid = false
        }

        if (panCard.isEmpty()){
            binding.etPanCardNumber.error = errorMessage2
            isInputValid = false
        }

        if (!isPanFormatCorrect){
            binding.etPanCardNumber.error = errorMessage
            isInputValid = false
        }

        if (whatYouDo.isEmpty()){
            binding.etWhatYouDo.error = errorMessage2
            isInputValid = false
        }

        return isInputValid
    }

    private fun isValidInputNominee(): Boolean{
        var isInputValid = true

        val nomineeName = binding.etNomineeName.text.toString()
        val nomineeRelation = binding.etNomineeRelation.text.toString()
        val nomineeAadhaarCard = binding.etNomineeAadhaar.text.toString()

        val errorMessage = "Please provide valid input"
        val errorMessage2 = "Field is Required"

        if (nomineeName.isEmpty()){
            binding.etNomineeName.error = errorMessage2
            isInputValid = false
        }

        if (nomineeRelation.isEmpty()){
            binding.etNomineeRelation.error = errorMessage2
            isInputValid = false
        }

        if (nomineeAadhaarCard.isEmpty()){
            binding.etNomineeAadhaar.error = errorMessage2
            isInputValid = false
        }

        if (nomineeAadhaarCard.length < 12){
            binding.etNomineeAadhaar.error = errorMessage
            isInputValid = false
        }

        return isInputValid
    }

    private fun setDetails(kycItems: KycItems){
        binding.tvName.text = kycItems.kyc?.name
        binding.tvAadhaarNumber.text = kycItems.kyc?.aadhaarNumber
        binding.tvPanNo.text = kycItems.kyc?.panNumber
        binding.tvOccupation.text = kycItems.kyc?.occupation

        binding.tvNomineeName.text = kycDetails["nomineeName"].toString()
        binding.tvNomineeAadhaarNumber.text = kycDetails["nomineeAadhaarNo"].toString()
        binding.tvRelation.text = kycDetails["nomineeRelation"].toString()
    }

    private fun initImageViews(){
        binding.frameAadharFront.setOnClickListener{
            aadharFrontRequest = true
            imageUploadedName = Constants.KYC_IMAGE_AADHAAR_FRONT
            invokeImagePicker()
        }

        binding.frameAadharBack.setOnClickListener{
            aadharBackRequest = true
            imageUploadedName = Constants.KYC_IMAGE_AADHAAR_BACK
            invokeImagePicker()
        }

        binding.framePan.setOnClickListener{
            panCardRequest = true
            imageUploadedName = Constants.KYC_IMAGE_PAN_CARD
            invokeImagePicker()

        }

        binding.frameSelfie.setOnClickListener{
            selfieRequest = true
            imageUploadedName = Constants.KYC_IMAGE_SELFIE
            invokeImagePicker()
        }

        binding.btnSubmit.setOnClickListener {
            if (aadharFrontRequest && aadharBackRequest && panCardRequest && selfieRequest){
                // check for all images then dismiss the bottom sheet
                // images uploaded
                finkartLoading.showProgress(requireContext())
                uploadImage(Constants.KYC_IMAGE_AADHAAR_FRONT, imageFiles[Constants.KYC_IMAGE_AADHAAR_FRONT]!!)
            }else{
                Toast.makeText(context, "Please provide all the required images.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun invokeImagePicker() {
        ImagePicker.Companion.with(this)
            .crop()
            .compress(512)
            .maxResultSize(1080, 1080)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data

            var imageView: ImageView? = null
            if (imageUploadedName == Constants.KYC_IMAGE_AADHAAR_FRONT) {
                imageView = binding.ivAadharFront
                imageFiles[Constants.KYC_IMAGE_AADHAAR_FRONT] = imageUri!!

                binding.ivAadharFront.visibility = View.VISIBLE
                binding.lvAadharFront.visibility = View.GONE
            }

            if (imageUploadedName == Constants.KYC_IMAGE_AADHAAR_BACK) {
                imageView = binding.ivAadharBack
                imageFiles[Constants.KYC_IMAGE_AADHAAR_BACK] = imageUri!!

                binding.ivAadharBack.visibility = View.VISIBLE
                binding.lvAadharBack.visibility = View.GONE
            }

            if (imageUploadedName == Constants.KYC_IMAGE_PAN_CARD) {
                imageView = binding.ivPan
                imageFiles[Constants.KYC_IMAGE_PAN_CARD] = imageUri!!

                binding.ivPan.visibility = View.VISIBLE
                binding.lvPan.visibility = View.GONE
            }

            if (imageUploadedName == Constants.KYC_IMAGE_SELFIE) {
                imageView = binding.ivSelfie
                imageFiles[Constants.KYC_IMAGE_SELFIE] = imageUri!!

                binding.ivSelfie.visibility = View.VISIBLE
                binding.lvSelfie.visibility = View.GONE
            }

            loadImage(imageUri!!, imageView!!)
        }
    }

    private fun loadImage(file: Uri, imageView: ImageView){
        try {
            Glide.with(this)
                .load(file)
                .into(imageView)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun uploadImage(fileName: String, file: Uri){
        storageRef.child(fileName).putFile(file)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {uri ->
                    fileCount += 1                 // here we are increasing the file count

                    addImages(fileName, uri.toString())

                    if (fileCount < 4){           // Here we are checking for the no. of files uploaded
                        passFile(fileName)
                    }

                    if (fileCount == 4){
                        doKyc()
                    }
                }
            }
    }

    private fun doKyc(){
        val authToken = Constants.TOKEN_PREFIX_BEARER + authConfig.getToken()

        val name = binding.etName.text.toString()
        val aadhaarCardNumber = binding.etAadhaarCard.text.toString()
        val panCardNumber = binding.etPanCardNumber.text.toString()
        val occupation = binding.etWhatYouDo.text.toString()
        val nomineeName = binding.etNomineeName.text.toString()
        val nomineeRelation = binding.etNomineeRelation.text.toString()
        val nomineeAadhaarNo = binding.etNomineeRelation.text.toString()

        kycDetails["name"] = name
        kycDetails["aadhaarNumber"] = aadhaarCardNumber
        kycDetails["panNumber"] = panCardNumber
        kycDetails["occupation"] = occupation
        kycDetails["nomineeName"] = nomineeName
        kycDetails["nomineeRelation"] = nomineeRelation
        kycDetails["nomineeAadhaarNo"] = nomineeAadhaarNo

        try {
            val call = retrofitBuilder.getFinkartService().doKyc(authToken, kycDetails)
            call.enqueue(object : Callback<GenericResponse>{
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    finkartLoading.hideProgress()

                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null){
                            when(data.subCode){
                                Constants.RESPONSE_SUCCESS -> {
                                    binding.viewFlipper.visibility = View.GONE
//                                binding.frameButton.visibility = View.GONE

                                    binding.lvPending.visibility = View.VISIBLE
                                    authConfig.setProfile(authConfig.getName()!!, kycDetails["selfie"].toString(), authConfig.getOccupation()!!)

//                                ConfigSingleton.isKycDone = true               // this will check for kyc submitted and will not show kyc notice on home page
                                }

                                Constants.RESPONSE_BAD_REQUEST -> {
                                    val message = data.message
                                    if (message == "kyc is already added"){
                                        Toast.makeText(context, "kyc is already added", Toast.LENGTH_SHORT).show()
                                    }else {
                                        Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                else -> {
                                    Toast.makeText(context, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            // an error occurred
                            onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, "add")
                        }
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    finkartLoading.hideProgress()
                }

            })
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun passFile(fileName: String){
        when(fileName){
            Constants.KYC_IMAGE_AADHAAR_FRONT -> {
                uploadImage(Constants.KYC_IMAGE_AADHAAR_BACK, imageFiles[Constants.KYC_IMAGE_AADHAAR_BACK]!!)
            }

            Constants.KYC_IMAGE_AADHAAR_BACK -> {
                uploadImage(Constants.KYC_IMAGE_PAN_CARD, imageFiles[Constants.KYC_IMAGE_PAN_CARD]!!)
            }

            Constants.KYC_IMAGE_PAN_CARD -> {
                uploadImage(Constants.KYC_IMAGE_SELFIE, imageFiles[Constants.KYC_IMAGE_SELFIE]!!)
            }
        }
    }

    private fun addImages(fileName: String, imageUrl: String){
        when(fileName){
            Constants.KYC_IMAGE_AADHAAR_FRONT -> {
                kycDetails["aadhaarFront"] = imageUrl
            }

            Constants.KYC_IMAGE_AADHAAR_BACK -> {
                kycDetails["aadhaarBack"] = imageUrl
            }

            Constants.KYC_IMAGE_PAN_CARD -> {
                kycDetails["panFront"] = imageUrl
            }

            Constants.KYC_IMAGE_SELFIE -> {
                kycDetails["selfie"] = imageUrl
            }
        }
    }

    private fun onError(message: String, from: String){
        when(message){
            Constants.ERROR_MESSAGE_NETWORK -> {
                showError(R.drawable.no_internet, Constants.TEXT_NO_INTERNET_CONNECTION, from)
            }

            Constants.ERROR_MESSAGE_TIMEOUT -> {
                showError(R.drawable.something_went_wrong, Constants.ERROR_MESSAGE_TIMEOUT,from)
            }

            else -> {
                showError(R.drawable.something_went_wrong, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, from)
            }
        }
    }

    private fun showError(image: Int, message: String, from: String){
        binding.shimmerContainer.visibility = View.GONE
        binding.viewFlipper.visibility = View.GONE
        binding.kycDetails.visibility = View.GONE
//        binding.frameButton.visibility = View.GONE
        binding.lvError.visibility = View.GONE

        binding.lvError.visibility = View.VISIBLE
        binding.ivErrorImage.setImageResource(image)
        binding.tvErrorMessage.text = message

        binding.btnRetry.setOnClickListener {
            binding.lvError.visibility = View.GONE
            getKyc()


            if (from == "add"){
                finkartLoading.showProgress(requireContext())
            }else{
                binding.shimmerContainer.visibility = View.VISIBLE
                binding.shimmerContainer.startShimmer()
            }
        }
    }

    private fun showNextAnimation(){
        binding.viewFlipper.setInAnimation(context, R.anim.slide_in_right)
        binding.viewFlipper.setOutAnimation(context, R.anim.slide_out_left)
    }
}