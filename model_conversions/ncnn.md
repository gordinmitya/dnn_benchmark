Build converter
```bash
git clone https://github.com/Tencent/ncnn
cd ncnn/tools/onnx
cmake . && make -j4
```

Convert onnx model
```bash
./onnx2ncnn ~/benchmark/model_convertions/output/mobilenet_v2.onnx mobilenet_v2.param mobilenet_v2.bin
```

run ncnnoptimizer (available at [convertmodel.com](https://convertmodel.com))

copy .param and .bin file into assets.