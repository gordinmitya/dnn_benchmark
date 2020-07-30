# Deep Neural Networks Benchmark for Android

Available on [Google Play](https://play.google.com/store/apps/details?id=ru.gordinmitya.dnnbenchmark).

## Road map

Frameworks:	

- [x] MNN Alibaba
- [x] NCNN Tencent ([ver. 20200727](https://github.com/Tencent/ncnn/releases/tag/20200727))
- [x] TFLite Google
- [x] TFMobile Google
- [x] Pytorch Facebook
- [x] OpenCV DNN
- [x] Mace Xiaomi
- [x] SNPE Qualcomm
- [ ] HiAI (Huawei)

Features:

- [x] Compare inference results between frameworks and desktop
- [x] Visualize progress/results
- [ ] Collect results on backend
- [ ] Publish to Play Market
- [ ] Users' models, converter on backend side

Models:

- [x] [MobileNet v2](https://www.tensorflow.org/lite/models/image_classification/overview)
- [x] [Deeplab v3](https://www.tensorflow.org/lite/models/segmentation/overview)
- [ ] [Bert Question and Answer](https://www.tensorflow.org/lite/models/bert_qa/overview)

> All models are floating point,
> quantization will be added later.


Supported ABIs: armeabi-v7a, arm64-v8a. 
Some frameworks (eg TF) also supports x86 and x86_64, but are they still exist in 2020?

## Run your own model

Almost each framework has its own format of model file. So you have to do a lot of conversions.

Here is repo with docker images contatining built converters and other nessesary tools. [gordinmitya/docker_that_framework](https://github.com/gordinmitya/docker_that_framework)

## SNPE

Qualcomm prohibits redestribution of their libraries, so you have to register there and download them by yourself. `¯\_(ツ)_/¯`

1. Register and download zip from [developer.qualcomm.com](https://developer.qualcomm.com/software/qualcomm-neural-processing-sdk);
2. Copy `android/snpe-release.aar` from archive into `snpe/libs`.

**OR** compile without snpe

1. Remove `, ':snpe'` from `settings.gradle`;
2. Remove `implementation project(path: ':snpe')` from `app/build.gradle`;
3. Remove amy mentions of SNPE in MainActivity.kt.

## Known issues

* SNPE DSP produces wrong results;
* smth wrong with NCNN – produces wrong results;

## License Summary

Project itself and code inside `ru.gordinmitya.*` packages are under MIT licanse as stated in [LICENSE](./LICENSE) file.

Code inside other packages (eg `org.opencv.*`) or some C plus plus code may be under other licenses.

## RANDOM

ImageNet samples were taken from [Kaggle](https://www.kaggle.com/dromosys/imagenet-fastai-sample#n01518878_27837.JPEG).
