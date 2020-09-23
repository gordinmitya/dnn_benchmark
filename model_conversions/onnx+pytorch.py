import os
import torch
import torchvision
import onnx
from onnxsim import simplify

'''
https://pytorch.org/docs/stable/torchvision/models.html

normalize = transforms.Normalize(
    mean=[0.485, 0.456, 0.406],
    std=[0.229, 0.224, 0.225])
'''

'''
https://github.com/daquexian/onnx-simplifier

pip install onnx-simplifier
'''

os.makedirs("./output", exist_ok=True)
ONNX_PATH = './output/mobilenet_v2.onnx'
TORCH_PATH = './output/mobilenet_v2.pt'

def save_simplify_onnx(model, example):
    # save
    torch.onnx.export(model, example, ONNX_PATH, input_names=["input"], output_names=["473"])
    # simplify
    model = onnx.load(ONNX_PATH)
    model_simp, check = simplify(model)
    assert check, "Simplified ONNX model could not be validated"
    onnx.save(model_simp, ONNX_PATH)

def save_pt(model, example):
    traced_script_module = torch.jit.trace(model, example)
    traced_script_module.save(TORCH_PATH)

if __name__ == "__main__":
    model = torchvision.models.mobilenet_v2(pretrained=True)
    model.eval()
    example = torch.rand(1, 3, 224, 224)

    save_pt(model, example)
    save_simplify_onnx(model, example)
