# Deep Neural Networks Benchmark for Android

Available on [Google Play](https://play.google.com/store/apps/details?id=ru.gordinmitya.dnnbenchmark).

## Road map

Frameworks:	

- [x] MNN Alibaba [ver. 1.0.2](https://github.com/alibaba/MNN/releases/tag/1.0.2)
- [x] NCNN Tencent [ver. 20200727](https://github.com/Tencent/ncnn/releases/tag/20200727)
- [x] TFLite Google [ver. 2.3.0](https://bintray.com/google/tensorflow/tensorflow-lite/)
- [x] TFMobile Google [ver. 1.13.1](https://mvnrepository.com/artifact/org.tensorflow/tensorflow-android)
- [x] Pytorch Facebook [ver. 1.5.0](https://github.com/pytorch/pytorch/tree/master/android)
- [x] OpenCV DNN [ver. 4.4.0](https://github.com/opencv/opencv/releases/)
- [x] onnxruntime Microsoft [ver. 1.4.0](https://github.com/microsoft/onnxruntime/releases/)
- [?] Mace by Xiaomi
- [ ] Tengine Lite OPEN AI LAB
- [ ] TNN Tencent
- [ ] NeoML ABBYY
- [?] SNPE Qualcomm
- [ ] HiAI Huawei
- [ ] NeuroPilot SDK Mediatek
- [ ] Paddle-Lite Baidu
- [ ] Samsung Neural SDK (if they approve my request)

Questionable/other:
* [huawei-noah/bolt](https://github.com/huawei-noah/bolt) not very popular?
* [JDAI-CV/dabnn](https://github.com/JDAI-CV/dabnn) binary networks


Features:

- [x] Compare inference results between frameworks and desktop
- [x] Visualize progress/results
- [x] Publish to Play Market
- [x] Collect results on backend
- [ ] Web site with agregated results

Models:

- [x] [MobileNet v2](https://pytorch.org/docs/stable/torchvision/models.html#mobilenet-v2)
- [x] [Deeplab v3](https://www.tensorflow.org/lite/models/segmentation/overview)
- [ ] [Bert Question and Answer](https://www.tensorflow.org/lite/models/bert_qa/overview)

> All models are floating point


Supported ABIs: armeabi-v7a, arm64-v8a. 
Some frameworks (eg TF) also supports x86 and x86_64, but are they still exist in 2020?

## Conversions / Run your own model

A detailed explanation of how to convert the model into each framework available [here](model_conversions/README.md).

Here is repo with docker images contatining some built converters and other maybe nessesary tools. [gordinmitya/docker_that_framework](https://github.com/gordinmitya/docker_that_framework)

## Credits

* Thanks to [Rohithkvsp/OnnxRuntimeAndorid](https://github.com/Rohithkvsp/OnnxRuntimeAndorid/) for sample code on how to use onnxruntime with nnapi!

## SNPE

Qualcomm prohibits redestribution of their libraries, so you have to register there and download them by yourself. `¯\_(ツ)_/¯`

1. Register and download zip from [developer.qualcomm.com](https://developer.qualcomm.com/software/qualcomm-neural-processing-sdk);
2. Copy `android/snpe-release.aar` from archive into `snpe/libs`.

**OR** compile without snpe

1. Remove `, ':snpe'` from `settings.gradle`;
2. Remove `implementation project(path: ':snpe')` from `app/build.gradle`;
3. Remove amy mentions of SNPE in MainActivity.kt.

## License Summary

Project itself and code inside `ru.gordinmitya.*` packages are under MIT licanse as stated in [LICENSE](./LICENSE) file.

Code inside other packages (eg `org.opencv.*`) or some C plus plus code may be under other licenses.

## RANDOM

ImageNet samples were taken from [Kaggle](https://www.kaggle.com/dromosys/imagenet-fastai-sample#n01518878_27837.JPEG).
