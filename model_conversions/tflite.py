import tensorflow as tf

'''
First you need to get saved model from tf.py
'''

converter = tf.lite.TFLiteConverter.from_saved_model("./output/mobilenet_saved")

tflite_model = converter.convert()
open("./output/mobilenet_v2.tflite", "wb").write(tflite_model)