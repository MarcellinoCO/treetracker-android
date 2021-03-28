package org.greenstand.android.TreeTracker.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Size
import android.view.TextureView
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
//import androidx.camera.core.ImageCaptureConfig
//import androidx.camera.core.Preview
//import androidx.camera.core.PreviewConfig
import java.io.File
import org.greenstand.android.TreeTracker.R
import org.greenstand.android.TreeTracker.camera.CameraScreen
import org.greenstand.android.TreeTracker.models.DeviceOrientation
//import org.greenstand.android.TreeTracker.utilities.AutoFitPreviewBuilder
import org.greenstand.android.TreeTracker.utilities.ImageUtils
import org.greenstand.android.TreeTracker.viewmodels.NewTreeViewModel.Companion.FOCUS_THRESHOLD
import org.koin.android.ext.android.inject
import timber.log.Timber

class CaptureImageContract : ActivityResultContract<Boolean, String?>() {

    companion object {
        const val SELFIE_MODE = "SELFIE_MODE"
        const val FOCUS_METRIC_VALUE = "FOCUS_METRIC_VALUE"
        const val TAKEN_IMAGE_PATH = "TAKEN_IMAGE_PATH"
    }

    override fun createIntent(context: Context, selfieMode: Boolean): Intent {
        return Intent(context, ImageCaptureActivity::class.java).apply {
            putExtra(SELFIE_MODE, selfieMode)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        if (resultCode == Activity.RESULT_OK) {
            return intent?.getStringExtra(TAKEN_IMAGE_PATH)
        }
        return null
    }

}

class ImageCaptureActivity : AppCompatActivity() {

//    private lateinit var viewFinder: TextureView
//    private lateinit var imageCaptureButton: ImageButton
//    private lateinit var toolbarTitle: TextView
//    private val deviceOrientation by inject<DeviceOrientation>()
//
    companion object {
        private const val SELFIE_MODE = "SELFIE_MODE"

        const val FOCUS_METRIC_VALUE = "FOCUS_METRIC_VALUE"
        const val TAKEN_IMAGE_PATH = "TAKEN_IMAGE_PATH"

        fun createIntent(context: Context, selfieMode: Boolean = false): Intent {
            return Intent(context, ImageCaptureActivity::class.java).apply {
                putExtra(SELFIE_MODE, selfieMode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val captureSelfie = intent.extras?.getBoolean(SELFIE_MODE, false) ?: false

        setContent {
            CameraScreen(isSelfieMode = captureSelfie) {
                val data = Intent().apply {
                    putExtra(CaptureImageContract.TAKEN_IMAGE_PATH, it)
                }
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }

    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.fragment_camera)
//
//        toolbarTitle = findViewById(R.id.toolbar_title)
//        viewFinder = findViewById(R.id.view_finder)
//        imageCaptureButton = findViewById(R.id.capture_button)
//
//        val captureSelfie = intent.extras?.getBoolean(SELFIE_MODE, false) ?: false
//
//        if (captureSelfie) {
//            toolbarTitle.text = getString(R.string.take_a_selfie)
//        } else {
//            toolbarTitle.text = getString(R.string.add_a_tree)
//        }
//
//        viewFinder.post { startCamera(captureSelfie) }
//    }
//
//    private fun startCamera(captureSelfie: Boolean) {
//        val preview = setupPreview(captureSelfie)
//
//        val lensFacing = if (captureSelfie) CameraX.LensFacing.FRONT else CameraX.LensFacing.BACK
//
//        // Create configuration object for the image capture use case
//        val imageCaptureConfig = ImageCaptureConfig.Builder()
//            .setLensFacing(lensFacing)
//            .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
//            .setTargetResolution(Size(800, 800))
//            .build()
//
//        val file = ImageUtils.createImageFile(this)
//
//        // Build the image capture use case and attach button click listener
//        val imageCapture = ImageCapture(imageCaptureConfig)
//        imageCaptureButton.setOnClickListener {
//            deviceOrientation.takeSnapshotAndDisable()
//            imageCapture.takePicture(
//                file,
//                object : ImageCapture.OnImageSavedListener {
//                    override fun onError(
//                        imageCaptureError: ImageCapture.ImageCaptureError,
//                        message: String,
//                        cause: Throwable?
//                    ) {
//                        Timber.tag("CameraXApp").e("Photo capture failed: $message")
//                        cause?.printStackTrace()
//                    }
//
//                    override fun onImageSaved(file: File) {
//                        ImageUtils.resizeImage(file.absolutePath, captureSelfie)
//                        Timber.tag("CameraXApp").d("Photo capture succeeded: ${file.absolutePath}")
//                        val focusMetric = testFocusQuality(file)
//
//                        val data = Intent().apply {
//                            putExtra(TAKEN_IMAGE_PATH, file.absolutePath)
//                            // if we can't trust the focus metric (it will be null), because of problems
//                            // generating the metric, we set the quality to "good" to
//                            // avoid false positives. Ultimately, some research would be needed
//                            // into determining the root cause of the false positives, but
//                            // that will be a future effort.
//                            if (focusMetric == null) {
//                                putExtra(FOCUS_METRIC_VALUE, FOCUS_THRESHOLD)
//                            } else {
//                                putExtra(FOCUS_METRIC_VALUE, focusMetric)
//                            }
//                        }
//
//                        setResult(Activity.RESULT_OK, data)
//                        finish()
//                    }
//                }
//            )
//        }
//        CameraX.bindToLifecycle(this, preview, imageCapture)
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        deviceOrientation.disable()
//    }
//
//    private fun setupPreview(captureSelfie: Boolean): Preview {
//
//        val lensFacing = if (captureSelfie) CameraX.LensFacing.FRONT else CameraX.LensFacing.BACK
//
//        val previewConfig = PreviewConfig.Builder()
//            .setLensFacing(lensFacing)
//            .build()
//
//        return AutoFitPreviewBuilder.build(previewConfig, viewFinder)
//    }
//
//    // Return either the focus metric, or, in the case of an
//    // exception return null.
//    private fun testFocusQuality(imageFile: File): Double? {
//        try {
//            // metric only cares about luminance.
//            // for memory limitations, and performance and metric consistency,
//            // the image is 200 pixels wide.
//            val grayImage = ImageUtils.getGrayPixelFromBitmap(imageFile.absolutePath, 200)
//            if (grayImage.isNullOrEmpty()) {
//                Timber.d("Failed to create Grayscale Image.")
//                return null
//            }
//            return ImageUtils.brennersFocusMetric(grayImage)
//        } catch (e: Exception) {
//            Timber.d("Unable to get focus metric.")
//            Timber.d(e.message)
//        }
//        // on an error, we return null.
//        return null
//    }
}
