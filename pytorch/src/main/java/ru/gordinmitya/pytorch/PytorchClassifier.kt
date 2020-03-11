package ru.gordinmitya.pytorch

import android.content.Context
import android.graphics.Bitmap
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.Classifier
import ru.gordinmitya.common.utils.AssetUtil

class PytorchClassifier(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel,
    val inferenceType: PytorchInfereceType
) : Classifier(configuration) {

    private var module: Module? = null

    override fun prepare() {
        val file = AssetUtil.copyFileToCache(context, convertedModel.file)
        module = Module.load(file.path)
    }

    override fun predict(input: Bitmap): FloatArray {
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            input,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        val outputTensor = module!!.forward(IValue.from(inputTensor)).toTensor()

        return outputTensor.dataAsFloatArray
    }

    override fun release() {
        module?.destroy()
    }
}