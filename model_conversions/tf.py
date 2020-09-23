import numpy as np
import torch
import torchvision
from pytorch2keras.converter import pytorch_to_keras
from torch.autograd import Variable
import tensorflow as tf
from tensorflow.python.framework.convert_to_constants import convert_variables_to_constants_v2

'''
https://github.com/nerox8664/pytorch2keras

pip install pytorch2keras
'''

# Create and load model
model = torchvision.models.mobilenet_v2(pretrained=True)
model.eval()

# Make dummy variables (and checking if the model works)
input_np = np.random.uniform(0, 1, (1, 3, 224, 224))
input_var = Variable(torch.FloatTensor(input_np))
output = model(input_var)

# Convert the model!
k_model = pytorch_to_keras(model, input_var, (3, 224, 224), 
                     verbose=True, name_policy='short',
                     change_ordering=True)

# Save model to SavedModel format
tf.saved_model.save(k_model, "./output/mobilenet_saved")


# Convert Keras model to ConcreteFunction
full_model = tf.function(lambda x: k_model(x))
full_model = full_model.get_concrete_function(
    tf.TensorSpec(k_model.inputs[0].shape, k_model.inputs[0].dtype))

# Get frozen ConcreteFunction
frozen_func = convert_variables_to_constants_v2(full_model)
frozen_func.graph.as_graph_def()

print("-" * 50)
print("Frozen model layers: ")
for layer in [op.name for op in frozen_func.graph.get_operations()]:
    print(layer)

print("-" * 50)
print("Frozen model inputs: ")
print(frozen_func.inputs)
print("Frozen model outputs: ")
print(frozen_func.outputs)

# Save frozen graph from frozen ConcreteFunction to hard drive
tf.io.write_graph(graph_or_graph_def=frozen_func.graph,
                  logdir="./output/frozen_models",
                  name="mobilenet_v2.pb",
                  as_text=False)