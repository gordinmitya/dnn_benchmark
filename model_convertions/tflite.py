import tensorflow as tf

converter = tf.lite.TFLiteConverter.from_saved_model("models")

tflite_model = converter.convert()
open("model.tflite", "wb").write(tflite_model)