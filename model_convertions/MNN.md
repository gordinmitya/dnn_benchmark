Build MNN converter. [Official doc](https://www.yuque.com/mnn/en/cvrt_linux)

```
mkdir build && cd build
cmake .. -DMNN_BUILD_CONVERTER=true
make -j12
```

*OR*

Use Dockerfile https://github.com/gordinmitya/docker_that_framework/tree/master/mnn

*OR* 

Use https://convertmodel.com/ (don't actually works)

*THEN*

```bash
./MNNConvert --bizCode MNN --MNNModel mobilenet_v2.mnn --framework ONNX --modelFile ~/dev/benchmark/android/model_convertions/mobilenet_v2.onnx
```